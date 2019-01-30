class Jeopardy {
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
        this.fields = [];
        this.globalFieldIndex = 0;
    }

    getPogsPlugin(){
        return this.pogsPlugin;
    }

    registerListenerAndGetFieldId(fieldImpl){

        let field = this.globalFieldIndex;
        //console.log("Field: --------" + this.globalFieldIndex);
        this.fields[field] = fieldImpl;
        this.globalFieldIndex = this.globalFieldIndex + 1;
        return field;
    }

    setupJeopardy(questionBluePrint, jeopardyBluePrint){
        console.info("Starting Jeopardy setup...");
        let self = this;
        var questionJson = $.parseJSON(questionBluePrint);
        var jeopardyJson = $.parseJSON(jeopardyBluePrint);
        this.fields.push(new JeopardyRadioField(this,questionJson,jeopardyJson));
    }
//Unedited
    broadcastReceived(message){
        let attrName = message.content.attributeName;
        let index = attrName
            .replace(JEOPARDY_CONST.FIELD_NAME, "")
            .replace(JEOPARDY_TRANSIENT.CLICK_RADIO_NOT_LOG,"")
            .replace(JEOPARDY_TRANSIENT.FOCUS_IN_CELL,"")
            .replace(JEOPARDY_TRANSIENT.MOUSE_OVER_FIELD,"")
            .replace(JEOPARDY_TRANSIENT.MOUSE_OUT_OF_FIELD,"");

        if(this.fields.length > index) {
            if(message.sender != this.pogsPlugin.subjectId) {
                this.fields[index].broadcastReceived(message);
            }
        }
    }

}

class SurveyTaskEdit {

    init(taskConfigId, currentAttributes) {
        let question_number = 0;


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

                this.fieldList.push(new RadioFieldEdit(question_number, bluePrint[i].question, withVideo, bluePrint[i].video_url, bluePrint[i].value,answerSheet[i],bluePrint[i].orientation));



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
