package com.mgmtp.radio.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
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
