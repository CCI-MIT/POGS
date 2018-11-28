class MemoryGridTask {
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
    }
    setupGrid(gridBluePrint){

        console.log("setup grid blueprint: " + gridBluePrint.rowsSize + " - " + gridBluePrint.colsSize);
        let total = 0;
        for(let i=0;i<gridBluePrint.rowsSize;i++){
            let tableRow = $('<tr/>');

            for(let j = 0 ; j< gridBluePrint.colsSize; j++) {
                let td = $('<td/>',
                           {
                               id: 'cell_'+total
                           });
                td.append(
                    $('<input/>', {
                        'data-cell-reference-index': total
                    })
                );
                total++;
                td.append(
                    $('<div/>', {
                        'class': 'workingOn'
                    })
                );
                tableRow.append(td);
            }
            $("#gridTable").append(tableRow);

        }
        $("input").on('mouseenter', this.handleOnClick.bind(this));

        $("input").on('change', this.handleOnBlur.bind(this));

        $('input' ).on('mouseleave',this.handleMouseLeave.bind(this))
    }
    handleMouseLeave(event){
        var cellIndex = parseInt($(event.target).data( "cell-reference-index"));
        console.log("OnClick - Sending cell index: " + cellIndex);
        if(!isNaN(cellIndex)) {
            this.pogsPlugin.saveCompletedTaskAttribute('focusOutCell',
                                                       "", 0.0,
                                                       cellIndex, false);
        }

    }
    broadcastReceived(message){
        let attrName = message.content.attributeName;
        let index = message.content.attributeIntegerValue;

        console.log("Broadcast received sender: " + message.sender);
        console.log("Broadcast received subject id: " + this.pogsPlugin.getSubjectId());
        console.log("Broadcast Attr name: " + attrName);

        if(message.sender != this.pogsPlugin.getSubjectId()) {
            if (attrName == "focusInCell") {

                let sub = this.pogsPlugin.getSubjectByExternalId(message.sender);
                $('#cell_' + index + ' .workingOn').empty();

                $('<span class="badge ' + sub.externalId + '_color username">' + sub.displayName
                  + '</span>')
                    .appendTo('#cell_' + index + ' .workingOn');
                //set timeout
            }
            if(attrName == 'focusOutCell'){
                let sub = this.pogsPlugin.getSubjectByExternalId(message.sender);
                $('#cell_' + index + ' .workingOn '+ '.'+ sub.externalId + '_color').remove();
            }
            else{
                console.log(" not focus in cell: " + message.content.attributeStringValue);
                index = attrName.replace('memoryGridAnswer', '');
                console.log("INDEX: " + index)
                $('#cell_' + index + ' input').val(message.content.attributeStringValue);
            }
        }
        //All attributes sync(send all set attributes)
    }
    handleOnClick(event){

        var cellIndex = parseInt($(event.target).data( "cell-reference-index"));

        if(!isNaN(cellIndex)) {
            this.pogsPlugin.saveCompletedTaskAttribute('focusInCell',
                                                       "", 0.0,
                                                       cellIndex, false);
        }

    }
    handleOnBlur(event){


        let cellIndex = parseInt($(event.target).data( "cell-reference-index"));

        if(!isNaN(cellIndex)) {

            let valueTyped = $(event.target).val();

            if(valueTyped != null) {
                this.pogsPlugin.saveCompletedTaskAttribute('memoryGridAnswer' + cellIndex,
                    valueTyped, 0.0,
                    0, true, '');
            }
        }
    }
}

var memoryGridTaskPlugin = pogs.createPlugin('memoryGridTaskPlugin',function(){


    let memoryGridTask = new MemoryGridTask(this);
    // get config attributes from task plugin
    memoryGridTask.setupGrid($.parseJSON(this.getStringAttribute("gridBluePrint")));
    this.subscribeTaskAttributeBroadcast(memoryGridTask.broadcastReceived.bind(memoryGridTask))

});