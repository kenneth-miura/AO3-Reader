package com.km.ao3reader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class ReadWorkActivity extends AppCompatActivity {
    public static String KEY_PDF_PATH = "com.km.ao3reader.PDF_PATH";
    private File pdf;
    private float defaultZoom = 1.0f;

    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_work);
        pdfView = findViewById(R.id.pdfView);
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            String directory = getExternalFilesDir(null).getAbsolutePath();
            String filePath = String.format("%s/%s.pdf", directory, extras.getString(KEY_PDF_PATH));
            pdf = new File(filePath);

            Toast.makeText(this, String.format("Path:%s, exists: %b", pdf.getAbsolutePath(), pdf.exists()), Toast.LENGTH_LONG).show();
            pdfView.fromFile(pdf)
                    .swipeHorizontal(true)
                    .pageSnap(true)
                    .autoSpacing(true)
                    .pageFling(true)
                    .load();

            // TODO: Switch over to reading the html file, make sure we can resize text in that
            /*
            render the HTML in a webview
             */
            //TODO: add zooming as a + - button on top
            /*
            CSS select all text elements (may need to exclude some) and change their font-size property
            Then, re-render the HTML in a webview
             */
        }
        else{
            Toast.makeText(this, "Did not find extra", Toast.LENGTH_SHORT).show();

        }

    }


    }
}