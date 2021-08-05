package de.dlyt.yanndroid.metronome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.dlyt.yanndroid.oneui.ThemeColor;
import de.dlyt.yanndroid.oneui.layout.ToolbarLayout;

public class MainActivity extends AppCompatActivity {

    public static boolean colorSettingChanged = false;

    private SharedPreferences sharedPreferences;

    private SoundPool soundPool;
    private Timer timer;
    private Vibrator vibrator;

    private ArrayList<Integer> sounds = new ArrayList<>();

    private MaterialCheckBox play_pause;
    private MaterialCheckBox stop_config;
    private MaterialTextView counter;

    private int counterValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ThemeColor(this);
        setContentView(R.layout.activity_main);

        ToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbarLayout.getToolbar());

        play_pause = findViewById(R.id.play_pause);
        stop_config = findViewById(R.id.stop_config);
        counter = findViewById(R.id.counter);


        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0); //soundPool = new SoundPool.Builder().build();
        timer = new Timer();
        sharedPreferences = getSharedPreferences("metronome", Activity.MODE_PRIVATE);


        sounds.add(soundPool.load(getBaseContext(), R.raw.mode_1_first, 1));
        sounds.add(soundPool.load(getBaseContext(), R.raw.mode_1_others, 1));
        sounds.add(soundPool.load(getBaseContext(), R.raw.mode_2_first, 1));
        sounds.add(soundPool.load(getBaseContext(), R.raw.mode_2_others, 1));


        stop_config.setOnClickListener(v -> {
            if (stop_config.isChecked()) { //settings click action
                stop_config.setChecked(false);

                /*SettingsDialog bottomSheetDialog = SettingsDialog.newInstance();
                bottomSheetDialog.show(getSupportFragmentManager(), "tag");*/


            } else { //stop click action
                play_pause.setChecked(false);
                timer.cancel();
                counterValue = 0;
                counter.setText(String.valueOf(counterValue));
            }
        });


        play_pause.setOnClickListener(v -> {
            if (play_pause.isChecked()) { //play click action

                stop_config.setChecked(true);
                timer = new Timer();

                int beat_counter = sharedPreferences.getInt("beat_counter", 4);
                int beat_denominator = sharedPreferences.getInt("beat_denominator", 4);
                int tempo = sharedPreferences.getInt("tempo", 60);

                boolean vib = sharedPreferences.getBoolean("vib_switch", false);
                int vib1 = sharedPreferences.getInt("vib1", 150);
                int vibN = sharedPreferences.getInt("vibN", 75);

                boolean sound = sharedPreferences.getBoolean("sound_switch", false);
                int sound1 = sounds.get(sharedPreferences.getInt("sound_choice", 0));
                int soundN = sounds.get(sharedPreferences.getInt("sound_choice", 0) + 1);


                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> {

                            counterValue++;
                            if (counterValue == beat_counter + 1) counterValue = 1;
                            counter.setText(String.valueOf(counterValue));

                            if (counterValue == 1) {
                                if (sound) soundPool.play(sound1, 1, 1, 0, 0, 1);
                                if (vib)
                                    vibrator.vibrate(VibrationEffect.createOneShot(vib1, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                if (sound) soundPool.play(soundN, 1, 1, 0, 0, 1);
                                if (vib)
                                    vibrator.vibrate(VibrationEffect.createOneShot(vibN, VibrationEffect.DEFAULT_AMPLITUDE));
                            }


                        });
                    }
                }, 0, (long) ((long) (((double) 60 / tempo) * 1000) / ((double) beat_counter / beat_denominator)));


            } else { //pause click action
                timer.cancel();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent().setClass(getApplicationContext(), SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (colorSettingChanged) {
            colorSettingChanged = false;
            recreate();
        }
    }
}