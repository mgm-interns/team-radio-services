package com.mgmtp.radio.social.facebook.model;

import com.google.gson.annotations.SerializedName;

public class FacebookAvatar {

    @SerializedName("data")
    public FacebookAvatarData data;

    public class FacebookAvatarData {
        public String url;
    }
}
