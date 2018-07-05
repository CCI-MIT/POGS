'use strict';

const TODO_TYPE = {

    CREATE_TODO: "CREATE_TODO",
    ASSIGN_ME: "ASSIGN_ME",
    UNASSIGN_ME: "UNASSIGN_ME",
    DELETE_TODO: "DELETE_TODO",
    MARK_DONE: "MARK_DONE",
    MARK_UNDONE: "MARK_UNDONE"

};

class CollaborationPlugin extends PogsPlugin {
    constructor(pogsRef) {
        super('collaborationPlugin', null, pogsRef);
        this.initFunc = this.init;
    }

    init() {
        console.log("Init");

        if (this.pogsRef.hasCollaborationTodoListEnabled) {
            new TodoListManager(this);
        }
        if (this.pogsRef.hasCollaborationVotingWidget) {
            console.log("HasCollaboratonVoting");
        }
        if (this.pogsRef.hasCollaborationFeedbackWidget) {
            console.log("HasCollaboratonVoting");
        }

    }

    subscribeCollaborationBroadcast(funct) {
        this.pogsRef.subscribe('collaborationMessage', funct);
    }

    sendMessage(message, type) {

        var messageContent = {
            message: message,
            type: type
        };

        this.pogsRef.sendMessage("/pogsapp/collaboration.sendMessage", "COLLABORATION_MESSAGE",
                                 messageContent,
                                 this.getSubjectId(), null, this.getCompletedTaskId(),
                                 this.getSessionId());
    }
}
