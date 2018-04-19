package com.mgmtp.radio.social.facebook.model;

import com.google.gson.annotations.SerializedName;

public class FacebookUser {

    public String id;

    public String name;

    public String email;

    @SerializedName("first_name")
    public String firstName;

    @SerializedName("last_name")
    public String lastName;
}
