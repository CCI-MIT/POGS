var _teammates = JSON.parse(teammates);
var _subject = JSON.parse(subject);
var _isSoloTask = isSoloTask;
var _taskConfigurationAttributes = JSON.parse(taskConfigurationAttributes);
var _completedTaskAttributes = JSON.parse(completedTaskAttributes);
var _dictionary = JSON.parse(dictionary);
var _exportRecordLines = []
var _headerColumns = "";

_headerColumns = "Author;Color;Answer;Ground Truth";
var API_ADDRESS = "http://localhost:8080";
var dicionaryContents = "";
var columnSections;


for(var i=0 ; i < _taskConfigurationAttributes.length; i ++) {
    if(_taskConfigurationAttributes[i].attributeName == "gridBluePrint"){
        columnSections = JSON.parse(_taskConfigurationAttributes[i].stringValue);
        break;
    }
}



//print(JSON.stringify(columnSections));

var colorDictByDictId = {};
var colorArray = [];
for(var i=0 ; i < columnSections.length; i ++) {
    if(!colorDictByDictId[columnSections[i].color]) {
        colorDictByDictId[columnSections[i].color] = {
            author: '',
            authorText: '',
            originalText: '',
            dictionaryEntryIds: [],
            color: columnSections[i].color,

        };
    }
    var dictEntryId = columnSections[i].text;
    var ret = getDictionaryEntry(_dictionary.id, dictEntryId);

    colorDictByDictId[columnSections[i].color].originalText += atob(ret.entryValue);
    colorDictByDictId[columnSections[i].color].dictionaryEntryIds.push(dictEntryId);

}
for(color in colorDictByDictId){
    colorArray.push(colorDictByDictId[color].color);
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
var colorAssignments = []
for(var i=0; i < _completedTaskAttributes.length; i++){

    if(_completedTaskAttributes[i].attributeName ==("fullTextHTML")) {

        spans = getAllSpansInText(_completedTaskAttributes[i].stringValue);

        //_exportRecordLines.push(cleanEntry(_completedTaskAttributes[i].stringValue) + ";" + cleanEntry(dicionaryContents));

    }
    if(_completedTaskAttributes[i].attributeName.indexOf("subjectAssignedToColor_")!= -1){
        colorAssignments.push({
            subjectExternalId :_completedTaskAttributes[i].stringValue,
            colorIndex: _completedTaskAttributes[i].extraData})
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
for(var te in  textsByAuthor){

    var userFullText = "";
    for(var j=0; j< textsByAuthor[te].texts.length; j++){
        userFullText+=getTextInSpans(textsByAuthor[te].texts[j]);
    }
    textsByAuthor[te].fullText = cleanEntry(userFullText);
    textsByAuthor[te].subject = getAuthorSubject(te);


    var ass = getAuthorChosenColor(textsByAuthor[te].subject.externalId);

    ass.color = colorArray[ass.colorIndex];

    _exportRecordLines.push(textsByAuthor[te].subject.externalId + ";"+ ass.color
                            + ";"+ cleanEntry(textsByAuthor[te].fullText)
                            + ";" + cleanEntry(colorDictByDictId[ass.color].originalText));
}

function getAuthorChosenColor(sub){

    for(var i=0;i<colorAssignments.length;i++){

        if(colorAssignments[i].subjectExternalId == sub){
            return colorAssignments[i];
        }
    }
    return null;
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