package com.hencoder.threadinteraction;

public class Main {
    public static void main(String[] args) {
//      new Thread().start();
//      new Thread().stop();
//      runThreadInteractionDemo2();
//      runWaitDemo01();
//      runWaitDemo02();
//      runWaitDemo03();
//      runWaitDemo04();
        runWaitDemo05();
    }

    static void runThreadInteractionDemo0(){
        new ThreadInteractionDemo0().runTest();
    }

    static void runThreadInteractionDemo1() {
        new ThreadInteractionDemo1().runTest();
    }

    static void runThreadInteractionDemo2(){
        new ThreadInteractionDemo2().runTest();
    }

    static void runWaitDemo01() {
        new WaitDemo01().runTest();
    }

    static void runWaitDemo02() {
        new WaitDemo02().runTest();
    }

    static void runWaitDemo03() {
        new WaitDemo03().runTest();
    }

    static void runWaitDemo04() {
        new WaitDemo04().runTest();
    }

    static void runWaitDemo05() {
        new WaitDemo05().runTest();
    }
}
