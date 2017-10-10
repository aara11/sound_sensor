package com.example.aarushi.sound_sensor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

        import android.content.DialogInterface;
        import android.content.pm.PackageManager;
        import android.media.MediaPlayer;
        import android.media.MediaRecorder;
        import android.support.annotation.NonNull;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.view.View;
        import android.widget.Button;
        import android.widget.Toast;

        import java.io.IOException;

        import static android.Manifest.permission.RECORD_AUDIO;
        import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String mFileName = null;

    private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    private PlayButton   mPlayButton = null;
    private MediaPlayer   mPlayer = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {

            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        Toast.makeText(MainActivity.this, "recoding has value "+ getNoiseLevel() , Toast.LENGTH_SHORT).show();
        Log.e(LOG_TAG, "max amplitude= ");
        //)
        mRecorder.release();
        mRecorder = null;
    }

    class RecordButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }

    class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Record to the external cache directory for visibility
        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        LinearLayout ll = new LinearLayout(this);
        mRecordButton = new RecordButton(this);
        ll.addView(mRecordButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        mPlayButton = new PlayButton(this);
        ll.addView(mPlayButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        setContentView(ll);
    }
    public String getNoiseLevel()
    {
        double REFERENCE=0.00002;
        //Log.d("SPLService", "getNoiseLevel() ");
        int x = mRecorder.getMaxAmplitude();
        double x2 = x;
        //return x2;
//        Log.d("SPLService", "x="+x);
        double db = (20 * Math.log10(x2 / REFERENCE));
        //Log.d("SPLService", "db="+db);
        if(db>=0.0)
        {
            return "more than thresh";
        }
        else
        {
            return "less than thresh";
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}




/*
public class MainActivity extends AppCompatActivity {
    Button buttonstart,buttonstop, buttonplay, buttonstopplay;

    Thread runner;
    private static double eEMA = 0.0;
    static final private double EMA_FILTER=0.6;
    public static final int RequestPermissionCode=1;


    String audiosavepathindevice=null;
    MediaRecorder mediaRecorder;
    MediaPlayer mediapLayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonstart = (Button) findViewById(R.id.start_b);
        buttonstop = (Button) findViewById(R.id.stop_b);
        buttonstopplay = (Button) findViewById(R.id.stop_play);
        buttonplay = (Button) findViewById(R.id.play);
        buttonstop.setEnabled(false);
        buttonstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckPermission())
                {
                    audiosavepathindevice="/storage/extSdCard/audiorecord.jpg";
                    MediaRecorderReady();

                    try
                    {
                        mediaRecorder.prepare();
                        mediaRecorder.start();

                    }catch(IllegalStateException e)
                    {
                        e.printStackTrace();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    buttonstart.setEnabled(false);
                    buttonstop.setEnabled(true);

                    Toast.makeText(MainActivity.this, "Recording started", Toast.LENGTH_SHORT).show();
                }
                else{
                    RequestPermission();
                }

            }
        });
        buttonstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaRecorder.stop();
                buttonstop.setEnabled(false);
                buttonstart.setEnabled(true);
                buttonplay.setEnabled(true);
                Toast.makeText(MainActivity.this, "Recording stopped", Toast.LENGTH_SHORT).show();
            }
        });
        buttonplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException,SecurityException,IllegalStateException{
                buttonstop.setEnabled(false);
               // buttonplay.setEnabled(false);
                buttonstart.setEnabled(false);
                buttonstopplay.setEnabled(true);
                mediapLayer = new MediaPlayer();
                try(){
                    mediapLayer.setDataSource(audiosavepathindevice);
                    mediapLayer.prepare();
                }catch (IOException e)
                {e.printStackTrace();                }
                mediapLayer.start();
                Toast.makeText(MainActivity.this,"recording playing",Toast.LENGTH_SHORT).show();
        }
        });

        buttonstopplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonstart.setEnabled(true);
                buttonplay.setEnabled(true);
                buttonstop.setEnabled(false);
                buttonstopplay.setEnabled(false);
            }
        });
    }

    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audiosavepathindevice);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case RequestPermissionCode:
                if(grantResults.length>0) {
                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(StoragePermission && RecordPermission)
                    {
                        Toast.makeText(this,"PERMISSION IS GRANTED",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this,"PERMISSION DENIED",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void RequestPermission(){
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{WRITE_EXTERNAL_STORAGE,RECORD_AUDIO},RequestPermissionCode);
    }
    public boolean CheckPermission(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),WRITE_EXTERNAL_STORAGE);
        int r1=ContextCompat.checkSelfPermission(getApplicationContext(),RECORD_AUDIO);

        return result== PackageManager.PERMISSION_GRANTED && r1 == PackageManager.PERMISSION_GRANTED;

    }
}

*/


