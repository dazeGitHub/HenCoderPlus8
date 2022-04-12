package com.hencoder.threadsync;

import java.util.concurrent.locks.ReentrantLock;

/**
 * java 中的可重用锁 ReentrantLock
 */
public class ReadWriteLockDemo1 implements TestDemo {
    private int x = 0;
    ReentrantLock lock = new ReentrantLock();

    //使用 lock 手动加锁和解锁
    private void count() {
        lock.lock();    //加锁
        //如果在 lock.lock() 和 lock.unlock() 之间的代码抛异常了, 那么 lock.unlock() 将不会执行, 就永远不会解锁了
        //并且其他地方再调用 count() 方法也不会拿到锁, 因为之前的锁还没有释放, 就是死锁了, 所以使用 try{} finally{} 来保证肯定会解锁
        //synchronized 是一个关键字, 是自动加锁解锁的过程, 所以使用 synchronized{} 包裹的代码块编译器会做处理, 就不需要 try{} finally{} 包裹
        try{
            x++;
        }finally {
            lock.unlock();  //解锁
        }
    }

    private void print(int time) {
        System.out.print(x + " ");
    }

    @Override
    public void runTest() {

    }
}