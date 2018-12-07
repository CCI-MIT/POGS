class MemoryGridTask {
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
        this.subjectsHasColumn = [];
        this.colorHasSubjects = [];
    }
    setupGrid(gridBluePrint){

        console.log("setup grid blueprint: " + gridBluePrint.rowsSize + " - " + gridBluePrint.colsSize);

        if(gridBluePrint.columnColors){

            let allSubjectsHaveAtLeastOneColor = false;
            let allColorsHaveAtLeastOneSubject = false;


            for(let k = 0 ; k < gridBluePrint.columnColors.length; k ++){
                this.colorHasSubjects[k] = [];
            }
            for(let k = 0; k < this.pogsPlugin.getTeammates().length; k ++){
                this.subjectsHasColumn[k] = [];
            }

            while(!allColorsHaveAtLeastOneSubject && !allSubjectsHaveAtLeastOneColor) {

                for(let k = 0 ; k < this.subjectsHasColumn.length; k ++){
                    if(this.subjectsHasColumn[k].length == 0){
                        allSubjectsHaveAtLeastOneColor = false;
                    }
                }

                if(!allSubjectsHaveAtLeastOneColor) {
                    for (let i = 0; i < this.pogsPlugin.getTeammates().length; i++) {
                        if(this.subjectsHasColumn[i].length == 0 ) {
                            let x = Math.floor(
                                Math.random() * gridBluePrint.columnColors.length);
                            this.subjectsHasColumn[i].push(x);
                            this.colorHasSubjects[x].push(i);
                        }
                    }
                }
                for(let k = 0 ; k < this.colorHasSubjects.length; k ++){
                    if(this.colorHasSubjects[k].length == 0){
                        allColorsHaveAtLeastOneSubject = false;
                    }
                }
                if(!allColorsHaveAtLeastOneSubject) {
                    for( let i=0; i < gridBluePrint.columnColors; i ++){
                        if(this.colorHasSubjects[i].length == 0 ){
                            let x = Math.floor(
                                Math.random() * gridBluePrint.columnColors.length);
                            this.subjectsHasColumn[x].push(i);
                            this.colorHasSubjects[i].push(x);
                        }
                    }
                }

            }
        }


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
                        'data-cell-reference-index': total,
                        'style': 'background-color: ' + gridBluePrint.columnColors[j] + ';color:' +
                                 generateFontColorBasedOnBackgroundColor(gridBluePrint.columnColors[j]) + ';'
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

        $("#taskWorkInfo").html(gridBluePrint.taskText);
        $("input").on('mouseenter', this.handleOnClick.bind(this));

        $("input").on('change', this.handleOnBlur.bind(this));

        $('input' ).on('mouseleave',this.handleMouseLeave.bind(this))
    }
    handleMouseLeave(event){
        let cellIndex = parseInt($(event.target).data( "cell-reference-index"));
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

function generateFontColorBasedOnBackgroundColor(colorz) {
    let color = parseColor(colorz);
    let r = color[0];
    let g = color[1];
    let b = color[2];
    let yiq = ((r * 299) + (g * 587) + (b * 114)) / 1000;
    return (yiq >= 128) ? "#000000" : "#FFFFFF";
}
function parseColor(input) {
    let  m = input.match(/^#([0-9a-f]{6})$/i)[1];
    if( m) {
        return [
            parseInt(m.substr(0,2),16),
            parseInt(m.substr(2,2),16),
            parseInt(m.substr(4,2),16)
        ];
    }
}