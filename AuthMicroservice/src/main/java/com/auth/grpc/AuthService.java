package com.auth.grpc;

import com.auth.dto.NewUserDTO;
import com.auth.dto.TokenDTO;
import com.auth.exception.PasswordsNotMatchingException;
import com.auth.exception.RepeatedPasswordNotMatchingException;
import com.auth.exception.TokenExpiredException;
import com.auth.exception.UserAlreadyExistsException;
import com.auth.saga.dto.OrchestratorResponseDTO;
import com.auth.service.AuthenticationService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import proto.*;

@GrpcService
public class AuthService extends AuthGrpcServiceGrpc.AuthGrpcServiceImplBase {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public void addUser(NewUserProto request, StreamObserver<NewUserResponseProto> responseObserver) {
        NewUserResponseProto responseProto;
        try {
            NewUserDTO newUserDTO = new NewUserDTO(request.getFirstName(), request.getLastName(),
                    request.getEmail(), request.getPhoneNumber(), request.getGender(),
                    request.getDateOfBirth(), request.getUsername(), request.getPassword(),
                    request.getBiography());

            OrchestratorResponseDTO response = authenticationService.signUp(newUserDTO);

            if (!response.getSuccess())
                responseProto = NewUserResponseProto.newBuilder().setId(response.getId()).setStatus("Status 500").build();
            else
                responseProto = NewUserResponseProto.newBuilder().setId(response.getId()).setStatus("Status 200").build();
        } catch (UserAlreadyExistsException e) {
            responseProto = NewUserResponseProto.newBuilder().setId("").setStatus("Status 400").build();
        }

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }

    @Override
    public void login(LoginProto request, StreamObserver<LoginResponseProto> responseObserver) {
        LoginResponseProto responseProto;
        try {
            TokenDTO tokenDTO = authenticationService.login(request.getUsername(), request.getPassword());
            responseProto = LoginResponseProto.newBuilder().setJwt(tokenDTO.getJwt()).setStatus("Status 200").build();
        } catch (Exception ex) {
            responseProto = LoginResponseProto.newBuilder().setJwt("").setStatus("Status 400").build();
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
        }catch(TokenExpiredException ex) {
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
    public void recoverAccount(RecoverProto request, StreamObserver<RecoverResponseProto> responseObserver) {

        RecoverResponseProto responseProto;

        boolean accomplished = authenticationService.recoverAccount(request.getId(), request.getEmail());

        if(accomplished){
            responseProto = RecoverResponseProto.newBuilder().setStatus("Status 200").build();
        }else{
            responseProto = RecoverResponseProto.newBuilder().setStatus("Status 400").build();
        }

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();

    }

    @Override
    public void changePasswordRecovery(RecoveryPasswordProto request, StreamObserver<RecoveryPasswordResponseProto> responseObserver) {

        RecoveryPasswordResponseProto responseProto = null;

        try{
            authenticationService.changePasswordRecovery(request.getPassword(), request.getRepeatedPassword(), request.getToken());
            responseProto = RecoveryPasswordResponseProto.newBuilder().setStatus("Status 200").build();
        } catch (RepeatedPasswordNotMatchingException e) {
            responseProto = RecoveryPasswordResponseProto.newBuilder().setStatus("Status 400").build();
        } catch (TokenExpiredException e) {
            responseProto = RecoveryPasswordResponseProto.newBuilder().setStatus("Status 418").build();
        }

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();

    }
}
