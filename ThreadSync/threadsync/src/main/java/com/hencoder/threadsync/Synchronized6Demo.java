package com.hencoder.threadsync;

/**
 * 静态方法使用静态变量做为锁
 */
public class Synchronized6Demo implements TestDemo {

    private static int x = 0;
    private static int y = 0;
    private static String name;
    private final static Object monitor1 = new Object();
    private final Object monitor2 = new Object();

    private static void count(int newValue) {
        synchronized (monitor1) {
            x = newValue;
            y = newValue;
        }
    }

    private void minus(int delta) {
        synchronized (monitor1) {
            x -= delta;
            y -= delta;
        }
    }

    private synchronized static void setName(String newName) {
        name = newName;
    }

    //这种写法和上边是等同的
//    private static void setName(String newName) {
//        synchronized(Synchronized6Demo.class){
//            name = newName;
//        }
//    }

    @Override
    public void runTest() {

    }
}