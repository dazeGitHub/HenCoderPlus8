package com.hencoder.threadinteraction;

public class WaitDemo02 implements TestDemo {
    private String sharedString;

    private synchronized void initString() {
        sharedString = "rengwuxian";
        notifyAll();
    }

    private synchronized void printString() { //synchronized
        while (sharedString == null) {
            try {
                System.out.println("printString wait()");
                wait();
            } catch (InterruptedException e) {

            }
        }
        System.out.println("String: " + sharedString);
    }

    @Override
    public void runTest() {
        final Thread thread1 = new Thread() {
            @Override
            public void run() {
                try {
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