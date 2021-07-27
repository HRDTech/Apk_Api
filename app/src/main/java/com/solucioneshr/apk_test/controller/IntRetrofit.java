package com.solucioneshr.apk_test.controller;

import com.solucioneshr.apk_test.model.DataList;
import com.solucioneshr.apk_test.model.DataRandom;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IntRetrofit {

    @GET("random")
    Call<DataRandom> Get_Data_Random();

    @GET("search?")
    Call<DataList> Get_Data_List (@Query("query") String cat);

    @GET("categories")
    Call<List<String>> Get_Category();

}
