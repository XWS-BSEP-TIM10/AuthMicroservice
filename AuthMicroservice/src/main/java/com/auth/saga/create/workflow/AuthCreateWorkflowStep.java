package com.auth.saga.create.workflow;

import com.auth.model.User;
import com.auth.saga.workflow.WorkflowStep;
import com.auth.saga.workflow.WorkflowStepStatus;
import com.auth.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

public class AuthCreateWorkflowStep implements WorkflowStep {

    private final User user;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private WorkflowStepStatus status = WorkflowStepStatus.PENDING;

    public AuthCreateWorkflowStep(User user, UserService userService, PasswordEncoder passwordEncoder) {
        this.user = user;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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
