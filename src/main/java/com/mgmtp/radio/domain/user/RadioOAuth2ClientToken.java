package com.mgmtp.radio.domain.user;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "radio_oauth2_client_token")
@Getter
public class RadioOAuth2ClientToken {

    @Id
    private String id;
    private String tokenId;
    private byte[] token;
    private String authenticationId;
    private String username;
    private String clientId;

    @PersistenceConstructor
    public RadioOAuth2ClientToken(final String id,
                                  final String tokenId,
                                  final byte[] token,
                                  final String authenticationId,
                                  final String username,
                                  final String clientId) {
        this.id = id;
        this.tokenId = tokenId;
        this.token = token;
        this.authenticationId = authenticationId;
        this.username = username;
        this.clientId = clientId;
    }

}
