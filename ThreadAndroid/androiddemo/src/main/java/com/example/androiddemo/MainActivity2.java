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

        MyAsyncTask task = new MyAsyncTask();
        task.execute(); //传入参数

        //这样写也是有内存泄漏的风险的, 但是只要任务时间不长, 完全不用做防止内存泄漏的处理
        new Thread(){
            @Override
            public void run() {
                //...
            }
        };
    }

    class User1{
        String username = name;
        int age;
    }

    //按照网上文章的说法, 所有的内部类都需要是 静态内部类 + 弱引用, 否则都会内存泄漏
    //但是 User1, User2, MyView 都不会导致内存泄漏, 只有 MyAsyncTask 才会内存泄漏
    static class User2 {
        WeakReference<MainActivity2> activityWeakReference;
        String username = activityWeakReference.get().name;
        int age;
    }

    class MyView extends View {
        //MyView 内部也可以拿到 MainActivity2 的引用, 但是为什么 MyAsyncTask 会导致内存泄漏, MyView 却不会导致内存泄漏?
        String activityName = name;

        public MyView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }
    }

    class MyAsyncTask extends AsyncTask {

        //在前台执行
        @Override
        protected void onPreExecute() {
            //因为当前的 MyAsyncTask 是可以隐式拿到外部类 MainActivity2 的引用, 所以可以调用 MainActivity2 对象的 name 属性
            System.out.println(name);
            super.onPreExecute();
        }

        //在后台执行
        @Override
        protected Object doInBackground(Object[] objects) {
            return null;
        }

        //又返回前台执行
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }
}
