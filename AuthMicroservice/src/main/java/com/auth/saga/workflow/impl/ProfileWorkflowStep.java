package com.auth.saga.workflow.impl;

import com.auth.dto.RegisterDTO;
import com.auth.saga.dto.ProfileResponseDTO;
import com.auth.saga.workflow.WorkflowStep;
import com.auth.saga.workflow.WorkflowStepStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ProfileWorkflowStep implements WorkflowStep {

    private final WebClient webClient;
    private final RegisterDTO requestDTO;
    private ProfileResponseDTO profileResponseDTO;
    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;
    private boolean started = false;
    private final String uri = "api/v1/profiles";

    public ProfileWorkflowStep(WebClient webClient, RegisterDTO requestDTO) {
        this.webClient = webClient;
        this.requestDTO = requestDTO;
    }

    @Override
    public WorkflowStepStatus getStatus() {
        return stepStatus;
    }

    @Override
    public Mono<Boolean> process() {
        started = true;
        return this.webClient
                .post()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(requestDTO), RegisterDTO.class)
                .retrieve()
                .bodyToMono(ProfileResponseDTO.class)
                .doOnNext(val -> this.profileResponseDTO = val)
                //.bodyToMono(String.class)
                .map(ProfileResponseDTO::isSuccess)
                //.map(r -> true)
                .doOnNext(b -> this.stepStatus = b ? WorkflowStepStatus.COMPLETE : WorkflowStepStatus.FAILED);
    }

    @Override
    public Mono<Boolean> revert() {
        while(this.stepStatus == WorkflowStepStatus.PENDING && started){}
        return this.webClient
                .delete()
                .uri(uri + "/" + profileResponseDTO.getId())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                //.body(Mono.just(requestDTO), RegisterDTO.class)
                .retrieve()
                .bodyToMono(ProfileResponseDTO.class)
                //.bodyToMono(String.class)
                .map(ProfileResponseDTO::isSuccess)
                .map(r -> true);

    }
}
