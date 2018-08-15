package edu.mit.cci.pogs.messages;

public class CollaborationMessage extends PogsMessage<CollaborationMessageContent>  {

    public enum CollaborationType{
        TODO_LIST,
        VOTING_LIST
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
            for(TodoType tt : TodoType.values()){
                if(tt.name().equals(messageType)){
                    return tt;
                }
            }
            return null;
        }
    }
}

