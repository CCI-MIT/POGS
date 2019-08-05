
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



function createAuthorIfNotExistsFor(authorMapper, name) {
    var apiResponse = JSON.parse(newConnection(
        API_ADDRESS + "createAuthorIfNotExistsFor/?apikey="+API_KEY+"&authorMapper="+authorMapper + "&name="+ name, "GET"));
    if(apiResponse){
        if(apiResponse.data){
            if(apiResponse.data.authorID){
                //print(apiResponse.data.authorID);
                return apiResponse.data.authorID;
            }
        }
    }
    return null;
}

function createGroupIfNotExistsFor(groupMapper) {
    var apiResponse = JSON.parse(newConnection(
        API_ADDRESS + "createGroupIfNotExistsFor/?apikey="+API_KEY+"&groupMapper="+groupMapper, "GET"));
    if(apiResponse){
        if(apiResponse.data){
            if(apiResponse.data.groupID){
                //print(apiResponse.data.groupID);
                return apiResponse.data.groupID;
            }
        }
    }
    return null;
}


function createGroupPad(groupID, initialText, padNamed) {
    var padName = (padNamed)?(padNamed):(uuidv4());
    var apiResponse = JSON.parse(newConnection(
        API_ADDRESS + "createGroupPad/?apikey="+API_KEY+"&groupID="+groupID + "&padName="+padName + "&text="+initialText, "GET"));
    if(apiResponse){
        if(apiResponse.data){
            if(apiResponse.data.padID){
                //print(apiResponse.data.padID);
                return apiResponse.data.padID;
            }
        }
    }
    return null;
}


function createSession(groupID,authorID) {
    var validUntil = new Date().getTime() + (1000 * 365 * 24 * 60 * 60)
    var textResp = newConnection(
        API_ADDRESS + "createSession/?apikey="+API_KEY+"&groupID="+groupID + "&authorID="+authorID + "&validUntil="+validUntil, "GET")
    var apiResponse = JSON.parse(textResp);
    if(apiResponse){

        if(apiResponse.data){
            if(apiResponse.data.sessionID){
                return apiResponse.data.sessionID;
            }
        }
    }
    return null;
}


function padExists( padName) {

    var apiResponse = JSON.parse(newConnection(
        API_ADDRESS + "isPasswordProtected/?apikey="+API_KEY+ "&padName="+padName, "GET"));
    if(apiResponse){
        if(apiResponse.code){
            return (apiResponse.code == 1);

        }
    }
    return 0;
}

function uuidv4() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}


var teammatez = JSON.parse(teammates);
var _completedTaskAttributes = JSON.parse(completedTaskAttributes);

var sessionId_ = sessionId;
var completedTaskId_ = completedTaskId;

//1 Create new session if not already created;
var groupId = createGroupIfNotExistsFor("s"+ sessionId_);

var _subjectAttributesToAdd = [];
//2 For each subject
for(var k=0; k < teammatez.length; k++){
    print("Trying to get subjectSessionId " + teammatez[k].id);
    var subjectSessionId = getSubjectAttribute(teammatez[k],SUBJECT_ETHERPAD_SESSION_ID);
    //check if already have session ID. if not create new one.
    if(subjectSessionId == null ){
        var newAttr = [];
        print("subjectSessionId not found creating a new one: for " + teammatez[k].id);
        var authorId = createAuthorIfNotExistsFor(teammatez[k].id,teammatez[k].displayName);
        print("new author id created : " + authorId + " for : "+ teammatez[k].id);

        newAttr.push({
                         "attributeName": SUBJECT_ETHERPAD_AUTHOR_ID,
                         "stringValue": authorId,
                         "internalAttribute" : true
                     });

        var sessionId = createSession(groupId,authorId);
        //print("new session id created : " + sessionId + " for : "+ teammatez[k].id);
        newAttr.push({
                         "attributeName": SUBJECT_ETHERPAD_SESSION_ID,
                         "stringValue": sessionId,
                         "internalAttribute" : true
                     });
        _subjectAttributesToAdd.push({
                                         "externalId": teammatez[k].externalId,
                                         "attributes": newAttr
                                     });
    }
    print("------\n")
}

//3 For the task at hand create the pad if it is not created YET.
// work around for test check if PAD id attribute already exists on the completedTaskAttributes.
if(getCompletedTaskAttribute("padID")!= null){
        if(!padExists(getCompletedTaskAttribute("padID"))){
            var padID = createGroupPad(groupId, "",getCompletedTaskAttribute("padID"));
            print("padID: " + padID);
            attributesToAddz = [{
                "attributeName": "padID",
                "stringValue": padID
            }];

            completedTaskAttributesToAdd = JSON.stringify(attributesToAddz);
        }
} else {
    var padID = createGroupPad(groupId, "");
    print("padID: " + padID);
    attributesToAddz = [{
        "attributeName": "padID",
        "stringValue": padID
    }];

    completedTaskAttributesToAdd = JSON.stringify(attributesToAddz);

}

/*
*
* */

function getCompletedTaskAttribute(attributeName){

    for(var j=0 ; j < _completedTaskAttributes.length; j ++){
        if(_completedTaskAttributes[j].attributeName == attributeName) {
            return _completedTaskAttributes[j].stringValue;
        }
    }
    return null;
}
function getSubjectAttribute(subject, attributeName){
    for(var j=0 ; j < subject.attributes.length; j ++){
        if(subject.attributes[j].attributeName == attributeName) {
            print("Attribute found" + attributeName);
            return subject.attributes[j];
        }
    }
    return null;
}





if(_subjectAttributesToAdd.length > 0) {
    subjectAttributesToAdd = JSON.stringify(_subjectAttributesToAdd);
}