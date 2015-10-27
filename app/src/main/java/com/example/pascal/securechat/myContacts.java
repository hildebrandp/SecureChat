package com.example.pascal.securechat;


import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

                        userID.add(2L);
                        userNAME.add("Karl");
                        userEMAIL.add("gergeilste@lappenland.de");

                        Toast.makeText(getApplicationContext(), Name , Toast.LENGTH_LONG).show();
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
        startActivityForResult(i, 1);
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

}
