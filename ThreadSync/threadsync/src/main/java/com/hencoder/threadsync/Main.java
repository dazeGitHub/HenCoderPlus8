package com.hencoder.threadsync;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) {
//        thread();
//        runnable();
//        threadFactory();
//        executor();
//        callable();
//        runSynchronized0Demo();
//        runSynchronized1Demo();
//        runSynchronized2Demo();
          runSynchronized3Demo();
//        runReadWriteLockDemo();
    }

    /**
     * 使用 Thread 类来定义工作
     */
    static void thread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                System.out.println("Thread started!");
            }
        };
        thread.start();
        //thread.run();
    }

    /**
     * 使用 Runnable 类来定义工作
     */
    static void runnable() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("Thread with Runnable started!");
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    static void threadFactory() {
        ThreadFactory factory = new ThreadFactory() {
            AtomicInteger count = new AtomicInteger(0); // int

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Thread-" + count.incrementAndGet()); // ++count
                //count.getAndIncrement();// count++
            }
        };

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " started!");
            }
        };

        Thread thread = factory.newThread(runnable);
        thread.start();
        Thread thread1 = factory.newThread(runnable);
        thread1.start();
    }

    static void executor() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("Thread with Runnable started!");
            }
        };

        Executor executor = Executors.newCachedThreadPool();
        //Executor executor = Executors.newSingleThreadExecutor();

        Executors.newFixedThreadPool(10);
        //处理 Bitmap 是个爆发任务, 可以用完了线程就直接回收
//        List<Bitmap> bitmaps = ...
//        ExecutorService fixedExecutor = Executors.newFixedThreadPool(20);
//        for(Bitmap bitmap : bitmaps){
//            fixedExecutor.execute(processImageRunnable);
//        }
//        fixedExecutor.shutdown();
//        processImageRunnable // processImages();

//        Executors.newScheduledThreadPool()
//        Executors.newSingleThreadScheduledExecutor();

        executor.execute(runnable);
        executor.execute(runnable);
        executor.execute(runnable);
    }

    static void executorService(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("Thread with Runnable started!");
            }
        };

        ExecutorService myExecutorService = new ThreadPoolExecutor(5, 100,
                5, TimeUnit.MINUTES, new SynchronousQueue<Runnable>());

        myExecutorService.execute(runnable);
    }

    static void callable() {
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "Done!";
            }
        };

        ExecutorService executor = Executors.newCachedThreadPool();
        Future<String> future = executor.submit(callable);
        while (true) {
            if (future.isDone()) {
                try {
                    String result = future.get();
                    System.out.println("result: " + result);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    static void runSynchronized0Demo(){
        new Synchronized0Demo().runTest();
    }

    static void runSynchronized1Demo() {
        new Synchronized1Demo().runTest();
    }

    static void runSynchronized2Demo() {
        new Synchronized2Demo().runTest();
    }

    static void runSynchronized3Demo() {
        new Synchronized3Demo().runTest();
    }

    static void runReadWriteLockDemo() {
        new ReadWriteLockDemo2().runTest();
    }
}
