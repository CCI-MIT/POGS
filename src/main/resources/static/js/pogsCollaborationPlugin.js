'use strict';

class CollaborationPlugin extends PogsPlugin {
    constructor(pogsRef){
        super('collaborationPlugin', null, pogsRef);
        this.initFunc = this.init;
    }
    init(){
        console.log("Init");
        //check for the enabled widgets and instantiate their managers
    }
    subscribeCollaborationBroadcast(funct) {
        this.pogsRef.subscribe('collaborationMessage', funct);
    }
    sendMessage(message, channel, type, receiver) {

        var messageContent = {
            message: message,
            type: type,
            channel: channel
        };

        this.pogsRef.sendMessage("/pogsapp/collaboration.sendMessage", "COLLABORATION_MESSAGE",
                                 messageContent,
                                 this.getSubjectId(), receiver, this.getCompletedTaskId(),
                                 this.getSessionId());
    }
}


//Create the TodoListManager
/* Responsabilities
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
//Create the VotingPoolManager
/*
 - create the html for the voting pool
 - handle onclick for adding new pool and new options
 - handle vote
 - show pool statistics.
 - handle broadcast messages for all events
*/