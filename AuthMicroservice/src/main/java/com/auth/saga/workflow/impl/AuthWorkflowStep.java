package com.auth.saga.workflow.impl;

import com.auth.model.User;
import com.auth.repository.UserRepository;
import com.auth.saga.workflow.WorkflowStep;
import com.auth.saga.workflow.WorkflowStepStatus;
import reactor.core.publisher.Mono;

public class AuthWorkflowStep implements WorkflowStep {

    private final User user;
    private final UserRepository userRepository;
    private WorkflowStepStatus status = WorkflowStepStatus.PENDING;

    public AuthWorkflowStep(User user, UserRepository userRepository) {
        this.user = user;
        this.userRepository = userRepository;
    }

    @Override
    public WorkflowStepStatus getStatus() {
        return status;
    }

    @Override
    public Mono<Boolean> process() {
        userRepository.save(user);
        this.status = WorkflowStepStatus.COMPLETE;
        return Mono.just(true);
    }

    @Override
    public Mono<Boolean> revert() {
        userRepository.delete(user);
        return Mono.just(true);
    }
}
