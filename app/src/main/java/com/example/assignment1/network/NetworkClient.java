package com.example.assignment1.network;

import com.example.assignment1.model.UsersListResponse;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {
    private static final String BASE_URL = "https://reqres.in/api/";
    private static NetworkClient instance = null;
    private final ReqResAPI reqResAPI;

    private NetworkClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        reqResAPI = retrofit.create(ReqResAPI.class);
    }

    /**
     * Provides a singleton instance of NetworkClient.
     *
     * @return The singleton instance of NetworkClient.
     */
    public static synchronized NetworkClient getInstance() {
        if (instance == null) {
            instance = new NetworkClient();
        }
        return instance;
    }
    /**
     * Makes a network call to fetch the list of users.
     *
     * @param pageNumber The page number to retrieve.
     * @return A Call object for the network request.
     */
    public Call<UsersListResponse> getUsers(String pageNumber) {
        return reqResAPI.getUsersList(pageNumber);
    }
}
