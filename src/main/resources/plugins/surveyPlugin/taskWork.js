class Survey {
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
    }
    resolveVariablesForNetworkQuestions(surveyItem){
        var regex = new RegExp(/\${.*}/gi);
        var allVariables = ['\\${allTeammates}','\\${otherTeamates}', '\\${lastTaskName}',
                            '\\${allTasksNames}', '\\${sessionName}'];

        var replacements = [['m01', 'm02', 'm03'], [['m02', 'm03']],"Last task name", ["tast 1", "task 2","task 3"],
        "session one"]
        if(surveyItem.question.match(regex)) {
            for(var i=0; i < allVariables.length; i ++) {
                var replacer = "";
                if(surveyItem.question.match('/' + allVariables[i] + '/gi')) {
                    if(replacements[i].constructor === Array) {

                       for(var j =0 ; j < replacements[i].length; j ++) {
                           replacer += replacements[i][j];
                           if(j + 1 != replacements[i].length){
                               replacer += ", ";
                           }
                       }

                    } else {
                        replacer = replacements[i];
                    }
                    surveyItem.question =
                        surveyItem.question.replace('/' + allVariables[i] + '/gi', replacer);
                }
            }
        } else {
            if (surveyItem.options && surveyItem.options.length > 0) {
                for (var i = 0; i < surveyItem.options.length; i++) {
                    if (surveyItem.options[i].match(regex)) {



                    if (surveyItem.options[i].match('/' + allVariables[i] + '/gi')) {
                        if(replacements[i].constructor === Array) {
                            surveyItem.options = replacements[i];
                        }else{
                            surveyItem.options[i] =
                                surveyItem.question.replace('/' + allVariables[i] + '/gi', replacements[i]);
                        }
                    }


                    }
                }
            }
        }
        return surveyItem;
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
            e = this.resolveVariablesForNetworkQuestions(e);
            console.log(e.type);
            if(e.type == "text"){ // setup text question
                str += '<div class="form-group" style="min-width: 300px;">'
                str += '<label for="answer'+i+'" id="question'+i+'" class="text-left text-dark row">'+ e.question +'</label>'
                if(e.video_url){
                    str += ' <div class="embed-responsive embed-responsive-16by9 row"><iframe class="embed-responsive-item" src="'+e.video_url+'" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe></div>'
                }
                str += '<input type="text" class="form-control row" id="answer'+i+'" data-cell-reference-index="'+i+'" placeholder="'+e.placeholder+'"></div> <br>'
            }
            else if(e.type == "radio"){ // setup radio question
                str += '<div class="form-group" style="min-width: 300px;">'
                str += '<label id="question'+i+'" class="text-left text-dark row">'+ e.question +'</label>'

                if(e.video_url){
                    str += ' <div class="embed-responsive embed-responsive-16by9 row"><iframe class="embed-responsive-item" src="'+e.video_url+'" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe></div>'
                }

                str += '<div id="answer'+i+'">'
                $.each(e.value,function(j, choice){ // setup radio question
                    str += '<div class="form-check form-inline row">'
                    str += '  <label class="form-check-label text-left text-dark">'
                    str += '    <input type="radio" class="form-check-input" name="answer'+i+'" value="'+choice+'" data-cell-reference-index="'+i+'">' + choice
                    str += '  </label> </div>'
                });

                str += ' </div> </div> <br>'
            }
            else if(e.type == "select") {
                str += '<div class="form-group" style="min-width: 300px;">'
                str += '<label for="answer'+i+'" id="question'+i+'" class="text-left text-dark row">'+e.question+'</label>'

                if(e.video_url){
                    str += ' <div class="embed-responsive embed-responsive-16by9 row"><iframe class="embed-responsive-item" src="'+e.video_url+'" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe></div>'
                }

                str += '<select class="form-control row" id="answer'+i+'" data-cell-reference-index="'+i+'">'
                $.each(e.value, function(j, option) {
                    str += '<option value="'+option+'">'+option+'</option>'
                });
                str += '</select></div> <br>'
            }
            else if(e.type == "checkbox") {
                str += '<div class="form-group" style="min-width: 300px;">'
                str += '<label id="question'+i+'" class="text-left text-dark row">'+ e.question +'</label>'

                if(e.video_url){
                    str += ' <div class="embed-responsive embed-responsive-16by9 row"><iframe class="embed-responsive-item" src="'+e.video_url+'" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe></div>'
                }

                str += '<div id="answer'+i+'">'
                $.each(e.value, function(j, choice){
                    str += '<div class="form-check form-inline row">'
                    str += '<label class="form-check-label text-dark">'
                    str += '<input type="checkbox" class="form-check-input" name="answer'+i+'" value="'+choice+'" data-cell-reference-index="'+i+'">' + choice
                    str += '</label> </div>'
                });
                str += '</div></div> <br>'
            }
            if(e.type == "introduction") {
                str += '<div class="form-group" style="min-width: 300px;">';
                str += '<label class="control-label text-dark text-left row question-intro">'+e.question+'</label>';

                if(e.video_url){
                    str += ' <div class="embed-responsive embed-responsive-16by9 row"><iframe class="embed-responsive-item" src="'+e.video_url+'" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe></div>'
                }
                str += '</div>';
            }

            console.log(i + '----'+ JSON.stringify(e));
        }.bind(this));

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
                if($("#answer" + cell).attr('type') == "text" && !($("#answer" + cell).is( ":focus" ))){ // sync text field when selected
                             $("#answer" + cell)
                                 .addClass(message.sender+"_color focusOpacity") //get subject backgroud COLOR
                }
            } else {
                if (attrName.indexOf("surveyAnswer_") != -1) {
                    var cell = attrName.replace("surveyAnswer_", "");
                    if($("#answer" + cell).attr('type') == "text"){ // sync text field
                        $("#answer" + cell).val(message.content.attributeStringValue);
                        $("#answer" + cell)
                        .delay(1000).queue(function (next) {
                                $(this).removeClass(message.sender+"_color focusOpacity");
                                next();
                            });
                    }

                }
                else if(attrName.indexOf("radioAnswer_") != -1){ //sync radio button
                    var question_number = attrName.replace("radioAnswer_", "");
                    var radioButtons = $("#answer"+question_number).find("input[value='"+message.content.attributeStringValue+"']").prop("checked",true);
                }
                //change to  (#answer + cell).checked = message.content attributeIntVal
                else if (attrName.indexOf("changeSelect_") != -1) {
                    var cell = attrName.replace("changeSelect_", "");
                    $("#answer" + cell).val(message.content.attributeStringValue)
                        .addClass(message.sender+"_color focusOpacity")
                        .delay(1000).queue(function (next) {
                            $(this).removeClass(message.sender+"_color focusOpacity");
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
                this.pogsPlugin.saveCompletedTaskAttribute('radioAnswer_' + cellIndex,
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