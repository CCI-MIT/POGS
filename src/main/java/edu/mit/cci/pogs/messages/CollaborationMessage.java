package edu.mit.cci.pogs.messages;

import org.jooq.tools.json.JSONObject;

public class CollaborationMessage extends PogsMessage<CollaborationMessageContent> {


    public enum CollaborationType {
        TODO_LIST,
        VOTING_LIST,
        FEEDBACK_BAR
    }

    public enum TodoType {
        CREATE_TODO,
        ASSIGN_ME,
        UNASSIGN_ME,
        DELETE_TODO,
        MARK_DONE,
        MARK_UNDONE,
        BROADCAST_TODO_ITEMS;

        public static TodoType getType(String messageType) {
            for (TodoType tt : TodoType.values()) {
                if (tt.name().equals(messageType)) {
                    return tt;
                }
            }
            return null;
        }
    }

    public enum VotingPoolType {
        BROADCAST_VOTING_POOLS,
        CREATE_VOTING_POOL,
        CAST_VOTE,
        DELETE_VOTING_POOL,
        CREATE_OPTION,
        DELETE_OPTION;

        public static VotingPoolType getType(String messageType) {
            for (VotingPoolType tt : VotingPoolType.values()) {
                if (tt.name().equals(messageType)) {
                    return tt;
                }
            }
            return null;
        }
    }
}

