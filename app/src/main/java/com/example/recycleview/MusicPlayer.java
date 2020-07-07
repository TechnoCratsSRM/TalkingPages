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
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recycleview.Services.onClearFromRecentService;

import java.io.IOException;


public class MusicPlayer extends AppCompatActivity implements View.OnClickListener,Playable {

    private Thread thread;
    MediaPlayer mediaPlayer;
    private ImageView artistPic;
    private TextView songName;
    private TextView artistName;
    private SeekBar seekBar;
    private TextView leftTime;
    private TextView rightTime;
    private Button prevButton;
    private Button playButton;
    private Button nextButton;
    private Bundle data;
    private String url = "http://2c723b33.ngrok.io/audio/";
    private int k=0;
    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        setupUI();
        check();
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createChannel();
            registerReceiver(broadcastReceiver,new IntentFilter("Track_tracks"));
            startService(new Intent(getBaseContext(), onClearFromRecentService.class));
        }

        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                // SimpleDateFormat dateFormat = new SimpleDateFormat("m:ss");
                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();

                leftTime.setText(time(currentPosition));
                rightTime.setText("-"+time(duration - currentPosition));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void check()
    {
        ConnectivityManager manager= (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork =manager.getActiveNetworkInfo();
        if(activeNetwork==null)
        {
            Toast.makeText(this,"No Internet",Toast.LENGTH_LONG).show();
        }
        return;
    }

    public void setupUI() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.fairytail);

        artistPic = findViewById(R.id.artistPic);
        artistName = findViewById(R.id.artistName);
        songName = findViewById(R.id.songName);
        seekBar = findViewById(R.id.seekBar);
        leftTime = findViewById(R.id.leftTime);
        rightTime = findViewById(R.id.rightTime);
        prevButton = findViewById(R.id.prevButton);
        playButton = findViewById(R.id.playButton);
        nextButton = findViewById(R.id.nextButton);

        playButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        data=getIntent().getExtras();
        if(data!=null)
        {
            songName.setText(data.getString("title"));
            artistName.setText(data.getString("author"));
            url=url+data.getString("title")+","+data.getString("author")+","+data.getString("series")+","+data.getString("genre")+".mp3";
        }
        try{
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                        CreateNotification.createNotification(MusicPlayer.this, data.getString("title"), data.getString("author"), R.drawable.ic_play, R.drawable.ic_fast_rewind, R.drawable.ic_fast_forward);
                    }
                    startMusic();
                }
            });
            mediaPlayer.prepareAsync();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    public void createChannel()
    {
      if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
      {
          NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,"Talking Pages",NotificationManager.IMPORTANCE_HIGH);
          notificationManager = getSystemService(NotificationManager.class);
          if(notificationManager!=null)
          {
              notificationManager.createNotificationChannel(channel);
          }
      }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prevButton:
                backMusic();
                break;
            case R.id.playButton:
                if (mediaPlayer.isPlaying()) {
                    pauseMusic();
                } else {
                    startMusic();
                }

                break;
            case R.id.nextButton:
                nextMusic();
                break;
        }


    }

    public void pauseMusic()
    {
        if(mediaPlayer!=null)
        {
            mediaPlayer.pause();
            unregisterReceiver(noisyAudioStreamReciver);
            playButton.setBackgroundResource(android.R.drawable.ic_media_play);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                CreateNotification.createNotification(MusicPlayer.this, data.getString("title"), data.getString("author"), R.drawable.ic_play, R.drawable.ic_fast_rewind, R.drawable.ic_fast_forward);
            }
        }
    }

    public void startMusic()
    {
           if (mediaPlayer != null) {
               mediaPlayer.start();
               registerReceiver(noisyAudioStreamReciver,intentFilter);
               updateThread();
               playButton.setBackgroundResource(android.R.drawable.ic_media_pause);

               if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                   CreateNotification.createNotification(MusicPlayer.this, data.getString("title"), data.getString("author"), R.drawable.ic_pause, R.drawable.ic_fast_rewind, R.drawable.ic_fast_forward);
               }
           }
    }

    public void backMusic()
    {
        if(mediaPlayer.getCurrentPosition()-10000>=0)
        {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
        }
        else
        {
            mediaPlayer.seekTo(0);
        }
    }


    public void  nextMusic()
    {
        if(mediaPlayer.getCurrentPosition()+10000<mediaPlayer.getDuration())
        {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
        }
        else
        {
            mediaPlayer.seekTo(mediaPlayer.getDuration()-100);
        }
    }

    public void updateThread()
    {

        thread=new Thread(){

            @Override
            public void run() {

                try {
                    while (mediaPlayer!=null&&mediaPlayer.isPlaying())
                    {
                        thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int newPosition = mediaPlayer.getCurrentPosition();
                                int newMax = mediaPlayer.getDuration();
                                seekBar.setMax(newMax);
                                seekBar.setProgress(newPosition);

                                //rightTime.setText(String.valueOf(new java.text.SimpleDateFormat("hh:mm:ss").format(new Date(mediaPlayer.getDuration()-mediaPlayer.getCurrentPosition()))));
                                //leftTime.setText(String.valueOf(new java.text.SimpleDateFormat("hh:mm:ss").format(new Date(mediaPlayer.getCurrentPosition()))));
                                leftTime.setText(time(mediaPlayer.getCurrentPosition()));
                                rightTime.setText("-"+time(mediaPlayer.getDuration()-mediaPlayer.getCurrentPosition()));
                                if(!mediaPlayer.isPlaying())
                                {
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                                        CreateNotification.createNotification(MusicPlayer.this, data.getString("title"), data.getString("author"), R.drawable.ic_play, R.drawable.ic_fast_rewind, R.drawable.ic_fast_forward);
                                    }
                                    playButton.setBackgroundResource(android.R.drawable.ic_media_play);
                                }

                            }
                        });
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }
    public String time(int k)
    {
        int s= k/1000;
        int p1=s%60;
        int p2 =s/60;
        int p3=p2%60;
        p2= p2 / 60;
        String st= p2+":"+p3+":"+p1;
        return (st);
    }


    PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                pauseMusic();
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    @Override
    public boolean onKeyDown (int keyCode,KeyEvent event) {
        // This is the center button for headphones
        if (event.getKeyCode() == KeyEvent.KEYCODE_HEADSETHOOK) {
            //Toast.makeText(context, "BUTTON PRESSED!", Toast.LENGTH_SHORT).show();
            if(mediaPlayer.isPlaying()) {
                pauseMusic();
            }
            else{
                startMusic();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private class NoisyAudioStreamReciver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())){
                pauseMusic();
            }
        }
    }
    private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private NoisyAudioStreamReciver noisyAudioStreamReciver = new NoisyAudioStreamReciver();

    @Override
    public void onBackPressed() {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
        }
        else
        {
            mediaPlayer=null;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null) {
            mediaPlayer.stop();
            //mediaPlayer = null;
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            notificationManager.cancelAll();
            unregisterReceiver(broadcastReceiver);
        }
        //mediaPlayer=null;
        //thread.interrupt();
        //thread=null;
    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");

            switch (action){
                case CreateNotification.ACTION_REWIND:
                    rewind();
                    break;
                case CreateNotification.ACTION_PLAY:
                    if(mediaPlayer.isPlaying()){
                        pause();
                    }
                    else {
                        play();
                    }
                    break;
                case CreateNotification.ACTION_FORWARD:
                    forward();
                    break;
            }
        }
    };

    @Override
    public void rewind() {
        CreateNotification.createNotification(MusicPlayer.this,data.getString("title"),data.getString("author"),R.drawable.ic_pause,R.drawable.ic_fast_rewind,R.drawable.ic_fast_forward);
        backMusic();
    }

    @Override
    public void play() {
        CreateNotification.createNotification(MusicPlayer.this,data.getString("title"),data.getString("author"),R.drawable.ic_pause,R.drawable.ic_fast_rewind,R.drawable.ic_fast_forward);
        startMusic();
    }

    @Override
    public void pause() {
        CreateNotification.createNotification(MusicPlayer.this,data.getString("title"),data.getString("author"),R.drawable.ic_play,R.drawable.ic_fast_rewind,R.drawable.ic_fast_forward);
        pauseMusic();
    }

    @Override
    public void forward() {
        CreateNotification.createNotification(MusicPlayer.this,data.getString("title"),data.getString("author"),R.drawable.ic_pause,R.drawable.ic_fast_rewind,R.drawable.ic_fast_forward);
        nextMusic();
    }
}


