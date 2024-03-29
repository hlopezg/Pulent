package com.example.pulent.repository;

public class Resource<T> {
    public final Status status;
    public final  String message;
    public final T data;

    public Resource(Status status, T data, String message) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> Resource<T> success(T data){
        return new Resource<>(Status.SUCCESS, data, null);

    }

    public static <T> Resource<T> error(String msg, T data){
        return new Resource<>(Status.ERROR, data, msg);
    }

    public static <T> Resource<T> loading(T data){
        return new Resource<>(Status.LOADING, data, null);
    }
}
