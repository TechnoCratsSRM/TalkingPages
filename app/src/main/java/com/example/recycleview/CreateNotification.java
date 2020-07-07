package com.example.recycleview;
/*
Talking Pages
Free Audiobook App
Created By: Soumya Chowdhury
            Makineedi Sai Harsh
            Nagani Vrudant Gopalbhai
            Mukul Bhambari
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.session.MediaSession;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.recycleview.Services.NotificationActionService;

import Model.ListItem;

public class CreateNotification {

    public static final String CHANNEL_ID = "channel";
    public static final String ACTION_REWIND = "actionrewind";
    public static final String ACTION_FORWARD = "actionforward";
    public static final String ACTION_PLAY = "actionplay";

    public static Notification notification;

    public static void createNotification(Context context, String title,String author, int playbutton,int rewindbutton,int forwardbutton){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");

            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),R.drawable.icon);

            PendingIntent pendingIntentrewind;
            Intent intentrewind = new Intent(context, NotificationActionService.class).setAction(ACTION_REWIND);
            pendingIntentrewind = PendingIntent.getBroadcast(context,0,intentrewind,PendingIntent.FLAG_UPDATE_CURRENT);

            PendingIntent pendingIntentplay;
            Intent intentplay = new Intent(context, NotificationActionService.class).setAction(ACTION_PLAY);
            pendingIntentplay = PendingIntent.getBroadcast(context,0,intentplay,PendingIntent.FLAG_UPDATE_CURRENT);

            PendingIntent pendingIntentforward;
            Intent intentforward = new Intent(context, NotificationActionService.class).setAction(ACTION_FORWARD);
            pendingIntentforward = PendingIntent.getBroadcast(context,0,intentforward,PendingIntent.FLAG_UPDATE_CURRENT);



            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.icon)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon,128,128,false))
                    .setContentTitle(title)
                    .setContentText(author)
                    .setOnlyAlertOnce(true)
                    .setShowWhen(false)
                    .addAction(rewindbutton,"Rewind",pendingIntentrewind)
                    .addAction(playbutton,"Play",pendingIntentplay)
                    .addAction(forwardbutton,"Forward",pendingIntentforward)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0,1,2)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setOngoing(true)
                    .build();
            //notification.flags|= Notification.FLAG_NO_CLEAR;
            notificationManagerCompat.notify(1, notification);
        }
    }

}
