package de.dlyt.yanndroid.metronome.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.util.SeslMisc;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

import de.dlyt.yanndroid.metronome.MainActivity;
import de.dlyt.yanndroid.metronome.R;
import de.dlyt.yanndroid.oneui.layout.ToolbarLayout;
import de.dlyt.yanndroid.oneui.preference.ColorPickerPreference;
import de.dlyt.yanndroid.oneui.preference.HorizontalRadioPreference;
import de.dlyt.yanndroid.oneui.preference.Preference;
import de.dlyt.yanndroid.oneui.preference.PreferenceFragment;
import de.dlyt.yanndroid.oneui.preference.SwitchPreference;
import de.dlyt.yanndroid.oneui.preference.SwitchPreferenceScreen;
import de.dlyt.yanndroid.oneui.utils.ThemeUtil;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ThemeUtil(this);
        setContentView(R.layout.activity_settings);

        ToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);
        toolbarLayout.setNavigationButtonTooltip(getString(R.string.sesl_navigate_up));
        toolbarLayout.setNavigationButtonOnClickListener(v -> onBackPressed());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.settings, new SettingsFragment()).commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        private Context mContext;
        private SettingsActivity mActivity;

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            mContext = getContext();
            if (getActivity() instanceof SettingsActivity)
                mActivity = ((SettingsActivity) getActivity());
        }

        @Override
        public void onCreatePreferences(Bundle bundle, String str) {
            addPreferencesFromResource(R.xml.preferences);
        }

        @SuppressLint("RestrictedApi")
        @Override
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);

            int darkMode = ThemeUtil.getDarkMode(mContext);

            HorizontalRadioPreference darkModePref = (HorizontalRadioPreference) findPreference("dark_mode");
            darkModePref.setOnPreferenceChangeListener(this);
            darkModePref.setDividerEnabled(false);
            darkModePref.setTouchEffectEnabled(false);
            darkModePref.setEnabled(darkMode != ThemeUtil.DARK_MODE_AUTO);
            darkModePref.setValue(SeslMisc.isLightTheme(mContext) ? "0" : "1");

            SwitchPreference autoDarkModePref = (SwitchPreference) findPreference("dark_mode_auto");
            autoDarkModePref.setOnPreferenceChangeListener(this);
            autoDarkModePref.setChecked(darkMode == ThemeUtil.DARK_MODE_AUTO);

            ColorPickerPreference colorPickerPref = (ColorPickerPreference) findPreference("color");
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("de.dlyt.yanndroid.metronome_preferences", Context.MODE_PRIVATE);
            ArrayList<Integer> recent_colors = new Gson().fromJson(sharedPreferences.getString("recent_colors", new Gson().toJson(new int[]{getResources().getColor(R.color.primary_color, mContext.getTheme())})), new TypeToken<ArrayList<Integer>>() {
            }.getType());
            for (Integer recent_color : recent_colors) colorPickerPref.onColorChanged(recent_color);

            colorPickerPref.setOnPreferenceChangeListener((var1, var2) -> {
                Color color = Color.valueOf((Integer) var2);

                recent_colors.add((Integer) var2);
                sharedPreferences.edit().putString("recent_colors", new Gson().toJson(recent_colors)).apply();

                ThemeUtil.setColor(mActivity, color.red(), color.green(), color.blue());
                MainActivity.colorSettingChanged = true;
                return true;
            });

            checkForUpdate();
        }

        private void checkForUpdate() {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(getString(R.string.firebase_child_name));
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        HashMap<String, String> hashMap = new HashMap<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            hashMap.put(child.getKey(), child.getValue().toString());
                        }

                        if (Integer.parseInt(hashMap.get("versionCode")) > mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode) {
                            Preference about_app = findPreference("about_app");
                            about_app.setWidgetLayoutResource(R.layout.sesl_preference_badge);
                        }
                    } catch (PackageManager.NameNotFoundException ignored) {
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getView().setBackgroundColor(getResources().getColor(R.color.item_background_color, mContext.getTheme()));
        }

        @Override
        public void onStart() {
            super.onStart();
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("de.dlyt.yanndroid.metronome_preferences", Context.MODE_PRIVATE);

            ((SwitchPreferenceScreen) findPreference("vibration")).setChecked(sharedPreferences.getBoolean("vibration", false));
            ((SwitchPreferenceScreen) findPreference("sound")).setChecked(sharedPreferences.getBoolean("sound", true));
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            switch (preference.getKey()) {
                case "dark_mode":
                    String currentDarkMode = String.valueOf(ThemeUtil.getDarkMode(mContext));
                    if (currentDarkMode != newValue) {
                        ThemeUtil.setDarkMode(mActivity, ((String) newValue).equals("0") ? ThemeUtil.DARK_MODE_DISABLED : ThemeUtil.DARK_MODE_ENABLED);
                    }
                    return true;
                case "dark_mode_auto":
                    HorizontalRadioPreference darkModePref = (HorizontalRadioPreference) findPreference("dark_mode");
                    if ((boolean) newValue) {
                        darkModePref.setEnabled(false);
                        ThemeUtil.setDarkMode(mActivity, ThemeUtil.DARK_MODE_AUTO);
                    } else {
                        darkModePref.setEnabled(true);
                    }
                    return true;
            }

            return false;
        }

    }
}