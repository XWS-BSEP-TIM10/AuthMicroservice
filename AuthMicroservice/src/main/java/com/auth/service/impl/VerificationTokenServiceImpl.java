package com.auth.service.impl;

import com.auth.model.User;
import com.auth.model.VerificationToken;
import com.auth.repository.VerificationTokenRepository;
import com.auth.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;


    @Override
    public VerificationToken saveVerificationToken(VerificationToken token) {
        return verificationTokenRepository.save(token);
    }


    @Override
    public VerificationToken findVerificationTokenByToken(String token) {
        return verificationTokenRepository.findVerificationTokenByToken(token);
    }

    @Override
    public VerificationToken findVerificationTokenByUser(String id) {
        return verificationTokenRepository.findVerificationTokenByUserId(id);
    }

    @Override
    public void delete(VerificationToken verificationToken) {
        verificationTokenRepository.delete(verificationToken);
    }
}
