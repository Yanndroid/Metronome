package de.dlyt.yanndroid.metronome.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import de.dlyt.yanndroid.metronome.R;
import de.dlyt.yanndroid.oneui.SwitchBar;
import de.dlyt.yanndroid.oneui.ThemeColor;
import de.dlyt.yanndroid.oneui.layout.ToolbarLayout;

public class SubSettings extends AppCompatActivity {

    private String spKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ThemeColor(this);
        setContentView(R.layout.sub_settings);

        SharedPreferences sharedPreferences = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);
        MaterialTextView summary_text = findViewById(R.id.summary_text);
        SwitchBar main_switch = findViewById(R.id.main_switch);
        MaterialCardView fragment_container = findViewById(R.id.fragment_container);

        toolbarLayout.setNavigationOnClickListener(v -> onBackPressed());

        main_switch.addOnSwitchChangeListener((switchCompat, b) -> {
            if (spKey != null) sharedPreferences.edit().putBoolean(spKey, b).apply();
            fragment_container.setVisibility(b ? View.VISIBLE : View.GONE);
        });


        switch (getIntent().getStringExtra("setting")) {
            case "sound":
                spKey = "sound_switch";
                toolbarLayout.setTitle(getString(R.string.sound));
                summary_text.setText(R.string.sound_summary);
                main_switch.setChecked(sharedPreferences.getBoolean("sound_switch", true));
                ft.replace(R.id.fragment, new SoundFragment());
                break;
            case "vibration":
                spKey = "vib_switch";
                toolbarLayout.setTitle(getString(R.string.vibration));
                summary_text.setText(R.string.vibration_summary);
                main_switch.setChecked(sharedPreferences.getBoolean("vib_switch", false));
                ft.replace(R.id.fragment, new VibrationFragment());
                break;
        }

        ft.commit();

    }

}