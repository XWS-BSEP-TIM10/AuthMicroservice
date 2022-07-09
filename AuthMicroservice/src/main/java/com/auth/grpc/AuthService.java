package com.auth.grpc;

import com.auth.dto.NewUserDTO;
import com.auth.dto.RegisterDTO;
import com.auth.dto.TokenDTO;
import com.auth.exception.*;
import com.auth.model.Event;
import com.auth.model.User;
import com.auth.service.AuthenticationService;
import com.auth.service.EventService;
import com.auth.service.UserService;
import com.auth.service.impl.LoggerServiceImpl;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import proto.*;

import java.util.ArrayList;
import java.util.List;

@GrpcService
public class AuthService extends AuthGrpcServiceGrpc.AuthGrpcServiceImplBase {

    private final AuthenticationService authenticationService;
    private final LoggerServiceImpl loggerService;
    private final UserService userService;
    private final EventService eventService;
    private static final String STATUS_CONFLICT = "Status 409";
    private static final String STATUS_OK = "Status 200";
    private static final String STATUS_TEAPOT = "Status 418";
    private static final String STATUS_BAD_REQUEST = "Status 400";
    private static final String STATUS_NOT_FOUND = "Status 404";

    @Autowired
    public AuthService(AuthenticationService authenticationService, UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
        this.authenticationService = authenticationService;
        this.loggerService = new LoggerServiceImpl(this.getClass());
    }

    @Override
    public void addUser(NewUserProto request, StreamObserver<NewUserResponseProto> responseObserver) {
        NewUserResponseProto responseProto;
        try {
            NewUserDTO newUserDTO = new NewUserDTO(request.getFirstName(), request.getLastName(),
                    request.getEmail(), request.getPhoneNumber(), request.getGender(),
                    request.getDateOfBirth(), request.getUsername(), request.getPassword(),
                    request.getBiography());
            newUserDTO.setId(request.getId());
            RegisterDTO registered = authenticationService.signUp(newUserDTO);
            eventService.save(new Event("User successfully signed up. Username: " + request.getUsername()));
            loggerService.userSignedUp(registered.getUuid());
            responseProto = NewUserResponseProto.newBuilder().setId(registered.getUuid()).setStatus(STATUS_OK).build();
        } catch (UserAlreadyExistsException e) {
            loggerService.userSigningUpFailed("User signup failed, user already exists");
            responseProto = NewUserResponseProto.newBuilder().setId("").setStatus(STATUS_CONFLICT).build();
        } catch (EmailAlreadyExistsException e) {
            loggerService.userSigningUpFailed("User signup failed, email already exists");
            responseProto = NewUserResponseProto.newBuilder().setId("").setStatus(STATUS_TEAPOT).build();
        }

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }

    @Override
    public void login(LoginProto request, StreamObserver<LoginResponseProto> responseObserver) {
        LoginResponseProto responseProto;
        try {
            TokenDTO tokenDTO = authenticationService.login(request.getUsername(), request.getPassword(), request.getCode());
            eventService.save(new Event("Login successful. Username: " + request.getUsername()));
            loggerService.loginSuccess(request.getUsername());
            responseProto = LoginResponseProto.newBuilder().setJwt(tokenDTO.getJwt()).setRefreshToken(tokenDTO.getRefreshToken()).setStatus(STATUS_OK).build();
        } catch (CodeNotMatchingException codeNotMatchingException) {
            loggerService.login2FAFailedCodeNotMatching(request.getUsername());
            responseProto = LoginResponseProto.newBuilder().setJwt("").setStatus("Status 300").build();
        } catch (Exception ex) {
            loggerService.loginFailed(request.getUsername());
            responseProto = LoginResponseProto.newBuilder().setJwt("").setStatus(STATUS_BAD_REQUEST).build();
        }

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }

    @Override
    public void generateAPIToken(APITokenProto request, StreamObserver<APITokenResponseProto> responseObserver) {
        APITokenResponseProto responseProto;
        try {
            String token = authenticationService.generateAPIToken(request.getUserId());
            loggerService.generateAPITokenSuccess(request.getUserId());
            responseProto = APITokenResponseProto.newBuilder().setToken(token).setStatus(STATUS_OK).build();
        } catch (Exception ex) {
            loggerService.generateAPITokenFailed(request.getUserId());
            responseProto = APITokenResponseProto.newBuilder().setToken("").setStatus(STATUS_BAD_REQUEST).build();
        }
        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }

