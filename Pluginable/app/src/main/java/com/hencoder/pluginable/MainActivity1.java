package com.hencoder.pluginable;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

//import com.hencoder.pluginable.utils.Utils;
//'com.hencoder.pluginable.utils.Utils' is not public in 'com.hencoder.pluginable.utils'.
// Cannot be accessed from outside package

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Utils utils = new Utils();
//        utils.shout();

        //使用反射，utils 对象用反射来实现, shout() 方法使用反射来调用
        try {
            //Class utilsClass = Utils.class;
            Class utilsClass = Class.forName("com.hencoder.pluginable.utils.Utils");

            //取第 0 个构造方法是因为构造方法只有 1 个
            Constructor utilsConstructor = utilsClass.getDeclaredConstructors()[0];

            //设置构造方法的访问性为可访问
            utilsConstructor.setAccessible(true);

            //不使用 Class 而是使用构造器来创建对象
            //Object utils2 = utilsClass.newInstance();
            Object utils2 = utilsConstructor.newInstance();

            Method shoutMethod = utilsClass.getDeclaredMethod("shout");
            shoutMethod.setAccessible(true);
            shoutMethod.invoke(utils2);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.e("TAG", "e.message = " + e.getMessage());
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