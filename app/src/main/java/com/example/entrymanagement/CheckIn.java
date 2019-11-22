package com.example.entrymanagement;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CheckIn extends AppCompatActivity {
    Button btnSubmit;
    EditText etVisEmail, etVisPhone, etVisName, etHostName, etHostEmail, etHostPhone, etHostAddress;
    DatabaseReference rootRef, demoRef;
    String checkInTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        findViews();

        //database reference pointing to root of database
        rootRef = FirebaseDatabase.getInstance().getReference();

        //database reference pointing to demo node
        demoRef = rootRef.child("Details");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInTime = getRecentTime();
                storeData(checkInTime);
                sendMessage(checkInTime);
            }
        });

    }

    private void findViews(){
        etVisEmail = findViewById(R.id.etVisEmail);
        etVisPhone = findViewById(R.id.etVisPhone);
        etVisName = findViewById(R.id.etVisName);
        etHostName = findViewById(R.id.etHostName);
        etHostEmail = findViewById(R.id.etHostEmail);
        etHostPhone = findViewById(R.id.etHostPhone);
        etHostAddress = findViewById(R.id.etHostAddress);
        btnSubmit = findViewById(R.id.btnSubmit);
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
        dialog.setTitle("Sending Email");
        dialog.setMessage("Please wait");
        dialog.show();
        Thread sender = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("innovaccerpractice@gmail.com", "practice1234");
                    sender.sendMail("EmailSender App",
                            Data,
                            "innovaccerpractice@gmail.com",
                            etHostEmail.getText().toString());
                    dialog.dismiss();
                } catch (Exception e) {
                    Log.e("mylog", "Error: " + e.getMessage());
                }
            }
        });
        sender.start();
    }

    private void storeData(String checkInTime){
        demoRef.child(etVisPhone.getText().toString()).child("VisName").setValue(etVisName.getText().toString());
        demoRef.child(etVisPhone.getText().toString()).child("VisEmail").setValue(etVisEmail.getText().toString());
        demoRef.child(etVisPhone.getText().toString()).child("VisPhone").setValue(etVisPhone.getText().toString());
        demoRef.child(etVisPhone.getText().toString()).child("HostName").setValue(etHostName.getText().toString());
        demoRef.child(etVisPhone.getText().toString()).child("HostEmail").setValue(etHostEmail.getText().toString());
        demoRef.child(etVisPhone.getText().toString()).child("HostPhone").setValue(etHostPhone.getText().toString());
        demoRef.child(etVisPhone.getText().toString()).child("HostAddress").setValue(etHostAddress.getText().toString());
        demoRef.child(etVisPhone.getText().toString()).child("CheckInTime").setValue(checkInTime);

    }

}
