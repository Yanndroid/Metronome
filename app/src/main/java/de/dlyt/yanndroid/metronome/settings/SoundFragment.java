package de.dlyt.yanndroid.metronome.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import de.dlyt.yanndroid.metronome.R;

public class SoundFragment extends Fragment {

    public SoundFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sound, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = getContext();

        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Activity.MODE_PRIVATE);
        Spinner spinner = view.findViewById(R.id.sound_mode_spinner);
        ImageView test_sound = view.findViewById(R.id.test_sound);


        List<String> categories = new ArrayList<String>();
        for (int i = 1; i < 3; i++) categories.add(getString(R.string.sound_mode) + " " + i);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, categories);

        spinner.setAdapter(dataAdapter);
        spinner.setSelection(sharedPreferences.getInt("sound_choice", 0));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sharedPreferences.edit().putInt("sound_choice", position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SoundPool soundPool = new SoundPool.Builder().build();
        ArrayList<Integer> sounds = new ArrayList<>();
        sounds.add(soundPool.load(context, R.raw.mode_1_first, 1));
        sounds.add(soundPool.load(context, R.raw.mode_2_first, 1));
        sounds.add(soundPool.load(context, R.raw.mode_1_others, 1));
        sounds.add(soundPool.load(context, R.raw.mode_2_others, 1));

        test_sound.setOnClickListener(v -> {
            soundPool.play(sounds.get(sharedPreferences.getInt("sound_choice", 0)), 1, 1, 0, 0, 1);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    soundPool.play(sounds.get(sharedPreferences.getInt("sound_choice", 0) + 2), 1, 1, 0, 0, 1);
                }
            }, 500);
        });
    }
}