package com.auth.grpc;

import com.auth.dto.NewUserDTO;
import com.auth.dto.TokenDTO;
import com.auth.exception.EmailAlreadyExistsException;
import com.auth.exception.PasswordsNotMatchingException;
import com.auth.exception.RepeatedPasswordNotMatchingException;
import com.auth.exception.TokenExpiredException;
import com.auth.exception.UserAlreadyExistsException;
import com.auth.model.User;
import com.auth.saga.dto.OrchestratorResponseDTO;
import com.auth.service.AuthenticationService;
import com.auth.service.impl.LoggerServiceImpl;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import proto.APITokenProto;
import proto.APITokenResponseProto;
import proto.AuthGrpcServiceGrpc;
import proto.ChangePasswordProto;
import proto.ChangePasswordResponseProto;
import proto.LoginProto;
import proto.LoginResponseProto;
import proto.NewUserProto;
import proto.NewUserResponseProto;
import proto.RecoveryPasswordProto;
import proto.RecoveryPasswordResponseProto;
import proto.RefreshTokenProto;
import proto.SendTokenProto;
import proto.SendTokenResponseProto;
import proto.TokenProto;
import proto.VerifyAccountProto;
import proto.VerifyAccountResponseProto;

@GrpcService
public class AuthService extends AuthGrpcServiceGrpc.AuthGrpcServiceImplBase {

    private final AuthenticationService authenticationService;
    private final LoggerServiceImpl loggerService;

