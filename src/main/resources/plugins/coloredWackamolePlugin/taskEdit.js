class WhackTaskEdit {

    constructor(){
        this.numOfPlayer = 0;
    }

    init(taskConfigId, currentAttributes) {
        //console.log("taskConfigId: " + taskConfigId);
        console.log("currentAttributes: ")
        console.log(currentAttributes);

        this.taskConfigId = taskConfigId;


        let numberOfColors = "3",
            colorFrequency= "25,25,50",
            colorWeights= "3,2,1",
            totalRoundTime= "600000",
            moleDuration = "1000",
            randomMolePlacement= "false",
            gridSize = "100",
            blueprint = "";
        let configuration = null;
        for (let i = 0; i < currentAttributes.length; i++) {
            if (currentAttributes[i].attributeName == "blueprint") {
                blueprint = currentAttributes[i].stringValue;
            }
            if (currentAttributes[i].attributeName == "configuration") {
                configuration = $.parseJSON(currentAttributes[i].stringValue);
                numberOfColors = configuration.numberOfColors
                colorFrequency = configuration.colorFrequency;
                colorWeights = configuration.colorWeights;
                totalRoundTime = configuration.totalRoundTime;
                moleDuration = configuration.moleDuration;
                randomMolePlacement = configuration.randomMolePlacement;
                gridSize = configuration.gridSize;
            }
        }

        this.setupHtmlFromAttributeString(numberOfColors,
        colorFrequency,
        colorWeights,
        totalRoundTime,
        moleDuration,
        randomMolePlacement,
        gridSize,
        blueprint);


        $("#generateScript").click(function () { //Setup add question button

            let numberOfColors = $("#numberOfColors").val();
            let colorFrequency = $("#colorFrequency").val();
            let colorWeights = $("#colorWeights").val();
            let totalRoundTime = $("#totalRoundTime").val();
            let moleDuration = $("#moleDuration").val();
            let blueprint = $("#blueprint").val();
            let randomMolePlacement = $("#randomMolePlacement").prop("checked");
            let gridSize = $("#gridSize").val();

            /*
            let numberOfColors = "3",
                colorFrequency= "20,30,50",
                colorWeights= "3,2,1",
                totalRoundTime= "600000",
                moleDuration = "1000",
                randomMolePlacement= "false",
                gridSize = "100",
                blueprint = "";*/

            let moleCOLORS = ["RED","GREEN","BLUE","BROWN"];
            let molePERCENTAGES = [];
            let moleCOLORSINARRAY = [];


            molePERCENTAGES = colorFrequency.split(",");
            let intGridSize = parseInt(gridSize);
            if(numberOfColors!=molePERCENTAGES.length){
                $("#errorMsgGeneration").html("Configuration mismatch, the frequency array is different from the number of colors");
                $("#errorMsgGeneration").show();
                return;
            } else {
                let total = 0;
                for(let i=0;i < molePERCENTAGES.length; i++){
                    total += parseInt(molePERCENTAGES[i]);
                }
                if(total != 100){
                    $("#errorMsgGeneration").html("Configuration mismatch, the total frequency value must add up to 100");
                    $("#errorMsgGeneration").show();
                    return;
                }
            }
            if(numberOfColors!=colorWeights.split(",").length){
                $("#errorMsgGeneration").html("Configuration mismatch, the weights array is different from the number of colors");
                $("#errorMsgGeneration").show();
                return;
            }


            for(let i=0; i < molePERCENTAGES.length ; i++){
                for(let j=0; j < parseInt(molePERCENTAGES[i]);j++){
                    moleCOLORSINARRAY.push(moleCOLORS[i]);
                }
            }
            let moleMult = 1;

            let intMoleDuration = parseInt(moleDuration);
// DOABLE AMOUNT OF MOLES:
            let amountOfMoles = parseInt(totalRoundTime)/parseInt(moleDuration);
            let randomCell;
            let randomColorRespectingPercentages;
            let timeGoing;
            for(let i=0; i < amountOfMoles ; i++){
                randomCell = Math.floor(Math.random() * intGridSize);
                randomColorRespectingPercentages = Math.floor(Math.random() * 100);
                if(randomMolePlacement == "true"){
                    moleMult = Math.random();
                }
                timeGoing = i * moleMult * intMoleDuration;
                blueprint = blueprint  + (timeGoing+ ";" + moleCOLORSINARRAY[randomColorRespectingPercentages] + ";" + randomCell) + "\n";
            }
            $("#blueprint").val(blueprint);
            $("#errorMsgGeneration").hide();

        }.bind(this));

    }


    setupHtmlFromAttributeString(numberOfColors,
                                 colorFrequency,
                                 colorWeights,
                                 totalRoundTime,
                                 moleDuration,
                                 randomMolePlacement,
                                 gridSize,
                                 blueprint){

        $("#numberOfColors").val(numberOfColors);
        $("#colorFrequency").val(colorFrequency);
        $("#colorWeights").val(colorWeights);
        $("#totalRoundTime").val(totalRoundTime);
        $("#moleDuration").val(moleDuration);
        $("#blueprint").val(blueprint);
        $("#gridSize").val(gridSize);

        if(randomMolePlacement == "true") {
            $("#randomMolePlacement").prop("checked", true);
        }else {
            $("#randomMolePlacement").removeProp("checked");
        }



    }

    setupAttributesFromHtml(){
        let numberOfColors = $("#numberOfColors").val();
        let colorFrequency = $("#colorFrequency").val();
        let colorWeights = $("#colorWeights").val();
        let totalRoundTime = $("#totalRoundTime").val();
        let moleDuration = $("#moleDuration").val();
        let blueprint = $("#blueprint").val();
        let randomMolePlacement = $("#randomMolePlacement").prop("checked");
        let gridSize = $("#gridSize").val();

        return  {blueprint: blueprint, configuration: JSON.stringify({numberOfColors: numberOfColors , colorFrequency: colorFrequency, colorWeights: colorWeights,gridSize: gridSize,
                                                                         totalRoundTime: totalRoundTime,moleDuration: moleDuration, randomMolePlacement: randomMolePlacement })};
    }
    beforeSubmit(){
        //return string message if should not save else return TRUE.

        //check all fields for non empty fields


        var attr = this.setupAttributesFromHtml();

        createOrUpdateAttribute("blueprint",attr.blueprint,null,null,this.taskConfigId,0, "");
        createOrUpdateAttribute("configuration",attr.configuration,null,null,this.taskConfigId,1, "");
    }

}
pogsTaskConfigEditor.register(new WhackTaskEdit());

