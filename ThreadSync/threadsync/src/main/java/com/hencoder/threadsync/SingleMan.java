package com.hencoder.threadsync;

class SingleMan {

    //volatile : 禁止发生 dex 指令重排
    private static volatile SingleMan sInstance;

    private SingleMan() {

    }

    static SingleMan newInstance() {
        if (sInstance == null) {
            synchronized (SingleMan.class) {
                if (sInstance == null) {
                    sInstance = new SingleMan();
                }
            }
        }
        return sInstance;
    }
}
