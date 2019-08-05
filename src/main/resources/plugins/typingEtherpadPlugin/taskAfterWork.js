

function newConnection(url, method){
    var httpConnection = new java.net.URL(url).openConnection();
    httpConnection.setRequestMethod(method);
    httpConnection.setRequestProperty("Accept", "application/json");

    if(method == "POST"){
        httpConnection.setDoOutput(true);
    }

    try{
        var  br = new java.io.BufferedReader(new java.io.InputStreamReader(httpConnection.getInputStream(), "utf-8"));
        var response = new java.lang.StringBuilder();
        var  responseLine = null;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }

        return response.toString();

    }catch (ignore) {
        print("Something wrong happened : " + ignore);
        return null;
    }
}

var API_ADDRESS = "http://localhost:9001/api/1.2.13/";
var API_KEY = "8jsa738hjkds89hhjhk";
var SUBJECT_ETHERPAD_AUTHOR_ID = "ETHERPAD_AUTHOR_ID";
var SUBJECT_ETHERPAD_SESSION_ID = "ETHERPAD_SESSION_ID";


function getPadText(padId) {
    var apiResponse = JSON.parse(newConnection(
        API_ADDRESS + "getText/?apikey="+API_KEY+"&padID="+padId , "GET"));
    if(apiResponse){
        if(apiResponse.data){
            if(apiResponse.data.text){
                //print(apiResponse.data.authorID);
                return apiResponse.data.text;
            }
        }
    }
    return null;
}

function getCompletedTaskAttribute(attributeName){

    for(var j=0 ; j < _completedTaskAttributes.length; j ++){
        if(_completedTaskAttributes[j].attributeName == attributeName) {
            return _completedTaskAttributes[j].stringValue;
        }
    }
    return null;
}

var _completedTaskAttributes = JSON.parse(completedTaskAttributes);
var padID = getCompletedTaskAttribute("padID");
if(padID!= null){

    var fullText = getPadText(padID);
    var attributesToAddz = [{
        "attributeName": "fullText",
        "stringValue": fullText
    }];
    completedTaskAttributesToAdd = JSON.stringify(attributesToAddz);
}