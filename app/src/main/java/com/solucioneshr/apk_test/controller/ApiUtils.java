package com.solucioneshr.apk_test.controller;

public class ApiUtils {
    public static final String BASE_URL = "https://api.chucknorris.io/jokes/";

    public static IntRetrofit getDataRandom (){
        return RetrofitClient.getClient(BASE_URL).create(IntRetrofit.class);
    }

    public static IntRetrofit getDataList (){
        return RetrofitClient.getClient(BASE_URL).create(IntRetrofit.class);
    }

    public static IntRetrofit getListCategory (){
        return RetrofitClient.getClient(BASE_URL).create(IntRetrofit.class);
    }
}
