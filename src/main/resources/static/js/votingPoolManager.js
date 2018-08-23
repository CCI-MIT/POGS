'use strict';

var VOTING_POOL_IDS = {
    VOTING_CONTAINER: "votingPoolContainer",
    VOTING_MODAL_CONTAINER: "votingModal",
    VOTING_POOL_ID: "voting_",
    VOTING_POOL_OPTION_ID: "voting_option_",

}

class VotingPoolManager {
    constructor(pogsColaborationPlugin) {
        this.pogsColaborationPlugin = pogsColaborationPlugin;
        this.setupHTMLHooks();
        this.pogsColaborationPlugin.subscribeCollaborationBroadcast(
            this.onVotingBroadcastReceived.bind(this));
    }

    setupHTMLHooks() {

        $("#createVotingPool").unbind().click(this.createVotingPool.bind(this));

        $(".voteForPool").unbind().click(this.castVote.bind(this));
        $(".deleteVotingPool").unbind().click(this.deleteVotingQuestion.bind(this));
        $(".addVotingOption").unbind().click(this.createVotingOption.bind(this));
        $(".deleteOption").unbind().click(this.deleteVotingOption.bind(this));
    }

    createVotingPool(promptEntry) {
        if (typeof promptEntry != 'string') {
            promptEntry = '';
        }
        var votingPoolText = prompt(
            "Please describe the issue to be voted on (Max. 25 characters).", promptEntry);

        if (votingPoolText.length >= 26) {
            alert("Task description should be less than 25 characters.");
            this.createVotingPool(votingPoolText);
            return;
        }
        if (votingPoolText != null) {
            this.createVotingQuestion(votingPoolText);
        }
    }

    deleteVotingPoolInstance(votingPoolText) {
        this.pogsColaborationPlugin.sendMessage(votingPoolText,
                                                VOTING_TYPE.DELETE_VOTING_POOL,
                                                COLLABORATION_TYPE.VOTING_LIST);
    }

    createVotingQuestion(votingPoolText) {
        this.pogsColaborationPlugin.sendMessage(votingPoolText,
                                                VOTING_TYPE.CREATE_VOTING_POOL,
                                                COLLABORATION_TYPE.VOTING_LIST);
    }

    deleteVotingQuestion() {

        var poolid = $(event.target).data("poolid");
        var r = confirm("Do you want to delete this voting pool?");
        if (r == true) {
            this.pogsColaborationPlugin.sendMessage(poolid,
                                                    VOTING_TYPE.DELETE_VOTING_POOL,
                                                    COLLABORATION_TYPE.VOTING_LIST);
        }
        event.stopPropagation();


    }

    createVotingOption(promptEntry) {

        var poolid = $(event.target).data("poolid");

        if (typeof promptEntry != 'string') {
            promptEntry = '';
        }
        var votingPoolText = prompt(
            "Please describe the option (Max. 25 characters).", promptEntry);

        if (votingPoolText.length >= 26) {
            alert("Task description should be less than 25 characters.");
            this.createVotingOption(votingPoolText);
            return;
        }
        if (votingPoolText != null) {
            this.pogsColaborationPlugin.sendMessage(votingPoolText+"<separator>"+poolid,
                                                    VOTING_TYPE.CREATE_OPTION,
                                                    COLLABORATION_TYPE.VOTING_LIST);
        }


    }

    deleteVotingOption(event) {
        var optionid = $(event.target).data("optionid");

        var r = confirm("Do you want to delete this option?");
        if (r == true) {
            this.pogsColaborationPlugin.sendMessage(optionid,
                                                    VOTING_TYPE.DELETE_OPTION,
                                                    COLLABORATION_TYPE.VOTING_LIST);
        }

        var modalItem = '<div class="modal fade" id="poolVotingModal_'+votingPoolRef+'" tabindex="-1" role="dialog"'
        event.stopPropagation();

    }

    castVote() {
        var poolid = $(event.target).data("poolid");
        var radioOptions  = 'options_' + poolid;
        var radioValue = $("input[name='"+radioOptions+"']:checked").val();
        if(radioValue) {
            this.pogsColaborationPlugin.sendMessage(radioValue,
                                                    VOTING_TYPE.CAST_VOTE,
                                                    COLLABORATION_TYPE.VOTING_LIST);
        }
    }

