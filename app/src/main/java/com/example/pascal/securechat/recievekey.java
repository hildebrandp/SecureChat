package com.example.pascal.securechat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Pascal on 23.10.2015.
 */
public class recievekey extends AppCompatActivity {

    private static Button btnenterkey;
    private static Button btnenterkeybluetooth;
    private static Button btnenterkeynfc;

    private boolean doubleBackToExitPressedOnce = false;
    private String result = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recivekey_layout);

        btnenterkey = (Button)findViewById(R.id.btnenterkey);
        btnenterkeynfc = (Button)findViewById(R.id.btnenterkeynfc);
        btnenterkeybluetooth = (Button)findViewById(R.id.btnenterkeybluetooth);

        btnenterkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity();

            }
        });

        btnenterkeynfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create Class for recieving Key via NFC

            }
        });

        btnenterkeybluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create Class for recieving Key via Bluetooth

            }
        });


    }

    private void startActivity(){
        Intent i = new Intent(this, enterkey.class);
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
