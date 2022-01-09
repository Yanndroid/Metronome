package de.dlyt.yanndroid.metronome;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import de.dlyt.yanndroid.metronome.utils.Updater;
import de.dlyt.yanndroid.oneui.layout.AboutPage;
import de.dlyt.yanndroid.oneui.utils.ThemeUtil;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ThemeUtil(this);
        setContentView(R.layout.activity_about);

        checkForUpdate();
    }

    private void checkForUpdate() {
        AboutPage about_page = findViewById(R.id.about_page);
        MaterialButton about_github = findViewById(R.id.about_github);

        Updater.checkForUpdate(this, new Updater.UpdateChecker() {
            @Override
            public void updateAvailable(boolean available, String url, String versionName) {
                if (available) {
                    about_page.setUpdateState(AboutPage.UPDATE_AVAILABLE);
                    about_page.setUpdateButtonOnClickListener(v -> Updater.downloadAndInstall(getBaseContext(), url, versionName));
                } else {
                    about_page.setUpdateState(AboutPage.NO_UPDATE);
                }
            }

            @Override
            public void githubAvailable(String url) {
                about_github.setVisibility(View.VISIBLE);
                about_github.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))));
            }

            @Override
            public void noConnection() {
                about_page.setUpdateState(AboutPage.NO_CONNECTION);
                about_page.setRetryButtonOnClickListener(v -> {
                    about_page.setUpdateState(AboutPage.LOADING);
                    checkForUpdate();
                });
            }
        });
    }

}