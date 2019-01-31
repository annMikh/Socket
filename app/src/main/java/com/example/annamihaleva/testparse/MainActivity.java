package com.example.annamihaleva.testparse;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.concurrent.atomic.AtomicReference;


public class MainActivity extends AppCompatActivity {

    static RecyclerView news;
    public static AdapterNews adapter = null;
    public static boolean loading = true;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        news = findViewById(R.id.recycler_news);

        AtomicReference<LinearLayoutManager> layoutManager =
                new AtomicReference<>(new LinearLayoutManager(MainActivity.this));
        news.setLayoutManager(layoutManager.get());

        Parsing task = new Parsing(MainActivity.this, news);
        task.execute();
    }

}

