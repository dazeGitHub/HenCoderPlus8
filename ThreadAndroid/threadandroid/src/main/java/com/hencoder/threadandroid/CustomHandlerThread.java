package com.hencoder.threadandroid;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 */
public class CustomHandlerThread extends Thread {

    Looper looper = new Looper();

    @Override
    public void run() {
        looper.loop();
    }

    class Looper {
        private Runnable task;
        private AtomicBoolean quit = new AtomicBoolean(false);

        synchronized void setTask(Runnable task) {
            this.task = task;
        }

        void quit() {
            quit.set(true);
        }

        void loop(){
            while (!quit.get()){
                synchronized (this) {
                    if (task != null) {
                        task.run();
                        task = null;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        CustomHandlerThread thread = new CustomHandlerThread();
        thread.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.looper.setTask(new Runnable() {
            @Override
            public void run() {
                System.out.println("hahahaha");
            }
        });
        thread.looper.quit();
    }
}
