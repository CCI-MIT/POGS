

class Survey {
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
        this.replacements  = [];
        this.replacements.push(this.pogsPlugin.getTeammatesDisplayNames());
        this.replacements.push(this.pogsPlugin.getOtherTeammates());
        this.replacements.push(this.pogsPlugin.getLastTask());
        this.replacements.push(this.pogsPlugin.getTaskList());
        this.replacements.push(this.pogsPlugin.getOtherTasks());
        this.replacements.push(this.pogsPlugin.getSessionName());

        this.fields = [];
        this.globalFieldIndex = 0;
        this.totalAnswerableFields = 0;
        this.lastCheckedAnswers = 0;
    }
    showHelperTextOnTop(){
        $("#surveyForm").css("margin-top",'20px');
        this.updateTextOnHelperText();
    }
    updateTextOnHelperText(){

        let answeredFields = 0;
        for(let i=0;i<this.fields.length;i++){
            if(this.fields[i].jsonInfo.type != "introduction" && this.fields[i].finalAnswerSubject!=null){
                console.log('this.fields[i]: ' + i + ' - ' + this.fields[i].jsonInfo.type)
                answeredFields++;
            }
        }
        if( this.lastCheckedAnswers ==0 || (this.lastCheckedAnswers != answeredFields ) ) {
            $("#descriptionOfFieldsAnswered")
                .html('Answered: '+answeredFields+' out ' + this.totalAnswerableFields + ' questions');
            this.lastCheckedAnswers = answeredFields
        }
    }
    resolveVariablesForNetworkQuestions(surveyItem){
        var regex = new RegExp(/\${.*}/gi);
        var allVariables = ['\\${allTeammates}','\\${otherTeamates}', '\\${lastTaskName}',
                            '\\${allTasksNames}','\\${otherTasksNames}', '\\${sessionName}'];

        var replacements = this.replacements;
        //console.log(" ---- CUSTOM VARIABLES: " + this.customVariables)
        if(this.customVariables!= null) {
            let custVar = JSON.parse(this.customVariables);
            for(var i= 0; i < custVar.length; i++) {
                allVariables.push('\\' + custVar[i].variableName);
                if (custVar[i].variableReplacements != null) {
                    replacements.push(custVar[i].variableReplacements)
                } else {
                    replacements.push("")
                }
            }
        }

        // [['m01', 'm02', 'm03'],
        //     ['m02', 'm03'],
        //     "Last task name",
        //     ["tast 1", "task 2","task 3"],
        //     ["task 2","task3"],
        //     "session one"]

        if(surveyItem.question.match(regex)) {

            for(var i=0; i < allVariables.length; i ++) {

                var replacer = "";
                if(surveyItem.question.match(new RegExp(allVariables[i] ,'gi'))) {
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
                        surveyItem.question.replace( new RegExp( allVariables[i] ,'gi'), replacer);
                }
            }
        }
        if(!surveyItem.value){
            return surveyItem;
        }

        if (surveyItem.value.constructor === Array) {
            //console.log("value is array");
            if (surveyItem.value !== undefined && surveyItem.value.length > 0) {
                for (var i = 0; i < surveyItem.value.length; i++) {

                    if (surveyItem.value[i].match(new RegExp(regex, 'gi'))) {

                        for (var j = 0; j < allVariables.length; j++) {
                            if (surveyItem.value[i].match(new RegExp(allVariables[j], 'gi'))) {

                                if (replacements[i].constructor === Array) {
                                    surveyItem.value = [];

                                    for (var k = 0; k < replacements[j].length; k++) {
                                        surveyItem.value.push(replacements[j][k]);
                                    }
                                    return surveyItem;
                                } else {
                                    surveyItem.value[i] =
                                        surveyItem.value.replace(new RegExp(allVariables[j], 'gi'),
                                                                 replacements[j]);
                                }
                            }
                        }
                    }

                }
            }
        } else {

            if (surveyItem.value.constructor === Object) {
                //console.log("value is object");
                for (let i = 0; i < surveyItem.value.columns.length; i++) {

                    if (surveyItem.value.columns[i].match(new RegExp(regex, 'gi'))) {

                        for (let j = 0; j < allVariables.length; j++) {
                            if (surveyItem.value.columns[i].match(new RegExp(allVariables[j], 'gi'))) {

                                if (replacements[i].constructor === Array) {
                                    surveyItem.value.columns = [];

                                    for (let k = 0; k < replacements[j].length; k++) {
                                        surveyItem.value.columns.push(replacements[j][k]);
                                    }
                                    return surveyItem;
                                } else {
                                    surveyItem.value.columns[i] =
                                        surveyItem.value.columns.replace(new RegExp(allVariables[j], 'gi'),
                                                                         replacements[j]);
                                }
                            }
                        }
                    }
                }
                for (let i = 0; i < surveyItem.value.rows.length; i++) {

                    if (surveyItem.value.rows[i].match(new RegExp(regex, 'gi'))) {

                        for (let j = 0; j < allVariables.length; j++) {
                            if (surveyItem.value.rows[i].match(new RegExp(allVariables[j], 'gi'))) {

                                if (replacements[i].constructor === Array) {

                                    surveyItem.value.rows = [];

                                    for (let k = 0; k < replacements[j].length; k++) {
                                        surveyItem.value.rows.push(replacements[j][k]);
                                    }
                                    return surveyItem;
                                } else {

                                    surveyItem.value.rows[i] =
                                        surveyItem.value.rows.replace(new RegExp(allVariables[j], 'gi'),
                                                                      replacements[j]);
                                }
                            }
                        }
                    }
                }
            }
        }
        return surveyItem;
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
    setupSurvey(surveyBluePrint, customVariables, orderOfFields){
        console.info("starting survey setup...");
        this.customVariables = customVariables;

        let surveyValues = $.parseJSON(surveyBluePrint);
        let self = this;

        let str = '';
        let hasRandomizedFields = false;
        for( let i = 0; i < surveyValues.length;i++) {
            let e = surveyValues[i];
            e = this.resolveVariablesForNetworkQuestions(e);
            if(e.shouldRandomize) {
                hasRandomizedFields = true;
            }
            if(e.type == "text"){ // setup text question
                new InputField(this,e);
                this.totalAnswerableFields++;
            }
            else if(e.type == "radio"){ // setup radio question
                new RadioField(this,e);
                this.totalAnswerableFields++;
            }
            else if(e.type == "select") {
                new SelectField(this,e);
                this.totalAnswerableFields++;
            }
            else if(e.type == "checkbox") {
                new CheckboxField(this,e);
                this.totalAnswerableFields++;
            }
            else if(e.type == "introduction") {
                new InformationField(this,e);
            }else if(e.type == "radiotable"){ // setup radio question
                new RadioTableField(this,e);
                this.totalAnswerableFields++;
            }






            //console.log(i + '----'+ JSON.stringify(e));
        }

        if(hasRandomizedFields) {
            let orderOfFieldsJSON = JSON.parse(orderOfFields);
            for(var i=0;i< orderOfFieldsJSON.length;i++){
                $("#surveyField_" + orderOfFieldsJSON[i])
                    .appendTo("#surveyFormOrdered");
            }
        }
        this.showHelperTextOnTop();

    }

    broadcastReceived(message){
        let attrName = message.content.attributeName;
        let index = attrName
            .replace(SURVEY_CONST.FIELD_NAME, "")
            .replace(SURVEY_TRANSIENT.CLICK_RADIO_NOT_LOG,"")
            .replace(SURVEY_TRANSIENT.FOCUS_IN_CELL,"")
            .replace(SURVEY_TRANSIENT.CLICK_CHECKBOX_NOT_LOG,"")
            .replace(SURVEY_TRANSIENT.MOUSE_OVER_FIELD,"")
            .replace(SURVEY_TRANSIENT.MOUSE_OUT_OF_FIELD,"");

        if(this.fields.length > index) {
            if(message.sender != this.pogsPlugin.subjectId) {
                this.fields[index].broadcastReceived(message);
            }
            this.updateTextOnHelperText();
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
            sortable('#survey')[0].addEventListener('sortupdate', function(e) {


                resetOrder();

            });

        }

        $(".move_toggle").click(function(event){
            $(".content").toggle();
            let tx = $($(".move_toggle")[0]).text();
            $(".move_toggle").text(((tx=="Minimize")?("Maximize"):("Minimize")));
            event.stopPropagation();
        });


        $("#addQuestion").click(function () { //Setup add question button

            question_number = question_number + 1;
            let withVideo = $("#withVideo").prop("checked");
            if (($("#questionType")).val() == "text") { //For adding short answer (text) question
                this.fieldList.push(new InputFieldEdit(question_number, "", withVideo, "", "", "", false));

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

            this.registerDeleteButton();

            sortable('#survey')[0].addEventListener('sortupdate', function(e) {


                resetOrder();

            });
            resetOrder();

        }.bind(this));
        this.registerDeleteButton();

    }

    setupHtmlFromAttributeString(bluePrint, answerSheet){
        var question_number = 1;
        for(var i=0; i < bluePrint.length; i++){
            let withVideo = false;
            if(bluePrint[i].video_url != undefined){
                withVideo = true;
            }
            if(bluePrint[i].type == "text"){

                this.fieldList.push(new InputFieldEdit(question_number, bluePrint[i].question, withVideo, bluePrint[i].video_url, answerSheet[i], bluePrint[i].placeholder, bluePrint[i].shouldRandomize));

            } else if(bluePrint[i].type == "radio"){

                this.fieldList.push(new RadioFieldEdit(question_number, bluePrint[i].question, withVideo, bluePrint[i].video_url, bluePrint[i].value,answerSheet[i],bluePrint[i].orientation, bluePrint[i].shouldRandomize));



            } else if(bluePrint[i].type == "checkbox"){

                this.fieldList.push(new CheckboxFieldEdit(question_number, bluePrint[i].question, withVideo, bluePrint[i].video_url, bluePrint[i].value,answerSheet[i],bluePrint[i].shouldRandomize));

            } else if(bluePrint[i].type == "select"){

                this.fieldList.push(new SelectFieldEdit(question_number, bluePrint[i].question, withVideo, bluePrint[i].video_url, bluePrint[i].value,bluePrint[i].shouldRandomize));




            } else if(bluePrint[i].type == "introduction") {
                this.fieldList.push(new InformationFieldEdit(question_number, bluePrint[i].question, withVideo, bluePrint[i].video_url,bluePrint[i].shouldRandomize));

            }else if(bluePrint[i].type == "radiotable"){

                this.fieldList.push(new RadioTableFieldEdit(question_number, bluePrint[i].question,
                                                            withVideo, bluePrint[i].video_url,
                                                            bluePrint[i].value,answerSheet[i],bluePrint[i].shouldRandomize));

            }
            question_number++;
        }

    }
    deleteItem(e) { //setup removeQuestion Button
        var id = $(e.target).attr('id').match(/\d+/);

        var question_set = "#question_set" + id;
        var index = parseInt(id);

        let newOrderFieldList = []
        for(let i = 0 ; i < this.fieldList.length; i ++ ){
            if((index -1) != i){
                newOrderFieldList.push(this.fieldList[i]);
            }
        }
        this.fieldList = newOrderFieldList;

        $(question_set).remove();
        resetOrder();
    }
    registerDeleteButton(){

        $(".remove-question").unbind().click(this.deleteItem.bind(this));

    }
    setupAttributesFromHtml(){
        var bluePrint = [], answerSheet = [];

        let newOrderFieldList = []
        for(let i = 0 ; i < this.fieldList.length; i ++ ){
            let fielNewPosition = $('#question_set'+this.fieldList[i].questionNumber + ' .question_number').text();
            newOrderFieldList[(parseInt(fielNewPosition) -1)] = this.fieldList[i];
        }

        this.fieldList = newOrderFieldList;

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

function resetOrder(){
    $(".question_number").each(function( index ) {
        //console.log( index + ": " + $( this ).text() );
        $( this ).text(+ (index + 1))
    });
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