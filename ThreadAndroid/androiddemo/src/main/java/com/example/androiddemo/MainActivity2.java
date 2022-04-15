package com.example.androiddemo;

import android.content.Context;
import android.os.*;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class MainActivity2 extends AppCompatActivity {
    String name = "rengwuxian";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    static class User {
        WeakReference<MainActivity2> activityWeakReference;
        String username = activityWeakReference.get().name;
        int age;
    }

    class MyView extends View {
        String activityName = name;

        public MyView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }
    }

    class MyAsyncTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            System.out.println(name);
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }

}
