package com.mgmtp.radio.controller.v1;

import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.exception.RadioServiceException;
import com.mgmtp.radio.service.user.UserService;
import com.mgmtp.radio.social.facebook.FacebookServiceGenerator;
import com.mgmtp.radio.social.facebook.model.FacebookAvatar;
import com.mgmtp.radio.social.facebook.model.FacebookUser;
import com.mgmtp.radio.social.facebook.service.FacebookService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.web.bind.annotation.*;
import retrofit2.Call;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@Log4j2
@RestController
@RequestMapping(SocialConnectController.BASE_URL)
public class SocialConnectController {

    public static final String BASE_URL = "/login";

    @Value("${radio.client.id}")
    String clientId;

    @Value("${radio.client.secret}")
    String clientSecret;

    private final UserService userService;

    private final DefaultTokenServices tokenServices;

    public SocialConnectController(DefaultTokenServices tokenServices,
                                   UserService userService) {
        this.tokenServices = tokenServices;
        this.userService = userService;
    }

    @PostMapping("/facebook")
    public OAuth2AccessToken facebookConnect(@RequestHeader("Authorization") String facebookAccessToken) throws IOException, RadioServiceException {
        FacebookService facebookService = FacebookServiceGenerator.createService(FacebookService.class);
        Call<FacebookUser> callFacebookUser = facebookService.getUsers(facebookAccessToken);
        FacebookUser facebookUser = callFacebookUser.execute().body();

        if(facebookUser == null) {
            throw new RadioServiceException("invalid access token.");
        }

        Call<FacebookAvatar> callFacebookAvatar = facebookService.getUserAvatar(facebookAccessToken);
        FacebookAvatar facebookAvatar = callFacebookAvatar.execute().body();


        User newUser = userService.registerByFacebook(facebookUser, facebookAvatar);
        return authorize(newUser);
    }

    @PostMapping("/google")
    public OAuth2AccessToken googleConnect() {
        //TODO: implement google login
        User user = new User();
        return authorize(user);
    }

    private OAuth2AccessToken authorize(User user) {
        Set<GrantedAuthority> authorities = getDefaultUserGrantedAuthority();

        Map<String, String> requestParameters = new HashMap<>();

        boolean approved = true;

        Set<String> scope = new HashSet<String>() {{
            add("openId");
        }};

        Set<String> resourceIds = new HashSet<>();
        Set<String> responseTypes = new HashSet<String>() {{
            add("code");
        }};
        Map<String, Serializable> extensionProperties = new HashMap<>();

        OAuth2Request oAuth2Request = new OAuth2Request(requestParameters, clientId,
                authorities, approved, scope,
                resourceIds, null, responseTypes, extensionProperties);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, authorities);
        OAuth2Authentication auth = new OAuth2Authentication(oAuth2Request, authenticationToken);
        OAuth2AccessToken token = tokenServices.createAccessToken(auth);
        return token;
    }

    private Set<GrantedAuthority> getDefaultUserGrantedAuthority() {
        return new HashSet<GrantedAuthority>() {{
            add(new SimpleGrantedAuthority("USER"));
        }};
    }

}
