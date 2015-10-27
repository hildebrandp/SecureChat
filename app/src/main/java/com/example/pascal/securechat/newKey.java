package com.example.pascal.securechat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

import crypto.AESHelper;
import crypto.RSA;
import crypto.Crypto;

public class newKey extends AppCompatActivity {

    private static Button btncreatekey;
    private static Button btnrecievekey;

    private boolean doubleBackToExitPressedOnce = false;
    private String result = "false";

    public static SharedPreferences mPreferences;
    public static SharedPreferences.Editor editor;

    private static String encryptedKey;
    private static String decryptedKey;
    private String resp;


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
                btnrecievekey.setClickable(false);
                btncreatekey.setClickable(false);
                btncreatekey.setText("Please Wait...");

                new createKey().execute();


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

    private class createKey extends AsyncTask<String, Integer, Double> {

        protected Double doInBackground(String... params) {

            final KeyPair keyPair = RSA.generate();

            StringWriter privateStringWriter = new StringWriter();

            try {
                PemWriter pemWriter = new PemWriter(privateStringWriter);
                pemWriter.writeObject(new PemObject("PRIVATE KEY", keyPair.getPrivate().getEncoded()));
                pemWriter.flush();
                pemWriter.close();

                try {
                    decryptedKey = AESHelper.encrypt(MainActivity.seedValue , privateStringWriter.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mPreferences.edit().putString("RSA_PRIVATE_KEY", decryptedKey).commit();

            } catch (IOException e) {
                Log.e("RSA", e.getMessage());
                e.printStackTrace();
            }

            final StringWriter publicStringWriter = new StringWriter();

            try {
                PemWriter pemWriter = new PemWriter(publicStringWriter);
                pemWriter.writeObject(new PemObject("PUBLIC KEY", keyPair.getPublic().getEncoded()));
                pemWriter.flush();
                pemWriter.close();

                try {
                    encryptedKey = AESHelper.encrypt(MainActivity.seedValue, publicStringWriter.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mPreferences.edit().putString("RSA_PUBLIC_KEY", encryptedKey).commit();

            } catch (IOException e) {
                Log.e("RSA", e.getMessage());
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });

            postData(publicStringWriter.toString());
            return null;
        }

        protected void onPostExecute(Double result){
            //Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
        }
        protected void onProgressUpdate(Integer... progress){
        }

        public void postData(String valueIWantToSend1) {


            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://schisskiss.no-ip.biz/SecureChat/updateKey.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("useremail", mPreferences.getString("USER_EMAIL", "")));
                nameValuePairs.add(new BasicNameValuePair("userpassword", mPreferences.getString("USER_PASSWORD", "")));
                nameValuePairs.add(new BasicNameValuePair("userpublickey", valueIWantToSend1));
                nameValuePairs.add(new BasicNameValuePair("key", "16485155612574852"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();

                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append((line));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                resp = sb.toString();
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }

            runOnUiThread(new Runnable() {
                public void run() {

                    String[] splitResult = String.valueOf(resp).split("::");

                    if(splitResult[0].equals("login_false")) {

                        //Toast.makeText(getApplicationContext(), "Key Revoke failed", Toast.LENGTH_LONG).show();

                    }else if(splitResult[0].equals("login_true")){

                        if(splitResult[1].equals("update_true")){

                            Toast.makeText(getApplicationContext(), "Key update successful", Toast.LENGTH_LONG).show();

                            result = "true";
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("result", result);
                            setResult(RESULT_OK, returnIntent);

                            finish();
                        }else{

                            Toast.makeText(getApplicationContext(), "Key update fail" , Toast.LENGTH_LONG).show();

                            result = "false";
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("result", result);
                            setResult(RESULT_OK, returnIntent);

                            finish();
                        }

                    }else {

                        Toast.makeText(getApplicationContext(), "Error" , Toast.LENGTH_LONG).show();

                        result = "false";
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", result);
                        setResult(RESULT_OK, returnIntent);

                        finish();
                    }
                }
            });


        }

    }

}
