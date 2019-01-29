class JeopardyRadioField extends JeopardyField {
    constructor(jeopardyReference,questionJson,jeopardyJson){
        super(jeopardyReference,questionJson,jeopardyJson);
        //todo
        this.index = this.registerListenerAndGetFieldId(this);
        //open file and keep the questions in a variable
        //______


        this.questionJson = [{
            "ID":1
            ,"question":"On the verge of going bust in 1997, it was saved by a $150M investment by rival Microsoft"
            ,"Answer":"Apple"
            ,"value":["HP", "Google", "Dell", "Apple"]
            ,"Category":"Science and Technology"
            ,"Level":"Medium"
        },{"ID":2
            ,"question":"On the verge of going bust in 1997, it was saved by a $150M investment by rival Microsoft"
            ,"Answer":"Apple"
            ,"value":["HP", "Google", "Dell", "Apple"]
            ,"Category":"Science and Technology"
            ,"Level":"Medium"
    }];

        this.localPogsPlugin = this.getPogsPlugin();
        var teammates = this.localPogsPlugin.getTeammates();
        var currentId = this.localPogsPlugin.getSubjectId();
        for (var i=0;i<teammates.length;i++){
            if (currentId==teammates[i]){
                if (i==0)
                    this.prob = jeopardyJson.prob1;
                else if (i==1)
                    this.prob = jeopardyJson.prob2;
                else if (i==2)
                    this.prob = jeopardyJson.prob3;
                else
                    this.prob = jeopardyJson.prob4;
            }
        }
        this.setupHTML();
        this.setupHooks();
    }
    setupHTML(){
        let str = "";
        str += '<div class="form-group" id="jeopardyField_'+this.index+'" style="min-width: 300px;">'
        str += '<label id="question'+this.index+'" class="text-left text-dark row">'+ this.questionJson.question +'</label>'

        str += '<div id="answer'+this.index+'">'

        $.each(this.questionJson.value, function (j, choice) { // setup radio question
            str += '<div class="form-check form-inline row">'
            str += '  <label class="form-check-label text-left text-dark">'
            str +=
                '    <input type="radio" class="form-check-input" name="answer' + this.index
                + '" value="' + choice + '" data-cell-reference-index="' + this.index + '">'
                + choice
            str += '  </label> </div>'

        }.bind(this));

        str += ' </div> ';
        str += this.getInteractionIndicatorHTML();
        str+= '</div> <br>';

        $('#jeopardyForm').append(str);
    }
    setupHooks(){
        super.setupHooks();
        $('#answer'+this.index+' input').on('change',this.handleRadioOnClick.bind(this));
    }
    handleRadioOnClick(event){
        let cellIndex = parseInt($(event.target).data( "cell-reference-index"));
        //console.log("answer " + cellIndex);
        if(!isNaN(cellIndex)) {
            // console.log($(event.target))
            var valueTyped = $(event.target).attr('value'); // value of radio button
            // console.log("Typed Value: " + valueTyped);
            if(valueTyped != null) {
                this.getPogsPlugin().saveCompletedTaskAttribute(SURVEY_CONST.FIELD_NAME + cellIndex,
                    valueTyped, 0.0,
                    0, true, SURVEY_CONST.RADIO_FIELD);
            }
        }
    }
    broadcastReceived(message){
        super.broadcastReceived(message);
        let attrName = message.content.attributeName;
        if(attrName.indexOf(SURVEY_CONST.FIELD_NAME) != -1){ //sync radio button
            var question_number = attrName.replace(SURVEY_CONST.FIELD_NAME, "");
            var radioButtons = $("#answer"+question_number).find("input[value='"+message.content.attributeStringValue+"']").prop("checked",true);
            this.setFinalAnswer(message.sender);
            //Show next questions
            //End of round give a message
            //End of task -> Thanks
        }
    }
}
