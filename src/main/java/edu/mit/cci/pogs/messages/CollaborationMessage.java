package edu.mit.cci.pogs.messages;

public class CollaborationMessage extends PogsMessage<TodoListMessageContent>  {

    public enum TodoType {
        CREATE_TODO,
        ASSIGN_ME,
        UNASSIGN_ME,
        DELETE_TODO,
        MARK_DONE,
        MARK_UNDONE
    }
}

