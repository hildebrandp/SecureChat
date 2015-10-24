package com.example.pascal.securechat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyPair;

import crypto.RSA;
import crypto.Crypto;

public class newKey extends AppCompatActivity {

    private static Button btncreatekey;
    private static Button btnrecievekey;

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

        btncreatekey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final KeyPair keyPair = RSA.generate();

                StringWriter privateStringWriter = new StringWriter();

                try {
                    PemWriter pemWriter = new PemWriter(privateStringWriter);
                    pemWriter.writeObject(new PemObject("PRIVATE KEY", keyPair.getPrivate().getEncoded()));
                    pemWriter.flush();
                    pemWriter.close();
                    mPreferences.edit().putString("RSA_PRIVATE_KEY", privateStringWriter.toString()).commit();

                } catch (IOException e) {
                    Log.e("RSA", e.getMessage());
                    e.printStackTrace();
                }

                StringWriter publicStringWriter = new StringWriter();

                try {
                    PemWriter pemWriter = new PemWriter(publicStringWriter);
                    pemWriter.writeObject(new PemObject("PUBLIC KEY", keyPair.getPublic().getEncoded()));
                    pemWriter.flush();
                    pemWriter.close();
                    mPreferences.edit().putString("RSA_PUBLIC_KEY", publicStringWriter.toString()).commit();

                } catch (IOException e) {
                    Log.e("RSA", e.getMessage());
                    e.printStackTrace();
                }

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
                startActivity();

            }
        });





    }

    private void startActivity(){
        Intent i = new Intent(this, recievekey.class);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");

                if(result.equals("true")){

                    result = "true";
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", result);
                    setResult(RESULT_OK, returnIntent);

                    finish();
                }else{

                    Toast.makeText(this, "You need a Key", Toast.LENGTH_SHORT).show();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

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