    @Autowired
    public AuthService(AuthenticationService authenticationService) {
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
            OrchestratorResponseDTO response = authenticationService.signUp(newUserDTO);

            if (!response.getSuccess())
                responseProto = NewUserResponseProto.newBuilder().setId(response.getId()).setStatus("Status 500").build();
            else
                responseProto = NewUserResponseProto.newBuilder().setId(response.getId()).setStatus("Status 200").build();
        } catch (UserAlreadyExistsException e) {
            responseProto = NewUserResponseProto.newBuilder().setId("").setStatus("Status 409").build();
        } catch (EmailAlreadyExistsException e) {
            responseProto = NewUserResponseProto.newBuilder().setId("").setStatus("Status 418").build();
        }

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }

    @Override
    public void login(LoginProto request, StreamObserver<LoginResponseProto> responseObserver) {
        LoginResponseProto responseProto;
        try {
            TokenDTO tokenDTO = authenticationService.login(request.getUsername(), request.getPassword());
            responseProto = LoginResponseProto.newBuilder().setJwt(tokenDTO.getJwt()).setRefreshToken(tokenDTO.getRefreshToken()).setStatus("Status 200").build();
        } catch (Exception ex) {
            responseProto = LoginResponseProto.newBuilder().setJwt("").setStatus("Status 400").build();
        }

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }

    @Override
    public void generateAPIToken(APITokenProto request, StreamObserver<APITokenResponseProto> responseObserver) {
        APITokenResponseProto responseProto;
        try {
            String token = authenticationService.generateAPIToken(request.getUserId());
            responseProto = APITokenResponseProto.newBuilder().setToken(token).setStatus("Status 200").build();
        } catch (Exception ex) {
            responseProto = APITokenResponseProto.newBuilder().setToken("").setStatus("Status 400").build();
        }
        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }

    @Override
    public void verifyUserAccount(VerifyAccountProto request, StreamObserver<VerifyAccountResponseProto> responseObserver) {
        VerifyAccountResponseProto responseProto;
        try {
            String username = authenticationService.verifyUserAccount(request.getVerificationToken());
            responseProto = VerifyAccountResponseProto.newBuilder().setUsername(username).setStatus("Status 200").build();
        } catch (TokenExpiredException ex) {
            responseProto = VerifyAccountResponseProto.newBuilder().setUsername("").setStatus("Status 400").build();
        }


        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }

    @Override
    public void changePassword(ChangePasswordProto request, StreamObserver<ChangePasswordResponseProto> responseObserver) {
        ChangePasswordResponseProto responseProto;

        try {
            authenticationService.changePassword(request.getUserId(), request.getOldPassword(), request.getNewPassword(), request.getRepeatedNewPassword());
            responseProto = ChangePasswordResponseProto.newBuilder().setStatus("Status 200").setMessage("Password changed").build();
        } catch (PasswordsNotMatchingException ex) {
            responseProto = ChangePasswordResponseProto.newBuilder().setStatus("Status 400").setMessage(ex.getMessage()).build();
        } catch (RepeatedPasswordNotMatchingException ex) {
            responseProto = ChangePasswordResponseProto.newBuilder().setStatus("Status 418").setMessage(ex.getMessage()).build();
        }

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }

    @Override
    public void recoverAccount(SendTokenProto request, StreamObserver<SendTokenResponseProto> responseObserver) {

        SendTokenResponseProto responseProto;

        boolean accomplished = authenticationService.recoverAccount(request.getId(), request.getEmail());

        if (accomplished) {
            responseProto = SendTokenResponseProto.newBuilder().setStatus("Status 200").build();
        } else {
            responseProto = SendTokenResponseProto.newBuilder().setStatus("Status 400").build();
        }

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();

    }

    //----------------------------
    @Override
    public void changePasswordRecovery(RecoveryPasswordProto request, StreamObserver<RecoveryPasswordResponseProto> responseObserver) {
        RecoveryPasswordResponseProto responseProto;
        try {
            User user = authenticationService.changePasswordRecovery(request.getPassword(), request.getRepeatedPassword(), request.getToken());
            loggerService.passwordRecovered(user.getId());
            responseProto = RecoveryPasswordResponseProto.newBuilder().setStatus("Status 200").build();
        } catch (RepeatedPasswordNotMatchingException e) {
            responseProto = RecoveryPasswordResponseProto.newBuilder().setStatus("Status 400").build();
        } catch (TokenExpiredException e) {
            responseProto = RecoveryPasswordResponseProto.newBuilder().setStatus("Status 418").build();
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
            responseProto = SendTokenResponseProto.newBuilder().setStatus("Status 200").build();
        } else {
            loggerService.passwordlessTokenGeneratingFailed("User not found", request.getId());
            responseProto = SendTokenResponseProto.newBuilder().setStatus("Status 400").build();
        }

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();

    }

    @Override
    public void passwordlessLogin(VerifyAccountProto request, StreamObserver<LoginResponseProto> responseObserver) {
        LoginResponseProto responseProto;
        try {
            TokenDTO tokenDTO = authenticationService.passwordlessSignIn(request.getVerificationToken());
            responseProto = LoginResponseProto.newBuilder().setJwt(tokenDTO.getJwt()).setRefreshToken(tokenDTO.getRefreshToken()).setStatus("Status 200").build();
        } catch (Exception ex) {
            responseProto = LoginResponseProto.newBuilder().setJwt("").setStatus("Status 400").build();
        }

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }

    @Override
    public void refreshToken(RefreshTokenProto request, StreamObserver<LoginResponseProto> responseObserver) {
        LoginResponseProto responseProto;
        try {
            TokenDTO tokenDTO = authenticationService.refreshToken(request.getToken());
            responseProto = LoginResponseProto.newBuilder().setJwt(tokenDTO.getJwt()).setRefreshToken(tokenDTO.getRefreshToken()).setStatus("Status 200").build();
        } catch (Exception ex) {
            responseProto = LoginResponseProto.newBuilder().setJwt("").setStatus("Status 400").build();
        }

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }

    @Override
    public void checkToken(TokenProto request, StreamObserver<SendTokenResponseProto> responseObserver) {
        SendTokenResponseProto responseProto;

        Boolean isValid = authenticationService.checkToken(request.getToken());
        if (isValid) responseProto = SendTokenResponseProto.newBuilder().setStatus("Status 200").build();
        else responseProto = SendTokenResponseProto.newBuilder().setStatus("Status 404").build();

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }


}
