package com.km.ao3reader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ReadWorkActivity extends AppCompatActivity {
    public static String KEY_WORK_DIR_NAME = "com.km.ao3reader.WORK_DIR_NAME";
    private final String LOG_TAG = ReadWorkActivity.class.getSimpleName();
    private File work;
    private TextView workText;
    private int currentChapter;
    String directory;
    ScrollView scrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_work);
        workText = findViewById(R.id.work_text);
        scrollView = findViewById(R.id.scroll_view);
        Bundle extras = getIntent().getExtras();
        currentChapter = 1;
        if (extras != null){
            directory = String.format("%s/%s", getExternalFilesDir(null).getAbsolutePath(), extras.getString(KEY_WORK_DIR_NAME));
            renderChapter(currentChapter);
        }
        else{
            Toast.makeText(this, "Did not find extra", Toast.LENGTH_SHORT).show();

        }

    }
    private String getHTMLfromFile(File f){

        try {
            InputStream input =  new FileInputStream(f);
            int size = input.available();

            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            String html =  new String(buffer);
            return html;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private void renderChapter(int chapterNum){
        String filePath = String.format("%s/%s.%s", directory, String.format("Chapter %d", chapterNum), getString(R.string.work_document_type));
        work = new File(filePath);
        String html = getHTMLfromFile(work);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            workText.setText(Html.fromHtml(html, Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV));
        } else{
            workText.setText(html);
        }

    }

    public void changeChapter(View view) {
        int viewId = view.getId();
        switch (viewId){
            case R.id.forward_fab:
                currentChapter++;
                break;
            case R.id.backwards_fab:
                currentChapter--;
                break;
            default:
                Log.d(LOG_TAG, "viewID for changeChapter is invalid");
        }
        Log.d(LOG_TAG, "current Chapter:" + currentChapter);
        scrollView.fullScroll(ScrollView.FOCUS_UP);
        renderChapter(currentChapter);

    }
}