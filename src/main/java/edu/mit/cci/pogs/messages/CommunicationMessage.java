package edu.mit.cci.pogs.messages;

public class CommunicationMessage extends PogsMessage<CommunicationMessageContent> {
    public enum CommunicationType {

        JOINED, //To add to panel if not there
        MESSAGE,//NormalMessage
        IS_TYPING,//typing event


        //Dyadic event
        REQUEST_CHAT,
        HANG_UP_CHAT,
        ACCEPT_CHAT,

        STATUS
    }
}


