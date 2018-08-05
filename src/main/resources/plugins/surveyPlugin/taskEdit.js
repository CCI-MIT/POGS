class SurveyTaskEdit {
    
    init(taskConfigId, currentAttributes) {
        var question_number = 0;
        //test
        console.log("taskConfigId: " + taskConfigId);
        console.log("currentAttributes: " + currentAttributes)

        this.taskConfigId = taskConfigId;
        var surveyBluePrint = null;
        var answerSheet = null;
        for (var i = 0; i < currentAttributes.length; i++) {
            if (currentAttributes[i].attributeName == "surveyBluePrint") {
                surveyBluePrint = $.parseJSON(currentAttributes[i].stringValue);
                console.log(surveyBluePrint);
            }
            if (currentAttributes[i].attributeName == "answerSheet") {
                answerSheet = $.parseJSON(currentAttributes[i].stringValue);
                console.log(answerSheet);
            }
        }

        if (surveyBluePrint != null && answerSheet != null) {

            this.setupHtmlFromAttributeString(surveyBluePrint, answerSheet);
            question_number = surveyBluePrint.length;
            sortable('#survey');
            // createOrUpdateAttribute("surveyBluePrint", surveyBluePrint.stringValue, null, null, this.taskConfigId, 0, surveyBluePrint.id);
            // createOrUpdateAttribute("answerSheet", answerSheet.stringValue, null, null, this.taskConfigId, 1, answerSheet.id);
        }


        $("#addQuestion").click(function () { //Setup add question button

            var withVideo = $("#withVideo").prop("checked");
            if (($("#questionType")).val() == "text") { //For adding short answer (text) question
                this.addTextQuestion(question_number, "", withVideo, "", "", "");

            } else if (($("#questionType")).val() == "radio") { //For adding multiple choices question
                this.addRadioQuestion(question_number, "", withVideo, "", [""]);

            } else if(($("#questionType")).val() == "checkbox"){
                this.addCheckboxQuestion(question_number, "", withVideo, "", [""]);

            } else if(($("#questionType")).val() == "select"){
                this.addSelectQuestion(question_number, "", withVideo, "", [""]);

            } else if(($("#questionType")).val() == "introduction") {
                this.addIntroduction(question_number, "", withVideo, "", [""]);
            }

            question_number = question_number + 1;

            sortable('#survey');

        }.bind(this));

    }

    addTextQuestion(question_number, question, withVideo, video_url, answer, placeholder){
        var str = "";

        str += '<div class="container question_set" id="question_set' + question_number + '" data-question-type = "text">';

        //add question field
        str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Question: </label><input class="form-control col-sm-8" type="text" id="question' + question_number + '" placeholder = "Put question here" value="'+question+'">  <button type="button" class="btn btn-danger remove-question btn-sm" id="removeQuestion' + question_number + '">remove</button> </div>';

        if(withVideo){ //add field for video url
            str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Video_url: </label> <input class="form-control col-sm-8" type="text" id="video_url' + question_number + '" placeholder = "Put video url" value="'+video_url+'"></div>';
        }

        //add answer field
        str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Answer: </label><input type="text" class="form-control col-sm-8" id="answer'+question_number+'" placeholder = "Put answer here" value="'+answer+'"></div>';

        //add placeholder field
        str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Placeholder: </label> <input class="form-control col-sm-8" type="text" id="placeholder'+question_number+'" placeholder = "Put placeholder here" value="'+placeholder+'"></div>';
        str += '</div>'

        str += '</div>  '

        $("#survey").append(str);

        $("#removeQuestion"+question_number).click(function () { //setup removeQuestion Button
            var question_set = "#question_set" + $(this).attr('id').match(/\d+/);

            $(question_set).remove();
        });
    }

    addRadioQuestion(question_number, question, withVideo, video_url, choices){
        var str = "";
        str += '<div class="container question_set" id="question_set' + question_number + '" data-question-type = "radio" >'

        //add question field
        str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Question: </label><input class=" form-control col-sm-8" type="text" id="question' + question_number + '" placeholder = "Put question here" value="'+question+'"> <button type="button" class="btn btn-danger remove-question btn-sm" id="removeQuestion' + question_number + '">remove</button> </div>'

        if(withVideo){ //add field for video url
            str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Video_url: </label> <input class="form-control col-sm-8" type="text" id="video_url' + question_number + '" placeholder = "Put video url" value="'+video_url+'"></div>';
        }

        str += '<div id="answer'+question_number+'">'
        //add button and choices field
        $.each(choices,function(i){
            str += '<div class="form-check form-inline row">'
            str += '<div class="col-sm-1"> </div>'
            str += '<input class="col-sm-1" type="radio" name="answer' + question_number + '"> '
            str += '<input type="text" class="form-control col-sm-8" placeholder="put radio label here" value="'+choices[i]+'"> '
            str += '<button type="button" class="btn btn-danger btn-sm remove-radio-choice"> X </button>'
            str += '</div>'
        });
        str += '</div>'

        str += '<div class="form-check form-inline row"> <div class="col-sm-2"> </div> <button type="button" class="btn btn-secondary btn-sm add-radio-choice" id="addRadioChoice' + question_number + '">+</button> </div>'

        str += '</div> '

        $("#survey").append(str);

        $("#addRadioChoice" + question_number).click(function () { //setup addRadioChoice Button
            var questionNum = $(this).attr('id').match(/\d+/);
            var newRadio = '<div class="form-check form-inline row"><div class="col-sm-1"> </div><input class="col-sm-1" type="radio" name="answer' + questionNum + '" > <input  class="form-control col-sm-8" type="text" placeholder="put radio label here"> <button type="button" class="btn btn-danger btn-sm remove-radio-choice"> X </button></div>'
            $("#answer"+questionNum).append(newRadio);

            $(".remove-radio-choice").click(function () {
                ($(this).parent()).remove();
            });
        });

        $(".remove-radio-choice").click(function () { //setup removeRadio Button
            ($(this).parent()).remove();
        });

        $("#removeQuestion"+question_number).click(function () { //setup removeQuestion Button
            var question_set = "#question_set" + $(this).attr('id').match(/\d+/);

            $(question_set).remove();

        });
    }

    addCheckboxQuestion(question_number, question, withVideo, video_url, choices){
        var str = "";
        str += '<div class="container question_set" id="question_set' + question_number + '" data-question-type = "checkbox" >'

        //add question field
        str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Question: </label><input class=" form-control col-sm-8" type="text" id="question' + question_number + '" placeholder = "Put question here" value="'+question+'"> <button type="button" class="btn btn-danger remove-question btn-sm" id="removeQuestion' + question_number + '">remove</button> </div>'

        if(withVideo){ //add field for video url
            str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Video_url: </label> <input class="form-control col-sm-8" type="text" id="video_url' + question_number + '" placeholder = "Put video url" value="'+video_url+'"></div>';
        }

        //add button and choices field
        str += '<div id="answer'+question_number+'">'
        $.each(choices,function(i){
            str += '<div class="form-check form-inline row">'
            str += '<div class="col-sm-1"> </div>'
            str += '<input class="col-sm-1" type="checkbox" name="answer' + question_number + '"> '
            str += '<input type="text" class="form-control col-sm-8" placeholder="put checkbox label here" value="'+choices[i]+'"> '
            str += '<button type="button" class="btn btn-danger btn-sm remove-checkbox-choice"> X </button>'
            str += '</div>'
        });
        str += '</div>'


        str += '<div class="form-check form-inline row"> <div class="col-sm-2"> </div> <button type="button" class="btn btn-secondary btn-sm add-checkbox-choice" id="addCheckboxChoice' + question_number + '">+</button> </div>'

        str += '</div> '

        $("#survey").append(str);

        $("#addCheckboxChoice" + question_number).click(function () { //setup addCheckboxChoice Button
            var questionNum = $(this).attr('id').match(/\d+/);
            var newCheckbox = '<div class="form-inline form-check row"><div class="col-sm-1"> </div><input class="col-sm-1" type="checkbox" name="answer' + questionNum + '" > <input  class="form-control col-sm-8" type="text" placeholder="put checkbox label here"> <button type="button" class="btn btn-danger btn-sm remove-checkbox-choice"> X </button></div>'
            $("#answer"+questionNum).append(newCheckbox);

            $(".remove-checkbox-choice").click(function () {
                ($(this).parent()).remove();
            });
        });

        $(".remove-checkbox-choice").click(function () { //setup remove checkbox button
            ($(this).parent()).remove();
        });

        $("#removeQuestion"+question_number).click(function () { //setup removeQuestion Button
            var question_set = "#question_set" + $(this).attr('id').match(/\d+/);

            $(question_set).remove();

        });
    }

    addSelectQuestion(question_number, question, withVideo, video_url, choices){
        var str = "";
        str += '<div class="container question_set" id="question_set' + question_number + '" data-question-type = "select" >'

        //add question field
        str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Question: </label><input class=" form-control col-sm-8" type="text" id="question' + question_number + '" placeholder = "Put question here" value="'+question+'"> <button type="button" class="btn btn-danger remove-question btn-sm" id="removeQuestion' + question_number + '">remove</button> </div>'

        if(withVideo){ //add field for video url
            str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Video_url: </label> <input class="form-control col-sm-8" type="text" id="video_url' + question_number + '" placeholder = "Put video url" value="'+video_url+'"></div>';
        }

        //add button and choices field
        str += '<div class="form-group row"> <div class="col-sm-2"> </div> <select class="col-sm-8" id="answer'+question_number+'"> </select></div>'

        str += '<div id="answerChoices'+question_number+'">'
        $.each(choices,function(i){
            str += '<div class="form-check form-inline row">'
            str += '<div class="col-sm-2"> </div>'
            str += '<input type="text" class="form-control col-sm-8" placeholder="put select choices here" value="'+choices[i]+'"> '
            str += '<button type="button" class="btn btn-danger btn-sm remove-select-choice"> X </button>'
            str += '</div>'
        });
        str += '</div>'

        str += '<div class="form-check form-inline row"> <div class="col-sm-2"> </div> <button type="button" class="btn btn-secondary btn-sm add-select-choice" id="addSelectChoice' + question_number + '">+</button> </div>'

        str += '</div> '

        $("#survey").append(str);

        $.each(choices,function(i){
            $("#answer"+question_number).append('<option value="'+choices[i]+'">'+choices[i]+'</option>');
        });

        $("#addSelectChoice" + question_number).click(function () { //setup addSelectChoice Button
            var questionNum = $(this).attr('id').match(/\d+/);
            var newSelectChoice = '<div class="form-check form-inline row"><div class="col-sm-2"> </div><input  class="form-control col-sm-8" type="text" placeholder="put select label here"> <button type="button" class="btn btn-danger btn-sm remove-select-choice"> X </button></div>'
            $("#answerChoices"+questionNum).append(newSelectChoice);

            $(".remove-select-choice").click(function () {
                var valueOfDeletedField = $(this).siblings('input').val();
                if(valueOfDeletedField != ""){
                    ($("#answer"+questionNum+" option[value="+valueOfDeletedField+"]")).remove();
                }
                ($(this).parent()).remove();
            });

            $("#answerChoices"+questionNum).find("input").on("blur",function () {
                $("#answer"+questionNum).empty();
                $.each($("#answerChoices"+questionNum).find("input"),function(i,e){
                    if(e.value != ""){
                        $("#answer"+questionNum).append('<option value="'+e.value+'">'+e.value+'</option>');
                    }
                });
            });
        });

        $(".remove-select-choice").click(function () {
            var questionNum = $(this).parents("div[id*=question_set]").attr("id").match(/\d+/);
            var valueOfDeletedField = $(this).siblings('input').val();
            if(valueOfDeletedField != ""){
                ($("#answer"+questionNum+" option[value="+valueOfDeletedField+"]")).remove();
            }
            ($(this).parent()).remove();
        });

        $("#answerChoices"+question_number).find("input").on("blur",function () {
            var questionNum = $(this).parents("div[id*=question_set]").attr("id").match(/\d+/);
            $("#answer"+questionNum).empty();
            $.each($("#answerChoices"+questionNum).find("input"),function(i,e){
                if(e.value != ""){
                    $("#answer"+questionNum).append('<option value="'+e.value+'">'+e.value+'</option>');
                }
            });
        });

        $("#removeQuestion"+question_number).click(function () { //setup removeQuestion Button
            var question_set = "#question_set" + $(this).attr('id').match(/\d+/);

            $(question_set).remove();

        });
    }

    addIntroduction(question_number, question, withVideo, video_url) {
        var str = "";
        str += '<div class="container question_set" id="question_set' + question_number + '" data-question-type = "introduction">';
        // add text area
        str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Introduction:</label><textarea class="form-control col-sm-8" id="question'+question_number+'" placeholder="Introduction goes here">'+question+'</textarea><button type="button" class="btn btn-danger btn-sm remove-intro" id="removeQuestion' + question_number + '">remove</button> </div>';
        // str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Question: </label><input class="form-control col-sm-8" type="text" id="question' + question_number + '" placeholder = "Put question here" value="'+question+'">  <button type="button" class="btn btn-danger remove-question btn-sm" id="removeQuestion' + question_number + '">remove</button> </div>';

        if(withVideo){ // add video url
            str += '<div class="form-group row"><label class="col-sm-2 col-form-label">Video_url: </label> <input class="form-control col-sm-8" type="text" id="video_url' + question_number + '" placeholder = "Put video url" value="'+video_url+'"></div>';
        }

        str += '</div>';

        $("#survey").append(str);

        $("#removeQuestion"+question_number).click(function () { //setup removeQuestion Button
            var question_set = "#question_set" + $(this).attr('id').match(/\d+/);

            $(question_set).remove();
        });
    }



    setupHtmlFromAttributeString(bluePrint, answerSheet){
        var question_number = 0;
        console.log("setupHtmlFromAttributeString TEST!!!");
        for(var i=0; i < bluePrint.length; i++){
            if(bluePrint[i].type == "text"){
                var withVideo = false;
                if(bluePrint[i].video_url != undefined){
                    withVideo = true;
                }
                this.addTextQuestion(question_number, bluePrint[i].question, withVideo, bluePrint[i].video_url, answerSheet[i], bluePrint[i].placeholder);

            } else if(bluePrint[i].type == "radio"){
                var withVideo = false;
                if(bluePrint[i].video_url != undefined){
                    withVideo = true;
                }
                this.addRadioQuestion(question_number, bluePrint[i].question, withVideo, bluePrint[i].video_url, bluePrint[i].value);

                //select answer for radio question
                $('#question_set'+question_number+' div input[value="'+answerSheet[i]+'"]').parent().find('input[type=radio]').prop("checked",true);

            } else if(bluePrint[i].type == "checkbox"){
                var withVideo = false;
                if(bluePrint[i].video_url != undefined){
                    withVideo = true;
                }
                this.addCheckboxQuestion(question_number, bluePrint[i].question, withVideo, bluePrint[i].video_url, bluePrint[i].value);

                //select answer for checkbox question
                $.each(answerSheet[i],function(j,e){
                    $('#question_set'+question_number+' div input[value="'+e+'"]').parent().find('input[type=checkbox]').prop("checked",true);
                });


            } else if(bluePrint[i].type == "select"){
                var withVideo = false;
                if(bluePrint[i].video_url != undefined){
                    withVideo = true;
                }
                this.addSelectQuestion(question_number, bluePrint[i].question, withVideo, bluePrint[i].video_url, bluePrint[i].value);

                //select answer for checkbox question
                $('#question_set'+question_number+' div select option[value="'+answerSheet[i]+'"]').prop("selected",true);

            } else if(bluePrint[i].type == "introduction") {
                var withVideo = false;
                if(bluePrint[i].video_url != undefined){
                    withVideo = true;
                }
                this.addIntroduction(question_number, bluePrint[i].question, withVideo, bluePrint[i].video_url);
            }


            question_number++;
        }

    }

    setupAttributesFromHtml(){
        var bluePrint = [], answerSheet = [];

        $.each($(".question_set"),function(i,e){
            var questionNum = $(this).attr('id').match(/\d+/);
            var question_set = {};
            var placeholder = "";

            // create question_set and insert it into bluePrint
            question_set["question"] = $("#question"+questionNum).val();
            question_set["type"] = $(this).attr('data-question-type');
            if($("#video_url"+questionNum).val() != undefined){ // if question contains video_url add it
                question_set["video_url"] = $("#video_url"+questionNum).val();
            }

            if($("#placeholder"+questionNum)){ // if question contains placeholder add it
                question_set["placeholder"] = $("#placeholder"+questionNum).val();
            }

            // adding options/choices for multiple choices questions
            if($(this).attr('data-question-type') == "radio"){ // if radio question add all choices
                var choices = [];
                $.each($("#answer"+questionNum).find("input[type=text]"),function(j,input){
                    choices.push(input.value);
                });
                question_set["value"] = choices;
            } else if($(this).attr('data-question-type') == "checkbox"){ // if checkbox question add all choices
                var choices = [];
                $.each($("#answer"+questionNum).find("input[type=text]"),function(j,input){
                    choices.push(input.value);
                });
                question_set["value"] = choices;
            }else if($(this).attr('data-question-type') == "select"){ // if select question add all choices
                var choices = [];
                $.each($("#answerChoices"+questionNum).find("input[type=text]"),function(j,input){
                    choices.push(input.value);
                });
                question_set["value"] = choices;
            }
            bluePrint.push(question_set);

            // insert into answerSheet
            if($(this).attr('data-question-type') == "text"){
                var answer = "";
                answer =  $("#answer"+questionNum).val();
                answerSheet.push(answer);
            }else if($(this).attr('data-question-type') == "radio"){
                var answer = "";
                answer = $("#question_set"+questionNum+" div input:checked").siblings("input[type=text]").val();
                answerSheet.push(answer);
            }else if($(this).attr('data-question-type') == "checkbox"){
                answer = [];
                $.each($("#question_set"+questionNum+" div input:checked"), function(){
                    answer.push($(this).siblings("input[type=text]").val());
                });
                answerSheet.push(answer);
            }else if($(this).attr('data-question-type') == "select"){
                var answer = "";
                answer = $("#question_set"+questionNum+" option:checked").val();
                answerSheet.push(answer);
            }

        });

        return  {bluePrint: JSON.stringify(bluePrint), answerSheet: JSON.stringify(answerSheet)};
    }
    beforeSubmit(){
        //return string message if should not save else return TRUE.

        //check all fields for non empty fields
        $.each($(":input[type=text]"),function(i,e){
            if(e.value == ""){
                return "Please fill in all text fields";
            }
        });

        var attr = this.setupAttributesFromHtml();

        createOrUpdateAttribute("surveyBluePrint",attr.bluePrint,null,null,this.taskConfigId,0, "");
        createOrUpdateAttribute("answerSheet",attr.answerSheet,null,null,this.taskConfigId,1, "");
    }

}
pogsTaskConfigEditor.register(new SurveyTaskEdit());

