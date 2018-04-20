package com.mgmtp.radio.social.google.service;

import com.mgmtp.radio.social.google.model.GoogleUser;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleService {

    @GET("userinfo?alt=json")
    Call<GoogleUser> getUsers(@Query("access_token") String accessToken);
}
