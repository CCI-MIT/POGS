package edu.mit.cci.pogs.utils;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class MessageUtils {

    public static void addSuccessMessage(String message,RedirectAttributes redirectAttributes ){
        redirectAttributes.addFlashAttribute("message",message);
        redirectAttributes.addFlashAttribute("messageType",MessageType.SUCCESS.getType());
    }
    public static void addErrorMessage(String message,RedirectAttributes redirectAttributes ){
        redirectAttributes.addFlashAttribute("message",message);
        redirectAttributes.addFlashAttribute("messageType",MessageType.ERROR.getType());
    }
}

enum MessageType{
    SUCCESS("alert-success"),
    ERROR("alert-danger");

    String type;

    String getType(){
        return type;
    }
    MessageType(String type){
        this.type = type;
    }

}
