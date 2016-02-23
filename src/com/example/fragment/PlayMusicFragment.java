package com.example.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapp.MediaManager;
import com.example.myapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by NeverDie on 18/02/2016.
 */
public class PlayMusicFragment extends BaseFragment implements View.OnClickListener {
    private static final String PLAYING = "STATE_PLAY";
    private static final String PAUSE = "STATE_PAUSE";
    private ImageView btBack, btStartPause, btShuffle, btPrevious, btNext, btRepeat;
    private TextView tvSongName, tvSongArtist, tvIndex, tvTimeSong;
    private SeekBar seekBarPlay;
    private MediaManager mediaManager;
    private static int beginSong;

    private static String state;

    private static boolean IS_RUNNING = false;


    public PlayMusicFragment(int idLayout) {
        super(idLayout);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        initView();
        return rootView;
    }

    private void initView() {
        state = PLAYING;

        SharedPreferences sharedPref = getActivity().getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        beginSong = sharedPref.getInt("CurrentSong", 0);
        Toast.makeText(getActivity(), beginSong + "", Toast.LENGTH_LONG).show();

        btBack = (ImageView) rootView.findViewById(R.id.bt_Back);
        btStartPause = (ImageView) rootView.findViewById(R.id.bt_StartPause);
        btShuffle = (ImageView) rootView.findViewById(R.id.bt_Shuffle);
        btPrevious = (ImageView) rootView.findViewById(R.id.bt_Previous);
        btNext = (ImageView) rootView.findViewById(R.id.bt_Next);
        btRepeat = (ImageView) rootView.findViewById(R.id.bt_Repeat);
        btBack.setOnClickListener(this);
        btStartPause.setOnClickListener(this);
        btShuffle.setOnClickListener(this);
        btPrevious.setOnClickListener(this);
        btNext.setOnClickListener(this);
        btRepeat.setOnClickListener(this);

        tvSongName = (TextView) rootView.findViewById(R.id.tv_SongName2);
        tvSongArtist = (TextView) rootView.findViewById(R.id.tv_SongArtist2);
        tvIndex = (TextView) rootView.findViewById(R.id.tv_Index);
        tvTimeSong = (TextView) rootView.findViewById(R.id.tv_SongTime2);

        seekBarPlay = (SeekBar) rootView.findViewById(R.id.seekBar);

        mediaManager = new MediaManager(getActivity());
        mediaManager.getAllAudioSongs();

        IS_RUNNING = true;
        if (startSeekBar.getStatus() == AsyncTask.Status.RUNNING) {
        }


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
    public void onDetach() {

        super.onDetach();
    }
}
