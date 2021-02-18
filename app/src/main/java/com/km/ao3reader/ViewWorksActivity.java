package com.km.ao3reader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

public class ViewWorksActivity extends AppCompatActivity implements WorkListAdapter.OnWorkListener{
    public static final String KEY_WORKS_LIST= "com.km.ao3.WORKS_LIST";
    public static final String LOG_TAG = ViewWorksActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private WorkListAdapter adapter;
    private String[] works;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_works);
        initDataset();
        for (String s: works){
            Log.d(LOG_TAG, s);
        }


        //Setting up recycler view stuff
        recyclerView = findViewById(R.id.recyclerview);
        adapter = new WorkListAdapter(this, works, this);
        recyclerView.setAdapter(adapter);


    }

    private void initDataset(){
        File directory = getExternalFilesDir(null);
        Log.d("Files", "Path: " + directory.getAbsolutePath());
        File[] workDirectories = directory.listFiles(f -> f.isDirectory());
        works = new String[workDirectories.length];
        Log.d("Files", "Size: " + works.length);
        for (int i =0; i < works.length; i++){
            File f = workDirectories[i];
            String filename = f.getName();
            works[i] = filename;
        }

    }

    @Override
    public void onWorkClick(String work) {
        Intent intent = new Intent(this, ReadWorkActivity.class);
        intent.putExtra(ReadWorkActivity.KEY_WORK_DIR_NAME, work);
        startActivity(intent);

    }
}