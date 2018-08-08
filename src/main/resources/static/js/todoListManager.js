'use strict';

class TodoListManager {
    constructor(pogsColaborationPlugin) {
        this.pogsColaborationPlugin = pogsColaborationPlugin;
        this.setupHTML();
        this.pogsColaborationPlugin.subscribeCollaborationBroadcast(this.onTodoBroadcastReceived.bind(this));
    }

    setupHTML() {
        this.createInitialHTML();
        $("#createTodoItem").click(this.createTodoItem.bind(this));
        $("#selfAssignTodoItem").click(this.selfAssignTodoItem.bind(this));
        $("#unassignTodoItem").click(this.unassignTodoItem.bind(this));
        $("#deleteTodoItem").click(this.deleteTodoItem.bind(this));
        $("#markDoneTodoItem").click(this.markDoneTodoItem.bind(this));
        $("#markUndoneTodoItem").click(this.markUndoneTodoItem.bind(this));
    }

    createTodoItem(promptEntry) {
        var todoText = prompt("Please describe the task (Max. 25 characters).", (promptEntry == '') ? ('') : (promptEntry));

        if (todoText.length >= 26) {
            alert("Task description should be less than 25 characters.")
            this.createTodoItem(todoText);
            return;
        }
        if (todoText != null) {
            this.createTodoEntry(todoText);
        }
    }

    createTodoEntry(todoText) {
        this.pogsColaborationPlugin.sendMessage(todoText, TODO_TYPE.CREATE_TODO, COLLABORATION_TYPE.TODO_LIST);
    }

    selfAssignTodoItem(todoTaskId) {
        if (todoTaskId == null) {
            alert("Todo item is not selected!");
            return;
        }
        var r = confirm("Do you want to assign yourself to this task?");
        if (r == true) {
            this.selfAssignTodoEntry(todoTaskId)
        }
    }

    selfAssignTodoEntry(todoTaskId) {
        this.pogsColaborationPlugin.sendMessage(todoTaskId, TODO_TYPE.ASSIGN_ME, COLLABORATION_TYPE.TODO_LIST);
    }

    unassignTodoItem(todoTaskId) {
        if (todoTaskId == null) {
            alert("Todo item is not selected!");
            return;
        }
        var r = confirm("Do you want to unassign yourself from this task?");
        if (r == true) {
            unssignTodoEntry(todoTaskId)
        }
    }

    unssignTodoEntry(todoTaskId) {
        this.pogsColaborationPlugin.sendMessage(todoTaskId, TODO_TYPE.UNASSIGN_ME, COLLABORATION_TYPE.TODO_LIST);
    }

    deleteTodoItem(todoTaskId) {
        if (todoTaskId == null) {
            alert("Todo item is not selected!");
            return;
        }
        var r = confirm("Do you want to delete this task?");
        if (r == true) {
            deleteTodoEntry(todoTaskId)
        }
    }

    deleteTodoEntry(todoTaskId) {
        this.pogsColaborationPlugin.sendMessage(todoTaskId, TODO_TYPE.DELETE_TODO, COLLABORATION_TYPE.TODO_LIST);
    }

    markDoneTodoItem(todoTaskId) {
        if (todoTaskId == null) {
            alert("Todo item is not selected!");
            return;
        }
        var r = confirm("Do you want to mark this task as done?");
        if (r == true) {
            markDoneTodoEntry(todoTaskId)
        }
    }

    markDoneTodoEntry(todoTaskId) {
        this.pogsColaborationPlugin.sendMessage(todoTaskId, TODO_TYPE.MARK_DONE, COLLABORATION_TYPE.TODO_LIST);
    }

    markUndoneTodoItem(todoTaskId) {
        if (todoTaskId == null) {
            alert("Todo item is not selected!");
            return;
        }
        var r = confirm("Do you want to mark this task as not done?");
        if (r == true) {
            markUndoneTodoEntry(todoTaskId)
        }
    }

    markUndoneTodoEntry(todoTaskId) {
        this.pogsColaborationPlugin.sendMessage(todoTaskId, TODO_TYPE.MARK_UNDONE, COLLABORATION_TYPE.TODO_LIST);
    }

