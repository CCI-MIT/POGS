'use strict';

class TodoListManager {
    constructor(pogsCollaborationPlugin) {
        this.pogsCollaborationPlugin = pogsCollaborationPlugin;
        this.resetHTMLHooks();
        this.pogsCollaborationPlugin.subscribeCollaborationBroadcast(
            this.onTodoBroadcastReceived.bind(this));
    }

    resetHTMLHooks() {

        $("#createTodoItem").unbind().click(this.createTodoItem.bind(this));

        $(".assignTodoItem").unbind().click(this.selfAssignTodoItem.bind(this));
        $(".unassignTodoItem").unbind().click(this.unassignTodoItem.bind(this));
        $(".deleteTodoItem").unbind().click(this.deleteTodoItem.bind(this));
        $(".markDoneTodoItem").unbind().click(this.markDoneTodoItem.bind(this));
        //$(".markUndoneTodoItem").unbind().click(this.markUndoneTodoItem.bind(this));
    }

    createTodoItem(promptEntry) {
        if (typeof promptEntry != 'string') {
            promptEntry = "";
        }
        var todoText = prompt("Please describe the task (Max. 25 characters).", promptEntry);

        if (todoText.length >= 26) {
            alert("Task description should be less than 25 characters.")
            return;
        }
        if (todoText != null) {
            this.createTodoEntry(todoText);
        }
    }

    createTodoEntry(todoText) {
        this.pogsCollaborationPlugin.sendMessage(todoText, TODO_TYPE.CREATE_TODO,
                                                 COLLABORATION_TYPE.TODO_LIST);
        console.log("Sending todo text message" + todoText)
    }

    selfAssignTodoItem(event) {

        var todoTaskId = $(event.target).data("todoid");
        console.log("Asked to assign to : " + todoTaskId);
        var r = confirm("Do you want to assign yourself to this task?");
        if (r == true) {
            this.selfAssignTodoEntry(todoTaskId)
        }
        event.stopPropagation();
    }

    selfAssignTodoEntry(todoTaskId) {
        this.pogsCollaborationPlugin.sendMessage(todoTaskId, TODO_TYPE.ASSIGN_ME,
                                                 COLLABORATION_TYPE.TODO_LIST);
    }

    unassignTodoItem(event) {
        var todoTaskId = $(event.target).data("todoid");

        var r = confirm("Do you want to unassign yourself from this task?");
        if (r == true) {
            this.unssignTodoEntry(todoTaskId)
        }
        event.stopPropagation();
    }

    unssignTodoEntry(todoTaskId) {
        this.pogsCollaborationPlugin.sendMessage(todoTaskId, TODO_TYPE.UNASSIGN_ME,
                                                 COLLABORATION_TYPE.TODO_LIST);
    }

    deleteTodoItem(event) {

        var todoTaskId = $(event.target).data("todoid");

        var r = confirm("Do you want to delete this task?");
        if (r == true) {
            this.deleteTodoEntry(todoTaskId)
        }
        event.stopPropagation();
    }

    deleteTodoEntry(todoTaskId) {
        this.pogsCollaborationPlugin.sendMessage(todoTaskId, TODO_TYPE.DELETE_TODO,
                                                 COLLABORATION_TYPE.TODO_LIST);
    }

    markDoneTodoItem(event) {
        var todoTaskId = $(event.target).data("todoid");

        if ($(event.target).hasClass("fa-square")) {

            var r = confirm("Do you want to mark this task as done?");
            if (r == true) {
                this.markDoneTodoEntry(todoTaskId)
            }
        } else {
            var r = confirm("Do you want to mark this task as not done?");
            if (r == true) {
                this.markUndoneTodoEntry(todoTaskId)
            }
        }
        event.stopPropagation();
    }

    markDoneTodoEntry(todoTaskId) {
        this.pogsCollaborationPlugin.sendMessage(todoTaskId, TODO_TYPE.MARK_DONE,
                                                 COLLABORATION_TYPE.TODO_LIST);
    }

    markUndoneTodoEntry(todoTaskId) {
        this.pogsCollaborationPlugin.sendMessage(todoTaskId, TODO_TYPE.MARK_UNDONE,
                                                 COLLABORATION_TYPE.TODO_LIST);
    }

    removeAssignMe(entryId) {
        $("#" + entryId + "_assignMe").remove();
    }

    addAssignMe(entryId) {
        var todoItem = "";
        if ($("#" + entryId + "_assignMe").length) {
            return;
        } else {
            todoItem +=
                '        <span class="badge assignTodoItem badge-pill badge-info" id="' + entryId
                + '_assignMe" data-todoid="'
                + entryId + '">Assign me</span>\n';

            $(todoItem).appendTo("#entry_" + entryId + " .assignMeContainer");
        }
    }

    isCurrentUserAssigned(entryId) {
        var extId = this.pogsCollaborationPlugin.getSubjectId();
        return ($('#' + entryId + '_' + extId + '_assign').length);
    }

    removeAssignee(assigneeName, entryId) {
        $('#' + entryId + '_' + assigneeName + '_assign').remove();
    }

