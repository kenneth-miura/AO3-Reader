package com.km.ao3reader;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ReadWorkActivity extends AppCompatActivity {
    public static String KEY_WORK_DIR_NAME = "com.km.ao3reader.WORK_DIR_NAME";
    public static String KEY_NUM_OF_CHAPTERS = "com.km.ao3reader.NUM_OF_CHAPTERS";

    private final String LOG_TAG = ReadWorkActivity.class.getSimpleName();
    private String directory;
    private ScrollView scrollView;
    private File work;
    private TextView workText;
    private int currentChapter, numOfChapters;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_work);
        workText = findViewById(R.id.work_text);
        scrollView = findViewById(R.id.scroll_view);
        currentChapter = 1;


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            directory = String.format("%s/%s", getExternalFilesDir(null).getAbsolutePath(), extras.getString(KEY_WORK_DIR_NAME));
            numOfChapters = extras.getInt(KEY_NUM_OF_CHAPTERS);
            Log.d(LOG_TAG, "num of chapters is " + numOfChapters);
            renderChapter(currentChapter);
        } else {
            Toast.makeText(this, "Did not find extra", Toast.LENGTH_SHORT).show();

        }

    }

    private String getHTMLFromFile(File f) {

        try {
            InputStream input = new FileInputStream(f);
            int size = input.available();

            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            String html = new String(buffer);
            return html;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private void renderChapter(int chapterNum) {
        //TODO: This setBackgroundColor isn't supported I think. Figure out another option
        if (chapterNum >= numOfChapters || numOfChapters == 1) {
            // cover the case where it's a one shot with the second condition
            findViewById(R.id.forward_fab).setBackgroundColor(getResources().getColor(R.color.gray));
        }
        if (chapterNum <= 1) {
            findViewById(R.id.backwards_fab).setBackgroundColor(getResources().getColor(R.color.gray));
        }

        // It can be both cases
        String filePath = String.format("%s/%s.%s", directory, String.format("Chapter %d", chapterNum), getString(R.string.work_document_type));
        work = new File(filePath);
        String html = getHTMLFromFile(work);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            workText.setText(Html.fromHtml(html, Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV));
        } else {
            workText.setText(html);
        }

    }

    public void changeChapter(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.forward_fab:
                if (currentChapter < numOfChapters) {
                    currentChapter++;
                }
                break;
            case R.id.backwards_fab:
                if (currentChapter > 1) {
                    currentChapter--;
                }
                break;
            default:
                Log.d(LOG_TAG, "viewID for changeChapter is invalid");
        }
        Log.d(LOG_TAG, "current Chapter:" + currentChapter);
        // check if current chapter is beginning or end, grey out corresponding button
        // make sure it also grays out both if it's a oneshot
        scrollView.fullScroll(ScrollView.FOCUS_UP);
        renderChapter(currentChapter);

    }
}