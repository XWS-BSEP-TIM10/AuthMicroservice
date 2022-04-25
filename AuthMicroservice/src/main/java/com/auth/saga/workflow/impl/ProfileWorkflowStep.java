package com.auth.saga.workflow.impl;

import com.auth.dto.RegisterDTO;
import com.auth.saga.dto.ProfileResponseDTO;
import com.auth.saga.workflow.WorkflowStep;
import com.auth.saga.workflow.WorkflowStepStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ProfileWorkflowStep implements WorkflowStep {

    private final WebClient webClient;
    private final RegisterDTO requestDTO;
    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;

    public ProfileWorkflowStep(WebClient webClient, RegisterDTO requestDTO) {
        this.webClient = webClient;
        this.requestDTO = requestDTO;
    }

    @Override
    public WorkflowStepStatus getStatus() {
        return null;
    }

    @Override
    public Mono<Boolean> process() {
        return this.webClient
                .get()
                .uri("")
                //.body(BodyInserters.fromValue(this.requestDTO))
                .retrieve()
                //.bodyToMono(ProfileResponseDTO.class)
                .bodyToMono(String.class)
                //.map(r -> r.isSuccess())
                .map(r -> true)
                .doOnNext(b -> this.stepStatus = b ? WorkflowStepStatus.COMPLETE : WorkflowStepStatus.FAILED);
    }

    @Override
    public Mono<Boolean> revert() {
        return null;
    }
}
