class MemoryGridTaskPrimer{
    constructor(pogsPlugin) {
        this.pogsPlugin = pogsPlugin;

    }
    setupGrid(gridBluePrint, answerSheet){
        if(gridBluePrint.taskText) {
            $("#taskPrimerInfo").html(gridBluePrint.taskText);

        }


        let colorsInPrime = gridBluePrint.colorsInPrimer;

        let headersInPrimer = gridBluePrint.headersInPrimer;


        let total = 0;
        let allOptions = gridBluePrint.rowsSize * gridBluePrint.colsSize;
        let appearingOrder =[];
        let itemsShouldAppear = 3;
        for(let j=0;j< (allOptions/itemsShouldAppear); j++){
            appearingOrder[j] = [];
        }
        let letter = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J','K', 'L', 'M', 'N', 'O', 'P',
        'Q', 'R', 'S', 'T', 'U', 'W', 'V', 'X', 'Y', 'Z'];
        if(headersInPrimer){
            let tableRow = $('<tr/>');
             let tdz = $('<td/>', {text: '-'});
            tableRow.append(tdz);

            for(let j = 0 ; j< parseInt(gridBluePrint.colsSize); j++) {
                let td = $('<th/>', {text: letter[j]});
                tableRow.append(td);
            }
            $("#gridTable").append(tableRow);
        }
        for(let i=0;i< parseInt(gridBluePrint.rowsSize);i++){
            let tableRow = $('<tr/>');
            if(headersInPrimer){
                let td = $('<th/>', {text: (i+1),
                    scope: "row"});
                tableRow.append(td);
            }

            for(let j = 0 ; j< parseInt(gridBluePrint.colsSize); j++) {
                let td = $('<td/>',{
                });
                let span = $('<span/>',{
                    'id': 'gridCell' + total,
                    'style': ((!colorsInPrime)?('background-color:\'black\', color: \'white\''):('background-color: ' + gridBluePrint.columnColors[j] + ';padding:5px;display:none;border-radius: 50px;color:' +
                                                     generateFontColorBasedOnBackgroundColor(gridBluePrint.columnColors[j]) + ';')),
                    text: answerSheet[total]
                });
                td.append(span);


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
                    $(ref).show();
                    $(ref).addClass('fadeIn');
                    setTimeout(function () {
                        $(ref).addClass('fadeOut');
                        setTimeout(function () {
                            $(ref).hide();
                        }, 5000);
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

    var elem = document.getElementById("gridTable");
    elem.unselectable = "on"; // For IE and Opera

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