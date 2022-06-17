package com.auth.saga.create.workflow;

import com.auth.dto.ConnectionsRegisterDTO;
import com.auth.dto.RegisterDTO;
import com.auth.saga.dto.SagaResponseDTO;
import com.auth.saga.workflow.WorkflowStep;
import com.auth.saga.workflow.WorkflowStepStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ConnectionsCreateWorkflowStep implements WorkflowStep {

    private final WebClient webClient;
    private final ConnectionsRegisterDTO requestDTO;
    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;

    private static final String uri = "/api/v1/users";

    public ConnectionsCreateWorkflowStep(WebClient webClient, ConnectionsRegisterDTO requestDTO) {
        this.webClient = webClient;
        this.requestDTO = requestDTO;
    }


    @Override
    public WorkflowStepStatus getStatus() {
        return stepStatus;
    }

    @Override
    public Mono<Boolean> process() {
        stepStatus = WorkflowStepStatus.START;
        return webClient
                .post()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(requestDTO), RegisterDTO.class)
                .retrieve()
                .bodyToMono(SagaResponseDTO.class)
                .map(SagaResponseDTO::isSuccess)
                .doOnNext(b -> this.stepStatus = b ? WorkflowStepStatus.COMPLETE : WorkflowStepStatus.FAILED);
    }

    @Override
    public Mono<Boolean> revert() {
        if (this.stepStatus == WorkflowStepStatus.FAILED)
            return Mono.just(true);

        return webClient
                .delete()
                .uri(uri + "/" + requestDTO.getId())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(SagaResponseDTO.class)
                .map(SagaResponseDTO::isSuccess)
                .map(r -> true);
    }
}
