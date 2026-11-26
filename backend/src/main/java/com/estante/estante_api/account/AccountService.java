package com.estante.estante_api.account;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public AccountService(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthService authService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public AccountProfileResponse getProfile(String email) {
        AppUser user = findUser(email);
        return new AccountProfileResponse(
                user.getName(),
                user.getEmail(),
                user.getFavoriteGenre());
    }

    @Transactional
    public AuthResponse updateProfile(String currentEmail, UpdateAccountRequest request) {
        AppUser user = findUser(currentEmail);
        String email = request.email().trim().toLowerCase();

        userRepository.findByEmailIgnoreCase(email)
                .filter(existing -> !existing.getId().equals(user.getId()))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "An account with this email already exists.");
                });

        user.updateProfile(
                request.name().trim(),
                email,
                request.favoriteGenre() == null
                        ? null
                        : request.favoriteGenre().trim());

        return authService.createTokenResponse(user);
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        AppUser user = findUser(email);

        if (!passwordEncoder.matches(
                request.currentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        user.changePassword(passwordEncoder.encode(request.newPassword()));
    }

    private AppUser findUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("Account not found."));
    }
}