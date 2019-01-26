class JeopardyTaskPlugin{
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
        this.gridOrder = null;
        this.orderIndex = -1;
        this.totalFieldIndex = -1;
        this.teamMates = this.pogsPlugin.getTeammates();
    }
    setup(bluePrint,gridOrder) {

        if(bluePrint.taskText) {
            $("#taskText").html(bluePrint.taskText);
        }
        this.gridOrder = gridOrder;
        this.createNextFieldAndWhoShouldBeAbleToEdit();
    }
    createNextFieldAndWhoShouldBeAbleToEdit(){
        this.orderIndex++;
        this.totalFieldIndex++;
        if(this.orderIndex == this.teamMates.length){
            this.orderIndex = 0;
        }

        let div = $('<div/>', {
            'id': 'cell_' + this.orderIndex
        });



        let sub = this.teamMates[this.orderIndex];

        let inp = null;
        if(sub.externalId == this.pogsPlugin.getSubjectId()) {
            inp = $('<input/>', {
                'id': 'inputEquation_' + this.totalFieldIndex,
                'data-cell-reference-index': this.totalFieldIndex
            });
        } else {
            inp = $('<input/>', {
                'id': 'inputEquation_' + this.totalFieldIndex,
                'disabled': 'disabled',
                'data-cell-reference-index': this.totalFieldIndex
            });
        }
        let btn  = $('<input/>', {
            "class": "btn btn-info",
            text: "Done"
        });

        div.append(inp);
        div.append(btn);

        let workOn = $('<div/>', {
            'class': 'workingOn'
        });



        if(sub.externalId == this.pogsPlugin.getSubjectId()){
            $('<span class="badge ' + sub.externalId + '_color username">' + sub.displayName
                + '(you)</span>').appendTo(workOn);
            $('<span style="font-size:10px;color:black;"> turn to answer</span>').appendTo(workOn);
        }else {
            $('<span class="badge ' + sub.externalId + '_color username">'
                + sub.displayName
                + '</span>').appendTo(workOn);

            $('<span style="font-size:10px;color:black;"> turn to answer</span>').appendTo(workOn);
        }
        div.append(workOn);

        $("#textInputPad").append(div);
        this.setupHooks();

    }
    setupHooks(){
        $("input").unbind().on('keyup', this.handleOnClick.bind(this));

        $("input").unbind().on('change', this.handleOnBlur.bind(this));


    }
    handleOnClick(event){

        var cellIndex = parseInt($(event.target).data( "cell-reference-index"));

        if(!isNaN(cellIndex)) {
            let valueTyped = $(event.target).val();
            this.pogsPlugin.saveCompletedTaskAttribute('typedInField',
                valueTyped, 0.0,
                cellIndex, false);
        }
    }
    handleOnBlur(event){


        let cellIndex = parseInt($(event.target).data( "cell-reference-index"));

        if(!isNaN(cellIndex)) {

            let valueTyped = $(event.target).val();

            if(valueTyped != null) {
                this.pogsPlugin.saveCompletedTaskAttribute('equationAnswer' + cellIndex,
                    valueTyped, 0.0,
                    0, true, '');
            }
        }
    }
    broadcastReceived(message) {
        let attrName = message.content.attributeName;

        if (attrName == "typedInField") {
            let index = message.content.attributeIntegerValue;
            //do nothing/
            $('#cell_' + index + ' input').val(message.content.attributeStringValue);
        } else {
            let index = attrName.replace('equationAnswer', '');
            $('#cell_' + index + ' input').val(message.content.attributeStringValue);

            if(this.totalFieldIndex == index) {
                this.createNextFieldAndWhoShouldBeAbleToEdit();
            }
        }
    }
}

var jeopardyTaskPlugin = pogs.createPlugin('jeopardyTaskPlugin',function(){

    let jeopardyTaskPlugin = new JeopardyTaskPlugin(this);
    // get config attributes from task plugin
    jeopardyTaskPlugin.setup($.parseJSON(this.getStringAttribute("jeopardyBluePrint")),
        $.parseJSON(this.getStringAttribute("questionOrder")));
    this.subscribeTaskAttributeBroadcast(jeopardyTaskPlugin.broadcastReceived.bind(jeopardyTaskPlugin))
});