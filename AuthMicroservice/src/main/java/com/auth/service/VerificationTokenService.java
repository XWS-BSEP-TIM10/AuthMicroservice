package com.auth.service;

import com.auth.model.VerificationToken;

public interface VerificationTokenService {
    VerificationToken saveVerificationToken(VerificationToken token);
    VerificationToken findVerificationTokenByToken(String token);
    void delete(VerificationToken verificationToken);
}
