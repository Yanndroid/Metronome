package de.dlyt.yanndroid.metronome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;

import androidx.appcompat.app.AppCompatActivity;

import de.dlyt.yanndroid.oneui.layout.SplashView;
import de.dlyt.yanndroid.oneui.utils.ThemeUtil;

public class SplashActivity extends AppCompatActivity {

    private boolean launchCanceled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new ThemeUtil(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SplashView splashView = findViewById(R.id.splash);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(splashView::startSplashAnimation, 500);

        splashView.setSplashAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!launchCanceled) launchApp();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void launchApp() {
        startActivity(new Intent().setClass(getApplicationContext(), MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        launchCanceled = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (launchCanceled) launchApp();
    }
}