package com.example.pascal.securechat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by Pascal on 22.10.2015.
 */
public class login extends Activity{

    private EditText useremail;
    private EditText userpassword;
    private Button login;
    private TextView newaccount;

    public static SharedPreferences user;
    public static SharedPreferences.Editor editor;

    private boolean doubleBackToExitPressedOnce = false;

    private String resp;
    private String result = "false";

    //String for Pencrypted Password and String for Encrypting
    private String encryptedPassword;
    private static String cryptoPass = "158564261646132185";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        useremail = (EditText)findViewById(R.id.edtusername);
        userpassword = (EditText)findViewById(R.id.edtuserpassword);
        login = (Button)findViewById(R.id.btnlogin);
        newaccount = (TextView)findViewById(R.id.txtnewAccount);

        //get Shared Preferences
        user = getSharedPreferences("myapplab.securechat", MODE_PRIVATE);
        editor = user.edit();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkinput()){
                    encryptedPassword = encryptIt(userpassword.getText().toString());
                    new acclogin().execute(useremail.getText().toString(), encryptedPassword);
                }
            }
        });

        newaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createnewacc();
            }
        });

    }

    private void createnewacc(){
        Intent i = new Intent(this, newaccount.class);
        startActivityForResult(i, 1);
    }

    private boolean checkinput(){

        if(useremail.equals("")){
            Toast.makeText(getApplicationContext(), "Empty E-Mail", Toast.LENGTH_LONG).show();
            return false;
        }else if(userpassword.equals("")){
            Toast.makeText(getApplicationContext(), "Empty Password", Toast.LENGTH_LONG).show();
            return false;
        }else{
            return true;
        }

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

                        Toast.makeText(getApplicationContext(), "Cancel Account creation" , Toast.LENGTH_LONG).show();
                    }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

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

    private class acclogin extends AsyncTask<String, Integer, Double> {

        protected Double doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0],params[1]);
            return null;
        }

        protected void onPostExecute(Double result){
            //Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
        }
        protected void onProgressUpdate(Integer... progress){
        }

        public void postData(String valueIWantToSend1, String valueIWantToSend2 ) {


            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://schisskiss.no-ip.biz/SecureChat/login.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("useremail", valueIWantToSend1));
                nameValuePairs.add(new BasicNameValuePair("userpassword", valueIWantToSend2));
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

                        Toast.makeText(getApplicationContext(), "Login not Successful", Toast.LENGTH_LONG).show();
                        useremail.setText("");

                    }else if(splitResult[0].equals("login_true")){

                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();

                        editor.putString("USER_ID", splitResult[1]);
                        editor.putString("USER_NAME", splitResult[2]);
                        editor.putString("USER_EMAIL", splitResult[3]);
                        editor.putString("USER_PHONENUMBER", splitResult[4]);
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
}
