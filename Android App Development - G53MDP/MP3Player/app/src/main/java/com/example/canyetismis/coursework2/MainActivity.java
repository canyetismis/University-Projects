package com.example.canyetismis.coursework2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.canyetismis.coursework2.MP3Player.MP3PlayerState;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.canyetismis.coursework2.MP3PlayerHandler.PAUSE;
import static com.example.canyetismis.coursework2.MP3PlayerHandler.PLAY;
import static com.example.canyetismis.coursework2.MP3PlayerHandler.STOP;

public class MainActivity extends AppCompatActivity {
    Button pauseButton;
    Button playButton;
    Button stopButton;
    ProgressBar progressBar;
    int time;
    private  MP3Service.MP3Binder binder = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pauseButton = (Button) findViewById(R.id.pauseButton);
        playButton = (Button) findViewById(R.id.playButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        final ListView lv = (ListView) findViewById(R.id.musicList);
        File musicDir = new File(
                Environment.getExternalStorageDirectory().getPath()+ "/Music/");
        File list[] = musicDir.listFiles();
        lv.setAdapter(new ArrayAdapter<File>(this,
                android.R.layout.simple_list_item_1, list));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter,
                                    View myView,
                                    int myItemInt,
                                    long mylng) {
                File selectedFromList =(File) (lv.getItemAtPosition(myItemInt));
                Log.d("g53mdp", selectedFromList.getAbsolutePath());
                // do something with selectedFromList...
                //String song = selectedFromList.getAbsolutePath();
                binder.load(selectedFromList.getAbsolutePath());
                setProgressBar();
                }
        });

        Intent intent = new Intent(this, MP3Service.class);
        this.startService(intent);
        this.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (MP3Service.MP3Binder) service;
            setProgressBar();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    };

    public void onPauseClicked(View v){
        binder.pause();
        setProgressBar();
    }

    public void onPlayClicked(View v){
        binder.play();
        setProgressBar();
    }

    public void onStopClicked(View v){
       binder.stop();
       setProgressBar();
       binder.stopNotification();

    }

    class Progress extends TimerTask{
        public void run(){
            time = binder.getProgress();
            Log.d("PROGRESS", Integer.toString(time));
            progressBar.setProgress(time);
        }
    }

    public void setProgressBar(){
        int duration = binder.getDuration();
        Log.d("g53mdp", Integer.toString(duration));
        progressBar.setMax(duration);
        Timer timer = new Timer();
        timer.schedule(new Progress(),0,1000);


    }

    @Override
    protected void onDestroy() {
        Log.d("g53mdp", "onDestroy: ");
        super.onDestroy();
        if (serviceConnection != null) {
            unbindService(serviceConnection);
            serviceConnection = null;
        }
        binder.createNotificationChannel();
    }
}
