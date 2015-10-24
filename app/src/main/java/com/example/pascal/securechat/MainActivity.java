package com.example.pascal.securechat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.view.menu.MenuView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private TextView showusername;
    private TextView showuseremail;

    public static SharedPreferences user;
    public static SharedPreferences.Editor editor;

    private String resp;

    Toolbar toolbar;

    DrawerLayout drawerLayoutgesamt;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = getSharedPreferences("myapplab.securechat", MODE_PRIVATE);
        editor = user.edit();

        showuseremail = (TextView)findViewById(R.id.showuseremail);
        showusername = (TextView)findViewById(R.id.showusername);

        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        drawerLayoutgesamt = (DrawerLayout) findViewById(R.id.drawerlayoutgesamt);
        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayoutgesamt,R.string.auf, R.string.zu);
        drawerLayoutgesamt.setDrawerListener(drawerToggle);

        navigationView = (NavigationView) findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_contacts:
                        break;
                    case R.id.nav_chats:
                        break;
                    case R.id.nav_secure:
                        break;
                    case R.id.nav_changedata:
                        break;
                    case R.id.nav_logout:

                        editor.putString("USER_ID", "");
                        editor.putString("USER_NAME", "");
                        editor.putString("USER_EMAIL", "");
                        editor.putString("USER_PHONENUMBER", "");
                        editor.putString("USER_PASSWORD", "");
                        editor.putBoolean("firstrun", true);
                        editor.commit();
                        openfirststart();

                        break;
                }

                drawerLayoutgesamt.closeDrawers();
                item.setChecked(true);

                return false;
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle.syncState();


        if(user.getBoolean("firstrun", true)) {

            openfirststart();
        }else{
            //update userinformation on menu
            updateuserinfo();
            //get public key an test if it´s ok!!!
            new checkPublicKey().execute(user.getString("USER_NAME",""), user.getString("USER_PASSWORD",""), user.getString("RSA_PUBLIC_KEY",null));
        }

        if(user.getString("USER_PRIVATE_KEY","").equals("")){
            createnewkey();
        }

        //
        //Create Class for sending contact numbers
        //

    }

    private void openfirststart(){

        Intent i = new Intent(this, login.class);
        startActivityForResult(i, 1);
    }

    private void updateuserinfo(){

        showusername.setText(user.getString("USER_NAME","Error loading Data"));
        showuseremail.setText(user.getString("USER_EMAIL", "Error loading Data"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");

                if(result.equals("true")){
                    //update userinformation on menu
                    updateuserinfo();
                    createnewkey();
                }else{
                    finish();
                }
            }else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }

        }else if(requestCode == 2) {

            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");

                if (result.equals("true")) {

                    Toast.makeText(getApplicationContext(), "New Key Saved", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), "You need a Key", Toast.LENGTH_LONG).show();
                    createnewkey();
                }
            }else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    private void createnewkey(){

        Intent i = new Intent(this, newKey.class);
        startActivityForResult(i, 2);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayoutgesamt);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(new Configuration());
    }

    private class checkPublicKey extends AsyncTask<String, Integer, Double> {

        protected Double doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0],params[1],params[2]);
            return null;
        }

        protected void onPostExecute(Double result){
            //Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
        }
        protected void onProgressUpdate(Integer... progress){
        }

        public void postData(String valueIWantToSend1, String valueIWantToSend2 , String valueIWantToSend3) {


            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://schisskiss.no-ip.biz/SecureChat/testkey.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("useremail", valueIWantToSend1));
                nameValuePairs.add(new BasicNameValuePair("userpassword", valueIWantToSend2));
                nameValuePairs.add(new BasicNameValuePair("userkey", valueIWantToSend3));
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

                        if(splitResult[0].equals("key_true")){

                            //Toast.makeText(getApplicationContext(), "Key revoke successful", Toast.LENGTH_LONG).show();
                        }else{
                            openfirststart();
                            editor.putString("RSA_PRIVATE_KEY", "");
                            editor.putString("RSA_PUBLIC_KEY", "");
                            editor.putBoolean("firstrun", true);
                            editor.commit();
                            Toast.makeText(getApplicationContext(), "Wrong Key!", Toast.LENGTH_LONG).show();

                        }

                    }else {

                        //Toast.makeText(getApplicationContext(), "Error" , Toast.LENGTH_LONG).show();
                    }
                }
            });


        }

    }
}
