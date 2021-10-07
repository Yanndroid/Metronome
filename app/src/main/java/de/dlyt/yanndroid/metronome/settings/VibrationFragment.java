package de.dlyt.yanndroid.metronome.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.dlyt.yanndroid.metronome.R;
import de.dlyt.yanndroid.oneui.view.SeekBar;

public class VibrationFragment extends Fragment {

    public VibrationFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vibration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = getContext();


        SeekBar first_beat_bar = view.findViewById(R.id.first_beat_bar);
        SeekBar other_beat_bar = view.findViewById(R.id.other_beat_bar);
        ImageView restore_first = view.findViewById(R.id.restore_first);
        ImageView restore_others = view.findViewById(R.id.restore_others);
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        SharedPreferences sharedPreferences = context.getSharedPreferences("de.dlyt.yanndroid.metronome_preferences", Activity.MODE_PRIVATE);


        first_beat_bar.setMax(300);
        first_beat_bar.setMin(30);

        first_beat_bar.setProgress(sharedPreferences.getInt("vib1", 150));
        first_beat_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sharedPreferences.edit().putInt("vib1", progress).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                vibrator.vibrate(VibrationEffect.createOneShot(seekBar.getProgress(), VibrationEffect.DEFAULT_AMPLITUDE));
            }
        });


        other_beat_bar.setMax(300);
        other_beat_bar.setMin(30);

        other_beat_bar.setProgress(sharedPreferences.getInt("vibN", 75));
        other_beat_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sharedPreferences.edit().putInt("vibN", progress).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                vibrator.vibrate(VibrationEffect.createOneShot(seekBar.getProgress(), VibrationEffect.DEFAULT_AMPLITUDE));
            }
        });
        
        restore_first.setOnClickListener(v -> first_beat_bar.setProgress(150));
        restore_others.setOnClickListener(v -> other_beat_bar.setProgress(75));


    }
}