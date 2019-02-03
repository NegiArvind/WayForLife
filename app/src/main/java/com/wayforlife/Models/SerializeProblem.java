package com.wayforlife.Models;

import java.io.Serializable;

public class SerializeProblem implements Serializable {

    public transient Problem problem;

    public SerializeProblem(Problem problem) {
        this.problem = problem;
    }

    public SerializeProblem() {
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }
}
