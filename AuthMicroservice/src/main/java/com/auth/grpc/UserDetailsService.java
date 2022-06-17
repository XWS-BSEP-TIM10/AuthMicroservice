package com.auth.grpc;

import com.auth.model.Role;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.auth.model.Permission;
import com.auth.model.User;
import com.auth.service.impl.CustomUserDetailsService;

import io.grpc.stub.StreamObserver;
import proto.PermissionProto;
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
        
		User userTemp = (User) customUserDetailsService.loadUserById(request.getUsername());
		List<RoleProto> roles = new ArrayList<>();
		for(Role role : userTemp.getRoles()) {
			List<PermissionProto> permissionsProtos = new ArrayList<>();
			for (Permission perm : role.getPermission()) {
				permissionsProtos.add(PermissionProto.newBuilder().setId(perm.getId()).setName(perm.getName()).build());
			}
			roles.add(RoleProto.newBuilder().setId(role.getId()).setName(role.getName()).addAllPermissions(permissionsProtos).build());
		}

		responseProto = UserDetailsResponseProto.newBuilder().setId(userTemp.getId()).setUsername(userTemp.getUsername()).setPassword(userTemp.getPassword()).addAllRole(roles).build();
        responseObserver.onNext(responseProto);
        responseObserver.onCompleted();
    }
}
