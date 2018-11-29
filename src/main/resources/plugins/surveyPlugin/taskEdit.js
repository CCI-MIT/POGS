class SurveyTaskEdit {
    
    init(taskConfigId, currentAttributes) {
        let question_number = 0;
        //test
        console.log("taskConfigId: " + taskConfigId);
        console.log("currentAttributes: " + currentAttributes);

        this.fieldList = [];

        this.taskConfigId = taskConfigId;
        var surveyBluePrint = null;
        var answerSheet = null;
        for (var i = 0; i < currentAttributes.length; i++) {
            if (currentAttributes[i].attributeName == "surveyBluePrint") {
                surveyBluePrint = $.parseJSON(currentAttributes[i].stringValue);
                surveyBluePrint.id = currentAttributes[i].id;
                console.log(surveyBluePrint);
            }
            if (currentAttributes[i].attributeName == "answerSheet") {
                answerSheet = $.parseJSON(currentAttributes[i].stringValue);
                answerSheet.id = currentAttributes[i].id;
                console.log(answerSheet);
            }
        }

        if (surveyBluePrint != null && answerSheet != null) {

            this.setupHtmlFromAttributeString(surveyBluePrint, answerSheet);
            createOrUpdateAttribute("surveyBluePrint",JSON.stringify(surveyBluePrint),null,null,this.taskConfigId,0, surveyBluePrint.id);
            createOrUpdateAttribute("answerSheet",JSON.stringify(answerSheet),null,null,this.taskConfigId,1, answerSheet.id);
            question_number = surveyBluePrint.length;
            sortable('#survey');

        }


        $("#addQuestion").click(function () { //Setup add question button

            let withVideo = $("#withVideo").prop("checked");
            if (($("#questionType")).val() == "text") { //For adding short answer (text) question
                this.fieldList.push(new InputFieldEdit(question_number, "", withVideo, "", "", ""));

            } else if (($("#questionType")).val() == "radio") { //For adding multiple choices question
                this.fieldList.push(new RadioFieldEdit(question_number, "", withVideo, "", [""],null));

            } else if(($("#questionType")).val() == "checkbox"){
                this.fieldList.push(new CheckboxFieldEdit(question_number, "", withVideo, "", [""],null));

            } else if(($("#questionType")).val() == "select"){
                this.fieldList.push(new SelectFieldEdit(question_number, "", withVideo, "", [""],null));

            } else if(($("#questionType")).val() == "introduction") {
                this.fieldList.push(new InformationFieldEdit(question_number, "", withVideo, "", [""]));
            } else if(($("#questionType")).val() == "radiotable") {
                this.fieldList.push(new RadioTableFieldEdit(question_number, "", withVideo, "", {columns: ["col 1","col 2"],rows: ["row 1","row 2"]},null));
            }

            question_number = question_number + 1;

            sortable('#survey');

        }.bind(this));

    }

    escapeValueStringsInQuotes(string) {
        return string.replace(/"/g, '\\"').replace(/'/g, '\\\'');
    }

    setupHtmlFromAttributeString(bluePrint, answerSheet){
        var question_number = 0;
        for(var i=0; i < bluePrint.length; i++){
            let withVideo = false;
            if(bluePrint[i].video_url != undefined){
                withVideo = true;
            }
            if(bluePrint[i].type == "text"){

                this.fieldList.push(new InputFieldEdit(question_number, bluePrint[i].question, withVideo, bluePrint[i].video_url, answerSheet[i], bluePrint[i].placeholder));

            } else if(bluePrint[i].type == "radio"){

                this.fieldList.push(new RadioFieldEdit(question_number, bluePrint[i].question, withVideo, bluePrint[i].video_url, bluePrint[i].value,answerSheet[i]));



            } else if(bluePrint[i].type == "checkbox"){

                this.fieldList.push(new CheckboxFieldEdit(question_number, bluePrint[i].question, withVideo, bluePrint[i].video_url, bluePrint[i].value,answerSheet[i]));

            } else if(bluePrint[i].type == "select"){

                this.fieldList.push(new SelectFieldEdit(question_number, bluePrint[i].question, withVideo, bluePrint[i].video_url, bluePrint[i].value,answerSheet[i]));




            } else if(bluePrint[i].type == "introduction") {
                this.fieldList.push(new InformationFieldEdit(question_number, bluePrint[i].question, withVideo, bluePrint[i].video_url));

            }else if(bluePrint[i].type == "radiotable"){

                this.fieldList.push(new RadioTableFieldEdit(question_number, bluePrint[i].question,
                                                            withVideo, bluePrint[i].video_url,
                                                            bluePrint[i].value,answerSheet[i]));

            }
            question_number++;
        }

    }

    setupAttributesFromHtml(){
        var bluePrint = [], answerSheet = [];

        for(let i = 0 ; i < this.fieldList.length; i ++ ){

            let field = this.fieldList[i];
            bluePrint.push(field.composeFieldFromHTML());

            answerSheet.push(field.composeAnswerFromHTML());


        }

        return  {bluePrint: JSON.stringify(bluePrint), answerSheet: JSON.stringify(answerSheet)};
    }
    beforeSubmit(){
        //return string message if should not save else return TRUE.

        //check all fields for non empty fields
        $.each($(":input[type=text]"),function(i,e){
            if($(e).val() == ""){
                console.log('Input text field???');
                return "Please fill in all text fields";
            }
        });


        let ret = this.validateAndChangeVideoURL();
        if( (typeof ret === 'string') || (ret instanceof String)){
            return ret;
        }

        let attr = this.setupAttributesFromHtml();
        console.log("Attributes before creating ");
        console.log(attr);
        createOrUpdateAttribute("surveyBluePrint",attr.bluePrint,null,null,this.taskConfigId,0, "");
        createOrUpdateAttribute("answerSheet",attr.answerSheet,null,null,this.taskConfigId,1, "");
    }

    validateAndChangeVideoURL(){

        let videoField = $(".video-input");

        for(let i= 0; i < videoField.length; i ++) {
            let field = $(videoField[i]);
            if(!validateYoutubeURL(field.val())){
                return "Invalid video URL!"
            }
            if(field.val().indexOf('youtube') != -1) {
                let videoId = getId(field.val());
                field.val('//www.youtube.com/embed/' + videoId);
            }

        }
    }

}

function validateYoutubeURL(url){
    if(url == ""){
        return false;
    }
    let regExp = /^(http(s)?:\/\/)?((w){3}.)?youtu(be|.be)?(\.com)?\/.+/;
    return url.match(regExp);

}
function getId(url) {

    var regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|\&v=)([^#\&\?]*).*/;
    var match = url.match(regExp);

    if (match && match[2].length == 11) {
        return match[2];
    } else {
        return 'error';
    }
}
pogsTaskConfigEditor.register(new SurveyTaskEdit());

