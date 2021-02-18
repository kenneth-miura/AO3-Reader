package com.km.ao3reader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ReadWorkActivity extends AppCompatActivity {
    public static String KEY_PDF_PATH = "com.km.ao3reader.PDF_PATH";
    private File work;
    private TextView workText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_work);
        Bundle extras = getIntent().getExtras();
        workText = findViewById(R.id.work_text);
        if (extras != null){
            String directory = getExternalFilesDir(null).getAbsolutePath();
            String filePath = String.format("%s/%s.%s", directory, extras.getString(KEY_PDF_PATH), getString(R.string.work_document_type));
            work = new File(filePath);
            String html = getHTMLfromFile(work);
            Toast.makeText(this, String.format("Path:%s, exists: %b", work.getAbsolutePath(), work.exists()), Toast.LENGTH_LONG).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                workText.setText(Html.fromHtml(html, Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV));
            } else{
                workText.setText(html);
            }
            //TODO: create a separate class (maybe a view) for showing a chapter by itself


            // TODO: Switch over to reading the html file, make sure we can resize text in that
            /*
            render the HTML in a webview
             */
            //TODO: add zooming as a + - button on top
            /*
            CSS select all text elements (may need to exclude some) and change their font-size property
            Then, re-render the HTML in a webview
             */
            //each div with class = "userstuff" contains the chapter.
            // maybe parse html so we get headings & text for each chapter
            // implement a next chapter, last chapter (floating?) action button
            // implement changing font size, keeping track of which chapter you are on
            // experiment with having the view with text "float" (Think card view)
            // keep track of progress for each work
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


}