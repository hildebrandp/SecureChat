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

import crypto.AESHelper;


public class enterkey extends AppCompatActivity{

    private static Button btnapplyenterkey;

    private EditText txtpublickey;
    private EditText txtprivatekey;

    public static SharedPreferences mPreferences;
    public static SharedPreferences.Editor editor;

    private boolean doubleBackToExitPressedOnce = false;
    private String result = "false";

    private String encryptedKeyPublic;
    private String encryptedKeyPrivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enterkey_layout);

        mPreferences = getSharedPreferences("myapplab.securechat", 0);

        btnapplyenterkey = (Button)findViewById(R.id.btnapplyenterkey);

        txtpublickey = (EditText)findViewById(R.id.edtenterpublickey);
        txtprivatekey = (EditText)findViewById(R.id.edtenterprivatekey);


        btnapplyenterkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtpublickey.getText().toString() != "" && txtprivatekey.getText().toString() != "") {

                    try {
                        encryptedKeyPrivate = AESHelper.encrypt(MainActivity.seedValue, txtprivatekey.getText().toString());
                        encryptedKeyPublic  = AESHelper.encrypt(MainActivity.seedValue, txtpublickey.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mPreferences.edit().putString("RSA_PUBLIC_KEY", encryptedKeyPublic).commit();
                    mPreferences.edit().putString("RSA_PRIVATE_KEY", encryptedKeyPrivate).commit();

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
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
