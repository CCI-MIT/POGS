'use strict';

class TodoListManager {
    constructor(pogsColaborationPlugin){
        this.pogsColaborationPlugin = pogsColaborationPlugin;

        this.setupHTML();

    }
    setupHTML(){
        $("#createTodoItem").click(this.createTodoItem.bind(this));
    }
    createTodoItem(promptEntry){
        var todoText = prompt("Please describe the task (Max. 25 characters).", (promptEntry=='')?(''):(promptEntry));

        if(todoText.length >= 26){
            alert("Task description should be less than 25 characters.")
            this.createTodoItem(todoText);
            return;
        }
        if (todoText != null) {
            this.createTodoEntry(todoText);
        }
    }
    createTodoEntry(todoText){
        this.pogsColaborationPlugin.sendMessage(todoText, TODO_TYPE.CREATE_TODO);
    }

    oldFunction(todoEntryText,currentlyAssignedSubjects) {

        var assignedSubjects = "";
        for(var i = 0; i < currentlyAssignedSubjects.length ; i ++){
            assignedSubjects +=  '<span class="badge badge-secondary">';

            if(currentlyAssignedSubjects[i] == this.pogsColaborationPlugin.getSubjectId()){
                assignedSubjects +=  '<i class="fas fa-times-circle"></i>';
            }
            '</span>';

        }

        var todoItem = '<span class="badge badge-primary">\n'
        + '        <i class="fa fa-check-square"></i>\n'
        + '        <span> '+todoEntryText+'</span>\n'
        + '        <span class="badge badge-success">' + assignedSubjects +'</span>\n'
        + '        <i class="fa fa-trash-alt"></i>\n'
        + '    </span>';
        $("#todoListContainer").append(todoItem);





        /*
           Send events

          - Created a todo item
            - crete the html
          - assigned themselves
             - add the badge to the assignee container
          - deleted
            - delete te HTML
          - unassigned themlselves
             - remove the badge from th assignee container
          - marked as done
            - toggle the layout
          - marked as undone
            - toggle the layout







          Regular full update

          - Created a todo item
            - sending created todo list

            - trigger a broadcast event from server side.
          - assigned themselves

          - deleted

          - unassigned themlselves

          - marked as done

          - marked as undone


           - Get a list of all todo items
           - render as IS.
           - listen to the broadcast of updated todo list
             - crete the html
              - add the badge to the assignee container
              - delete te HTML
              - remove the badge from th assignee container
              - toggle the layout
              - toggle the layout



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




        var assigneesHTML = "";
        var todoItem = {};
        if(todoItem.assignees.length == 0 && !todoItem.isDone){
            assigneesHTML = ('<span class="badge badge-assignme" data-todoid="'+
                             todoItem.todo_entry_id+'" onclick="assignMeToTask('+todoItem.todo_entry_id+')">Assign me!</span>');
        }
        else{
            var isAssigned = false;
            for(var i = 0 ; i < todoItem.assignees.length; i++){
                var subj = todoItem.assignees[i];
                if((subj.external_id) == (todolist_subject_external_id)) {
                    assigneesHTML += ('<span class="badge" data-todoid="' + todoItem.todo_entry_id + '" style="background-color:' +
                                      resolveColor(subj.color) + '">' + subj.assignee + ((todoItem.isDone)?(''):(' <span class="unassign" onclick="unassignMeToTask(\'+' +
                                      todoItem.todo_entry_id+'\')">(x)</span>'))+'</span>');
                    isAssigned = true;
                }else{
                    assigneesHTML += ('<span class="badge" data-todoid="' + todoItem.todo_entry_id + '" style="background-color:' +
                                      resolveColor(subj.color) + '">' + subj.assignee + '</span>');
                }
            }
            if(!isAssigned && !todoItem.isDone){
                assigneesHTML += ('<span class="badge badge-assignme" data-todoid="'+
                                  todoItem.todo_entry_id+'" onclick="assignMeToTask('+todoItem.todo_entry_id+')">Assign me!</span>');
            }
        }
        var taskStatus = (todoItem.isDone)?
                         ('<span style="cursor:pointer" onclick="markTaskAsUnDone('+todoItem.todo_entry_id+')">&#9745;</span>'):('<span style="cursor:pointer" alt="Click to mark task as done!" onclick="markTaskAsDone('+todoItem.todo_entry_id+')">&#9744;</span>');

        var taskDeletion = (todoItem.isDone)?
                           (''):('<span style="cursor:pointer" onclick="deleteTask('+todoItem.todo_entry_id+')">&#x2717;</span>');

        $('<div/>', {
            id: 'task_entry_' + todoItem.todo_entry_id,
            class: (todoItem.isDone)?('label_tasks_ label label-done'):('label_tasks_ label label-not-done'),
            html: taskStatus + '&#32;' + todoItem.text + assigneesHTML + '&#32; ' + taskDeletion
        }).appendTo('#todo_entries');



    }

    mark_task_as_done(todo_entry_id) {
        var data = {'todo_entry_id': todo_entry_id, 'completed_task_id' : todolist_completed_task_id,'subject_external_id' : todolist_subject_external_id};

        $.post("/mci/todo/done", data,
               function(json) {
               }, 'json');
    }
    mark_task_as_undone(todo_entry_id) {
        var data = {'todo_entry_id': todo_entry_id, 'completed_task_id' : todolist_completed_task_id,'subject_external_id' : todolist_subject_external_id};

        $.post("/mci/todo/undone", data, function(json) {}, 'json');
    }
    delete_task(todo_entry_id) {
        var data = {'todo_entry_id': todo_entry_id, 'completed_task_id' : todolist_completed_task_id,'subject_external_id' : todolist_subject_external_id};

        $.post("/mci/todo/delete", data,
               function(json) {
               }, 'json');
    }
    self_unassign_todolist_entry(todo_entry_id) {
        var data = {'todo_entry_id': todo_entry_id, 'completed_task_id' : todolist_completed_task_id,'subject_external_id' : todolist_subject_external_id};

        $.post("/mci/todo/unassign", data,
               function(json) {
               }, 'json');
    }
    self_assign_todolist_entry(todo_entry_id) {

        var data = {'todo_entry_id': todo_entry_id,'completed_task_id' : todolist_completed_task_id, 'subject_external_id' : todolist_subject_external_id};

        $.post("/mci/todo/assign", data,
               function(json) {
               }, 'json');
    }
}

//Create the TodoListManager
/* Responsabilities


 <div id="todoListContainer">
        <button>Create new item</button>


    <span class="badge badge-primary">
        <i class="fa fa-check-square"></i>
        <span> Read first paragraph</span>
        <span class="badge badge-success">Assign me /Unnasign me</span>
        <i class="fa fa-trash-alt"></i>
    </span>




    <span class="badge badge-primary status-text-desc statusAvailable">available</span>

     |(done)-name--assign/unnasign---delete|

     |----| |----| |----| |----| |----| |----|
 </div>
  - Create the HTML for the todo items
  - handle onclick on buttons for assignment/unnasignment
  - handle marked as done
  - handle broadcast messages for events
  - handle broadcast messages for todo item sync

  Events:
    - todo created
    - todo assigned
    - todo marked as done


 */