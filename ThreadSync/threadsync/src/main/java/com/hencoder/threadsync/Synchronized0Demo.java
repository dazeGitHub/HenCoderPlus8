package com.hencoder.threadsync;

import java.util.concurrent.atomic.AtomicBoolean;

public class Synchronized0Demo implements TestDemo {
    private volatile Boolean running = true;

    private void stop() {
        running = false;
        //System.out.println("Synchronized0Demo stop() running = " + running);
    }

    @Override
    public void runTest() {
        new Thread() {
            @Override
            public void run() {
                while (running) {
//                    try {
//                        Thread.sleep(200);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println("Synchronized0Demo runTest() Thread run() running = " + running);
                }
            }
        }.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stop();
    }
}

/**
 * use or not use volatile log always is :
 * Synchronized0Demo runTest() Thread run() running = true
 * Synchronized0Demo runTest() Thread run() running = true
 * Synchronized0Demo runTest() Thread run() running = true
 * Synchronized0Demo runTest() Thread run() running = true
 * Synchronized0Demo stop() running = false
 * Synchronized0Demo runTest() Thread run() running = false
 */
