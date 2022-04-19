package com.hencoder.pluginablehotfix;

import android.app.Application;
import android.content.Context;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

public class HotfixApp1 extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        //从缓存中找到 apk 包
        File apk = new File(getCacheDir() + "/hotfix.dex");

        if (apk.exists()) {
            try {
                ClassLoader originalLoader = getClassLoader();
                DexClassLoader classLoader = new DexClassLoader(apk.getPath(), getCacheDir().getPath(), null, null);

                //使用反射拿到 classLoader 里的 pathList, 而 pathList 是 ClassLoader 的父类 BaseDexClassLoader 的属性
                Class loaderClass = BaseDexClassLoader.class;
                Field pathListField = loaderClass.getDeclaredField("pathList");
                pathListField.setAccessible(true);
                Object pathListObject = pathListField.get(classLoader);

                //拿到 pathList 里边的 dexElements
                //但是 DexPathList 是 @hide 注释标注的, 所以无法直接使用, 那么可以使用 pathListObject.getClass() 来获得
                Class pathListClass = pathListObject.getClass();
                Field dexElementsField = pathListClass.getDeclaredField("dexElements");
                dexElementsField.setAccessible(true);
                Object dexElementsObject = dexElementsField.get(pathListObject);

                //同样使用反射拿到 originalLoader 里的 pathList
                Object originalPathListObject = pathListField.get(originalLoader);
                //使用反射这样赋值
                dexElementsField.set(originalPathListObject, dexElementsObject);

                //上边的反射代码指示为了实现如下的结果 :
                //originalClassLoader.pathList.dexElements = newClassLoader.pathList.dexElements;

            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}