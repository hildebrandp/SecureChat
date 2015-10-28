package com.example.pascal.securechat;


import android.content.Intent;
import android.content.res.Configuration;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
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

import databases.chatSQLiteHelper;
import databases.userEntryDataSource;
import databases.userSQLiteHelper;

import items.contactItem;
import items.contactListViewAdapter;

public class myContacts extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private TextView showusername;
    private TextView showuseremail;
    private ListView contactsView;
    private EditText searchuser;

    Toolbar toolbar;
    DrawerLayout drawerLayoutgesamt;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;

    private String usertableName = userSQLiteHelper.TABLE_USER;
    public userEntryDataSource datasourceUser;
    public SQLiteDatabase newDBuser;

    private String chattableName = chatSQLiteHelper.TABLE_CHAT;
    public userEntryDataSource datasourceChat;
    public SQLiteDatabase newDBchat;

    private ArrayList<Long> userID      = new ArrayList<Long>();
    private ArrayList<String> userNAME  = new ArrayList<String>();
    private ArrayList<String> userEMAIL = new ArrayList<String>();
    private List<contactItem> contactItems;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mycontacts_layout);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        showuseremail = (TextView)findViewById(R.id.txtshowuseremail);
        showusername = (TextView)findViewById(R.id.txtshowusername);
        contactsView = (ListView)findViewById(R.id.contactlistView);
        searchuser = (EditText)findViewById(R.id.searchcontact);

        userSQLiteHelper userdbHelper = new userSQLiteHelper(this);
        newDBuser = userdbHelper.getWritableDatabase();

        chatSQLiteHelper chatdbHelper = new chatSQLiteHelper(this);
        newDBchat = chatdbHelper.getWritableDatabase();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateuserinfo();

        contactsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                newChat(position);
            }
        });

        searchuser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                contactsView.removeAllViewsInLayout();
                try {
                    Cursor c = newDBuser.rawQuery("SELECT user_id,user_name,user_email FROM userlist WHERE user_name= %?%  ORDER BY user_name DESC", new String[] {searchuser.getText().toString()});

                    if (c != null ) {
                        if  (c.moveToFirst()) {
                            do {
                                Long ID = c.getLong(c.getColumnIndex("user_id"));
                                String Name  = c.getString(c.getColumnIndex("user_name"));
                                String Email = c.getString(c.getColumnIndex("user_email"));
                                userID.add(ID);
                                userNAME.add(Name);
                                userEMAIL.add(Email);

                            }while (c.moveToNext());
                        }
                        displayResultList();
                    }

                } catch (SQLiteException se ) {
                    Log.e(getClass().getSimpleName(), "Could not create or Open the database");
                }

                if(searchuser.getText().toString().equals("")){
                    openAndQueryDatabase();
                    displayResultList();
                }
            }
        });

        new checkContacts().execute();
        openAndQueryDatabase();
        displayResultList();
    }

    private void openAndQueryDatabase(){
        try {

            Cursor c = newDBuser.rawQuery("SELECT * FROM userlist ORDER BY user_name DESC", null);

            if (c != null ) {
                if  (c.moveToFirst()) {
                    do {
                        Long ID = c.getLong(c.getColumnIndex("user_id"));
                        String Name  = c.getString(c.getColumnIndex("user_name"));
                        String Email = c.getString(c.getColumnIndex("user_email"));
                        userID.add(ID);
                        userNAME.add(Name);
                        userEMAIL.add(Email);

                    }while (c.moveToNext());
                }
            }

        } catch (SQLiteException se ) {
            Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        } finally {

            try {
                contactsView.removeAllViewsInLayout();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    private void displayResultList() {

        contactItems = new ArrayList<contactItem>();
        for (int i = 0; i < userID.size(); i++) {
            contactItem item = new contactItem(userID.get(i),userNAME.get(i), userEMAIL.get(i));
            contactItems.add(item);
        }
        contactListViewAdapter adapter1 = new contactListViewAdapter(this,R.layout.contact_item, contactItems);
        contactsView.setAdapter(adapter1);
    }

    private void newChat(int id){

    }

    private void backtomain(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void logout(){
        MainActivity.editor.putString("USER_ID", "");
        MainActivity.editor.putString("USER_NAME", "");
        MainActivity.editor.putString("USER_EMAIL", "");
        MainActivity.editor.putString("USER_PHONENUMBER", "");
        MainActivity.editor.putString("USER_PASSWORD", "");
        MainActivity.editor.putBoolean("firstrun", true);
        MainActivity.editor.commit();

        userSQLiteHelper.cleanUserTable(newDBuser);
        chatSQLiteHelper.cleanChatTable(newDBchat);

        Intent i = new Intent(this, login.class);
        startActivity(i);
        finish();
    }

    private void updateuserinfo(){

        showusername.setText(MainActivity.user.getString("USER_NAME", "Error loading Data"));
        showuseremail.setText(MainActivity.user.getString("USER_EMAIL", "Error loading Data"));
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_contacts) {

        } else if (id == R.id.nav_chats) {
            backtomain();
        } else if (id == R.id.nav_secure) {

        } else if (id == R.id.nav_changedata) {

        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    private class checkContacts extends AsyncTask<String, Integer, Double> {

        String resp;

        protected Double doInBackground(String... params) {

            String phonenumbers = "";
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (phones.moveToNext())
            {
                String name   = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phone  = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phonenumbers = phonenumbers + phone + " ";
            }

            postData(MainActivity.user.getString("USER_EMAIL", ""),MainActivity.user.getString("USER_PASSWORD", ""),phonenumbers);
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
            HttpPost httppost = new HttpPost("http://schisskiss.no-ip.biz/SecureChat/newcontact.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("useremail", valueIWantToSend1));
                nameValuePairs.add(new BasicNameValuePair("userpassword", valueIWantToSend2));
                nameValuePairs.add(new BasicNameValuePair("usercontact", valueIWantToSend3));
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
                @Override
                public void run() {

                    String[] splitResult = String.valueOf(resp).split(">>");


                    if(splitResult[0].equals("login_true")){

                        String[] splitResult1 = String.valueOf(splitResult[1]).split("<<");

                        for(int i=0;i < (splitResult1.length);i++) {

                            String[] splitResult2 = String.valueOf(splitResult1[i]).split("::");
                            if(!splitResult2[0].equals("")){

                                try {
                                    Cursor c = newDBuser.rawQuery("SELECT user_phonenumber FROM userlist WHERE user_phonenumber LIKE ?", new String[] {splitResult2[3]});

                                    if (c == null ) {
                                        datasourceUser.createUserEntry(Long.parseLong(splitResult2[0]), splitResult2[1], splitResult2[2], splitResult2[3], splitResult2[4]);
                                    }

                                } catch (SQLiteException se ) {
                                    Log.e(getClass().getSimpleName(), "Could not create or Open the database");
                                }

                            }
                        }
                    }else{
                        //Toast.makeText(Appcontext, "Error Contacts", Toast.LENGTH_LONG).show();
                    }
                    openAndQueryDatabase();
                    displayResultList();

                }
            });





        }

    }
}
