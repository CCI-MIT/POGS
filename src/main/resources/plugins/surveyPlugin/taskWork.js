class Survey {
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
    }
    setupSurvey(surveyBluePrint){
    console.log("starting survey setup...");
        /*
            JSON format
            [
              {"question":"Survey question1?",
                "type": "text",
                "placeholder":"question1 placeholder",
                "default": "whatever"
              },
              {"question":"Survey question2?",
                "type": "radio/check",
                "placeholder":"question2 placeholder"
                "options": [...],
                "default": "whatever"
              },
              {"question":"Survey question3?",
                "type": "select",
                "placeholder":"question3 placeholder",
                "options": [...],
                "default": "whatever"
              }
            ]

        */
        var surveyValues = $.parseJSON(surveyBluePrint);
        var self = this;
        /*$("#surveyForm").find(".form-group").each(function(index) { // grab form-group
            var surveyValue = surveyValues[index];
            $("#question"+index).text(surveyValue["question"]);
            if(surveyValue["type"] == "text") {
                var $answer = $("#answer"+index);
                $answer.attr("placeholder", surveyValue["placeholder"]);
                $answer.on('focusin', self.handleOnClick.bind(self));
                $answer.on('focusout', self.handleOnBlur.bind(self));
            }
        });*/

        var str = '';

        $.each(surveyValues,function(i,e){
            console.log(e.type);
            if(e.type == "text"){
                str += '<div class="form-group">'
                str += '<label for="answer'+i+'" id="question'+i+'" class="question-label pull-left">'+e.question+'</label>'
                str += '<input type="text" class="form-control" id="answer'+i+'" data-cell-reference-index="'+i+'" placeholder="'+e.placeholder+'"></div>'
            }
            else if(e.type == "radio"){
                str += '<label for="answer'+i+'" id="question'+i+'" class="question-label pull-left">'+e.question+'</label> <br>'

                $.each(e.value,function(j, choice){
                    str += '<div class="form-check">'
                    str += '  <label class="form-check-label" for="radio'+j+'">'
                    str += '    <input type="radio" class="form-check-input" name="answer'+i+'" id="radio'+j+'" value="'+choice+'">' + choice
                    str += '  </label> </div>'
                });
            }

            console.log(i + '----'+ JSON.stringify(e));
        });

        $('#surveyForm').append(str);

        $("#surveyForm").find(".form-group").each(function(index) { // grab form-group
            var $answer = $("#answer"+index);
            if($answer.attr('type') == "text") {
                $answer.on('focusin', self.handleOnClick.bind(self));
                $answer.on('focusout', self.handleOnBlur.bind(self));
            }
        });
    }


    broadcastReceived(message){
        var attrName = message.content.attributeName;
        if(message.sender != this.pogsPlugin.subjectId) {
            if (attrName == "focusInCell") {
                   // handle on focus - add class for bg change
            } else {
                if (attrName.indexOf("surveyAnswer_") != -1) {
                    var cell = attrName.replace("surveyAnswer_", "");
                    $("#answer" + cell).val(message.content.attributeStringValue);
                }
            }
        }
    }

    handleOnClick(event){
        var cellIndex = parseInt($(event.target).data( "cell-reference-index"));
        if(!isNaN(cellIndex)) {
            this.pogsPlugin.saveCompletedTaskAttribute('focusInCell',
                                                       "", 0.0,
                                                       cellIndex, false);
        }
    }

    handleOnBlur(event){
        var cellIndex = parseInt($(event.target).data( "cell-reference-index"));
        if(!isNaN(cellIndex)) {
            var valueTyped = $(event.target).val().replace(/\r?\n?/g, '').trim();
            if(valueTyped != null) {
                this.pogsPlugin.saveCompletedTaskAttribute('surveyAnswer_' + cellIndex,
                    valueTyped, 0.0,
                    0, true);
            }
        }
    }
}

var surveyPlugin = pogs.createPlugin('surveyTaskPlugin',function(){

    console.log("Survey Plugin Loaded");

    var survey = new Survey(this);
    // get config attributes from task plugin
    survey.setupSurvey(this.getStringAttribute("surveyBluePrint"));
    this.subscribeTaskAttributeBroadcast(survey.broadcastReceived.bind(survey))

});