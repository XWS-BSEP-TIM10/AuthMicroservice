package com.auth.service;

import com.auth.model.User;
import com.auth.model.VerificationToken;

public interface VerificationTokenService {
    VerificationToken saveVerificationToken(VerificationToken token);
    VerificationToken findVerificationTokenByToken(String token);
    VerificationToken findVerificationTokenByUser(String id);
    void delete(VerificationToken verificationToken);
}
