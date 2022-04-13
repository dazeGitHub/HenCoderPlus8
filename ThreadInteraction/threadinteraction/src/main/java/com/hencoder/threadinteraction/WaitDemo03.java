package com.hencoder.threadinteraction;

/**
 * 唤醒 和 等待 使用不同的 monitor
 */
public class WaitDemo03 implements TestDemo {
    private String sharedString;
    private Object monitor = new Object();

    //使用 synchronized 保证 initString() 写入 sharedString 时 printString() 无法访问 sharedString
    private synchronized void initString() {
        sharedString = "rengwuxian";
        //通过 this 这个 monitor 来唤醒, 如果使用 monitor 对象, 那么唤醒不了
        notifyAll();
    }

    private synchronized void printString() {
        synchronized (monitor){
            //需要一直检查 sharedString, 直到它不为空
            while (sharedString == null) {
                try {
                    System.out.println("printString wait()");
                    //这里必须使用 monitor.wait(), 否则会报错
                    //这里使用 monitor.wait() 无法醒过来
                    monitor.wait();
                } catch (InterruptedException e) {

                }
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