package com.mgmtp.radio.domain.user;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
public class RadioOAuth2AccessToken {

    @Id
    private String tokenId;

    private byte[] token;

    private String authenticationId;

    private String username;

    private String clientId;

    private byte[] authentication;

    private String refreshToken;

    @PersistenceConstructor
    public RadioOAuth2AccessToken(final String tokenId,
                                  final byte[] token,
                                  final String authenticationId,
                                  final String username,
                                  final String clientId,
                                  final byte[] authentication,
                                  final String refreshToken) {
        this.tokenId = tokenId;
        this.token = token;
        this.authenticationId = authenticationId;
        this.username = username;
        this.clientId = clientId;
        this.authentication = authentication;
        this.refreshToken = refreshToken;
    }
}
