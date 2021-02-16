package com.km.ao3reader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DownloadCompletionReceiver extends BroadcastReceiver {
    public static final String KEY_DOWNLOAD_SOURCE = "com.km.ao3reader.DOWNLOAD_SOURCE";
    public static final String KEY_AO3_WORK_NAME = "com.km.ao3reader.AO3_WORK_NAME";
    public static final String LOG_TAG = DownloadCompletionReceiver.class.getSimpleName();
    public static final int SOURCE_AO3 = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isValidDLSource= (intent.getIntExtra(KEY_DOWNLOAD_SOURCE, -1) == SOURCE_AO3);
        String workName =  intent.getStringExtra(KEY_AO3_WORK_NAME);
        if (isValidDLSource && workName !=null){
            Toast.makeText(context, "Completed download for: " + workName, Toast.LENGTH_LONG).show();
        }
    }
}