    addAssignee(assigneeName, entryId) {

        var todoItem = "";
        var extId = this.pogsCollaborationPlugin.getSubjectId();

        if ($('#' + entryId + '_' + assigneeName + '_assign').length) {
            return;
        }
        if (assigneeName == extId) {

            todoItem +=
                '        <span class="badge badge-success assigneePill" id="' + entryId + '_'
                + assigneeName + '_assign">' +
                this.pogsCollaborationPlugin.getSubjectByExternalId(
                    assigneeName).displayName;
            todoItem += '       <i class="fa fa-times-circle unassignTodoItem"'
                        + 'data-todoid="' + entryId + '"></i></span>\n';
        } else {
            todoItem +=
                '        <span class="badge badge-success assigneePill" id="' + entryId + '_'
                + assigneeName + '_assign">' +
                this.pogsCollaborationPlugin.getSubjectByExternalId(assigneeName).displayName
                + '</span>\n';
        }

        $(todoItem).appendTo("#entry_" + entryId + " .assgineeContainer");
    }

    markAsDone(entryId) {
        $('#entry_' + entryId).removeClass("badge-primary");
        $('#entry_' + entryId).addClass("badge-light");
        $('#entry_' + entryId + ' .markDoneTodoItem').removeClass("fa-square");
        $('#entry_' + entryId + ' .markDoneTodoItem').addClass("fa-check-square");
        $('#entry_' + entryId + ' .markDoneTodoItem').addClass("itemIsDone");
        $('#entry_' + entryId + ' .unassignTodoItem').hide();
        $('#entry_' + entryId + ' .deleteTodoItem').hide();

        //disable unnasign buttons
    }

    markAsUnDone(entryId) {
        $('#entry_' + entryId).removeClass("badge-light");
        $('#entry_' + entryId).addClass("badge-primary");
        $('#entry_' + entryId + ' .markDoneTodoItem').addClass("fa-square");
        $('#entry_' + entryId + ' .markDoneTodoItem').removeClass("fa-check-square");
        $('#entry_' + entryId + ' .markDoneTodoItem').addClass("itemIsNotDone");
        $('#entry_' + entryId + ' .unassignTodoItem').show();
        $('#entry_' + entryId + ' .deleteTodoItem').show();
        //disable unnasign buttons
    }

    removeTodo(entryId) {
        $('#entry_' + entryId).remove();
    }

    createNewTodoItem(text, entryId) {

        if ($('#entry_' + entryId).length > 0) {
            return;
        }

        var todoItem = '<span class="badge badge-primary todoEntry" id="entry_' + entryId + '">\n';
        todoItem += '<i class="fa fa-square markDoneTodoItem " '
                    + 'data-todoid="' + entryId + '"></i>'
                    + '        <span> ' + text + '</span>'
                    + '<span class="assgineeContainer"></span>'
                    + '<span class="assignMeContainer"></span>\n';

        todoItem += '        <i class="fa fa-trash deleteTodoItem"  '
                    + 'data-todoid="' + entryId + '"></i>\n'
                    + '    </span>';
        $("#todoListContainer").append(todoItem);
    }

    onTodoBroadcastReceived(message) {
        if(message.content.collaborationType != COLLABORATION_TYPE.TODO_LIST){
            return;
        }
        var extId = this.pogsCollaborationPlugin.getSubjectId();

        if (message.content.triggeredBy == TODO_TYPE.DELETE_TODO ||
            message.content.triggeredBy == TODO_TYPE.UNASSIGN_ME) {

            if (message.content.triggeredBy == TODO_TYPE.UNASSIGN_ME) {
                var entryId = message.content.triggeredData;
                var extId = message.content.triggeredData2;
                //self-unnassing
                this.removeAssignee(extId, entryId)
                if (!this.isCurrentUserAssigned(entryId)) {
                    this.addAssignMe(entryId);
                } else {
                    this.removeAssignMe(entryId);
                }
            } else {
                //delete todo
                var entryId = message.content.triggeredData;
                this.removeTodo(entryId);
            }
        } else {

            var todoEntries = message.content.todoEntries;
            for (var i = 0; i < todoEntries.length; i++) {
                //create todo
                this.createNewTodoItem(todoEntries[i].entryText, todoEntries[i].entryId);
                for (var j = 0; j < todoEntries[i].assignedSubjects.length; j++) {
                    //self-assign
                    this.addAssignee(todoEntries[i].assignedSubjects[j], todoEntries[i].entryId);
                }
                if (todoEntries[i].entryMarkedDone) {
                    //mark as done
                    this.markAsDone(todoEntries[i].entryId);
                } else {
                    //mark as undone
                    this.markAsUnDone(todoEntries[i].entryId);
                }
                if (!this.isCurrentUserAssigned(todoEntries[i].entryId) && !todoEntries[i].entryMarkedDone) {
                    this.addAssignMe(todoEntries[i].entryId);
                } else {
                    this.removeAssignMe(todoEntries[i].entryId);
                }
            }
        }
        this.resetHTMLHooks();
    }
}