'use strict';

var FEEDBACK_BAR_IDS = {
    FEEDBACK_BAR_ID: "feedbackBarContainer"

}

class FeedbackBarManager {
    constructor(pogsColaborationPlugin) {
        this.pogsColaborationPlugin = pogsColaborationPlugin;
        this.setupHTMLHooks();
        this.pogsColaborationPlugin.subscribeCollaborationBroadcast(
            this.onFeedbackMessageReceived.bind(this));
    }

    setupHTMLHooks() {

    }
    onFeedbackMessageReceived(message) {
        console.log( message.content)
        if (message.content.collaborationType != COLLABORATION_TYPE.FEEDBACK_BAR) {
            return;
        }
        var completedTaskId = this.pogsColaborationPlugin.getCompletedTaskId();

        for(var i =0; i < message.content.completedTasks.length ; i ++) {
            if(message.content.completedTasks[i].completedTaskId = completedTaskId) {
                var subjectFeedbacks = message.content.completedTasks[i].subjectFeedbacks;
                for(var j = 0; j < subjectFeedbacks.length; j ++) {
                    var externalId = subjectFeedbacks[j].externalId;
                    var interaction = subjectFeedbacks[j].interaction;
                    var percentage = subjectFeedbacks[j].percentage;

                    if ($("#" + externalId +'_feedback_progressbar').length > 0) {
                        this.updateSubjectEntry(externalId, interaction, percentage);
                    } else {
                        this.createSubjectEntry(externalId, interaction, percentage);
                    }
                }
                return;

            }
        }

    }
    updateSubjectEntry(externalId, value, percentage) {
        $("#" +externalId +'_feedback_progressbar').width(percentage + "%");
        $("#" +externalId +'_feedback_progressbar').attr("aria-valuenow",percentage);
    }
    createSubjectEntry(externalId, value, percentage) {
        $('<div id="'+externalId+'_feedback_progressbar" data-author="subject-'+externalId+'" class="progress-bar" role="progressbar" '
          + 'style="width: '+percentage+'%;" aria-valuenow="'+percentage+'" aria-valuemin="0" '
          + 'aria-valuemax="100">'+
          this.pogsColaborationPlugin.getSubjectByExternalId(externalId).displayName
          +'</div>').appendTo("#" + FEEDBACK_BAR_IDS.FEEDBACK_BAR_ID + " .progress");

    }
}