class EquationTypingTaskPlugin{
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
        this.gridOrder = null;
        this.orderIndex = -1;
        this.totalFieldIndex = -1;
        this.teamMates = this.pogsPlugin.getTeammates();
        this.isTaskSolo = this.pogsPlugin.isSoloTask();
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
        let isUsersTurn = false;
        let btnEdit = null;
        if(sub.externalId == this.pogsPlugin.getSubjectId()) {
            inp = $('<input/>', {
                "class": "doneInp",
                'id': 'inputEquation_' + this.totalFieldIndex,
                'style':'margin-right: 5px;',
                'data-cell-reference-index': this.totalFieldIndex
            });
            isUsersTurn = true;
            btnEdit  = $('<input/>', {
                "class": "btn btn-danger btnEdit",
                'id': 'editEquation_' + this.totalFieldIndex,
                "type": "button",
                "style": "display: none;margin-left: 5px;",
                'data-cell-reference-index': this.totalFieldIndex,
                "value": "Edit"
            });

        } else {
            inp = $('<input/>', {
                "class": "doneInp",
                'id': 'inputEquation_' + this.totalFieldIndex,
                'disabled': 'disabled',
                'style':'margin-right: 5px;',
                'data-cell-reference-index': this.totalFieldIndex
            });
        }
        let btn  = $('<input/>', {
            "class": "btn btn-info doneBtn",
            'id': 'doneEquation_' + this.totalFieldIndex,
            "type": "button",
            'data-cell-reference-index': this.totalFieldIndex,
            "value": "Done"
        });




        div.append(inp);
        div.append(btn);
        if(isUsersTurn) {
            div.append(btnEdit);
        }

        let workOn = $('<div/>', {
            'class': 'workingOn',
            'style': 'margin-bottom: 5px;'
        });


        if(!this.isTaskSolo) {
            if (sub.externalId == this.pogsPlugin.getSubjectId()) {

                $('<span style="font-size:10px;color:black;">It\'s time for</span>')
                    .appendTo(workOn);
                $('<span class="badge ' + sub.externalId + '_color username">' + sub.displayName
                  + '(you)</span>').appendTo(workOn);
                $('<span style="font-size:10px;color:black;">to submit an answer</span>')
                    .appendTo(workOn);
            } else {
                $('<span style="font-size:10px;color:black;">It\'s time for</span>')
                    .appendTo(workOn);
                $('<span class="badge ' + sub.externalId + '_color username">'
                  + sub.displayName
                  + '</span>').appendTo(workOn);

                $('<span style="font-size:10px;color:black;">to submit an answer</span>')
                    .appendTo(workOn);
            }
        }
        div.append(workOn);

        $("#textInputPad").append(div);
        this.setupHooks();

    }
    setupHooks(){
        $(".doneInp").unbind().on('keyup', this.handleOnClick.bind(this));

        $(".doneBtn").unbind().on('click', this.handleOnBlur.bind(this));

        $(".btnEdit").unbind().on('click', this.handleOnClickEdit.bind(this));


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

            let valueTyped = $('#inputEquation_' + cellIndex).val();

            if(valueTyped != null) {
                this.pogsPlugin.saveCompletedTaskAttribute('equationAnswer' + cellIndex,
                                                           valueTyped, 0.0,
                                                           0, true, '');
                $("#editEquation_"+cellIndex).show();
            }
        }
    }
    handleOnClickEdit(event){
        console.log("Before cellIndex ");
        let cellIndex = parseInt($(event.target).data( "cell-reference-index"));
        console.log("Clicked edit " + cellIndex);

        $("#inputEquation_"+cellIndex).prop('disabled', false);
        $("#editEquation_"+cellIndex).hide();
        $("#inputEquation_"+cellIndex).focus();
        $('#doneEquation_' + cellIndex).attr("value", "Done");
    }
    broadcastReceived(message) {
        let attrName = message.content.attributeName;

        if (attrName == "typedInField") {
            let index = message.content.attributeIntegerValue;
            //do nothing/
            if(message.sender != this.pogsPlugin.getSubjectId()) {
                $('#cell_' + index + ' .doneInp').val(message.content.attributeStringValue);
            }
        } else {
            let index = attrName.replace('equationAnswer', '');

            //$('#cell_' + index + ' input').val(message.content.attributeStringValue);

            $("#inputEquation_"+index).val(message.content.attributeStringValue);
            $("#inputEquation_"+index).prop('disabled', true);
            $("#doneEquation_"+index).val(message.content.attributeStringValue);

            if(this.totalFieldIndex == index) {
                this.createNextFieldAndWhoShouldBeAbleToEdit();
            }
        }
    }
}




var equationTypingTaskPlugin = pogs.createPlugin('equationTypingTaskPlugin',function(){

    let equationTypingTaskPlugin = new EquationTypingTaskPlugin(this);
    // get config attributes from task plugin
    equationTypingTaskPlugin.setup($.parseJSON(this.getStringAttribute("gridBluePrint")),
                                   $.parseJSON(this.getCompletedTaskStringAttribute("gridOrder")));
    this.subscribeTaskAttributeBroadcast(equationTypingTaskPlugin.broadcastReceived.bind(equationTypingTaskPlugin))

});