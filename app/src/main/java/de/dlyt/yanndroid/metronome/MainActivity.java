package de.dlyt.yanndroid.metronome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.dlyt.yanndroid.metronome.settings.SettingsActivity;
import de.dlyt.yanndroid.metronome.utils.InputFilterMinMax;
import de.dlyt.yanndroid.metronome.utils.Updater;
import de.dlyt.yanndroid.oneui.layout.ToolbarLayout;
import de.dlyt.yanndroid.oneui.utils.ThemeUtil;
import de.dlyt.yanndroid.oneui.view.SeekBar;

public class MainActivity extends AppCompatActivity {

    public static boolean colorSettingChanged = false;

    private SharedPreferences sharedPreferences;
    private SoundPool soundPool;
    private Timer timer;
    private Vibrator vibrator;

    private ToolbarLayout toolbarLayout;
    private MaterialTextView counter;
    private ImageView play_button;
    private ImageView pause_button;
    private ImageView stop_button;
    private NumberPicker beat_counter_picker;
    private NumberPicker beat_denominator_picker;
    private EditText tempo_text;
    private SeekBar tempo_bar;

    private boolean seeking = false;
    private boolean paused = false;

    private int counterValue = 0;
    private int beat_counter;
    private int beat_denominator;
    private int tempo;

    private boolean vib;
    private int vib1;
    private int vibN;

    private boolean sound;
    private int sound1;
    private int soundN;
    private ArrayList<Integer> sounds = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ThemeUtil(this);
        setContentView(R.layout.activity_main);

        toolbarLayout = findViewById(R.id.toolbar_layout);
        toolbarLayout.setNavigationButtonTooltip(getString(R.string.action_settings));
        toolbarLayout.setNavigationButtonOnClickListener(v -> {
            if (play_button.getVisibility() == View.GONE) pauseTimer(null);
            startActivity(new Intent().setClass(getApplicationContext(), SettingsActivity.class));
        });

        counter = findViewById(R.id.counter);
        play_button = findViewById(R.id.play_button);
        pause_button = findViewById(R.id.pause_button);
        stop_button = findViewById(R.id.stop_button);
        beat_counter_picker = findViewById(R.id.beat_counter_picker);
        beat_denominator_picker = findViewById(R.id.beat_denominator_picker);
        tempo_bar = findViewById(R.id.tempo_bar);
        tempo_text = findViewById(R.id.tempo_text);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        soundPool = new SoundPool.Builder().build();
        sharedPreferences = getSharedPreferences("de.dlyt.yanndroid.metronome_preferences", Activity.MODE_PRIVATE);

        sounds.add(soundPool.load(getBaseContext(), R.raw.mode_1_first, 1));
        sounds.add(soundPool.load(getBaseContext(), R.raw.mode_2_first, 1));
        sounds.add(soundPool.load(getBaseContext(), R.raw.mode_1_others, 1));
        sounds.add(soundPool.load(getBaseContext(), R.raw.mode_2_others, 1));

        initConfigCard();

