package com.auth.saga;

import com.auth.dto.RegisterDTO;
import com.auth.exception.WorkflowException;
import com.auth.model.User;
import com.auth.saga.dto.OrchestratorResponseDTO;
import com.auth.saga.workflow.Workflow;
import com.auth.saga.workflow.WorkflowStep;
import com.auth.saga.workflow.WorkflowStepStatus;
import com.auth.saga.workflow.impl.AuthWorkflowStep;
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

    public OrchestratorService(UserService userService, WebClient profileClient) {
        this.userService = userService;
        this.profileClient = profileClient;
    }

    public Mono<OrchestratorResponseDTO> registerUser(final RegisterDTO requestDTO){
        Workflow workflow = this.getRegisterUserWorkflow(requestDTO);
        Mono<OrchestratorResponseDTO> f = Flux.fromStream(() -> workflow.getSteps().stream())
                .flatMap(WorkflowStep::process)
                .handle(((aBoolean, synchronousSink) -> {
                    if(aBoolean)
                        synchronousSink.next(true);
                    else
                        synchronousSink.error(new WorkflowException("register user failed!"));
                }))
                .then(Mono.fromCallable(() -> getResponseDTO(requestDTO, true, "")))
                .onErrorResume(ex -> this.revertRegistration(workflow, requestDTO));
        f.subscribe();
        return f;
    }

    private Mono<? extends OrchestratorResponseDTO> revertRegistration(Workflow workflow, RegisterDTO requestDTO) {
        return Flux.fromStream(() -> workflow.getSteps().stream())
                .filter(wf -> wf.getStatus().equals(WorkflowStepStatus.COMPLETE))
                .flatMap(WorkflowStep::revert)
                .retry(3)
                .then(Mono.just(this.getResponseDTO(requestDTO, false, "register user failed!")));
    }

    private Workflow getRegisterUserWorkflow(RegisterDTO registerDTO) {
        List<WorkflowStep> workflowSteps = new ArrayList<>();
        User user = new User(registerDTO.getUsername(), registerDTO.getPassword(), null);
        AuthWorkflowStep authWorkflowStep = new AuthWorkflowStep(user, userService);
        workflowSteps.add(authWorkflowStep);
        ProfileWorkflowStep profileWorkflowStep = new ProfileWorkflowStep(profileClient, registerDTO);
        workflowSteps.add(profileWorkflowStep);

        return new Workflow(workflowSteps);
    }

    private OrchestratorResponseDTO getResponseDTO(RegisterDTO registerDTO, boolean success, String message) {
        return new OrchestratorResponseDTO(registerDTO.getUsername(), success, message);
    }
}
