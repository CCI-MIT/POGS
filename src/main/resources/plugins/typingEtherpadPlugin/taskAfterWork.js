var _teammates = JSON.parse(teammates);
var _subject = JSON.parse(subject);
var _isSoloTask = isSoloTask;
var _taskConfigurationAttributes = JSON.parse(taskConfigurationAttributes);


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

var API_ADDRESS = "http://localhost:9001/api/1.2.15/";
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

function getPadHTML(padId) {
    var apiResponse = JSON.parse(newConnection(
        API_ADDRESS + "getHTML/?apikey="+API_KEY+"&padID="+padId , "GET"));
    if(apiResponse){
        if(apiResponse.data){
            if(apiResponse.data.html){
                //print(apiResponse.data.authorID);
                return apiResponse.data.html;
            }
        }
    }
    return null;
}

function getRevisionsCount(padId) {
    var apiResponse = JSON.parse(newConnection(
        API_ADDRESS + "getRevisionsCount/?apikey="+API_KEY+"&padID="+padId , "GET"));
    if(apiResponse){
        if(apiResponse.data){
            if(apiResponse.data.revisions){
                //print(apiResponse.data.authorID);
                return apiResponse.data.revisions;
            }
        }
    }
    return null;
}

function getRevisionChangeset(padId, rev) {
    var apiResponse = JSON.parse(newConnection(
        API_ADDRESS + "getRevisionChangeset/?apikey="+API_KEY+"&padID="+padId +"&rev="+rev, "GET"));
    if(apiResponse){
        if(apiResponse.data){
                //print(apiResponse.data.authorID);
            return apiResponse.data;

        }
    }
    return null;
}

function getRevisionDate(padId, rev) {
    var apiResponse = JSON.parse(newConnection(
        API_ADDRESS + "getRevisionDate/?apikey="+API_KEY+"&padID="+padId +"&rev="+rev, "GET"));
    if(apiResponse){
        if(apiResponse.data){
            return apiResponse.data;
        }
    }
    return null;
}

function getRevisionAuthor(padId, rev) {
    var apiResponse = JSON.parse(newConnection(
        API_ADDRESS + "getRevisionAuthor/?apikey="+API_KEY+"&padID="+padId +"&rev="+rev, "GET"));
    if(apiResponse){
        if(apiResponse){
            if(apiResponse.data){
                return apiResponse.data;
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
var SUBJECT_ETHERPAD_AUTHOR_ID = "ETHERPAD_AUTHOR_ID";

if(padID!= null){

    var teammatez = JSON.parse(teammates);
    var etherpadAuthorMap = {};
    for(var k=0; k < teammatez.length; k++) {
        var subjectEtherpadId = getSubjectAttribute(teammatez[k], SUBJECT_ETHERPAD_AUTHOR_ID);
        //print("Etherpad author id : " + subjectEtherpadId.stringValue +" for subject : " + teammatez[k].id );
        etherpadAuthorMap[subjectEtherpadId.stringValue] = teammatez[k];
    }

    var fullText = getPadText(padID);
    var htmlText = getPadHTML(padID);
    var spans = getAllSpansInText(htmlText)
    var attributesToAddz = [];
    var eventLogsToAddz = [];
    attributesToAddz.push({
                              "attributeName": "fullText",
                              "stringValue": fullText
                          });
    attributesToAddz.push({
                              "attributeName": "fullTextHTML",
                              "stringValue": htmlText
                          });
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

        textsByAuthor[te].fullText = (userFullText);
        textsByAuthor[te].subject = getAuthorSubject(te);

        attributesToAddz.push({
          "attributeName": "fullTextAuthor_" + textsByAuthor[te].subject.externalId ,
          "stringValue": userFullText
                              });
    }

    var revCount = parseInt(getRevisionsCount(padID));
    var revisions = [];
    for(var i = 0; i < revCount; i ++){

        var changeset = getRevisionChangeset(padID,i);
        var typedValue = getTypedValueFromRevisionChangeset(changeset);
        var timestamp = getRevisionDate(padID,i);
        var etherAuthor = getRevisionAuthor(padID,i);
        var authorz = etherpadAuthorMap[etherAuthor];
        var author = "undefined";
        if(authorz != null){
            author = authorz.externalId;
        }
        attributesToAddz.push({
                                  "attributeName": "revision_"+i,
                                  "stringValue": JSON.stringify(
                                      {changeset: changeset, typedValue: typedValue,
                                          author: author, timestamp: timestamp, index: i}
                                  ),
                                "lastAuthorSubjectId" : author
                              });

        eventLogsToAddz.push({ "eventType" : "TASK_ATTRIBUTE", "eventContent": "",
                                 "timestamp" : timestamp, "sender" : author,
                                "summaryDescription" : typedValue

                             }
        )
    }
    eventLogsToAdd = JSON.stringify(eventLogsToAddz);
    completedTaskAttributesToAdd = JSON.stringify(attributesToAddz);
}

function getSubjectAttribute(subject, attributeName){
    for(var j=0 ; j < subject.attributes.length; j ++){
        if(subject.attributes[j].attributeName == attributeName) {
            //print("Attribute found" + attributeName);
            return subject.attributes[j];
        }
    }
    return null;
}

function getTypedValueFromRevisionChangeset(changeset){
    var index = changeset.indexOf("$");
    if(index == -1) return "";
    return changeset.substring((index + 1),changeset.length);

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