    @Override
    public void verifyUserAccount(VerifyAccountProto request, StreamObserver<VerifyAccountResponseProto> responseObserver) {
        VerifyAccountResponseProto responseProto;
        try {
            String username = authenticationService.verifyUserAccount(request.getVerificationToken());
            eventService.save(new Event("User successfully verified account. Username: " + username));
            loggerService.accountConfirmed(username);
            responseProto = VerifyAccountResponseProto.newBuilder().setUsername(username).setStatus(STATUS_OK).build();
        } catch (TokenExpiredException ex) {
            loggerService.accountConfirmedFailedTokenExpired(request.getVerificationToken());
            responseProto = VerifyAccountResponseProto.newBuilder().setUsername("").setStatus(STATUS_BAD_REQUEST).build();
        }

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }

    @Override
    public void changePassword(ChangePasswordProto request, StreamObserver<ChangePasswordResponseProto> responseObserver) {
        ChangePasswordResponseProto responseProto;

        try {
            authenticationService.changePassword(request.getUserId(), request.getOldPassword(), request.getNewPassword(), request.getRepeatedNewPassword());
            eventService.save(new Event("User successfully changed password. Username: " + userService.findById(request.getUserId()).getUsername()));
            loggerService.passwordChanged(request.getUserId());
            responseProto = ChangePasswordResponseProto.newBuilder().setStatus(STATUS_OK).setMessage("Password changed").build();
        } catch (PasswordsNotMatchingException ex) {
            loggerService.passwordChangingFailed("Entered password doesn't match existing", request.getUserId());
            responseProto = ChangePasswordResponseProto.newBuilder().setStatus(STATUS_BAD_REQUEST).setMessage(ex.getMessage()).build();
        } catch (RepeatedPasswordNotMatchingException ex) {
            loggerService.passwordChangingFailed("Entered repeated password doesn't match", request.getUserId());
            responseProto = ChangePasswordResponseProto.newBuilder().setStatus(STATUS_TEAPOT).setMessage(ex.getMessage()).build();
        }

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }

    @Override
    public void recoverAccount(SendTokenProto request, StreamObserver<SendTokenResponseProto> responseObserver) {

        SendTokenResponseProto responseProto;

        boolean accomplished = authenticationService.recoverAccount(request.getId(), request.getEmail());

        if (accomplished) {
            loggerService.accountRecovered(request.getId());
            eventService.save(new Event("User successfully recovered account. Username: " + userService.findById(request.getId()).getUsername()));
            responseProto = SendTokenResponseProto.newBuilder().setStatus(STATUS_OK).build();
        } else {
            loggerService.accountRecoverFailed(request.getId());
            responseProto = SendTokenResponseProto.newBuilder().setStatus(STATUS_BAD_REQUEST).build();
        }

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();

    }

    @Override
    public void changePasswordRecovery(RecoveryPasswordProto request, StreamObserver<RecoveryPasswordResponseProto> responseObserver) {
        RecoveryPasswordResponseProto responseProto;
        try {
            User user = authenticationService.changePasswordRecovery(request.getPassword(), request.getRepeatedPassword(), request.getToken());
            loggerService.passwordRecovered(user.getId());
            responseProto = RecoveryPasswordResponseProto.newBuilder().setStatus(STATUS_OK).build();
        } catch (RepeatedPasswordNotMatchingException e) {
            responseProto = RecoveryPasswordResponseProto.newBuilder().setStatus(STATUS_BAD_REQUEST).build();
        } catch (TokenExpiredException e) {
            responseProto = RecoveryPasswordResponseProto.newBuilder().setStatus(STATUS_TEAPOT).build();
        }

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }

