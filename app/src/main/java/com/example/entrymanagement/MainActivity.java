package com.example.entrymanagement;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static final int SMS_PERMISSION_CODE =0 ;
    Button btnNew, btnOut;
    int perm=0,perm2=0;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains("permission")) {
            perm = sharedpreferences.getInt("permission",0 );
        }
        if(!isSmsPermissionGranted())
        {
            requestReadAndSendSmsPermission();
        }

        btnNew = findViewById(R.id.btnNew);
        btnOut = findViewById(R.id.btnOut);


        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), CheckIn.class);
                intent.putExtra("perm",perm);
                startActivity(intent);
            }
        });

        btnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getApplicationContext(), CheckOut.class);
                intent.putExtra("perm",perm);
                startActivity(intent);

            }
        });



    }

    public boolean isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request runtime SMS permission
     */
    private void requestReadAndSendSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {

        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SMS_PERMISSION_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    perm = 1;
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt("permission",perm);
                    editor.commit();


                } else {
                    perm = 0;
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt("permission",perm);
                    editor.commit();

                }
                return;
            }

        }
    }
}
