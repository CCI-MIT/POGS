package edu.mit.cci.pogs.view.workspace.beans;

public class ErrorLogBean {

    public ErrorLogBean(String message){
        this.message = message;
    }
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
