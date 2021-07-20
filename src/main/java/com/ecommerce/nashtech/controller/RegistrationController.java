package com.ecommerce.nashtech.controller;

import com.ecommerce.nashtech.dto.RegistrationRequest;
import com.ecommerce.nashtech.exception.ApiRequestException;
import com.ecommerce.nashtech.exception.EmailException;
import com.ecommerce.nashtech.exception.InputFieldException;
import com.ecommerce.nashtech.mapper.AuthenticationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/registration")
public class RegistrationController {

    private final AuthenticationMapper authenticationMapper;


    @PostMapping
    public ResponseEntity<String> registration(@Valid @RequestBody RegistrationRequest user, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new InputFieldException(bindingResult);
        }
        if (!authenticationMapper.registerUser(user)) {
            throw new EmailException("Email is already used.");
        }
        return ResponseEntity.ok("User successfully registered.");
    }

    @GetMapping("/activate/{code}")
    public ResponseEntity<String> activateEmailCode(@PathVariable String code) {
        if (!authenticationMapper.activateUser(code)) {
            throw new ApiRequestException("Activation code not found.", HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok("User successfully activated.");
        }
    }
}
