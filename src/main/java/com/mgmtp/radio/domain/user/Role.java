package com.mgmtp.radio.domain.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

@Document(collection = "role")
@Data
public class Role implements GrantedAuthority {

    @Id
    String id;

    String authority;

    @Override
    public String getAuthority() {
        return this.authority;
    }
}
