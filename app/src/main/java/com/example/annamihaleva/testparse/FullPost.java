package com.example.annamihaleva.testparse;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

public class FullPost extends AppCompatActivity {

    static View view = null;

    public static void setView(View v){
        view = v;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_post);

        if (view != null) {
            ((ViewGroup) view.getParent()).removeView(view);
            setContentView(view);
        }
    }
}
