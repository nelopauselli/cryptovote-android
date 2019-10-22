package com.nelo.cryptovote.WebApiAdapters;

public interface RequestListener<T> {
    void onComplete(T response);

    void onError(int statusCode);
}
