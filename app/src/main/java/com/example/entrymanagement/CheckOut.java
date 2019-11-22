package com.example.entrymanagement;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.base.Joiner;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class CheckOut extends AppCompatActivity {

    EditText etOutVisPhone;
    Button btnSubmit;
    DatabaseReference rootRef, demoRef;
    String checkOutTime;
    Map<String, Object> map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        etOutVisPhone = findViewById(R.id.etOutVisPhone);
        btnSubmit = findViewById(R.id.btnSubmit);

        //database reference pointing to root of database
        rootRef = FirebaseDatabase.getInstance().getReference();

        //database reference pointing to demo node
        demoRef = rootRef.child("Details");


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etOutVisPhone.getText().toString();

                //Get Details
                demoRef.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        map = (Map<String, Object>) dataSnapshot.getValue();
                        String mail = map.get("VisEmail").toString();
                        Toast.makeText(getApplicationContext(), "" + mail, Toast.LENGTH_LONG).show();
                        String data = map.toString();
                        Toast.makeText(getApplicationContext(), "" + data, Toast.LENGTH_LONG).show();
                        checkOutTime = getRecentTime();
                        storeData(checkOutTime);
                        sendMessage(mail,data);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });




            }
        });
    }

    private void sendMessage(final String mail, final String data) {

       // final String Data = "Visit Details\n\n" + "Visitor Name: " + map.get("VisName") + "\nVisitor Phone: " + map.get("VisPhone") +
         //       "\nCheckIn Time: " + map.get("CheckInTime") + "\nCheckOut Time: " + map.get("VisCheckOutTime") + "\nHost Name: " + map.get("HostName") +
           //     "\nAddress Visited: " + map.get("HostAddress");
        //Toast.makeText(getApplicationContext(),"" + Data, Toast.LENGTH_LONG).show();



        final ProgressDialog dialog = new ProgressDialog(CheckOut.this);
        dialog.setTitle("Sending Email");
        dialog.setMessage("Please wait");
        dialog.show();
        Thread sender = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("innovaccerpractice@gmail.com", "practice1234");
                    sender.sendMail("EmailSender App",
                            ""+data,
                            "innovaccerpractice@gmail.com",
                            ""+mail);
                    dialog.dismiss();
                } catch (Exception e) {
                    Log.e("mylog", "Error: " + e.getMessage());
                }
            }
        });
        sender.start();

    }

    private void storeData(String checkOutTime) {
        demoRef.child(etOutVisPhone.getText().toString()).child("VisCheckOutTime").setValue(checkOutTime);
    }

    private String getRecentTime() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm.ss aa");
        String output = dateFormat.format(currentTime);
        //Toast.makeText(getApplicationContext(),"Time Is: " + output, Toast.LENGTH_LONG).show();
        return output;
    }




}

