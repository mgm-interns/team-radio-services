package com.mgmtp.radio.social.facebook.service;

import com.mgmtp.radio.social.facebook.model.FacebookAvatar;
import com.mgmtp.radio.social.facebook.model.FacebookUser;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FacebookService {

    @GET("me?fields=id,name,first_name,last_name,email")
    Call<FacebookUser> getUsers(@Query("access_token") String accessToken);

    @GET("me/picture?&redirect=false&fields=url")
    Call<FacebookAvatar> getUserAvatar(@Query("access_token") String accessToken);
}
