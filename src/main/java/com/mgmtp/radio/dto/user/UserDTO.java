package com.mgmtp.radio.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {

    String id;
    String email;
    String username;
    String password;
    String name;
    String firstName;
    String lastName;
    String country;
    String city;
    String bio;
    String avatarUrl;
    String coverUrl;

}
