package com.hencoder.pluginable;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DexClassLoader 无法直接从 asset 目录里读取, 所以要将 assets 目录里的 apk 复制到缓存里
        File outputApk = new File(getCacheDir() + "/plugin.apk");

        //将需要关闭的代码放到 try() 里边, 那么就可以自动关闭
        try (Source source = Okio.source(getAssets().open("apk/plugin.apk"));
             BufferedSink sink = Okio.buffer(Okio.sink(outputApk))) { //sink : 下沉,淹没
            sink.writeAll(source);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //public DexClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) { }
        //dexPath 也可以是 apk 的 path, 因为 apk 里包含了 dex 文件
        //optimizedDirectory : 优化的目录, 即 odex 的目录, 随便指定一个即可
        //librarySearchPath  : 不需要提供
        //parent : 父 ClassLoader, 靠 DexClassLoader 自己加载文件即可, 不需要父 ClassLoader
        DexClassLoader classLoader = new DexClassLoader(
                outputApk.getPath(),
                getCacheDir().getPath(),
                null,
                null
        );
        try {
            //这里不再用 Class.forName(), 而是使用 lassLoader.loadClass() 来生成 Class 对象
            //这里的包名 om.hencoder.plugin 需要和插件化模块的包名相同
            Class utilsClass = classLoader.loadClass("com.hencoder.plugin.Utils");
            Constructor utilsConstructor = utilsClass.getDeclaredConstructors()[0];
            utilsConstructor.setAccessible(true);
            Object utils = utilsConstructor.newInstance();
            Method shoutMethod = utilsClass.getDeclaredMethod("shout");
            shoutMethod.setAccessible(true);
            shoutMethod.invoke(utils);

            Intent intent = new Intent();
            intent.setClassName("com.hencoder.plugin", "com.hencoder.plugin.MainActivity");
            startActivity(intent);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}