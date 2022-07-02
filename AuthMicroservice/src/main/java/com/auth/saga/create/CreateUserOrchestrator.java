package com.auth.saga.create;

import com.auth.dto.ConnectionsRegisterDTO;
import com.auth.dto.RegisterDTO;
import com.auth.exception.WorkflowException;
import com.auth.model.Role;
import com.auth.model.User;
import com.auth.saga.create.workflow.AuthCreateWorkflowStep;
import com.auth.saga.create.workflow.ConnectionsCreateWorkflowStep;
import com.auth.saga.create.workflow.ProfileCreateWorkflowStep;
import com.auth.saga.dto.OrchestratorResponseDTO;
import com.auth.saga.workflow.Workflow;
import com.auth.saga.workflow.WorkflowStep;
import com.auth.saga.workflow.WorkflowStepStatus;
import com.auth.service.RoleService;
import com.auth.service.UserService;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Nats;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateUserOrchestrator {

    private final UserService userService;

    private final RoleService roleService;

    private final WebClient profileClient;

    private final WebClient connectionsClient;

    private final PasswordEncoder passwordEncoder;


    public CreateUserOrchestrator(UserService userService, RoleService roleService, WebClient profileClient, WebClient connectionsClient, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.profileClient = profileClient;
        this.connectionsClient = connectionsClient;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(final RegisterDTO requestDTO) throws IOException, InterruptedException {
        //Workflow workflow = this.getRegisterUserWorkflow(requestDTO);
        // nats connection
        // it connects to nats://localhost:4222 by default
        Connection nats = Nats.connect();

        /*nats.request("nats.demo.service", "Hello NATS".getBytes())
                .thenApply(Message::getData)  // gets executed when we get response from receiver
                .thenApply(String::new)
                .thenAccept(s -> System.out.println("Response from Receiver: " + s));*/

        nats.publish("nats.demo.service",  "Hello NATS".getBytes());
    }

    private Mono<? extends OrchestratorResponseDTO> revertRegistration(Workflow workflow, RegisterDTO requestDTO) {
        return Flux.fromStream(() -> workflow.getSteps().stream())
                .filter(wf -> wf.getStatus() == WorkflowStepStatus.COMPLETE || wf.getStatus() == WorkflowStepStatus.START)
                .flatMap(WorkflowStep::revert)
                .retry(3)
                .then(Mono.just(getResponseDTO(requestDTO, false, "register user failed!")));
    }

    private Workflow getRegisterUserWorkflow(RegisterDTO registerDTO) {
        List<WorkflowStep> workflowSteps = new ArrayList<>();
        List<Role> roles = new ArrayList<>();
        roles.add(roleService.findByName("ROLE_USER"));

        User user = new User(registerDTO.getUuid(), registerDTO.getUsername(), registerDTO.getPassword(), roles);

        AuthCreateWorkflowStep authWorkflowStep = new AuthCreateWorkflowStep(user, userService, passwordEncoder);
        workflowSteps.add(authWorkflowStep);

        ProfileCreateWorkflowStep profileWorkflowStep = new ProfileCreateWorkflowStep(profileClient, registerDTO);
        workflowSteps.add(profileWorkflowStep);

        if (userService.findById(user.getId()) == null) {
            ConnectionsCreateWorkflowStep connectionsWorkflowStep = new ConnectionsCreateWorkflowStep(connectionsClient, new ConnectionsRegisterDTO(registerDTO.getUuid(), registerDTO.getUsername()));
            workflowSteps.add(connectionsWorkflowStep);
        }

        return new Workflow(workflowSteps);
    }

    private OrchestratorResponseDTO getResponseDTO(RegisterDTO registerDTO, boolean success, String message) {
        return new OrchestratorResponseDTO(registerDTO.getUuid(), success, message);
    }
}
