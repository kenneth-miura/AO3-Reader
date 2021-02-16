package com.km.ao3reader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class DownloadRequestReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = DownloadRequestReceiver.class.getSimpleName();
    public static final int ACTION_DOWNLOAD_REQUEST = 1;
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

    //TODO: test cases
    /*
    Downloading from 1st chapter
    Downloading from entire work page
    Failing to download if non-ao3 page
    Failing to download if ao3 page, but non-work
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        String url = intent.getDataString();
        if (validAO3Work(url)){
            Toast.makeText(context, "Downloading: " + url, Toast.LENGTH_LONG).show();
            executor.submit(new DownloadTask(url, context));
        }
        else{
            // Need to make sure this is actually running in the case where it's not valid
            Log.d(LOG_TAG, "This is an invalid url");
            Toast.makeText(context, "Not a valid Archive of our own Work URL", Toast.LENGTH_SHORT).show();
        }
    }
    private Boolean validAO3Work(String myURL){
        String[] splitResult = myURL.split("/");
        if (splitResult.length < 6){
            // basically, if it's length is < 6, we know it's not a valid one b/c it can't have a string "chapters" in the correct place
           return false;
        }
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
                downloadPDF(dlLink, name,context);
                Log.d(LOG_TAG, "finished running DownloadTask");
                Intent finishedDownload = new Intent(context, DownloadCompletionReceiver.class);
                finishedDownload.putExtra(DownloadCompletionReceiver.KEY_DOWNLOAD_SOURCE, DownloadCompletionReceiver.SOURCE_AO3);
                finishedDownload.putExtra(DownloadCompletionReceiver.KEY_AO3_WORK_NAME, name);
                context.sendBroadcast(finishedDownload);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}