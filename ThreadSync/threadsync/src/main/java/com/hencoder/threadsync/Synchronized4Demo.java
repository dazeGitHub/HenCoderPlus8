package com.hencoder.threadsync;

/**
 * 在 Synchronized3Demo 之前
 */
public class Synchronized4Demo implements TestDemo {

    private int x = 0;
    private int y = 0;
    private String name;

//  加 synchronized 是为了保护数据, 而不是保护方法
//  让代码块内部的资源互斥访问, 在加了 synchronized 关键字后为这个方法提供了监视器 monitor, 如果有一个线程在访问该方法, 那么 monitor 会阻止其他线程访问
//  但是如果两个方法都加了 synchronized, 那么这两个方法都会使用同一个 monitor, 如果一个线程访问了 count(), 那么另一个线程访问 count() 不可以, 访问 setName() 也不可以
//  这样设计的意义 :
//  如果希望分别保护 count() 和 setName(), 那么就不能同时使用 synchronized,
    private synchronized void count(int newValue) {
        x = newValue;   // monitor
        y = newValue;
    }

    //这种写法和上边是等价的, monitor 都是当前对象
//    private void count(int newValue){
//        synchronized (this){
//            //...
//        }
//    }

    //在一个线程使用 count() 修改 x 和 y 的值时, 并不希望 minus() 方法也修改 x 和 y 的值, 解决方案就是为 minus() 添加 synchronized
    private synchronized void minus(int delta){
        x -= delta;
        y -= delta;
    }

    private synchronized void setName(String newName) {
        name = newName;
    }

    @Override
    public void runTest() {

    }
}