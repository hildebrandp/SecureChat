package com.example.pascal.securechat;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.ContactsContract;
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

import databases.userEntryDataSource;
import databases.userSQLiteHelper;


public class updateContacts {

    private Context Appcontext;

    private String tableName = userSQLiteHelper.TABLE_USER;
    public userEntryDataSource datasource;
    public SQLiteDatabase newDB;

    public SharedPreferences user;

    private String resp;


    public void searchfornewcontacts(Context applicationContext){

        String phonenumbers = "";

        userSQLiteHelper userdbHelper = new userSQLiteHelper(applicationContext);

        newDB = userdbHelper.getWritableDatabase();
        datasource = new userEntryDataSource(applicationContext);
        datasource.open();

        user = applicationContext.getSharedPreferences("myapplab.securechat", 0);

        Cursor phones = applicationContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext())
        {
            String name   = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone  = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phonenumbers = phonenumbers + phone + " ";

        }

        new checkContacts().execute(user.getString("USER_NAME", ""), user.getString("USER_PASSWORD", ""), phonenumbers);

        phones.close();


    }

    private class checkContacts extends AsyncTask<String, Integer, Double> {

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

            int i=0;
            String[] splitResult = String.valueOf(resp).split("|:|");

            if(splitResult[0] == "login_true"){

                String[] splitResult1 = String.valueOf(splitResult[1]).split("||");

                for(;i < (splitResult1.length-1);){

                    String[] splitResult2 = String.valueOf(splitResult[1]).split("::");
                    datasource.createUserEntry(Long.parseLong(splitResult2[0]),splitResult2[1],splitResult2[2],splitResult2[3],splitResult2[4]);

                }


            }else{
                //Toast.makeText(Appcontext, "Error Contacts", Toast.LENGTH_LONG).show();
            }



        }

    }
}