    @Override
    public void generateTokenPasswordless(SendTokenProto request, StreamObserver<SendTokenResponseProto> responseObserver) {
        SendTokenResponseProto responseProto;
        boolean accomplished = authenticationService.generateTokenPasswordless(request.getId(), request.getEmail());
        if (accomplished) {
            loggerService.passwordlessTokenGenerated(request.getId());
            responseProto = SendTokenResponseProto.newBuilder().setStatus(STATUS_OK).build();
        } else {
            loggerService.passwordlessTokenGeneratingFailed("User not found", request.getId());
            responseProto = SendTokenResponseProto.newBuilder().setStatus(STATUS_BAD_REQUEST).build();
        }

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();

    }

    @Override
    public void passwordlessLogin(VerifyAccountProto request, StreamObserver<LoginResponseProto> responseObserver) {
        LoginResponseProto responseProto;
        try {
            TokenDTO tokenDTO = authenticationService.passwordlessSignIn(request.getVerificationToken());
            eventService.save(new Event("User login passwordless. User token: " + request.getVerificationToken()));
            responseProto = LoginResponseProto.newBuilder().setJwt(tokenDTO.getJwt()).setRefreshToken(tokenDTO.getRefreshToken()).setStatus(STATUS_OK).build();
        } catch (Exception ex) {
            responseProto = LoginResponseProto.newBuilder().setJwt("").setStatus(STATUS_BAD_REQUEST).build();
        }

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }

    @Override
    public void refreshToken(RefreshTokenProto request, StreamObserver<LoginResponseProto> responseObserver) {
        LoginResponseProto responseProto;
        try {
            TokenDTO tokenDTO = authenticationService.refreshToken(request.getToken());
            responseProto = LoginResponseProto.newBuilder().setJwt(tokenDTO.getJwt()).setRefreshToken(tokenDTO.getRefreshToken()).setStatus(STATUS_OK).build();
        } catch (Exception ex) {
            responseProto = LoginResponseProto.newBuilder().setJwt("").setStatus(STATUS_BAD_REQUEST).build();
        }

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }

    @Override
    public void checkToken(TokenProto request, StreamObserver<SendTokenResponseProto> responseObserver) {
        SendTokenResponseProto responseProto;

        boolean isValid = authenticationService.checkToken(request.getToken());
        if (isValid) responseProto = SendTokenResponseProto.newBuilder().setStatus(STATUS_OK).build();
        else responseProto = SendTokenResponseProto.newBuilder().setStatus(STATUS_NOT_FOUND).build();

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }

    @Override
    public void change2FAStatus(Change2FAStatusProto request, StreamObserver<Change2FAStatusResponseProto> responseObserver) {
        Change2FAStatusResponseProto responseProto;
        try {
            String secret = authenticationService.change2FAStatus(request.getUserId(), request.getEnable2FA());
            eventService.save(new Event("User successfully changed 2FA status. Username: " + userService.findById(request.getUserId()).getUsername()));
            loggerService.twoFAStatusChanged(request.getEnable2FA(), request.getUserId());
            responseProto = Change2FAStatusResponseProto.newBuilder().setSecret(secret).setStatus(STATUS_OK).build();
        } catch (Exception e) {
            loggerService.twoFAStatusChangeFailed(request.getEnable2FA(), request.getUserId());
            responseProto = Change2FAStatusResponseProto.newBuilder().setStatus(STATUS_NOT_FOUND).build();
        }
        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }

    @Override
    public void check2FAStatus(TwoFAStatusProto request, StreamObserver<TwoFAStatusResponseProto> responseObserver) {
        TwoFAStatusResponseProto responseProto;
        try {
            boolean twoFAEnabled = authenticationService.checkTwoFaStatus(request.getUserId());
            loggerService.twoFAStatusCheck(request.getUserId());
            responseProto = TwoFAStatusResponseProto.newBuilder().setEnabled2FA(twoFAEnabled).setStatus(STATUS_OK).build();
        } catch (UserNotFoundException ex) {
            loggerService.two2FACheckFailed(request.getUserId());
            responseProto = TwoFAStatusResponseProto.newBuilder().setStatus(STATUS_NOT_FOUND).build();
        }
        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }

    @Override
    public void getEvents(EventProto request, StreamObserver<EventResponseProto> responseObserver) {

        List<String> events = new ArrayList<>();
        for(Event event : eventService.findAll()){events.add(event.getDescription());}

        EventResponseProto responseProto = EventResponseProto.newBuilder().addAllEvents(events).build();

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }


}
