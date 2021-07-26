package com.ecommerce.nashtech.service.Impl;

import com.ecommerce.nashtech.entity.AuthProvider;
import com.ecommerce.nashtech.entity.Role;
import com.ecommerce.nashtech.entity.User;
import com.ecommerce.nashtech.repository.UserRepository;
import com.ecommerce.nashtech.security.JwtProvider;
import com.ecommerce.nashtech.service.email.MailSender;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.ecommerce.nashtech.util.TestConstants.*;
import static com.ecommerce.nashtech.util.TestConstants.USER_EMAIL;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AuthenticationServiceImplTest {

    @Autowired
    private AuthenticationServiceImpl authenticationService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private MailSender mailSender;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Value("${hostname}")
    private String hostname;

    @Test
    public void findByPasswordResetCode() {
        User user = new User();
        user.setPasswordResetCode(USER_PASSWORD_RESET_CODE);
        authenticationService.findByPasswordResetCode(USER_PASSWORD_RESET_CODE);

        when(userRepository.findByPasswordResetCode(USER_PASSWORD_RESET_CODE)).thenReturn(user);
        assertEquals(USER_PASSWORD_RESET_CODE, user.getPasswordResetCode());
        verify(userRepository, times(1)).findByPasswordResetCode(USER_PASSWORD_RESET_CODE);
    }

    @Test
    public void login() {
        User user = new User();
        user.setId(123L);
        user.setEmail(USER_EMAIL);
        user.setActive(true);
        user.setFirstName(FIRST_NAME);
        user.setRoles(Collections.singleton(Role.USER));

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(user);
        assertEquals(123L, user.getId());
        assertEquals(USER_EMAIL, user.getEmail());
        assertEquals(FIRST_NAME, user.getFirstName());
        authenticationService.login(USER_EMAIL);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(jwtProvider, times(1)).createToken(user.getEmail(), user.getRoles().iterator().next().name());
    }

    @Test
    public void registerUser() {
        User user = new User();
        user.setFirstName(FIRST_NAME);
        user.setEmail(USER_EMAIL);
        boolean isUserCreated = authenticationService.registerUser(user);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("firstName", FIRST_NAME);
        attributes.put("registrationUrl", "http://" + hostname + "/activate/" + user.getActivationCode());

        assertTrue(isUserCreated);
        assertNotNull(user.getActivationCode());
        assertTrue(CoreMatchers.is(user.getRoles()).matches(Collections.singleton(Role.USER)));
        verify(userRepository, times(1)).save(user);
        verify(mailSender, times(1))
                .sendMessageHtml(
                        ArgumentMatchers.eq(user.getEmail()),
                        ArgumentMatchers.eq("Activation code"),
                        ArgumentMatchers.eq("registration-template"),
                        ArgumentMatchers.eq(attributes));
    }


    @Test
    public void sendPasswordResetCode() {
        User user = new User();
        user.setEmail(USER_EMAIL);
        user.setPasswordResetCode(USER_PASSWORD_RESET_CODE);

        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(user);
        authenticationService.sendPasswordResetCode(USER_EMAIL);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("firstName", null);
        attributes.put("resetUrl", "http://" + hostname + "/reset/" + user.getPasswordResetCode());

        assertEquals(USER_EMAIL, user.getEmail());
        assertNotNull(user.getPasswordResetCode());
        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(mailSender, times(1))
                .sendMessageHtml(
                        ArgumentMatchers.eq(user.getEmail()),
                        ArgumentMatchers.eq("Password reset"),
                        ArgumentMatchers.eq("password-reset-template"),
                        ArgumentMatchers.eq(attributes));
    }

    @Test
    public void passwordReset() {
        User user = new User();
        user.setEmail(USER_EMAIL);
        user.setPassword(USER_PASSWORD);

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(user);
        when(passwordEncoder.encode(USER_PASSWORD)).thenReturn(user.getPassword());
        when(userRepository.save(user)).thenReturn(user);
        authenticationService.passwordReset(user.getEmail(), user.getPassword());
        assertEquals(USER_EMAIL, user.getEmail());
        assertNotNull(user.getPassword());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(passwordEncoder, times(1)).encode(user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void activateUser() {
        User user = new User();
        user.setActivationCode(USER_ACTIVATION_CODE);

        when(userRepository.findByActivationCode(USER_ACTIVATION_CODE)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        boolean isActivated = authenticationService.activateUser(user.getActivationCode());
        assertTrue(isActivated);
        assertNull(user.getActivationCode());
        verify(userRepository, times(1)).save(user);
    }
}
