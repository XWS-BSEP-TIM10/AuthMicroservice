package com.auth.saga;

import com.auth.dto.ConnectionsRegisterDTO;
import com.auth.dto.RegisterDTO;
import com.auth.exception.WorkflowException;
import com.auth.model.User;
import com.auth.saga.dto.OrchestratorResponseDTO;
import com.auth.saga.workflow.Workflow;
import com.auth.saga.workflow.WorkflowStep;
import com.auth.saga.workflow.WorkflowStepStatus;
import com.auth.saga.workflow.impl.AuthWorkflowStep;
import com.auth.saga.workflow.impl.ConnectionsWorkflowStep;
import com.auth.saga.workflow.impl.ProfileWorkflowStep;
import com.auth.service.UserService;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class OrchestratorService {

    private final UserService userService;

    private final WebClient profileClient;

    private final WebClient connectionsClient;

    public OrchestratorService(UserService userService, WebClient profileClient, WebClient connectionsClient) {
        this.userService = userService;
        this.profileClient = profileClient;
        this.connectionsClient = connectionsClient;
    }

    public Mono<OrchestratorResponseDTO> registerUser(final RegisterDTO requestDTO) {
        Workflow workflow = this.getRegisterUserWorkflow(requestDTO);
        return Flux.fromStream(() -> workflow.getSteps().stream())
                .flatMap(WorkflowStep::process)
                .handle(((aBoolean, synchronousSink) -> {
                    if (aBoolean)
                        synchronousSink.next(true);
                    else
                        synchronousSink.error(new WorkflowException("register user failed!"));
                }))
                .then(Mono.fromCallable(() -> getResponseDTO(requestDTO, true, "")))
                .onErrorResume(ex -> this.revertRegistration(workflow, requestDTO));
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

        User user = new User(registerDTO.getUsername(), registerDTO.getPassword(), null);

        AuthWorkflowStep authWorkflowStep = new AuthWorkflowStep(user, userService);
        workflowSteps.add(authWorkflowStep);

        ProfileWorkflowStep profileWorkflowStep = new ProfileWorkflowStep(profileClient, registerDTO);
        workflowSteps.add(profileWorkflowStep);

        ConnectionsWorkflowStep connectionsWorkflowStep = new ConnectionsWorkflowStep(connectionsClient, new ConnectionsRegisterDTO(registerDTO.getUsername()));
        workflowSteps.add(connectionsWorkflowStep);

        return new Workflow(workflowSteps);
    }

    private OrchestratorResponseDTO getResponseDTO(RegisterDTO registerDTO, boolean success, String message) {
        return new OrchestratorResponseDTO(registerDTO.getUsername(), success, message);
    }
}
