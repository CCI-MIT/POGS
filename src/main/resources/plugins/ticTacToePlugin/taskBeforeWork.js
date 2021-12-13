var teammatez = JSON.parse(teammates);

var teamX = [];
var teamO = [];

for(var i=0; i < teammatez.length; i++){
    if(i%2 ==0){
        teamX.push(teammatez[i].externalId);
    } else {
        teamO.push(teammatez[i].externalId);
    }
}

var shouldXStart = parseInt(Math.random() * 2) === 0 ;
attributesToAddz = [];


attributesToAddz.push({"attributeName": "teamX",
    "stringValue":JSON.stringify(teamX) })

attributesToAddz.push({"attributeName": "teamO",
                          "stringValue":JSON.stringify(teamO) })

attributesToAddz.push({"attributeName": "shouldXStart",
                          "stringValue": shouldXStart })

completedTaskAttributesToAdd = JSON.stringify(attributesToAddz);