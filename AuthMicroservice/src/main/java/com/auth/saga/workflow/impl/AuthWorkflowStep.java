package com.auth.saga.workflow.impl;

import com.auth.model.User;
import com.auth.saga.workflow.WorkflowStep;
import com.auth.saga.workflow.WorkflowStepStatus;
import com.auth.service.UserService;
import reactor.core.publisher.Mono;

public class AuthWorkflowStep implements WorkflowStep {

    private final User user;
    private final UserService userService;
    private WorkflowStepStatus status = WorkflowStepStatus.PENDING;

    public AuthWorkflowStep(User user, UserService userService) {
        this.user = user;
        this.userService = userService;
    }

    @Override
    public WorkflowStepStatus getStatus() {
        return status;
    }

    @Override
    public Mono<Boolean> process() {
        if (userService.userExists(user.getUsername())) {
            return Mono.just(false);
        }
        userService.save(user);
        this.status = WorkflowStepStatus.COMPLETE;
//        try {
//            Thread.sleep(3000);
//        } catch (Exception e) {
//        }
        return Mono.just(true);
    }

    @Override
    public Mono<Boolean> revert() {
        userService.delete(user);
        return Mono.just(true);
    }
}
