package com.example.pascal.securechat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.util.ArrayList;
import java.util.List;
import crypto.AESHelper;
import databases.chatSQLiteHelper;
import databases.userEntryDataSource;
import databases.userSQLiteHelper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private TextView showusername;
    private TextView showuseremail;

    public static SharedPreferences user;
    public static SharedPreferences.Editor editor;
    public static final String seedValue = "dsf54g51FJN8df54HDK578sd15cdf";

    private String usertableName = userSQLiteHelper.TABLE_USER;
    public userEntryDataSource datasourceUser;
    public SQLiteDatabase newDBuser;

    private String chattableName = chatSQLiteHelper.TABLE_CHAT;
    public userEntryDataSource datasourceChat;
    public SQLiteDatabase newDBchat;

    private String resp;

    ActionBarDrawerToggle drawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = getSharedPreferences("myapplab.securechat", MODE_PRIVATE);
        editor = user.edit();

        userSQLiteHelper userdbHelper = new userSQLiteHelper(this);
        newDBuser = userdbHelper.getWritableDatabase();

        chatSQLiteHelper chatdbHelper = new chatSQLiteHelper(this);
        newDBchat = chatdbHelper.getWritableDatabase();

        datasourceUser = new userEntryDataSource(this);
        datasourceUser.open();

        showuseremail = (TextView)findViewById(R.id.txtshowuseremail);
        showusername = (TextView)findViewById(R.id.txtshowusername);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(user.getBoolean("firstrun", true)) {

            openfirststart();
        }else{

            String decryptedKey = "";
            try {
                decryptedKey = AESHelper.decrypt(MainActivity.seedValue, user.getString("RSA_PUBLIC_KEY", null));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //get public key an test if it´s ok!!!
            new checkPublicKey().execute(user.getString("USER_EMAIL", ""), user.getString("USER_PASSWORD", ""), decryptedKey);

            onAppStart();
        }

    }

    private void logout(){
        editor.putString("USER_ID", "");
        editor.putString("USER_NAME", "");
        editor.putString("USER_EMAIL", "");
        editor.putString("USER_PHONENUMBER", "");
        editor.putString("USER_PASSWORD", "");
        editor.putBoolean("firstrun", true);
        editor.commit();

        userSQLiteHelper.cleanUserTable(newDBuser);
        chatSQLiteHelper.cleanChatTable(newDBchat);

        openfirststart();
    }


    private void contacts(){
        Intent i = new Intent(this, myContacts.class);
        startActivity(i);
        finish();
    }

    private void openfirststart(){

        Intent i = new Intent(this, login.class);
        startActivityForResult(i, 1);
    }

    private void onAppStart(){

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
                    onAppStart();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_contacts) {
            contacts();
        } else if (id == R.id.nav_chats) {

        } else if (id == R.id.nav_secure) {

        } else if (id == R.id.nav_changedata) {

        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createnewkey(){

        Intent i = new Intent(this, newKey.class);
        startActivityForResult(i, 2);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

                        if(splitResult[1].equals("key_true")){

                            //Toast.makeText(getApplicationContext(), "Key revoke successful", Toast.LENGTH_LONG).show();
                        }else{
                            openfirststart();
                            logout();
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
