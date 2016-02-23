package com.example.myapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by NeverDie on 17/02/2016.
 */
public class PlayActivity extends Activity implements View.OnClickListener {
    private static final String PLAYING = "STATE_PLAY";
    private static final String PAUSE = "STATE_PAUSE";
    private ImageView btBack, btStartPause, btShuffle, btPrevious, btNext, btRepeat;
    private TextView tvSongName, tvSongArtist, tvIndex, tvTimeSong;
    private SeekBar seekBarPlay;
    private MediaManager mediaManager;
    private static int beginSong;
    private static String state;
    private static boolean IS_RUNNING = false;

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setContentView(R.layout.playsong);
        initView();
    }

    private void initView() {
        state = PLAYING;
        SharedPreferences sharedPref = this.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        beginSong = sharedPref.getInt("CurrentSong", 0);
        Toast.makeText(this, beginSong + "", Toast.LENGTH_LONG).show();

        btBack = (ImageView) findViewById(R.id.bt_Back);
        btStartPause = (ImageView) findViewById(R.id.bt_StartPause);
        btShuffle = (ImageView) findViewById(R.id.bt_Shuffle);
        btPrevious = (ImageView) findViewById(R.id.bt_Previous);
        btNext = (ImageView) findViewById(R.id.bt_Next);
        btRepeat = (ImageView) findViewById(R.id.bt_Repeat);
        btBack.setOnClickListener(this);
        btStartPause.setOnClickListener(this);
        btShuffle.setOnClickListener(this);
        btPrevious.setOnClickListener(this);
        btNext.setOnClickListener(this);
        btRepeat.setOnClickListener(this);

        tvSongName = (TextView) findViewById(R.id.tv_SongName2);
        tvSongArtist = (TextView) findViewById(R.id.tv_SongArtist2);
        tvIndex = (TextView) findViewById(R.id.tv_Index);
        tvTimeSong = (TextView) findViewById(R.id.tv_SongTime2);

        seekBarPlay = (SeekBar) findViewById(R.id.seekBar);

        mediaManager = new MediaManager(this);
        mediaManager.getAllAudioSongs();

        IS_RUNNING = true;
        startSeekBar.execute();

        seekBarPlay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo(seekBar.getProgress());
            }
        });

        mediaManager.play(beginSong);
        initSongDisplay();
    }

    private void seekTo(int progress) {
        mediaManager.seekTo(progress);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_Back:
                onBackPressed();
                break;
            case R.id.bt_StartPause:
                if (state == PLAYING) {
                    mediaManager.pause();
                    state = PAUSE;
                    btStartPause.setImageResource(R.drawable.play);
                } else if (state == PAUSE) {
                    mediaManager.play();
                    state = PLAYING;
                    btStartPause.setImageResource(R.drawable.pause);
                }
                break;
            case R.id.bt_Previous:
                mediaManager.previous();
                break;
            case R.id.bt_Next:
                mediaManager.next();
                break;
            case R.id.bt_Shuffle:
                break;
            case R.id.bt_Repeat:
                break;
        }
    }

    private void initSongDisplay() {
        tvSongName.setText(mediaManager.getCurrentSongName());
        seekBarPlay.setMax(mediaManager.getMaxDuration());
        tvIndex.setText(mediaManager.getCurrentIndex() + "/" + mediaManager.getArrSongs().size());
    }

    private AsyncTask<Void, Integer, Void> startSeekBar = new AsyncTask<Void, Integer, Void>() {

        @Override
        protected Void doInBackground(Void... params) {
            while (IS_RUNNING) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int currentDuration = mediaManager.getCurrentDuration();
                if (currentDuration > 0) {
                    publishProgress(currentDuration);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            seekBarPlay.setProgress(values[0]);
            tvTimeSong.setText(convertToDate(values[0]));
            tvIndex.setText(mediaManager.getCurrentIndex() + "/" + mediaManager.getArrSongs().size());
        }
    };

    private String convertToDate(Integer value) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        return dateFormat.format(new Date(value));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IS_RUNNING = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
