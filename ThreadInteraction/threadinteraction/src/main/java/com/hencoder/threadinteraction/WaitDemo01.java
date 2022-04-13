package com.hencoder.threadinteraction;

public class WaitDemo01 implements TestDemo {
    private String sharedString;

    private synchronized void initString() {
        sharedString = "rengwuxian";
    }

    private synchronized void printString() {
        while (sharedString == null) {

        }
        System.out.println("String: " + sharedString);
    }

    @Override
    public void runTest() {
        final Thread thread1 = new Thread() {
            @Override
            public void run() {
                try {
                    //耗时操作1
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                printString();
            }
        };
        thread1.start();
        Thread thread2 = new Thread() {
            @Override
            public void run() {
                try {
                    //耗时操作2
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                initString();
            }
        };
        thread2.start();
    }
}