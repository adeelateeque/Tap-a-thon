package com.zhideel.tapathon;

/**
 * Created by Adeel on 1/26/14.
 */
public final class Stopwatch {

    private long start;

    private Stopwatch() {
        start = System.currentTimeMillis();
    }

    public static Stopwatch start() {
        return new Stopwatch();
    }

    public int elapsed() {
        return (int) (System.currentTimeMillis() - start);
    }
}
