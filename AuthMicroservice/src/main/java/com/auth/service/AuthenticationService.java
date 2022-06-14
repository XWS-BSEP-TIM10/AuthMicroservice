package com.auth.service;

import com.auth.dto.NewUserDTO;
import com.auth.dto.TokenDTO;
import com.auth.exception.EmailAlreadyExistsException;
import com.auth.exception.PasswordsNotMatchingException;
import com.auth.exception.RepeatedPasswordNotMatchingException;
import com.auth.exception.TokenExpiredException;
import com.auth.exception.UserAlreadyExistsException;
import com.auth.model.User;
import com.auth.saga.dto.OrchestratorResponseDTO;

public interface AuthenticationService {

    String verifyUserAccount(String token) throws TokenExpiredException;

    Boolean userNotActivated(String id);

    boolean recoverAccount(String email, String id);

    User changePasswordRecovery(String newPassword, String repeatedNewPassword, String token) throws RepeatedPasswordNotMatchingException, TokenExpiredException;

    TokenDTO login(String username, String password, String code);

    OrchestratorResponseDTO signUp(NewUserDTO newUserDTO) throws UserAlreadyExistsException, EmailAlreadyExistsException;

    void changePassword(String userId, String oldPassword, String newPassword, String repeatedNewPassword) throws PasswordsNotMatchingException, RepeatedPasswordNotMatchingException;

    TokenDTO passwordlessSignIn(String token) throws TokenExpiredException;

    boolean generateTokenPasswordless(String id, String email);

    TokenDTO refreshToken(String token);

    Boolean checkToken(String token);

    String generateAPIToken(String userId);

    String change2FAStatus(String userId, boolean enable2FA);

    boolean checkTwoFaStatus(String userId);
}
