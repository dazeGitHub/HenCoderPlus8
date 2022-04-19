package com.hencoder.pluginablehotfix;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class MainActivity extends AppCompatActivity {
    TextView titleTv;
    Button loadPluginBtn;
    Button showTitleBt;
    Button hotfixBt;
    Button removeHotfixBt;
    Button killSelfBt;

    File apk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleTv = findViewById(R.id.titleTv);
        loadPluginBtn = findViewById(R.id.loadPluginBt);
        showTitleBt = findViewById(R.id.showTitleBt);
        hotfixBt = findViewById(R.id.hotfixBt);
        removeHotfixBt = findViewById(R.id.removeHotfixBt);
        killSelfBt = findViewById(R.id.killSelfBt);

        //把 apk 抽成全局变量是为了能移除热更新的缓存里的 dex 包
        apk = new File(getCacheDir() + "/hotfix.dex");

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                switch (v.getId()) {
                    case R.id.loadPluginBt:
                        //这里是手动点击按钮执行插件化操作, 也可以后台自动下载插件 apk,
                        //然后自动扫描是否有下载好的 apk, 将它们复制到缓存目录
                        //每次打开界面都尝试加载插件, 如果加载不到, 那么就使用默认内容
                        File pluginApk = new File(getCacheDir() + "/plugin.apk");
                        if (!pluginApk.exists()) {
                            try (Source source = Okio.source(getAssets().open("apk/plugin.apk"));
                                 BufferedSink sink = Okio.buffer(Okio.sink(pluginApk))) {
                                sink.writeAll(source);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (pluginApk.exists()) {
                            try {
                                DexClassLoader classLoader = new DexClassLoader(pluginApk.getPath(), getCacheDir().getPath(), null, null);
                                Class pluginUtilsClass = classLoader.loadClass("com.hencoder.plugin.Utils");
                                Constructor utilsConstructor = pluginUtilsClass.getDeclaredConstructors()[0];
                                Object utils = utilsConstructor.newInstance();
                                Method shoutMethod = pluginUtilsClass.getDeclaredMethod("shout");
                                shoutMethod.invoke(utils);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case R.id.showTitleBt:
                        Title title = new Title();
                        titleTv.setText(title.getTitle());
                        break;
                    case R.id.hotfixBt:
                        //在这里改变 case R.id.showTitleBt: 代码块中的行为, 即修改 title.getTitle() 中的值
                        //从而实现在一个地方热修复可以改全局任何一个地方的代码
                        //需要改变这个方法的行为 getClassLoader().loadClass() , 从而改变加载类的过程,
                        //使其加载的是热更新的类，而不是原来的类

                        //先打印一下是哪个 ClassLoader
                        System.out.println("Class Loader : " + getClassLoader().getClass().getName());
                        // I/System.out: Class Loader : dalvik.system.PathClassLoader

                        //将 assets/apk 目录中的 hotfix.apk 复制到缓存目录中
//                        File LocalHotFixApk = new File(getCacheDir() + "/hotfix.dex");//hotfix.apk
//                        if (!LocalHotFixApk.exists()) {
//                            try (Source source = Okio.source(getAssets().open("apk/hotfix.dex"));
//                                 BufferedSink sink = Okio.buffer(Okio.sink(LocalHotFixApk))) {
//                                sink.writeAll(source);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }

//                        try {
//                            ClassLoader originalLoader = getClassLoader();
//                            DexClassLoader classLoader = new DexClassLoader(LocalHotFixApk.getPath(), getCacheDir().getPath(), null, null);
//
//                            //使用反射拿到 classLoader 里的 pathList, 而 pathList 是 ClassLoader 的父类 BaseDexClassLoader 的属性
//                            Class loaderClass = BaseDexClassLoader.class;
//                            Field pathListField = loaderClass.getDeclaredField("pathList");
//                            pathListField.setAccessible(true);
//                            Object pathListObject = pathListField.get(classLoader);
//
//                            //拿到 pathList 里边的 dexElements
//                            //但是 DexPathList 是 @hide 注释标注的, 所以无法直接使用, 那么可以使用 pathListObject.getClass() 来获得
//                            Class pathListClass = pathListObject.getClass();
//                            Field dexElementsField = pathListClass.getDeclaredField("dexElements");
//                            dexElementsField.setAccessible(true);
//                            Object dexElementsObject = dexElementsField.get(pathListObject);
//
//                            //同样使用反射拿到 originalLoader 里的 pathList
//                            Object originalPathListObject = pathListField.get(originalLoader);
//                            //使用反射这样赋值
//                            dexElementsField.set(originalPathListObject, dexElementsObject);

                            //上边的反射代码指示为了实现如下的结果 :
                            //originalLoader.pathList.dexElements = classLoader.pathList.dexElements;
//                        } catch (NoSuchFieldException | IllegalAccessException e) {
//                            e.printStackTrace();
//                        }

                        OkHttpClient client = new OkHttpClient();
                        final Request request = new Request.Builder()
                                .url("https://api.hencoder.com/patch/upload/hotfix.dex")
                                .build();
                        client.newCall(request)
                                .enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                        v.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(MainActivity.this, "出错了", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                                        try (BufferedSink sink = Okio.buffer(Okio.sink(apk))) {
                                            sink.write(response.body().bytes());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        v.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(MainActivity.this, "补丁加载成功", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                        break;
                    case R.id.removeHotfixBt:
                        if (apk.exists()) {
                            apk.delete();
                        }
                        break;
                    case R.id.killSelfBt:
                        android.os.Process.killProcess(android.os.Process.myPid());
                        break;
                }
            }
        };

        showTitleBt.setOnClickListener(onClickListener);
        loadPluginBtn.setOnClickListener(onClickListener);
        hotfixBt.setOnClickListener(onClickListener);
        removeHotfixBt.setOnClickListener(onClickListener);
        killSelfBt.setOnClickListener(onClickListener);
    }
}
