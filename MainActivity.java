package com.swetha.stopwatch;

import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private StopwatchViewModel viewModel;
    private TextView timerText;
    private Button startButton, pauseButton, resetButton, lapButton;
    private RecyclerView lapsRecyclerView;
    private LapAdapter lapAdapter;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        timerText = findViewById(R.id.timerText);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        resetButton = findViewById(R.id.resetButton);
        lapButton = findViewById(R.id.lapButton);
        lapsRecyclerView = findViewById(R.id.lapsRecyclerView);

        lapAdapter = new LapAdapter();
        lapsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        lapsRecyclerView.setAdapter(lapAdapter);

        viewModel = new ViewModelProvider(this).get(StopwatchViewModel.class);

        viewModel.getCurrentTime().observe(this, time -> {
            if (time == null) return;
            long hours = time / 3600000;
            long minutes = (time % 3600000) / 60000;
            long seconds = (time % 60000) / 1000;
            long milliseconds = (time % 1000) / 10;
            timerText.setText(String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, milliseconds));
        });

        viewModel.getIsRunning().observe(this, isRunning -> {
            if (isRunning == null) return;
            if (isRunning) {
                startButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
            } else {
                startButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.GONE);
            }
        });

        viewModel.getLaps().observe(this, laps -> {
            if (laps != null) lapAdapter.setLaps(laps);
        });

        startButton.setOnClickListener(v -> {
            viewModel.startStopwatch();
            animateButton(v);
            vibrate();
        });

        pauseButton.setOnClickListener(v -> {
            viewModel.pauseStopwatch();
            animateButton(v);
            vibrate();
        });

        resetButton.setOnClickListener(v -> {
            viewModel.resetStopwatch();
            animateButton(v);
            vibrate();
        });

        lapButton.setOnClickListener(v -> {
            viewModel.recordLap();
            animateButton(v);
            vibrate();
        });
    }

    private void animateButton(View button) {
        button.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                .withEndAction(() -> button.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                .start();
    }

    private void vibrate() {
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(50);
            }
        }
    }
}