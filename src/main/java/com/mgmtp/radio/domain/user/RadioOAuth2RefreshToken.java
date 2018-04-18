package com.mgmtp.radio.domain.user;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
public class RadioOAuth2RefreshToken {

    @Id
    private String tokenId;
    private byte[] token;
    private byte[] authentication;

    @PersistenceConstructor
    public RadioOAuth2RefreshToken(final String tokenId,
                                   final byte[] token,
                                   final byte[] authentication) {
        this.tokenId = tokenId;
        this.token = token;
        this.authentication = authentication;
    }
}
