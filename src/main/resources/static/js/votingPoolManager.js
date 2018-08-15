'use strict';

class VotingPoolManager {
    constructor(pogsColaborationPlugin){
        this.pogsColaborationPlugin = pogsColaborationPlugin;
        this.setupHTML();
        this.pogsColaborationPlugin.subscribeCollaborationBroadcast(this.onVotingBroadcastReceived.bind(this));
    }
    setupHTML(){
        this.createInitialHTML();
        $("#createVotingPool").click(this.createVotingPool.bind(this));
        $("#deleteVotingPool").click(this.deleteVotingPoolInstance.bind(this));
        $("#createVotingQuestion").click(this.createVotingQuestion.bind(this));
        $("#deleteVotingQuestion").click(this.deleteVotingQuestion.bind(this));
        $("#createVotingOption").click(this.createVotingOption.bind(this));
        $("#deleteVotingOption").click(this.deleteVotingOption.bind(this));
    }
    createVotingPool(promptEntry){
        var votingPoolText = prompt("Please describe the issue to be voted on (Max. 25 characters).", (promptEntry=='')?(''):(promptEntry));

        if(votingPoolText.length >= 26){
            alert("Task description should be less than 25 characters.");
            this.createVotingPool(votingPoolText);
            return;
        }
        if (votingPoolText != null) {
            this.createVotingPoolInstance(votingPoolText);
        }
    }
    createVotingPoolInstance(votingPoolText){
        this.pogsColaborationPlugin.sendMessage(votingPoolText, VOTING_TYPE.CREATE_VOTING_POOL, COLLABORATION_TYPE.VOTING_LIST);
    }

    deleteVotingPoolInstance(votingPoolText){
        this.pogsColaborationPlugin.sendMessage(votingPoolText, VOTING_TYPE.DELETE_VOTING_POOL, COLLABORATION_TYPE.VOTING_LIST);
    }

    createVotingQuestion(votingPoolText) {
        this.pogsColaborationPlugin.sendMessage(votingPoolText, VOTING_TYPE.CREATE_QUESTION, COLLABORATION_TYPE.VOTING_LIST);
    }

    deleteVotingQuestion(votingPoolText) {
        this.pogsColaborationPlugin.sendMessage(votingPoolText, VOTING_TYPE.DELETE_QUESTION, COLLABORATION_TYPE.VOTING_LIST);
    }

    createVotingOption(votingPoolText) {
        this.pogsColaborationPlugin.sendMessage(votingPoolText, VOTING_TYPE.CREATE_OPTION, COLLABORATION_TYPE.VOTING_LIST);
    }

    deleteVotingOption(votingPoolText) {
        this.pogsColaborationPlugin.sendMessage(votingPoolText, VOTING_TYPE.DELETE_OPTION, COLLABORATION_TYPE.VOTING_LIST);
    }

    createInitialHTML() {
        return '<div id="votingPoolContainer">\n' +
            '        <button id = "createVotingPool">Create Voting Pool</button></div>';
    }

    onVotingBroadcastReceived(message) {
        var votingPools = message.content.votingPools;
        for (var i = 0; i < votingPools.length; i++) {
            if (!votingPools[i].currentAssigned) {
                if (!votingPools[i].markDone) {
                    var votingQuestion = '<i class="badge badge-primary">\n'
                        + '        <span> ' + votingPools[i].text + '</span>\n'
                        + '        <span class="badge badge-createq" id = "createQuestion" data-votingid="' + votingPools[i].voting_pool_id + '"></span>\n'
                        + '        <i class="fa fa-trash-alt" id="deleteQuestion" data-votingid="' + votingPools[i].voting_pool_id + '"></i>\n'
                        + '    </span>';
                    $("#votingPoolContainer").append(votingQuestion);
                } else {
                    var votingOption = '<i class="badge badge-primary">\n'
                        + '        <i class="fas fa-check-square" id="createOption" data-todoid="' + votingPools[i].voting_pool_id + '"></i>\n'
                        + '        <span> ' + votingPools[i].text + '</span>\n'
                        + '        <span class="badge badge-assignme" id="selfAssignTodoItem" data-todoid="' + votingPools[i].voting_pool_id + '"></span>\n'
                        + '    </span>';
                    $("#votingPoolContainer").append(votingOption);
                }
            } else if (votingPools[i].currentAssigned == this.pogsCollaborationPlugin.getSubjectId()) {
                if (!votingPools[i].markDone) {

                    var todoItem = '<i class="badge badge-primary">\n'
                        + '        <i class="far fa-square" id="markDoneTodoItem" data-todoid="' + votingPools[i].voting_pool_id + '"></i>\n'
                        + '        <span> ' + todoEntries[i].text + '</span>\n'
                        + '        <span class="badge badge-success">Me</span>\n'
                        + '       <i class="fas fa-times-circle" id ="unassignTodoItem" data-todoid="' + votingPools[i].voting_pool_id + '"></i>\n'
                        + '        <i class="fa fa-trash-alt" id="deleteTodoItem" data-todoid="' + votingPools[i].voting_pool_id + '"></i>\n'
                        + '    </span>';
                    $("#votingPoolContainer").append(todoItem);
                }
            }
        }
    }
}
//Create the VotingPoolManager
/*
 - create the html for the voting pool
 - handle onclick for adding new pool and new options

  .Question?
   - option1
   - option2


 - handle vote
 - show pool statistics.
 - handle broadcast messages for all events
*/