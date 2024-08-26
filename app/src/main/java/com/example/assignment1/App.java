package com.example.assignment1;

import android.app.Application;

import com.example.assignment1.room.AppDatabase;
import com.example.assignment1.utils.SharedPrefsUtil;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPrefsUtil.init(this);
        AppDatabase.init(this);
    }
}
