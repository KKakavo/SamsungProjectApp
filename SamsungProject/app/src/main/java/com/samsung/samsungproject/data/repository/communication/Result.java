package com.samsung.samsungproject.data.repository.communication;

import androidx.annotation.Nullable;

public abstract class Result {
    private Result() {}

    public static final class Success<T> extends Result {
        public T data;

        public Success(T data) {
            this.data = data;
        }
    }

    public static final class Error<T> extends Result {
        public Exception exception;
        public T data;
        public Error(Exception exception, @Nullable T data) {
            this.exception = exception;
            this.data = data;
        }
    }
}