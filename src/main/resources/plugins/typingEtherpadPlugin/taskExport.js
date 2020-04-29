var _teammates = JSON.parse(teammates);
var _subject = JSON.parse(subject);
var _isSoloTask = isSoloTask;
var _taskConfigurationAttributes = JSON.parse(taskConfigurationAttributes);
var _completedTaskAttributes = JSON.parse(completedTaskAttributes);
var _dictionary = JSON.parse(dictionary);
var _exportRecordLines = []
var _headerColumns = "";

_headerColumns = "Answer;Ground Truth";
var API_ADDRESS = "http://localhost:8080";
var dicionaryContents = "";




if(_dictionary){
    if(_dictionary.hasGroundTruth){
        var dictEntryId = _dictionary.dictionaryEntries[0];
        var ret = getDictionaryEntry(_dictionary.id,dictEntryId );
        if(ret) {
            print(ret);
            dicionaryContents = (atob(ret.entryValue));
        }
    }
}
function atob(encoded){
    return new java.lang.String(java.util.Base64.getDecoder().decode(encoded));
}
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
function getDictionaryEntry(dictId, dictEntryId) {

    print(API_ADDRESS + "/dictionaries/" + dictId + '/dictionaryentries/' + dictEntryId)
    var apiResponse = JSON.parse(newConnection(
        API_ADDRESS + "/dictionaries/" + dictId + '/dictionaryentries/' + dictEntryId, "GET"));
    if(apiResponse){
        return apiResponse;
    }
    return null;
}

function cleanEntry(summaryDescription) {
    if (summaryDescription === null) return "";
    summaryDescription = summaryDescription.replace(/\n/g, "¶");
    summaryDescription = summaryDescription.replace(/\r/g, "");
    summaryDescription = summaryDescription.replace(/,/g, "|");
    summaryDescription = summaryDescription.replace(/;/g, "|");
    return summaryDescription;
}

for(var i=0; i < _completedTaskAttributes.length; i++){

    if(_completedTaskAttributes[i].attributeName ==("fullText")) {

        _exportRecordLines.push(cleanEntry(_completedTaskAttributes[i].stringValue) + ";" + cleanEntry(dicionaryContents))
    }
}


exportRecordLines = JSON.stringify(_exportRecordLines);
headerColumns = _headerColumns;