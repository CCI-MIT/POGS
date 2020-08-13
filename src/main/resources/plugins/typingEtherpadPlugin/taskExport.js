var _teammates = JSON.parse(teammates);
var _subject = JSON.parse(subject);
var _isSoloTask = isSoloTask;
var _taskConfigurationAttributes = JSON.parse(taskConfigurationAttributes);
var _completedTaskAttributes = JSON.parse(completedTaskAttributes);
var _dictionary = JSON.parse(dictionary);
var _exportRecordLines = []
var _headerColumns = "";

_headerColumns = "Author;Answer;Ground Truth";
var API_ADDRESS = "http://localhost:8080";
var dicionaryContents = "";




if(_dictionary){
    if(_dictionary.hasGroundTruth){
        var dictEntryId = _dictionary.dictionaryEntries[0];
        var ret = getDictionaryEntry(_dictionary.id,dictEntryId );
        if(ret) {
            //print(ret);
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
        //print("Something wrong happened : " + ignore);
        return null;
    }
}
function getDictionaryEntry(dictId, dictEntryId) {

    //print(API_ADDRESS + "/dictionaries/" + dictId + '/dictionaryentries/' + dictEntryId)
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
var spans  ;
for(var i=0; i < _completedTaskAttributes.length; i++){

    if(_completedTaskAttributes[i].attributeName ==("fullText")) {

        _exportRecordLines.push("GROUP;" + cleanEntry(_completedTaskAttributes[i].stringValue) + ";" + cleanEntry(dicionaryContents))
    }
    if(_completedTaskAttributes[i].attributeName ==("fullTextHTML")) {

        spans = getAllSpansInText(_completedTaskAttributes[i].stringValue);
    }

}

var textsByAuthor = [];
if(spans != null) {
    for (var j = 0; j < spans.length; j++) {
        var aut = getAuthorOfSpan(spans[j]);

        if (!textsByAuthor[aut]) {
            textsByAuthor[aut] = {
                texts: [],
                author: aut,
                subject: null,
                fullText: ''
            };
        }
        textsByAuthor[aut].texts.push(spans[j]);
    }
}
var userFullText = "";
var tx = null;
for(var te in  textsByAuthor){

    userFullText = "";

    for(var j=0; j< textsByAuthor[te].texts.length; j++){
        tx = getTextInSpans(textsByAuthor[te].texts[j]);
        userFullText += tx;

    }

    textsByAuthor[te].fullText = cleanEntry(userFullText);
    textsByAuthor[te].subject = getAuthorSubject(te);

    _exportRecordLines.push(textsByAuthor[te].subject.externalId + ";"+
                            cleanEntry(userFullText)
                            + ";" + "EQUAL TO GROUP");

    //print(textsByAuthor[te].subject.externalId + ";"+
    //      cleanEntry(userFullText)
    //      + ";" + "EQUAL TO GROUP")
}

function getAuthorSubject(author){
    for(var i=0;i< _teammates.length; i++){
        for(var j=0; j< _teammates[i].attributes.length; j++){
            if(_teammates[i].attributes[j].attributeName =="ETHERPAD_AUTHOR_ID"){
                if(_teammates[i].attributes[j].stringValue == author){
                    return _teammates[i];
                } else {
                    break;
                }
            }
        }
    }
}

function getAuthorOfSpan(span){
    return span.match(/class="(.*?)"/i)[1].replace("author","").replace("_",".");
}
function getTextInSpans(fullTextHTML){
    return fullTextHTML.match(/<\s*span[^>]*>(.*?)<\s*\/\s*span>/i)[1];
}
function getAllSpansInText(fullTextHTML){
    return fullTextHTML.match(/<\s*span[^>]*>(.*?)<\s*\/\s*span>/g);

}

exportRecordLines = JSON.stringify(_exportRecordLines);
headerColumns = _headerColumns;