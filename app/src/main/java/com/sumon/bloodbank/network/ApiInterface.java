package com.sumon.bloodbank.network;

import com.sumon.bloodbank.Model.NotificationDto;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST(HttpParams.SEND_NOTIFICATION_ENDPOINT)
    Call<ResponseBody> sendNotification(@Body NotificationDto notificationDto);

}
