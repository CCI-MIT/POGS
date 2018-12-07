class MemoryGridTaskPrimer{
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;
        console.log('MemoryGridTaskPrimer constructor');
    }
    setupGrid(gridBluePrint, answerSheet){
        if(gridBluePrint.taskText) {
            $("#taskPrimerInfo").html(gridBluePrint.taskText);

        }
        console.log(" gridBluePrint.rowsSize: " + gridBluePrint.rowsSize);
        console.log(" gridBluePrint.colsSize: " + gridBluePrint.colsSize);
        let total = 0;
        let allOptions = gridBluePrint.rowsSize * gridBluePrint.colsSize;
        let appearingOrder =[];
        let itemsShouldAppear = 3
        for(let j=0;j< (allOptions/itemsShouldAppear); j++){
            appearingOrder[j] = [];
        }
        for(let i=0;i< parseInt(gridBluePrint.rowsSize);i++){
            let tableRow = $('<tr/>');

            for(let j = 0 ; j< parseInt(gridBluePrint.colsSize); j++) {
                let td = $('<td/>',{
                    'id': 'gridCell' + total,
                    'style': 'background-color: ' + gridBluePrint.columnColors[j] + ';color:' +
                             generateFontColorBasedOnBackgroundColor(gridBluePrint.columnColors[j]) + ';',
                    text: answerSheet[total]
                });


                tableRow.append(td);
                let ref = '#gridCell' + total;
                let placed = false;
                while(!placed) {
                    let x = Math.floor(Math.random() * appearingOrder.length);
                    if(appearingOrder[x].length < itemsShouldAppear) {
                        appearingOrder[x].push(ref);
                        placed = true;
                    }
                }


                total++;
            }
            $("#gridTable").append(tableRow);
        }

        //let scheduleTimeTo = (1000 * (Math.floor(Math.random() * 11) + 1));
        for(let j=0;j < appearingOrder.length; j++) {
            for(let k= 0 ; k < appearingOrder[j].length ; k ++) {
                let ref = appearingOrder[j][k];
                setTimeout(function () {
                    console.log("Working on ref: " + ref);
                    $(ref).addClass('fadeIn');
                    setTimeout(function () {
                        $(ref).addClass('fadeOut');
                    }, 4500);

                }, 5000 * j);
            }
        }
    }
}


let memoryGridTaskPrimerPlugin = pogs.createPlugin('memoryGridTaskPrimerPlugin',function(){
    console.log('creating the plugin');

    let memoryGridTaskPrimer = new MemoryGridTaskPrimer(this);
    // get config attributes from task plugin
    memoryGridTaskPrimer.setupGrid($.parseJSON(this.getStringAttribute("gridBluePrint")),$.parseJSON(this.getStringAttribute("answerSheet")));


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