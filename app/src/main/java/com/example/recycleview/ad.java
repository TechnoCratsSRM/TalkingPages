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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class ad extends AppCompatActivity implements RewardedVideoAdListener {
    private RewardedVideoAd rewardedVideoAd;
    private Button button;
    private Bundle data;
    private int k=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);
        Toast.makeText(this,"Please watch an Ad to listen to the Audio Book",Toast.LENGTH_LONG).show();
        check();
        button=findViewById(R.id.button5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });


    }

    public void check()
    {
        ConnectivityManager manager= (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork =manager.getActiveNetworkInfo();
        if(activeNetwork==null)
        {
           Toast.makeText(this,"No Connection\nPress RELOAD",Toast.LENGTH_LONG).show();
        }

        else
        {
            MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
            rewardedVideoAd= MobileAds.getRewardedVideoAdInstance(this);
            rewardedVideoAd.setRewardedVideoAdListener(this);
            loadAds();

        }
    }

    private void loadAds() {
        rewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        if(k==0) {
            if (rewardedVideoAd.isLoaded()) {
                rewardedVideoAd.show();
            }
        }

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        if(k==0){
        Toast.makeText(this,"Please watch complete ad\nPress RELOAD",Toast.LENGTH_LONG).show();
        }
        else
        {
            data=getIntent().getExtras();
            if(data!=null) {
                String title = data.getString("title");
                String author = data.getString("author");
                String series = data.getString("series");
                String genre = data.getString("genre");
                Intent next = new Intent(this, MusicPlayer.class);
                next.putExtra("title", title);
                next.putExtra("author", author);
                next.putExtra("series",series);
                next.putExtra("genre", genre);
                this.startActivity(next);
            }
        }
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        k++;

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }
    @Override
    protected void onResume() {
        rewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        rewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        rewardedVideoAd.destroy(this);
        super.onDestroy();
    }
}
