package com.km.ao3reader;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG=MainActivity.class.getSimpleName();

    public static int READFILE_RESULT_CODE=9;
    private Button browseBtn;
    private Button readFilesBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
        Bitmap downloadIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_download_button);
        readFilesBtn = (Button) findViewById(R.id.read_files_button);
        browseBtn = (Button) findViewById(R.id.browse_button);
        browseBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String url = "https://archiveofourown.org/works/7754443/chapters/17682043";
                String actionLabel = "Download";
                PendingIntent pendingIntent = createPendingIntent(DownloadRequestReceiver.ACTION_DOWNLOAD_REQUEST);
                CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
                intentBuilder.setActionButton(downloadIcon, actionLabel, pendingIntent);
                CustomTabColorSchemeParams params = new CustomTabColorSchemeParams.Builder()
                        .setToolbarColor(ContextCompat.getColor(context, R.color.crimson_dark))
                        .setNavigationBarColor(ContextCompat.getColor(context, R.color.crimson_dark))
                        .build();
                intentBuilder.setColorScheme(CustomTabsIntent.COLOR_SCHEME_LIGHT);
                intentBuilder.setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_LIGHT, params);

                CustomTabsIntent customTabsIntent = intentBuilder.build();
                customTabsIntent.launchUrl(view.getContext(), Uri.parse(url));
            }
        });

    }
    public void browseWorks(View view) {
        Intent viewWorks = new Intent(getApplicationContext(), ViewWorksActivity.class);
        startActivity(viewWorks);

    }

    private PendingIntent createPendingIntent(int actionSourceId){
        Intent actionIntent = new Intent(this.getApplicationContext(), DownloadRequestReceiver.class);
        return PendingIntent.getBroadcast(getApplicationContext(), actionSourceId, actionIntent, 0);
    }

}