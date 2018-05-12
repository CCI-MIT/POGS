package edu.mit.cci.pogs.model.dao.session;

public enum TaskExecutionType {

    SEQUENTIAL_FIXED_ORDER('S', "Sequential, fixed order"),
    SEQUENTIAL_RANDOM_ORDER('Z', "Sequential, randomized order"),
    PARALLEL_FIXED_ORDER('P', "Parallel, fixed "),
    PARALLEL_RANDOM_ORDER('L', "Parallel, randomized");

    private Character taskExecutionTypeChar;
    private String taskExecutionType;

    TaskExecutionType(Character taskExecutionTypeChar, String taskExecutionType){
        this.taskExecutionType = taskExecutionType;
        this.taskExecutionTypeChar = taskExecutionTypeChar;
    }

    public Character getId(){
        return taskExecutionTypeChar;
    }
    public String getTaskExecutionType(){
        return taskExecutionType;
    }
}
