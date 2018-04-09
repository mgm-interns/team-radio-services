package com.mgmtp.radio.support.validator.user;

import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.respository.user.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RegisterValidator implements Validator {

    private final UserRepository userRepository;
    private final MessageSourceAccessor messageSourceAccessor;

    public RegisterValidator(UserRepository userRepository,
                             MessageSourceAccessor messageSourceAccessor) {
        this.userRepository = userRepository;
        this.messageSourceAccessor = messageSourceAccessor;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UserDTO.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDTO userDTO = (UserDTO) target;
        this.validUsername(userDTO, errors);
        this.validateEmail(userDTO, errors);
        this.validateExists(userDTO, errors);
    }

    private void validateEmail(UserDTO userDTO, Errors errors) {
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(userDTO.getEmail());
        if(StringUtils.isAllEmpty(userDTO.getEmail()) || !matcher.matches()) {
            errors.rejectValue("email", "", messageSourceAccessor.getMessage("validation.error.email"));
        }
    }

    private void validUsername(UserDTO userDTO, Errors errors) {
        if(userDTO.getUsername() != null && userDTO.getUsername().trim().equals("")) {
            errors.rejectValue("email", "", messageSourceAccessor.getMessage("validation.error.username"));
        }
    }

    private void validateExists(UserDTO userDTO, Errors errors) {
        if (userDTO.getUsername() != null && isUsernameExisted(userDTO.getUsername())) {
            errors.rejectValue("username", "", messageSourceAccessor.getMessage("validation.error.unique", new String[]{"username"}));
        }

        if (isEmailExisted(userDTO.getEmail())) {
            errors.rejectValue("email", "", messageSourceAccessor.getMessage("validation.error.unique", new String[]{"email"}));
        }
    }

    private boolean isUsernameExisted(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent();
    }

    private boolean isEmailExisted(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }


}
