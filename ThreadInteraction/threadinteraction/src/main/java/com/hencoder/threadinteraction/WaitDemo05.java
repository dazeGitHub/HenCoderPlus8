package com.hencoder.threadinteraction;

/**
 * 使用 join 实例2
 */
public class WaitDemo05 implements TestDemo {
    private String sharedString;

    private synchronized void initString() {
        sharedString = "rengwuxian";
    }

    private synchronized void printString() {
        System.out.println("String: " + sharedString);
    }

    @Override
    public void runTest() {
        //change thread2 and thread1 position
        final Thread thread2 = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                initString();
            }
        };
        thread2.start();

        final Thread thread1 = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    thread2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                printString();
            }
        };
        thread1.start();
    }
}