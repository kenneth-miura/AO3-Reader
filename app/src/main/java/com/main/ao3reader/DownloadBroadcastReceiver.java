package com.main.ao3reader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.HttpsURLConnection;

public class DownloadBroadcastReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = DownloadBroadcastReceiver.class.getSimpleName();
    public static final int ACTION_DOWNLOAD = 1;
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);


    @Override
    public void onReceive(Context context, Intent intent) {
        String url = intent.getDataString();
        String dlLink = "https://archiveofourown.org/downloads/7754443/Constellations.pdf?updated_at=1605412565";
        Toast.makeText(context, dlLink, Toast.LENGTH_SHORT).show();
        executor.submit(() -> downloadUrl(dlLink, context));
    }
    public void downloadUrl(String myURL, Context context){

        File path = context.getExternalFilesDir(null);
        File writeTo = new File(path, "test.pdf");
        Log.d(LOG_TAG, writeTo.getAbsolutePath());
        try(BufferedInputStream in = new BufferedInputStream(new URL(myURL).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(writeTo)){
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead= in.read(dataBuffer, 0, 1024))!= -1){
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
        catch( IOException e ){
            Log.e(LOG_TAG, e.getMessage());
        }
    }
}