package com.km.ao3reader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class DownloadRequestReceiver extends BroadcastReceiver {
    public static final int ACTION_DOWNLOAD_REQUEST = 1;
    private static final String LOG_TAG = DownloadRequestReceiver.class.getSimpleName();
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

    //TODO: test cases
    /*
    Downloading from 1st chapter
    Downloading from entire work page
    Failing to download if non-ao3 page
    Failing to download if ao3 page, but non-work
     */
    //TODO: Add progress bar for downloads in a notification

    @Override
    public void onReceive(Context context, Intent intent) {
        String url = intent.getDataString();
        if (isValidAO3OneChapter(url)) {
            Toast.makeText(context, "Downloading oneshot: " + url, Toast.LENGTH_LONG).show();
            executor.submit(new DownloadTask(url, context, true));

        } else if (isValidAO3Work(url)) {
            Toast.makeText(context, "Downloading: " + url, Toast.LENGTH_LONG).show();
            executor.submit(new DownloadTask(url, context, false));
        } else {
            // Need to make sure this is actually running in the case where it's not valid
            Toast.makeText(context, "The URL:" + url + "is not a valid Archive of our own Work URL", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidAO3Work(String myURL) {
        String[] splitResult = myURL.split("/");
        if (splitResult.length < 5) {
            // basically, if it's length is < 5, we know it's not a valid one b/c it can't have the work id
            return false;
        }
        //Checking that it's under the archiveofourown domain
        Boolean isAO3Link = splitResult[2].equals("archiveofourown.org");
        //Checking that it is a valid work
        Boolean isWork = (splitResult[3].equals("works"));
        return isAO3Link && isWork;
    }

    private boolean isValidAO3OneChapter(String url) {
        return isValidAO3Work(url) && !url.contains("chapters");


    }

    private String makeDownloadLink(String myURL, Context context) throws IOException {
        Document document = Jsoup.connect(myURL).get();
        Element link = document.select(String.format("a[href*=.%s]", context.getResources().getString(R.string.work_document_type))).first();
        String relativeLink = link.attr("href");
        String dlLink = "https://archiveofourown.org" + relativeLink;
        return dlLink;
    }

    private String getWorkName(String myURL) throws IOException {
        Document document = Jsoup.connect(myURL).get();
        Element link = document.select(".title.heading").first();
        return link.text();

    }

    private void downloadAsChapters(String dlURL, File writeDirectory, String workDocumentType) throws IOException {
        Document document = Jsoup.connect(dlURL).get();
        Elements chapterTexts = document.select("div.userstuff:not(#chapters)");
        Elements chapterHeadings = document.select("div > h2.heading");
        Log.i("CHAPTER DOWNLOADS", String.format("num of texts: %d, num of headings: %d", chapterTexts.size(), chapterHeadings.size()));
        if (chapterHeadings.size() == chapterTexts.size()) {
            for (int i = 0; i < chapterTexts.size(); i++) {
                File writeTo = new File(String.format("%s/Chapter %d.%s", writeDirectory.getAbsolutePath(), i + 1, workDocumentType));
                BufferedWriter writer = new BufferedWriter(new FileWriter(writeTo));
                writer.write(chapterHeadings.get(i).html());
                writer.write(chapterTexts.get(i).html());
                writer.close();
            }
        } else {
            Log.i("CHAPTER_DOWNLOADS", "sizes don't match up");
        }
    }

    private void downloadOneChapterWork(String dlURL, File writeDirectory, String workDocumentType) throws IOException {
        Document document = Jsoup.connect(dlURL).get();
        Element chapter = document.select("div.userstuff").first();
        File writeTo = new File(String.format("%s/Chapter 1.%s", writeDirectory.getAbsolutePath(), workDocumentType));
        BufferedWriter writer = new BufferedWriter(new FileWriter(writeTo));
        writer.write(chapter.html());
        writer.close();


    }

    private void downloadWork(String myURL, String workName, Context context, boolean isOneChapter) {
        //TODO: put metadata (author, tags, rating, etc) in metadata in dir? if that's possible. If not, put it in a text file in the directory
        File path = context.getExternalFilesDir(null);
        File workDirectory = new File(path.getAbsolutePath() + '/' + workName);
        if (!workDirectory.exists() && !workDirectory.isDirectory()) {
            if (workDirectory.mkdir()) {
                Log.i("CreateDir", "App dir created");
            } else {
                Log.w("CreateDir", "Unable to create app dir");
            }
        } else {
            Log.i("CreateDir", "App dir already exists");
        }

        try {
            if (isOneChapter) {
                downloadOneChapterWork(myURL, workDirectory, context.getResources().getString(R.string.work_document_type));
            } else {
                downloadAsChapters(myURL, workDirectory, context.getResources().getString(R.string.work_document_type));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class DownloadTask implements Runnable {
        private String url;
        private Context context;
        private boolean isOneChapter;

        public DownloadTask(String url, Context context, boolean isOneChapter) {
            this.url = url;
            this.context = context;
            this.isOneChapter = isOneChapter;
        }

        @Override
        public void run() {
            try {
                String dlLink = makeDownloadLink(url, context);
                //Have to parse out the name because the download link's work name gets shortened if the name is too long
                Log.d(LOG_TAG, "dlLink: " + dlLink);
                String name = getWorkName(url);
                downloadWork(dlLink, name, context, isOneChapter);
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