package com.main.ao3reader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class DownloadBroadcastReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = DownloadBroadcastReceiver.class.getSimpleName();
    public static final int ACTION_DOWNLOAD = 1;
    private static String testDownloadLink = "https://archiveofourown.org/downloads/7754443/Constellations.pdf?updated_at=1605412565";
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
    //TODO: Write a download reciever that will notify us when the download is done?


    @Override
    public void onReceive(Context context, Intent intent) {
        String url = intent.getDataString();
        if (validAO3Work(url)){
            Toast.makeText(context, "Downloading: " + url, Toast.LENGTH_LONG).show();
            executor.submit(new DownloadTask(url, context));
        }
        else{
            // Need to make sure this is actually running in the case where it's not valid
            Toast.makeText(context, "Not a valid Archive of our own Work URL", Toast.LENGTH_SHORT);
        }
    }
    private Boolean validAO3Work(String myURL){
        String[] splitResult = myURL.split("/");
        //Checking that it's under the archiveofourown domain
        Boolean isAO3Link =  splitResult[2].equals("archiveofourown.org");
        //Checking that it is a valid work
        Boolean isWork = (splitResult[3].equals("works") && splitResult[5].equals("chapters"));
        return isAO3Link && isWork;
    }
    private String makeDownloadLink(String myURL) throws IOException {
        Document document = Jsoup.connect(myURL).get();
        Element link = document.select("a[href*=pdf]").first();
        String relativeLink = link.attr("href");
        String dlLink = "https://archiveofourown.org" + relativeLink;
        return dlLink;
    }
    private String getWorkName(String myURL) throws IOException {
        Document document = Jsoup.connect(myURL).get();
        Element link = document.select(".title.heading").first();
        return link.text();

    }
    private void downloadPDF(String myURL, String workName, Context context){

        File path = context.getExternalFilesDir(null);
        File writeTo = new File(path, workName +".pdf");
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
    private class DownloadTask implements Runnable {
        private String url;
        private Context context;

        public DownloadTask(String url,Context context){
            this.url = url;
            this.context = context;
        }
        @Override
        public void run() {
            try {
                String dlLink = makeDownloadLink(url);
                //Have to parse out the name because the download link's work name gets shortened if the name is too long
                String name = getWorkName(url);
                Log.d(LOG_TAG, "dlLink:" + dlLink);
                downloadPDF(dlLink, name,context);
                Log.d(LOG_TAG, "finished running DownloadTask");
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}