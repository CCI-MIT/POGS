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

        var str = '';
        $.each(surveyValues,function(i,e){
            console.log(e.type);
            if(e.type == "text"){ // setup text question
                str += '<div class="form-group">'
                str += '<label for="answer'+i+'" id="question'+i+'" class="question-label pull-left">'+ 'Question ' + i + ': ' + e.question +'</label>'
                if(e.video_url){
                    str += ' <iframe id="video_frame" class="question-video" src="'+e.video_url+'" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>'
                }
                str += '<input type="text" class="form-control" id="answer'+i+'" data-cell-reference-index="'+i+'" placeholder="'+e.placeholder+'"></div>'
            }
            else if(e.type == "radio"){
                str += '<div class="form-group">'
                str += '<label id="question'+i+'" class="question-label">'+ 'Question ' + i + ': ' + e.question +'</label>'
                if(e.video_url){
                    str += '<iframe id="video_frame" class="question-video" src="'+e.video_url+'" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>'
                }


                $.each(e.value,function(j, choice){ // setup radio question
                    str += '<div class="form-check" id="answer'+i+'">'
                    str += '  <label class="form-check-label" for="radio'+j+'">'
                    str += '    <input type="radio" class="form-check-input" name="answer'+i+'" id="radio'+j+'" value="'+choice+'" data-cell-reference-index="'+i+'">' + choice
                    str += '  </label> </div>'
                });

                str += ' </div>'
            }
            else if(e.type == "select") {
                str += '<div class="form-group">'
                str += '<label for="answer'+i+'" id="question'+i+'" class="question-label pull-left">'+e.question+'</label> <br>'
                str += '<select class="form-control" id="answer'+i+'" data-cell-reference-index="'+i+'">'
                $.each(e.options, function(j, option) {
                    str += '<option value="'+option+'">'+option+'</option>'
                });
                str += '</select></div>'
            }
            else if(e.type == "checkbox") {
                str += '<div class="form-group">'
                str += '<label id="question'+i+'" class="question-label">'+ e.question +'</label><div id="answer'+i+'">'
                $.each(e.value, function(j, choice){
                    str += '<div class="form-check">'
                    str += '<label class="form-check-label">'
                    str += '<input type="checkbox" class="form-check-input" name="answer'+i+'" value="'+choice+'" data-cell-reference-index="'+i+'">' + choice
                    str += '</label> </div>'
                });
                str += '</div></div>'
            }

            console.log(i + '----'+ JSON.stringify(e));
        });

        $('#surveyForm').append(str);

        $(":input").each(function(i){
            console.log($(this).attr('type'))
            if($(this).attr('type') == "text") {
                $(this).on('focusin', self.handleTextOnClick.bind(self));
                $(this).on('focusout', self.handleTextOnBlur.bind(self));
            } else if($(this).attr('type') == "radio"){
                $(this).on('change',self.handleRadioOnClick.bind(self));
            }
            else if($(this).attr('type') == "checkbox"){
                $(this).on('change',self.handleCheckboxOnClick.bind(self));
            }
        });

        $("#surveyForm select").each(function() {
            $(this).on('change', self.handleSelectOnChange.bind(self));
        });
    }



    broadcastReceived(message){
        var attrName = message.content.attributeName;
        if(message.sender != this.pogsPlugin.subjectId) {
            if (attrName == "focusInCell") {
                   // handle on focus - add class for bg change
                var cell = message.content.attributeIntegerValue;
                if($("#answer" + cell).attr('type') == "text" && !($("#answer" + cell).is( ":focus" ))){ // sync text field
                             $("#answer" + cell)
                                 .addClass("form-control--error") //get subject backgroud COLOR
                }
            } else {
                if (attrName.indexOf("surveyAnswer_") != -1) {
                    var cell = attrName.replace("surveyAnswer_", "");
                    if($("#answer" + cell).attr('type') == "text"){ // sync text field
                        $("#answer" + cell).val(message.content.attributeStringValue);
                        $("#answer" + cell)
                        .delay(1000).queue(function (next) {
                                $(this).removeClass("form-control--error");
                                next();
                            });
                    } else if($("#answer" + cell).attr('class') == "form-check"){ //sync radio button
                        var radioButtons = $("#answer"+cell+" label input[type=radio]")
                        $(radioButtons).each(function(i){
                            var radioButton = $("#answer"+cell+" label input[type=radio]")[i]
                            if($(radioButton).attr('value') == message.content.attributeStringValue){
                                $(radioButton).prop("checked",true)
                            }
                        });
                   }


                }
                //change to  (#answer + cell).checked = message.content attributeIntVal
                else if (attrName.indexOf("changeSelect_") != -1) {
                    var cell = attrName.replace("changeSelect_", "");
                    $("#answer" + cell).val(message.content.attributeStringValue)
                        .addClass("form-control--error")
                        .delay(1000).queue(function (next) {
                            $(this).removeClass("form-control--error");
                            next();
                        });
                }
                else if(attrName.indexOf("checkboxSelect_") != -1) {
                    var question_number = attrName.replace("checkboxSelect_", "");
                    $("#answer" + question_number).find("input[value='"+message.content.attributeStringValue+"']")
                        .prop("checked", true);
                }
                else if(attrName.indexOf("checkboxUnselect_") != -1) {
                    var question_number = attrName.replace("checkboxUnselect_", "");
                    $("#answer" + question_number).find("input[value='"+message.content.attributeStringValue+"']")
                        .prop("checked", false);
                }
            }
        }
    }

    handleTextOnClick(event){
        var cellIndex = parseInt($(event.target).data( "cell-reference-index"));
        if(!isNaN(cellIndex)) {
            this.pogsPlugin.saveCompletedTaskAttribute('focusInCell',
                                                       "", 0.0,
                                                       cellIndex, false);
        }
    }

    handleTextOnBlur(event){
        var cellIndex = parseInt($(event.target).data( "cell-reference-index"));
        console.log("cell reference " + cellIndex);
        if(!isNaN(cellIndex)) {
            console.log($(event.target))
            var valueTyped = $(event.target).val().replace(/\r?\n?/g, '').trim();
            console.log(valueTyped);
            if(valueTyped != null) {
                this.pogsPlugin.saveCompletedTaskAttribute('surveyAnswer_' + cellIndex,
                    valueTyped, 0.0,
                    0, true);
            }
        }
    }

    handleRadioOnClick(event){
        var cellIndex = parseInt($(event.target).data( "cell-reference-index"));
        console.log("answer " + cellIndex);
        if(!isNaN(cellIndex)) {
            // console.log($(event.target))
            var valueTyped = $(event.target).attr('value'); // value of radio button
            // console.log("Typed Value: " + valueTyped);
            if(valueTyped != null) {
                this.pogsPlugin.saveCompletedTaskAttribute('surveyAnswer_' + cellIndex,
                    valueTyped, 0.0,
                    0, true);
            }
        }
    }

    handleSelectOnChange(event) {
        console.log("here");
        var target = $(event.target);
        var cellIndex = parseInt(target.data( "cell-reference-index"));
        var option = target.val();
        if(!isNaN(cellIndex) && option != null) {
            this.pogsPlugin.saveCompletedTaskAttribute('changeSelect_' + cellIndex,
                option, 0.0,
                0, true);
        }
    }

    handleCheckboxOnClick(event) {
        console.log("checkbox clicked");
        var target = $(event.target);
        var questionIndex = parseInt(target.data( "cell-reference-index"));
        var option = target.val();
        if(!isNaN(questionIndex) && option != null) {
            if(target.is(":checked")) {
                this.pogsPlugin.saveCompletedTaskAttribute('checkboxSelect_' + questionIndex,
                    option, 0.0,
                    0, true);
            }
            else {
                this.pogsPlugin.saveCompletedTaskAttribute('checkboxUnselect_' + questionIndex,
                    option, 0.0,
                    0, false);
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