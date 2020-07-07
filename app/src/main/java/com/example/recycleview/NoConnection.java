package com.example.recycleview;
/*
Talking Pages
Free Audiobook App
Created By: Soumya Chowdhury
            Makineedi Sai Harsh
            Nagani Vrudant Gopalbhai
            Mukul Bhambari
 */
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NoConnection extends AppCompatActivity {

    private Button b ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);
        b=findViewById(R.id.retry);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
    }
    public void check() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (activeNetwork == null) {
            Toast.makeText(this,"No Connection\nPress RETRY",Toast.LENGTH_LONG).show();
        }
        else
        {Intent next = new Intent(this, MainActivity.class);
        startActivity(next);}
    }

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
       finish();
    }

    @Override
    protected void onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }
}
