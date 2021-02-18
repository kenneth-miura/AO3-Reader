package com.km.ao3reader;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class ViewWorksActivity extends AppCompatActivity implements WorkListAdapter.OnWorkListener {
    public static final String LOG_TAG = ViewWorksActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private WorkListAdapter adapter;
    private Work[] works;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_works);
        initDataset();


        //Setting up recycler view stuff
        recyclerView = findViewById(R.id.recyclerview);
        adapter = new WorkListAdapter(this, works, this);
        recyclerView.setAdapter(adapter);


    }

    private void initDataset() {
        File directory = getExternalFilesDir(null);
        Log.d("Files", "Path: " + directory.getAbsolutePath());
        File[] workDirectories = directory.listFiles(f -> f.isDirectory());
        works = new Work[workDirectories.length];
        Log.d("Files", "Size: " + works.length);
        for (int i = 0; i < works.length; i++) {
            File f = workDirectories[i];
            String filename = f.getName();
            int numOfChapters = f.listFiles().length;
            works[i] = new Work(filename, numOfChapters);
        }

    }

    @Override
    public void onWorkClick(Work work) {
        Intent intent = new Intent(this, ReadWorkActivity.class);
        intent.putExtra(ReadWorkActivity.KEY_WORK_DIR_NAME, work.getWorkName());
        intent.putExtra(ReadWorkActivity.KEY_NUM_OF_CHAPTERS, work.getNumOfChapters());
        startActivity(intent);

    }
}