class OrderableField extends Field {
    constructor(surveyRefence,jsoninfo){
        super(surveyRefence,jsoninfo);
        this.index = this.registerListenerAndGetFieldId(this);
        this.setupHTML();
        this.setupHooks();

    }
    setupHTML(){
        let str = "";


        str += '<div class="form-group" id="surveyField_'+this.index+'" style="min-width: 300px;">';
        str += '<label for="answer'+this.index+'" id="question'+this.index+'" class="text-left text-dark row">'+ this.jsonInfo.question +'</label>'
        if(this.jsonInfo.video_url){
            str += new VideoInformation(this.jsonInfo.video_url).getHTML();
        }
        str += '<input type="text" class="form-control row" id="answer'+this.index+'" data-cell-reference-index="'+this.index+'" placeholder="'+this.jsonInfo.placeholder+'">';
        str += this.getInteractionIndicatorHTML();
        str += '</div> <br>';
        $('#surveyForm').append(str);
    }
    setupHooks(){
        super.setupHooks();
        $('#answer'+this.index + '').on('change', this.handleTextOnBlur.bind(this));

    }

    handleTextOnBlur(event){
        var cellIndex = parseInt($(event.target).data( "cell-reference-index"));
        //console.log("cell reference " + cellIndex);
        if(!isNaN(cellIndex)) {
            // console.log($(event.target))
            var valueTyped = $(event.target).val().replace(/\r?\n?/g, '').trim();
            // console.log(valueTyped);
            if(valueTyped != null) {
                this.getPogsPlugin().saveCompletedTaskAttribute(SURVEY_CONST.FIELD_NAME + cellIndex,
                                                                valueTyped, 0.0,
                                                                0, true, SURVEY_CONST.INPUT_FIELD);
            }
        }
    }
    broadcastReceived(message){
        super.broadcastReceived(message);

        let attrName = message.content.attributeName;


        if (attrName.indexOf(SURVEY_CONST.FIELD_NAME) != -1) {
            var cell = attrName.replace(SURVEY_CONST.FIELD_NAME, "");

            if($("#answer" + cell).attr('type') == "text"){ // sync text field
                $("#answer" + cell).val(message.content.attributeStringValue);
                this.setFinalAnswer(message.sender);
            }

        }
    }


}