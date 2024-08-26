package com.example.assignment1.network;

import com.example.assignment1.model.UsersListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ReqResAPI {
    @GET("users")
    Call<UsersListResponse> getUsersList(@Query("page") String page);
}
