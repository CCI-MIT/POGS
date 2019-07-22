class MemoryGridTask {
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
        this.subjectsHasColumns = [];
        this.colorHasSubjects = [];
    }
    setupGrid(gridBluePrint){

        console.log("setup grid blueprint: " + gridBluePrint.rowsSize + " - " + gridBluePrint.colsSize);
        let teamMates = this.pogsPlugin.getTeammates();
        let columnsUserCanChange = [];
        for (let j = 0; j < gridBluePrint.colsSize; j++) {
            columnsUserCanChange[j] = true;
        }
        let colorsInTask = gridBluePrint.colorsInTask;
        let headersInTask = gridBluePrint.headersInTask;

        let letter = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J','K', 'L', 'M', 'N', 'O', 'P',
                      'Q', 'R', 'S', 'T', 'U', 'W', 'V', 'X', 'Y', 'Z'];
        if(gridBluePrint.columnColors) {

            this.subjectsHasColumns = JSON.parse(this.pogsPlugin.getCompletedTaskStringAttribute("subjectsHasColumns"));
            this.colorHasSubjects = JSON.parse(this.pogsPlugin.getCompletedTaskStringAttribute("colorHasSubjects"));

            let tableRow = $('<tr/>');
            let tdz = $('<td/>',{text:' - '});

            if(headersInTask){
                tableRow.append(tdz);
            }
            for (let j = 0; j < gridBluePrint.colsSize; j++) {
                let td = $('<th>');
                let divHeader = $('<div/>', {style: "font-color: black"});
                let divSubj = $('<div/>');
                for (let k = 0; k < this.colorHasSubjects[j].length; k++) {

                    let sub = teamMates[this.colorHasSubjects[j][k]];
                    if(sub.externalId == this.pogsPlugin.getSubjectId()){
                        $('<span class="badge ' + sub.externalId + '_color username">' + sub.displayName
                          + '(you)</span>').appendTo(divSubj);
                        columnsUserCanChange[j] = false;
                    }else {

                        $('<span class="badge ' + sub.externalId + '_color username">'
                          + sub.displayName
                          + '</span>').appendTo(divSubj);
                    }
                }
                if(headersInTask){
                    divHeader.append(letter[j]);
                    td.append(divHeader);
                }
                td.append(divSubj);
                tableRow.append(td);

            }
            $("#gridTable").append(tableRow);
        }
        let total = 0;
        for(let i=0;i<gridBluePrint.rowsSize;i++){
            let tableRow = $('<tr/>');

            if(headersInTask){
                let td = $('<th/>', {text: (i+1)});

                tableRow.append(td);
            }
            for(let j = 0 ; j< gridBluePrint.colsSize; j++) {


                let td = $('<td/>',
                           {
                               id: 'cell_'+total
                           });
                if(!columnsUserCanChange[j]) {
                    td.append(
                        $('<input/>', {
                            'data-cell-reference-index': total,
                            'style':((!colorsInTask)?(''):( 'background-color: ' + gridBluePrint.columnColors[j]
                                     + ';color:' +
                                     generateFontColorBasedOnBackgroundColor(
                                         gridBluePrint.columnColors[j]) + ';'))
                        })
                    );
                } else {
                    td.append(
                        $('<input/>', {
                            'data-cell-reference-index': total,
                            'disabled': 'disabled',
                            'style': ((!colorsInTask)?(''): ('background-color: ' + gridBluePrint.columnColors[j]
                                     + ';color:' +
                                     generateFontColorBasedOnBackgroundColor(
                                         gridBluePrint.columnColors[j]) + ';'))
                        })
                    );


                }
                total++;
                td.append(
                    $('<div/>', {
                        'class': 'workingOn'
                    })
                );
                td.append(
                    $('<div/>', {
                        'style': 'color:black;font-size:10px',
                         text: ((!columnsUserCanChange[j])?('Answer this field'):('You cannot edit this field!')),
                        'class': 'cantEdit'
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