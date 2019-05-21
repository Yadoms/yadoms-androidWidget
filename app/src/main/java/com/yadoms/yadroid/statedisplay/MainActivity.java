package com.yadoms.yadroid.statedisplay;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

//TODO ajouter le support du français

public class MainActivity
        extends AppCompatActivity {
    Handler checkConnectionTimerHandler = new Handler();
    Runnable checkConnectionTimerRunnable = new Runnable() {
        @Override
        public void run() {
            checkConnected();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScreenStateReceiver.start(getApplicationContext());

        ReadWidgetsStateWorker.startService();
    }

    private void checkConnected() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    YadomsRestClient client = new YadomsRestClient(getApplicationContext());
                    client.withTimeout(2000);
                    client.getLastEvent(new YadomsRestGetResponseHandler() {
                        @Override
                        void onSuccess(Object[] objects) {
                            onConnectionEvent(true);
                        }

                        @Override
                        void onFailure() {
                            onConnectionEvent(false);
                        }
                    });
                } catch (InvalidConfigurationException ignored) {
                    onConnectionEvent(false);
                }
            }
        });
    }

    public void openSettingsActivity(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void onConnectionEvent(final boolean connected) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textView = findViewById(R.id.connectionStateText);
                textView.setTextColor(connected ? getResources().getColor(R.color.colorAccent, getTheme()) : Color.RED);
                textView.setText(connected ? R.string.connection_ok : R.string.connection_failed);

                checkConnectionTimerHandler.postDelayed(checkConnectionTimerRunnable, 2000);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        MainPreferences.invalid();
        checkConnected();
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkConnectionTimerHandler.removeCallbacks(checkConnectionTimerRunnable);
    }
}