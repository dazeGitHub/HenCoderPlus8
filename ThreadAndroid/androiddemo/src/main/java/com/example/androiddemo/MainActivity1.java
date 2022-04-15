package com.example.androiddemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//使用 Handler 从子线程往主线程加任务
public class MainActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //在主线程创建 Handler 那么推任务的时候就可以推到主线程
        Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                //传过来的可能是具体的 runnable 任务, 也可能是简单的消息. 其中 MessageQueue 中也有很多消息
                //msg 的属性如下 :
//                msg.arg1;
//                msg.arg2;
//                msg.what;
//                msg.obj;
                msg.getCallback(); //这个 callback 就是 handler 实际 post 的 runnable, 将 runnable 装到一个 msg 里边然后交给 handler
            }
        };

        new Thread(){
            @Override
            public void run() {
                super.run();
                //子线程做完后台任务后往主线程推结果
                //存数据库
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //打印结果可以放到前台去做
                    }
                });
            }
        }.start();
    }
}
