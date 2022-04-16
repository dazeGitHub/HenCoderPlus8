package com.example.androiddemo;

import android.os.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//创建 Handler 的多种方式
public class MainActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Looper.myLooper();
        Looper.getMainLooper();

        // 方式一 : 直接使用当前主线程的 Looper
        Handler handler = new Handler(Looper.getMainLooper());

        // 方式二 : 创建一个 HandlerThread, 然后使用 HandlerThread 对象的 Looper
        HandlerThread handlerThread = new HandlerThread("second");
        handlerThread.start();

        Handler handlerSecond = new Handler(handlerThread.getLooper());
        handlerSecond.post(new Runnable() {
            @Override
            public void run() {
                System.out.println("hehehe");
            }
        });
    }
}
