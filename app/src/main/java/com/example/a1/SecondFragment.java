package com.example.a1;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SecondFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondFragment extends Fragment {

    ImageView playBtn, pauseBtn, stopBtn, timeLapseBtn;
    TextView timeView;
    TextView timeViewms;
    TextView timeLapse;

    int hours, minutes, secs, ms;
    private int seconds = 0;
    private boolean running;
    int lapCount=0;

    int ihours = 0;
    int iminutes = 0;
    float isecms = 0;
    String[] ims;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SecondFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SecondFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondFragment newInstance(String param1, String param2) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_second, container, false);
        playBtn = (ImageView) view.findViewById(R.id.playBtn);
        pauseBtn = (ImageView) view.findViewById(R.id.pauseBtn);
        stopBtn = (ImageView) view.findViewById(R.id.stopBtn);

        timeLapseBtn = (ImageView) view.findViewById(R.id.timeLapseBtn);
        timeView = (TextView) view.findViewById(R.id.time_view);
        timeViewms = (TextView) view.findViewById(R.id.time_view_ms);
        timeLapse = (TextView) view.findViewById(R.id.timeLapse);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Started", Toast.LENGTH_SHORT).show();

                playBtn.setVisibility(View.GONE);
                stopBtn.setVisibility(View.GONE);

                pauseBtn.setVisibility(View.VISIBLE);
                timeLapseBtn.setVisibility(View.VISIBLE);

                running = true;
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(), "Paused", Toast.LENGTH_SHORT).show();

                playBtn.setVisibility(View.VISIBLE);
                stopBtn.setVisibility(View.VISIBLE);
                timeLapseBtn.setVisibility(View.GONE);
                pauseBtn.setVisibility(View.GONE);

                running = false;
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(), "Stopped", Toast.LENGTH_SHORT).show();

                running = false;
                seconds = 0;
                lapCount = 0;

                timeView.setText("00:00:00");
                timeViewms.setText("00");
                timeLapse.setText("");

                playBtn.setVisibility(View.VISIBLE);

                pauseBtn.setVisibility(View.GONE);
                stopBtn.setVisibility(View.GONE);
                timeLapseBtn.setVisibility(View.GONE);
            }
        });

        timeLapseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeLapseFun();
            }
        });

        runTimer();
        return view;
    }
    private void runTimer(){

        final Handler handlertime = new Handler();
        final Handler handlerMs = new Handler();

        handlertime.post(new Runnable() {
            @Override

            public void run() {
                hours = seconds / 3600;
                minutes = (seconds % 3600) / 60;
                secs = seconds % 60;

                if (running) {
                    String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs);
                    timeView.setText(time);
                    seconds++;
                }

                handlertime.postDelayed(this, 1000);
            }
        });

        handlerMs.post(new Runnable() {
            @Override
            public void run() {

                if (ms >= 99) {
                    ms = 0;
                }

                if (running) {
                    String msString = String.format(Locale.getDefault(), "%02d", ms);
                    timeViewms.setText(msString);
                    ms++;
                }
                handlerMs.postDelayed(this, 1);
            }
        });
    }
    void timeLapseFun() {

        lapCount++;

        String laptext = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs);
        String msString = String.format(Locale.getDefault(), "%02d", ms);

        laptext = laptext + ":" + msString;

        if (lapCount > 0) {
            laptext = " Lap " + lapCount + " ------------->       "+ laptext + " \n " + timeLapse.getText();
        }

        Toast.makeText(getContext(), "Lap " + lapCount, Toast.LENGTH_SHORT).show();
        timeLapse.setText(laptext);
    }
}