'use strict';

const COLLABORATION_TYPE = {
    TODO_LIST: "TODO_LIST",
    VOTING_LIST: "VOTING_LIST"
}

const VOTING_TYPE = {
    CREATE_VOTING_POOL: "CREATE_VOTING_POOL",
    CAST_VOTE: "CAST_VOTE",
    DELETE_VOTING_POOL: "DELETE_VOTING_POOL",
    CREATE_OPTION: "CREATE_OPTION",
    DELETE_OPTION: "DELETE_OPTION"
};

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
        console.log("Init collab prlugin");

        if (this.pogsRef.hasCollaborationTodoListEnabled) {
            new TodoListManager(this);
            console.log("hasTodoFeature");
        }
        if (this.pogsRef.hasCollaborationVotingWidget) {
            console.log("HasCollaboratonVoting");
            new VotingPoolManager(this);
        }
        if (this.pogsRef.hasCollaborationFeedbackWidget) {
            console.log("HasCollaboratonVoting");
        }

    }

    subscribeCollaborationBroadcast(funct) {
        this.pogsRef.subscribe('collaborationMessage', funct);
    }

    sendMessage(message, messageType, collaborationType) {

        var messageContent = {
            message: message,
            collaborationType: collaborationType,
            messageType: messageType
        };
        console.log()
        this.pogsRef.sendMessage("/pogsapp/collaboration.sendMessage", "COLLABORATION_MESSAGE",
                                 messageContent,
                                 this.getSubjectId(), null, this.getCompletedTaskId(),
                                 this.getSessionId());
    }
}