        Updater.checkForUpdate(this, new Updater.UpdateChecker() {
            @Override
            public void updateAvailable(boolean available, String url, String versionName) {
                toolbarLayout.setNavigationButtonBadge(available ? ToolbarLayout.N_BADGE : 0);
            }

            @Override
            public void githubAvailable(String url) {

            }

            @Override
            public void noConnection() {

            }
        });
    }

    public void stopTimer(View view) {
        paused = false;
        timer.cancel();
        counterValue = 0;
        counter.setText(String.valueOf(counterValue));

        play_button.setVisibility(View.VISIBLE);
        pause_button.setVisibility(View.GONE);
        stop_button.setVisibility(View.GONE);
    }

    public void pauseTimer(View view) {
        paused = true;
        timer.cancel();

        play_button.setVisibility(View.VISIBLE);
        pause_button.setVisibility(View.GONE);
        stop_button.setVisibility(View.VISIBLE);
    }

    public void resumeTimer(View view) {
        paused = false;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timerTask();
            }
        }, 0, getPeriod());

        play_button.setVisibility(View.GONE);
        pause_button.setVisibility(View.VISIBLE);
        stop_button.setVisibility(View.VISIBLE);
    }

    private void updateTimer() {
        if (play_button.getVisibility() == View.GONE) {
            timer.cancel();
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    timerTask();
                }
            }, 50, getPeriod());
        }
    }

    private void timerTask() {
        counterValue++;
        if (counterValue >= beat_counter + 1 || counterValue == 1) {
            counterValue = 1;
            if (sound) soundPool.play(sound1, 1, 1, 0, 0, 1);
            if (vib)
                vibrator.vibrate(VibrationEffect.createOneShot(vib1, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            if (sound) soundPool.play(soundN, 1, 1, 0, 0, 1);
            if (vib)
                vibrator.vibrate(VibrationEffect.createOneShot(vibN, VibrationEffect.DEFAULT_AMPLITUDE));
        }
        runOnUiThread(() -> counter.setText(String.valueOf(counterValue)));
    }

    private long getPeriod() {
        return (long) ((long) (((double) 60 / tempo) * 1000) / ((double) beat_counter / beat_denominator));
    }


    private void initConfigCard() {
        beat_counter_picker.setMinValue(1);
        beat_counter_picker.setMaxValue(32);
        beat_counter_picker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            beat_counter = newVal;
            updateTimer();
        });

        beat_denominator_picker.setMinValue(1);
        beat_denominator_picker.setMaxValue(32);
        beat_denominator_picker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            beat_denominator = newVal;
            updateTimer();
        });


        tempo_bar.setMax(300);
        tempo_bar.setMin(20);
        tempo_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (seeking) {
                    tempo = i;
                    tempo_text.setText(String.valueOf(i));
                }
                updateTimer();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seeking = true;
                tempo_text.setEnabled(false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seeking = false;
                tempo_text.setEnabled(true);
            }
        });

        tempo_text.setFilters(new InputFilter[]{new InputFilterMinMax(1, 300)});
        tempo_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() != 0) {
                    if (20 <= Integer.parseInt(s.toString())) {
                        tempo = Integer.parseInt(s.toString());
                        tempo_bar.setProgress(Integer.parseInt(s.toString()));
                    } else {
                        tempo_text.setError("min 20");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        beat_counter = sharedPreferences.getInt("beat_counter", 4);
        beat_denominator = sharedPreferences.getInt("beat_denominator", 4);
        tempo = sharedPreferences.getInt("tempo", 60);

        vib = sharedPreferences.getBoolean("vibration", false);
        vib1 = sharedPreferences.getInt("vib1", 150);
        vibN = sharedPreferences.getInt("vibN", 75);

        sound = sharedPreferences.getBoolean("sound", true);
        sound1 = sounds.get(sharedPreferences.getInt("sound_choice", 0));
        soundN = sounds.get(sharedPreferences.getInt("sound_choice", 0) + 2);

        beat_counter_picker.setValue(beat_counter);
        beat_denominator_picker.setValue(beat_denominator);
        tempo_bar.setProgress(tempo);
        tempo_text.setText(String.valueOf(tempo));


        if (colorSettingChanged) {
            colorSettingChanged = false;
            recreate();
        }

        if (counterValue != 0) {
            stop_button.setVisibility(View.VISIBLE);
            counter.setText(String.valueOf(counterValue));

            if (paused) {
                play_button.setVisibility(View.VISIBLE);
                pause_button.setVisibility(View.GONE);
            } else {
                resumeTimer(null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("counterValue", counterValue);
        outState.putBoolean("paused", paused);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        counterValue = (int) savedInstanceState.get("counterValue");
        paused = (boolean) savedInstanceState.get("paused");
    }


    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences.edit()
                .putInt("beat_counter", beat_counter)
                .putInt("beat_denominator", beat_denominator)
                .putInt("tempo", tempo)
                .apply();
    }

}