package com.auth.saga.create.workflow;

import com.auth.model.User;
import com.auth.saga.workflow.WorkflowStep;
import com.auth.saga.workflow.WorkflowStepStatus;
import com.auth.service.UserService;
import reactor.core.publisher.Mono;

public class AuthCreateWorkflowStep implements WorkflowStep {

    private final User user;
    private final UserService userService;
    private WorkflowStepStatus status = WorkflowStepStatus.PENDING;

    public AuthCreateWorkflowStep(User user, UserService userService) {
        this.user = user;
        this.userService = userService;
    }

    @Override
    public WorkflowStepStatus getStatus() {
        return status;
    }

    @Override
    public Mono<Boolean> process() {
        status = WorkflowStepStatus.START;
        if (userService.userExists(user.getUsername())) {
            return Mono.just(false);
        }
        userService.save(user);
        this.status = WorkflowStepStatus.COMPLETE;

        return Mono.just(true);
    }

    @Override
    public Mono<Boolean> revert() {
        userService.delete(user);
        return Mono.just(true);
    }
}
