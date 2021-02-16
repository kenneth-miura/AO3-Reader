package com.main.ao3reader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static int downloadID = 1;
    Button btnLaunch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
        Bitmap downloadIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_download_button);
        btnLaunch = (Button) findViewById(R.id.browse_button);
        btnLaunch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String url = "https://developers.google.com/web/android/custom-tabs/";
                Toast.makeText(context, "RUNNING", Toast.LENGTH_SHORT);
                String actionLabel = "Download";
                PendingIntent pendingIntent = createPendingIntent(DownloadBroadcastReceiver.ACTION_DOWNLOAD);
                CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
                intentBuilder.setActionButton(downloadIcon, actionLabel, pendingIntent);

                CustomTabsIntent customTabsIntent = intentBuilder.build();
                customTabsIntent.launchUrl(view.getContext(), Uri.parse(url));




            }
        });

        // just do this: https://developer.android.com/guide/components/activities/activity-lifecycle#alc
        // Maybe set up a getter function? That either initializes it or, if it already exists, just gets the CustomTabsIntent
    }

    private PendingIntent createPendingIntent(int actionSourceId){
        Intent actionIntent = new Intent(this.getApplicationContext(), DownloadBroadcastReceiver.class);
        return PendingIntent.getBroadcast(getApplicationContext(), actionSourceId, actionIntent, 0);
    }

}