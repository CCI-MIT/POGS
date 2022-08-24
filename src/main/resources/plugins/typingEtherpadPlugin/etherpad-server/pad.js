// Get session id from URL params and set it on cookie, so private pads can be accessed
var urlParams = new URLSearchParams(window.location.search);
var sessionID = urlParams.get('sessionID');
if (sessionID) {
    document.cookie = 'sessionID=' + sessionID;
}