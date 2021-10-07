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
import de.dlyt.yanndroid.oneui.layout.SwitchBarLayout;
import de.dlyt.yanndroid.oneui.utils.ThemeUtil;
import de.dlyt.yanndroid.oneui.view.SwitchBar;

public class SwitchLayoutSetting extends AppCompatActivity {

    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new ThemeUtil(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switchbar);

        SharedPreferences sharedPreferences = getSharedPreferences("de.dlyt.yanndroid.metronome_preferences", Activity.MODE_PRIVATE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        SwitchBarLayout switchBarLayout = findViewById(R.id.switchbar_layout);
        MaterialTextView summary_text = findViewById(R.id.summary_text);
        SwitchBar main_switch = switchBarLayout.getSwitchBar();
        MaterialCardView fragment_container = findViewById(R.id.fragment_container);

        main_switch.addOnSwitchChangeListener((switchCompat, b) -> {
            if (key != null) sharedPreferences.edit().putBoolean(key, b).apply();
            fragment_container.setVisibility(b ? View.VISIBLE : View.GONE);
        });

        switch (getIntent().getStringExtra("key")) {
            case "sound":
                key = "sound";
                switchBarLayout.setToolbarTitle(getString(R.string.sound));
                summary_text.setText(R.string.sound_summary);
                main_switch.setChecked(sharedPreferences.getBoolean("sound", true));
                ft.replace(R.id.fragment, new SoundFragment());
                break;
            case "vibration":
                key = "vibration";
                switchBarLayout.setToolbarTitle(getString(R.string.vibration));
                summary_text.setText(R.string.vibration_summary);
                main_switch.setChecked(sharedPreferences.getBoolean("vibration", false));
                ft.replace(R.id.fragment, new VibrationFragment());
                break;
        }

        ft.commit();

    }
}
