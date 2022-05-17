package com.auth.grpc;

import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import com.auth.dto.NewUserDTO;
import com.auth.exception.UserAlreadyExistsException;
import com.auth.model.User;
import com.auth.saga.dto.OrchestratorResponseDTO;
import com.auth.service.impl.CustomUserDetailsService;

import io.grpc.stub.StreamObserver;
import proto.NewUserProto;
import proto.NewUserResponseProto;
import proto.RoleProto;
import proto.UserDetailsGrpcServiceGrpc;
import proto.UserDetailsProto;
import proto.UserDetailsResponseProto;

@GrpcService
public class UserDetailsService extends UserDetailsGrpcServiceGrpc.UserDetailsGrpcServiceImplBase{

	private final CustomUserDetailsService customUserDetailsService;
	
	@Autowired
	public UserDetailsService(CustomUserDetailsService customUserDetailsService) {
		this.customUserDetailsService = customUserDetailsService;
	}
	
	@Override
    public void getUserDetails(UserDetailsProto request, StreamObserver<UserDetailsResponseProto> responseObserver){
		UserDetailsResponseProto responseProto;
        
		User userTemp = (User) customUserDetailsService.loadUserByUsername(request.getUsername());
		RoleProto roleProto = RoleProto.newBuilder().setId(userTemp.getRole().getId()).setName(userTemp.getRole().getName()).build();
		responseProto = UserDetailsResponseProto.newBuilder().setId(userTemp.getId()).setUsername(userTemp.getUsername()).setPassword(userTemp.getPassword()).setRole(roleProto).build();
        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }
}
