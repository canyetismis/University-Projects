package com.example.canyetismis.coursework2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.canyetismis.coursework2.MP3Player.MP3PlayerState;

public class MP3Service extends Service {
    MP3Player player = new MP3Player();
    private final IBinder binder = new MP3Binder();
    private final String CHANNEL_ID = "100";
    int NOTIFICATION_ID = 001;

    public MP3Service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }



    public class MP3Binder extends Binder{
        void pause(){MP3Service.this.pause();}
        void play(){MP3Service.this.play();}
        void stop(){MP3Service.this.stop();}
        void load(String s){MP3Service.this.load(s);}
        void stopNotification(){MP3Service.this.stopNotification();}
        int getDuration(){
            int i = (player.getDuration()/1000);
            return i;
        }
        int getProgress(){
            int i =(player.getProgress()/1000);
            return i;
        }
        MP3PlayerState getState(){
            MP3PlayerState str = player.getState();
            return str;
        }
        void createNotificationChannel(){MP3Service.this.createNotificationChannel();}
    }

    private void pause(){
        Log.d("g53mdp", "pause");
        player.pause();
    }

    private void play(){
        Log.d("g53mdp", "play");
        player.play();
    }

    private void stop(){
        Log.d("g53mdp", "stop");
        player.stop();
    }

    private void load(String song){
        if(player.getState() == MP3PlayerState.PLAYING){
            player.stop();
        }
        player.load(song);
    }


    private void createNotificationChannel(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Music Player";
            String description = "Playing music...";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                    importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,
                    CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Music Player")
                    .setContentText("Playing music...")
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            startForeground(NOTIFICATION_ID,	mBuilder.build());
        }
    }

    private void stopNotification(){
        stopForeground(true);
        stopSelf();
    }
}
