package com.auth.saga;

import com.auth.dto.RegisterDTO;
import com.auth.exception.WorkflowException;
import com.auth.model.Role;
import com.auth.model.User;
import com.auth.repository.UserRepository;
import com.auth.saga.dto.OrchestratorResponseDTO;
import com.auth.saga.workflow.Workflow;
import com.auth.saga.workflow.WorkflowStep;
import com.auth.saga.workflow.WorkflowStepStatus;
import com.auth.saga.workflow.impl.AuthWorkflowStep;
import com.auth.saga.workflow.impl.ProfileWorkflowStep;
import com.auth.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrchestratorService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    public Mono<OrchestratorResponseDTO> registerUser(final RegisterDTO requestDTO){
        Workflow orderWorkflow = this.getRegisterUserWorkflow(requestDTO);
        return Flux.fromStream(() -> orderWorkflow.getSteps().stream())
                .flatMap(WorkflowStep::process)
                .handle(((aBoolean, synchronousSink) -> {
                    if(aBoolean)
                        synchronousSink.next(true);
                    else
                        synchronousSink.error(new WorkflowException("register user failed!"));
                }))
                .then(Mono.fromCallable(() -> getResponseDTO(requestDTO, true, "")))
                .onErrorResume(ex -> this.revertRegistration(orderWorkflow, requestDTO));

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
        User user = new User(registerDTO.getUsername(), registerDTO.getPassword(), new Role());
        AuthWorkflowStep authWorkflowStep = new AuthWorkflowStep(user, userRepository);
        workflowSteps.add(authWorkflowStep);
        //ProfileWorkflowStep profileWorkflowStep = new ProfileWorkflowStep(profileClient,registerDTO);
        //workflowSteps.add(profileWorkflowStep);
        return new Workflow(workflowSteps);
    }

    private OrchestratorResponseDTO getResponseDTO(RegisterDTO registerDTO, boolean success, String message) {
        return new OrchestratorResponseDTO(registerDTO.getUsername(), success, message);
    }
}
