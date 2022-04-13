package com.hencoder.threadinteraction;

/**
 * 测试线程 sleep() 的时候被打断
 */
public class ThreadInteractionDemo2 implements TestDemo {
    @Override
    public void runTest() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    //SystemClock.sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println("ThreadInteractionDemo2 InterruptedException");
                    e.printStackTrace();
                    return;
                }
            }
        };
        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.interrupt();
    }
}