    createNewVotingPool(question, votingPollId) {
        var votingPoolRef = VOTING_POOL_IDS.VOTING_POOL_ID + votingPollId;
        if ($('#' +votingPoolRef ).length > 0) {
            return;
        }

        //create the trigger
        //create the modal


        var buttonItem = '<span class="badge badge-primary votingBadge" id="'+votingPoolRef+'" class="btn btn-primary" data-toggle="modal" data-target="#poolVotingModal_'+votingPoolRef+'">'+question+'</span>';

        var todoItem = '    <div id="collapse_'+votingPoolRef+'" class="collapse show" '
                       + 'aria-labelledby="heading_'+votingPoolRef+'" data-parent="#'+VOTING_POOL_IDS.VOTING_CONTAINER+'">\n'
                       + '      <div class="card-body">\n'
                       + '           <div id="options_'+votingPoolRef+'"></div>'
                       + '           <button class="vote btn btn-primary d-none voteForPool" id="vote_'+votingPollId+'" data-poolid="' + votingPollId +'">Vote</button>'
                       + '      </div>\n'
                       + '    </div>\n';

        var modalItem = '<div class="modal fade" id="poolVotingModal_'+votingPoolRef+'" tabindex="-1" role="dialog"'
                        + ' aria-labelledby="modalTitle_id" aria-hidden="true">\n'
                        + '  <div class="modal-dialog" role="document">\n'
                        + '    <div class="modal-content">\n'
                        + '      <div class="modal-header">\n'
                        + '        <h5 class="modal-title" id="modalTitle_'+votingPoolRef+'">'+question+ ' - '
                        + '         <span class="btn btn-danger btn-sm deleteVotingPool" data-dismiss="modal" data-poolid="' + votingPollId + '"><i class="fa fa-trash "  \n'
                        + '            data-poolid="' + votingPollId + '" ></i></span>'
                        + '       <span class="addVotingOption btn btn-info btn-sm" data-poolid="' + votingPollId +'"><i class="fa fa-plus-square"  \n'
                        + '                   style="margin:4px" ></i>Add option</span>'
                        + '        </h5>\n'
                        + '        <button type="button" class="close" data-dismiss="modal" aria-label="Close">\n'
                        + '          <span aria-hidden="true">&times;</span>\n'
                        + '        </button>\n'
                        + '      </div>\n'
                        + '      <div class="modal-body">\n'
                        + todoItem
                        + '      </div>\n'
                        + '      <div class="modal-footer">\n'
                        + '        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>\n'
                        + '      </div>\n'
                        + '    </div>\n'
                        + '  </div>\n'
                        + '</div>\n';

        $('#' + VOTING_POOL_IDS.VOTING_CONTAINER).append(buttonItem);
        $('#' + VOTING_POOL_IDS.VOTING_MODAL_CONTAINER).append(modalItem);
    }
    deleteVotingPool(votingPollId){
        var votingPoolRef = VOTING_POOL_IDS.VOTING_POOL_ID + votingPollId;
        $('#' +votingPoolRef ).remove();
        $('collapse_'+votingPoolRef).remove();
        $('#poolVotingModal_' + votingPoolRef).modal('hide');
    }

    createNewVotingPoolOption(option, optionId, votingPollId, percentage, totalOfVotes) {
        var optionRef = VOTING_POOL_IDS.VOTING_POOL_OPTION_ID + optionId ;
        var votingPoolRef = VOTING_POOL_IDS.VOTING_POOL_ID + votingPollId;
        if ($('#' + optionRef).length > 0) {
            $('#'+optionRef+'_percent').width(''+percentage+'%');
            $('#'+optionRef+'_totalOfVotes').text(totalOfVotes+' votes');
            return;
        }
        var votingOption =   '<div id="'+optionRef+'">'
                             +'<div class="d-flex align-items-start">'
                           + '<span class="optionText"><input type="radio" class="radioOption" name="options_'+votingPollId+'" value="'+optionId+'"/>'+option+'</span>'
                             + '<div class="progress progressPosition" style="width: 150px">\n'
                             + '  <div class="progress-bar bg-info" id="'+optionRef+'_percent" style="width:'+percentage+'%"><span id="'+optionRef+'_totalOfVotes">'+totalOfVotes+' votes</span></div>\n'
                             + '</div>'
                           + '<span class="badge badge-danger btn-sm pointer deleteOption" data-poolid="'+votingPollId+'" data-optionid="' + optionId + '">Delete</span>\n'
                           + '</div></div>';
        ;
        $('#options_' + votingPoolRef).append(votingOption);
        $('#vote_'+votingPollId).removeClass("d-none")
    }

    deleteOption(optionId) {
        var optionRef = VOTING_POOL_IDS.VOTING_POOL_OPTION_ID + optionId ;
        console.log("Inisde delete option", optionRef);
        $('#' + optionRef).remove()
    }


    onVotingBroadcastReceived(message) {
        if(message.content.collaborationType != COLLABORATION_TYPE.VOTING_LIST){
            return;
        }
        console.log("Message received nothing happening")
        var votingPools = message.content.votingPools;

        console.log("Voting pools: " +votingPools)
        if (message.content.triggeredBy == VOTING_TYPE.DELETE_OPTION ||
            message.content.triggeredBy == VOTING_TYPE.DELETE_VOTING_POOL) {
            if (message.content.triggeredBy == VOTING_TYPE.DELETE_OPTION) {
                this.deleteOption(message.content.triggeredData);
                console.log("Delete option " + message.content.triggeredData);
            } else {
                this.deleteVotingPool(message.content.triggeredData);
            }
        } else {
            for (var i = 0; i < votingPools.length; i++) {
                var pool = votingPools[i];
                console.log("Voting question: " +pool.votingQuestion)
                this.createNewVotingPool(pool.votingQuestion,pool.votingPoolId);
                for( var j=0 ; j < pool.votingOptions.length; j++){
                    var option = pool.votingOptions[j];
                    this.createNewVotingPoolOption(option.votingOption,
                                                   option.votingPoolOptionId,
                                                   pool.votingPoolId,
                                                   option.percentage,
                                                   option.votes
                                                   );
                }
            }
        }
        this.setupHTMLHooks();
    }
}
