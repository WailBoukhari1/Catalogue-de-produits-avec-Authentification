package com.youcode.product_manage.validation;

import org.springframework.stereotype.Component;

import com.youcode.product_manage.dto.request.UserRequest;
import com.youcode.product_manage.exception.ValidationException;
import com.youcode.product_manage.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void validateForCreation(UserRequest request) {
        validateLogin(request.getLogin());
        validatePassword(request.getPassword());
        validateLoginUniqueness(request.getLogin());
    }

    public void validateForUpdate(Long id, UserRequest request) {
        validateLogin(request.getLogin());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            validatePassword(request.getPassword());
        }
        validateLoginUniquenessForUpdate(id, request.getLogin());
    }

    private void validateLogin(String login) {
        if (login == null || login.trim().length() < 3) {
            throw new ValidationException("Login must be at least 3 characters");
        }
        if (login.trim().length() > 50) {
            throw new ValidationException("Login must not exceed 50 characters");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new ValidationException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new ValidationException("Password must contain at least one number");
        }
    }

    private void validateLoginUniqueness(String login) {
        if (userRepository.existsByLogin(login)) {
            throw new ValidationException("Login already exists");
        }
    }

    private void validateLoginUniquenessForUpdate(Long id, String login) {
        userRepository.findByLogin(login).ifPresent(user -> {
            if (!user.getId().equals(id)) {
                throw new ValidationException("Login already exists");
            }
        });
    }
}
