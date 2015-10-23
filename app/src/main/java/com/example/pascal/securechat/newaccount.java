package com.example.pascal.securechat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;

/**
 * Created by Pascal on 22.10.2015.
 */
public class newaccount extends Activity {

    private EditText username;
    private EditText useremail;
    private EditText userphonenumber;
    private EditText userpassword1;
    private EditText userpassword2;
    private Button btncreateacc;

    private String resp;
    private String result = "false";

    public static SharedPreferences user;
    public static SharedPreferences.Editor editor;

    private boolean doubleBackToExitPressedOnce = false;

    //String for Pencrypted Password and String for Encrypting
    private String encryptedPassword;
    private static String cryptoPass = "158564261646132185";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newaccount_layout);

        username = (EditText)findViewById(R.id.edtnewusername);
        useremail = (EditText)findViewById(R.id.edtnewuseremail);
        userphonenumber = (EditText)findViewById(R.id.edtnewuserphonenumber);
        userpassword1 = (EditText)findViewById(R.id.edtnewuserpassword1);
        userpassword2 = (EditText)findViewById(R.id.edtnewuserpassword2);
        btncreateacc = (Button)findViewById(R.id.btncreateaccount);

        //get Shared Preferences
        user = getSharedPreferences("myapplab.securechat", MODE_PRIVATE);
        editor = user.edit();

        btncreateacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkinput()){
                    encryptedPassword = encryptIt(userpassword1.getText().toString());
                    new createnewaccount().execute(username.getText().toString(), useremail.getText().toString(), encryptedPassword, userphonenumber.getText().toString());
                }

            }
        });
    }

    private boolean checkinput(){

        if(username.equals("")){
            Toast.makeText(getApplicationContext(), "Empty User Name", Toast.LENGTH_LONG).show();
            return false;
        }else if(useremail.equals("")){
            Toast.makeText(getApplicationContext(), "Empty E-Mail", Toast.LENGTH_LONG).show();
            return false;
        }else if(userphonenumber.equals("")){
            Toast.makeText(getApplicationContext(), "Empty Phonenumber", Toast.LENGTH_LONG).show();
            return false;
        }else if(userpassword1.equals("")){
            Toast.makeText(getApplicationContext(), "Empty first Password", Toast.LENGTH_LONG).show();
            return false;
        }else if(userpassword2.equals("")){
            Toast.makeText(getApplicationContext(), "Empty second Password", Toast.LENGTH_LONG).show();
            return false;
        }else if(!userpassword1.getText().toString().equals(userpassword2.getText().toString())){
            Toast.makeText(getApplicationContext(), "Passwords not equal!", Toast.LENGTH_LONG).show();
            return false;
        }else{
            return true;
        }

    }


    private class createnewaccount extends AsyncTask<String, Integer, Double> {

        protected Double doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0],params[1],params[2],params[3]);
            return null;
        }

        protected void onPostExecute(Double result){
            //Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
        }
        protected void onProgressUpdate(Integer... progress){
        }

        public void postData(String valueIWantToSend1, String valueIWantToSend2, String valueIWantToSend3, String valueIWantToSend4) {


            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://schisskiss.no-ip.biz/SecureChat/newaccount.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username", valueIWantToSend1));
                nameValuePairs.add(new BasicNameValuePair("useremail", valueIWantToSend2));
                nameValuePairs.add(new BasicNameValuePair("userpassword", valueIWantToSend3));
                nameValuePairs.add(new BasicNameValuePair("phonenumber", valueIWantToSend4));
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

                    if(splitResult[0].equals("email_used")) {

                        Toast.makeText(getApplicationContext(), "E-Mail is already used", Toast.LENGTH_LONG).show();
                        useremail.setText("");

                    }else if(splitResult[0].equals("phone_used")){

                        Toast.makeText(getApplicationContext(), "Phonenumber is already used", Toast.LENGTH_LONG).show();
                        userphonenumber.setText("");

                    }else if(splitResult[0].equals("insert_error")){

                        Toast.makeText(getApplicationContext(), "Login error please try again", Toast.LENGTH_LONG).show();

                    }else if(splitResult[0].equals("insert_success")){

                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();

                        editor.putString("USER_ID", splitResult[1]);
                        editor.putString("USER_NAME", username.getText().toString());
                        editor.putString("USER_EMAIL", useremail.getText().toString());
                        editor.putString("USER_PHONENUMBER", userphonenumber.getText().toString());
                        editor.putString("USER_PASSWORD", encryptedPassword);
                        editor.putBoolean("firstrun", false);
                        editor.commit();

                        result = "true";
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", result);
                        setResult(RESULT_OK, returnIntent);
                        finish();

                    }else {

                        Toast.makeText(getApplicationContext(), "Error" , Toast.LENGTH_LONG).show();
                    }
                }
            });


        }

    }

    public static String encryptIt(String value) {
        try {
            DESKeySpec keySpec = new DESKeySpec(cryptoPass.getBytes("UTF8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] clearText = value.getBytes("UTF8");
            // Cipher is not thread safe
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            String encrypedValue = Base64.encodeToString(cipher.doFinal(clearText), Base64.DEFAULT);

            return encrypedValue;

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return value;
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
