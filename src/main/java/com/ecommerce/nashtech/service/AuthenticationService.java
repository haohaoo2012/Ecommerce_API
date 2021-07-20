package com.ecommerce.nashtech.service;

import com.ecommerce.nashtech.entity.User;

import java.util.Map;

public interface AuthenticationService {

    Map<String, String> login(String email);

    boolean registerUser(User user);

    boolean activateUser(String code);

    User findByPasswordResetCode(String code);

    boolean sendPasswordResetCode(String email);

    String passwordReset(String email, String password);
}
