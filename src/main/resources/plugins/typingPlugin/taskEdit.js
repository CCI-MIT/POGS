class TypingTaskEdit {

    init(taskConfigId, currentAttributes){
        this.taskConfigId = taskConfigId;
    }

    beforeSubmit() {
        //save attributes
    }
}

pogsTaskConfigEditor.register(new TypingTaskEdit());
