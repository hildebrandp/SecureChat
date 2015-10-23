package com.example.pascal.securechat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.security.KeyPair;

import crypto.RSA;
import crypto.Crypto;

public class newKey extends AppCompatActivity {

    private Button btncreatekey;
    private Button btnrecievekey;
    private Button btnenterkey;
    private Button btnenterkeybluetooth;
    private Button btnenterkeynfc;
    private Button btnapplyenterkey;

    private EditText txtpublickey;
    private EditText txtprivatekey;

    private boolean doubleBackToExitPressedOnce = false;
    private String result = "false";

    public static SharedPreferences mPreferences;
    public static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newkey_layout);

        mPreferences = getSharedPreferences("myapplab.securechat", 0);

        btncreatekey = (Button)findViewById(R.id.btncreatekey);
        btnrecievekey = (Button)findViewById(R.id.btnrecievekey);
        btnenterkey = (Button)findViewById(R.id.btnenterkey);
        btnenterkeynfc = (Button)findViewById(R.id.btnenterkeynfc);
        btnenterkeybluetooth = (Button)findViewById(R.id.btnenterkeybluetooth);
        btnapplyenterkey = (Button)findViewById(R.id.btnapplyenterkey);

        txtpublickey = (EditText)findViewById(R.id.edtenterpublickey);
        txtprivatekey = (EditText)findViewById(R.id.edtenterprivatekey);

        btncreatekey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final KeyPair keyPair = RSA.generate();
                Crypto.writePrivateKeyToPreferences(keyPair.getPrivate());
                Crypto.writePublicKeyToPreferences(keyPair.getPublic());

                result = "true";
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", result);
                setResult(RESULT_OK, returnIntent);

                finish();
            }
        });

        btnrecievekey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.newkey_layout);

            }
        });

        btnenterkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.enterkey_layout);

            }
        });

        btnenterkeynfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create Class for recieving Key via NFC

            }
        });

        btnenterkeybluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create Class for recieving Key via Bluetooth

            }
        });

        btnapplyenterkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtpublickey.getText().toString() != "" && txtprivatekey.getText().toString() != ""){

                    mPreferences.edit().putString("RSA_PUBLIC_KEY",txtpublickey.getText().toString()).commit();
                    mPreferences.edit().putString("RSA_PRIVATE_KEY", txtprivatekey.getText().toString()).commit();

                    result = "true";
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", result);
                    setResult(RESULT_OK, returnIntent);

                    finish();
                }

            }
        });

    }



    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {

            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", result);
            setResult(RESULT_OK, returnIntent);

            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
