package com.auth.grpc;

import com.auth.dto.NewUserDTO;
import com.auth.dto.TokenDTO;
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
    public void addUser(NewUserProto request, StreamObserver<NewUserResponseProto> responseObserver){
        NewUserResponseProto responseProto;
        try {
            NewUserDTO newUserDTO = new NewUserDTO(request.getFirstName(), request.getLastName(),
                            request.getEmail(), request.getPhoneNumber(), request.getGender(),
                            request.getDateOfBirth(),request.getUsername(), request.getPassword(),
                            request.getBiography());

            OrchestratorResponseDTO response = authenticationService.signUp(newUserDTO).block();

            if (!response.getSuccess())
                responseProto = NewUserResponseProto.newBuilder().setUsername(response.getUsername()).setStatus("Status 400").build();
            else
                responseProto =NewUserResponseProto.newBuilder().setUsername(response.getUsername()).setStatus("Status 200").build();
        } catch (UserAlreadyExistsException e) {
            responseProto = NewUserResponseProto.newBuilder().setUsername("").setStatus("Status 400").build();
        }

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }

    @Override
    public void login(LoginProto request, StreamObserver<LoginResponseProto> responseObserver){
        LoginResponseProto responseProto;
        try {
            TokenDTO tokenDTO = authenticationService.login(request.getUsername(), request.getPassword());
            responseProto = LoginResponseProto.newBuilder().setJwt(tokenDTO.getJwt()).setRole(tokenDTO.getRole()).setStatus("Status 200").build();
        }catch(Exception ex){
            responseProto = LoginResponseProto.newBuilder().setJwt("").setRole("").setStatus("Status 400").build();
        }

        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }

}
