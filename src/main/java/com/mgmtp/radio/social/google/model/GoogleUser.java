package com.mgmtp.radio.social.google.model;

import com.google.gson.annotations.SerializedName;

public class GoogleUser {

    public String id;

    public String email;

    @SerializedName("verified_email")
    boolean verifiedEmail;

    public String name;

    @SerializedName("given_name")
    public String firstName;

    @SerializedName("family_name")
    public String lastName;

    public String link;

    public String picture;

    public String locale;

}
