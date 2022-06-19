package com.sumon.bloodbank.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static Retrofit retrofit = null;

    public static ApiInterface getApiInterface(){
        if (retrofit == null){
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request().newBuilder()
                            .header("Authorization", "key="+HttpParams.WEB_API_KEY)
                            .header("Content-Type", "application/json")
                            .method(chain.request().method(), chain.request().body())
                            .build();
                    return chain.proceed(request);
                }
            });
            retrofit = new Retrofit.Builder().baseUrl(HttpParams.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return  retrofit.create(ApiInterface.class);
    }
}
