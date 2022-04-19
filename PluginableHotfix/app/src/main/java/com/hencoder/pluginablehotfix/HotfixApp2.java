package com.hencoder.pluginablehotfix;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

public class HotfixApp2 extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.e("TAG", "attachBaseContext()");
        Toast.makeText(this, "HotfixApp2 attachBaseContext() !",Toast.LENGTH_SHORT).show();

        File apk = new File(getCacheDir() + "/hotfix.dex");

        if (apk.exists()) {
            Toast.makeText(this, "HotfixApp2 attachBaseContext() apk exists() ",Toast.LENGTH_SHORT).show();

            try {
                ClassLoader originalLoader = getClassLoader();
                DexClassLoader classLoader = new DexClassLoader(apk.getPath(), getCacheDir().getPath(), null, null);
                Class loaderClass = BaseDexClassLoader.class;
                Field pathListField = loaderClass.getDeclaredField("pathList");
                pathListField.setAccessible(true);
                Object pathListObject = pathListField.get(classLoader);

                Class pathListClass = pathListObject.getClass();
                Field dexElementsField = pathListClass.getDeclaredField("dexElements");
                dexElementsField.setAccessible(true);
                //拿到新的 dexElements 对象
                Object dexElementsObject = dexElementsField.get(pathListObject);

                Object originalPathListObject = pathListField.get(originalLoader);
                //拿到旧的 dexElements 对象
                Object originalDexElementsObject = dexElementsField.get(originalPathListObject);

                //将 originalLoader.pathList.dexElements 的元素依次往后偏移, 将第 0 个位置赋值为 newDex
                //originalLoader.pathList.dexElements[3] = originalLoader.pathList.dexElements[2];
                //originalLoader.pathList.dexElements[2] = originalLoader.pathList.dexElements[1];
                //originalLoader.pathList.dexElements[1] = originalLoader.pathList.dexElements[0];
                //originalLoader.pathList.dexElements[0] = newDex
                //因为

                int oldLength = Array.getLength(originalDexElementsObject);
                int newLength = Array.getLength(dexElementsObject);
                //创建一个新的 Array
                Object concatDexElementsObject = Array.newInstance(dexElementsObject.getClass().getComponentType(), oldLength + newLength);
                //将新的 dex 从 0 开始放
                for (int i = 0; i < newLength; i++) {
                    Array.set(concatDexElementsObject, i, Array.get(dexElementsObject, i));
                }
                //将旧的 dex 从后边开始放
                for (int i = 0; i < oldLength; i++) {
                    Array.set(concatDexElementsObject, newLength + i, Array.get(originalDexElementsObject, i));
                }
                dexElementsField.set(originalPathListObject, concatDexElementsObject);

                // originalLoader.pathList.dexElements = classLoader.pathList.dexElements;
                // originalLoader.pathList.dexElements += classLoader.pathList.dexElements;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "HotfixApp2 attachBaseContext() apk not exists() ",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "HotfixApp2 onCreate() !",Toast.LENGTH_SHORT).show();
    }
}