    onTodoBroadcastReceived(message) {
        var todoEntries = message.content.todoEntries;
        for (var i = 0; i < todoEntries.length; i++) {
            if (!todoEntries[i].currentAssigned) {
                if (!todoEntries[i].markDone) {
                    var todoItem = '<i class="badge badge-primary">\n'
                        + '        <span> ' + todoEntries[i].text + '</span>\n'
                        + '        <span class="badge badge-assignme" id = "selfAssignTodoItem" data-todoid="' + todoEntries[i].todo_entry_id + '"></span>\n'
                        + '        <i class="fa fa-trash-alt" id="deleteTodoItem" data-todoid="' + todoEntries[i].todo_entry_id + '"></i>\n'
                        + '    </span>';
                    $("#todoListContainer").append(todoItem);
                } else {
                    var todoItem = '<i class="badge badge-primary">\n'
                        + '        <i class="fas fa-check-square" id="markUndoneTodoItem" data-todoid="' + todoEntries[i].todo_entry_id + '"></i>\n'
                        + '        <span> ' + todoEntries[i].text + '</span>\n'
                        + '        <span class="badge badge-assignme" id="selfAssignTodoItem" data-todoid="' + todoEntries[i].todo_entry_id + '"></span>\n'
                        + '    </span>';
                    $("#todoListContainer").append(todoItem);
                }
            } else if (todoEntries[i].currentAssigned == this.pogsColaborationPlugin.getSubjectId()){
                if (!todoEntries[i].markDone) {

                    var todoItem = '<i class="badge badge-primary">\n'
                        + '        <i class="far fa-square" id="markDoneTodoItem" data-todoid="' + todoEntries[i].todo_entry_id + '"></i>\n'
                        + '        <span> ' + todoEntries[i].text + '</span>\n'
                        + '        <span class="badge badge-success">Me</span>\n'
                        + '       <i class="fas fa-times-circle" id ="unassignTodoItem" data-todoid="' + todoEntries[i].todo_entry_id + '"></i>\n'
                        + '        <i class="fa fa-trash-alt" id="deleteTodoItem" data-todoid="' + todoEntries[i].todo_entry_id + '"></i>\n'
                        + '    </span>';
                    $("#todoListContainer").append(todoItem);
                } else {
                    var todoItem = '<i class="badge badge-primary">\n'
                        + '        <i class="fas fa-check-square" id="markUndoneTodoItem" data-todoid="' + todoEntries[i].todo_entry_id + '"></i>\n'
                        + '        <span> ' + todoEntries[i].text + '</span>\n'
                        + '        <span class="badge badge-success">Me</span>\n'
                        + '       <span class="badge badge-success">' + todoEntries[i].subjectId + '</span>\n'
                        + '       <i class="fas fa-times-circle" id ="unassignTodoItem" data-todoid="' + todoEntries[i].todo_entry_id + '"></i>\n'
                        + '    </span>';
                    $("#todoListContainer").append(todoItem);
                }
            } else{
                if (!todoEntries[i].markDone) {
                    var todoItem = '<i class="badge badge-primary">\n'
                        + '        <i class="far fa-square" id="markDoneTodoItem" data-todoid="' + todoEntries[i].todo_entry_id + '"></i>\n'
                        + '        <span> ' + todoEntries[i].text + '</span>\n'
                        + '        <span class="badge badge-success">'+ this.pogsColaborationPlugin.getSubjectByExternalId(todoEntries[i].externalId)+'</span>\n'
                        + '        <i class="fa fa-trash-alt" id="deleteTodoItem" data-todoid="' + todoEntries[i].todo_entry_id + '"></i>\n'
                        + '    </span>';
                    $("#todoListContainer").append(todoItem);
                } else {
                    var todoItem = '<i class="badge badge-primary">\n'
                        + '        <i class="fas fa-check-square" id="markUndoneTodoItem" data-todoid="' + todoEntries[i].todo_entry_id + '"></i>\n'
                        + '        <span> ' + todoEntries[i].text + '</span>\n'
                        + '        <span class="badge badge-success">'+ this.pogsColaborationPlugin.getSubjectByExternalId(todoEntries[i].externalId)+'</span>\n'
                        + '        <i class="fa fa-trash-alt" id="deleteTodoItem" data-todoid="' + todoEntries[i].todo_entry_id + '"></i>\n'
                        + '    </span>';
                    $("#todoListContainer").append(todoItem);
                }
            }
        }
    }


    createInitialHTML() {
        return '<div id="todoListContainer">\n' +
            '        <button id = "createTodoItem">Create Todo Entry</button></div>';
    }

}


/*
       LAYOUT -

      SEND (CLIENT SIDE) AFTER TRIGGER

      - create new
        - ask user for the message
        - if the message is not empty
          - send te event (CREATE_TODO, todoText)
       - assign
        - ask the user to confim
          - send te event (ASSIGN_ME, todoItemId)
       - unassign
        - ask the user to confim
          - send te event (UNASSIGN_ME, todoItemId)
       - delete
        - ask the user to confim
          - send te event (DELETE_TODO, todoItemId)
       - mark as done
        - ask the user to confim
          - send te event (MARK_DONE, todoItemId)
       - marked as undone
        - ask the user to confim
          - send te event (MARK_UNDONE, todoItemId)

        RECEIVE (FROM SERVER)

         - TODO_ITEMS_BROADCAST
           -
      */