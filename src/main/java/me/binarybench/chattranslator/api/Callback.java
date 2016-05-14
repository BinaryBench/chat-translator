package me.binarybench.chattranslator.api;

/**
 * Created by Bench on 5/13/2016.
 */
public interface Callback<T> {
    void call(T value);
}
