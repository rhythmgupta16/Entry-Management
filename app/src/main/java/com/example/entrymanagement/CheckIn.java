package com.example.entrymanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.ProgressDialog;
import android.util.Log;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CheckIn extends AppCompatActivity implements View.OnClickListener {
    Button btnSubmit;
    EditText etVisEmail, etVisPhone, etVisName, etHostName, etHostEmail, etHostPhone, etHostAddress;
    DatabaseReference rootRef, demoRef;
    String checkInTime;
    int perm;

    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        perm = getIntent().getIntExtra("perm", 0);
        //Toast.makeText(getApplicationContext(), "" + perm, Toast.LENGTH_LONG).show();

        findViews();

        //database reference pointing to root of database
        rootRef = FirebaseDatabase.getInstance().getReference();

        //database reference pointing to demo node
        demoRef = rootRef.child("Details");


    }

    private void findViews() {
        etVisEmail = findViewById(R.id.etVisEmail);
        etVisPhone = findViewById(R.id.etVisPhone);
        etVisName = findViewById(R.id.etVisName);
        etHostName = findViewById(R.id.etHostName);
        etHostEmail = findViewById(R.id.etHostEmail);
        etHostPhone = findViewById(R.id.etHostPhone);
        etHostAddress = findViewById(R.id.etHostAddress);
        btnSubmit = findViewById(R.id.btnSubmit);
        awesomeValidation.addValidation(this, R.id.etVisName, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.etHostName, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.etVisEmail, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        awesomeValidation.addValidation(this, R.id.etHostEmail, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        awesomeValidation.addValidation(this, R.id.etVisPhone, "^[2-9]{2}[0-9]{8}$", R.string.mobileerror);
        awesomeValidation.addValidation(this, R.id.etHostPhone, "^[2-9]{2}[0-9]{8}$", R.string.mobileerror);
        awesomeValidation.addValidation(this, R.id.etHostAddress, "^[#.0-9a-zA-Z\\s,-]+$", R.string.addresserror);

        btnSubmit.setOnClickListener(this);

    }

    private String getRecentTime() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm.ss aa");
        String output = dateFormat.format(currentTime);
        //Toast.makeText(getApplicationContext(),"Time Is: " + output, Toast.LENGTH_LONG).show();
        return output;
    }


    private void sendMessage(String checkInTime) {
        final String Data = "Visitor Details\n\n" + "Visitor Name: " + etVisName.getText().toString() + "\nVisitor Email: " +
                etVisEmail.getText().toString() + "\nVisitor Phone: " + etVisPhone.getText().toString() + "\nCheckIn Time: " + checkInTime;
        final ProgressDialog dialog = new ProgressDialog(CheckIn.this);
        dialog.setTitle("Sending Email and SMS");
        dialog.setMessage("Please wait");
        dialog.setCancelable(false);
        dialog.show();
        final Thread sender = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("innovaccerpractice@gmail.com", "practice1234");
                    sender.sendMail("EmailSender App",
                            Data,
                            "innovaccerpractice@gmail.com",
                            etHostEmail.getText().toString());

                } catch (Exception e) {
                    Log.e("mylog", "Error: " + e.getMessage());
                }
            }
        });

        final Thread sms = new Thread(new Runnable() {
            @Override
            public void run() {

                if (perm == 1) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("+91" + etHostPhone.getText().toString(), null, Data, null, null);
                }
                if (perm == 0) {
                    //Toast.makeText(getApplicationContext(), "Grant SMS permission first!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        sender.start();
        sms.start();

        Thread checker = new Thread(new Runnable() {
            @Override
            public void run() {
                while (sms.isAlive() || sender.isAlive()) {

                }
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        checker.start();


    }

    private void storeData(String checkInTime) {
        demoRef.child(etVisPhone.getText().toString()).child("VisName").setValue(etVisName.getText().toString());
        demoRef.child(etVisPhone.getText().toString()).child("VisEmail").setValue(etVisEmail.getText().toString());
        demoRef.child(etVisPhone.getText().toString()).child("VisPhone").setValue(etVisPhone.getText().toString());
        demoRef.child(etVisPhone.getText().toString()).child("HostName").setValue(etHostName.getText().toString());
        demoRef.child(etVisPhone.getText().toString()).child("HostEmail").setValue(etHostEmail.getText().toString());
        demoRef.child(etVisPhone.getText().toString()).child("HostPhone").setValue(etHostPhone.getText().toString());
        demoRef.child(etVisPhone.getText().toString()).child("HostAddress").setValue(etHostAddress.getText().toString());
        demoRef.child(etVisPhone.getText().toString()).child("CheckInTime").setValue(checkInTime);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(CheckIn.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if (view == btnSubmit) {
            submitForm();
        }
    }


    private void submitForm() {
        if (awesomeValidation.validate()) {
            checkInTime = getRecentTime();
            storeData(checkInTime);
            sendMessage(checkInTime);
        }

    }

}