package com.auth.saga.workflow;

import java.util.List;

public class Workflow {

    private List<WorkflowStep> steps;

    public Workflow(List<WorkflowStep> steps) {
        this.steps = steps;
    }

    public List<WorkflowStep> getSteps() {
        return steps;
    }

    public void setSteps(List<WorkflowStep> steps) {
        this.steps = steps;
    }
}
