package com.hencoder.threadinteraction;

public class ThreadInteractionDemo0 implements TestDemo {
    @Override
    public void runTest() {
        //create a thread print 100 wan
        Thread thread = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 1_000_000; i++) {
                    System.out.println("number: " + i);
                }
            }
        };
        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.stop();
    }
}