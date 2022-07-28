package com.example.xmppchat;

/**
 * Created by M Ahmed Mushtaq on 4/1/2018.
 */

public class MessagesData {
    private String heading,messages;
    public MessagesData(String head,String mess){
        heading = head;
        messages = mess;
    }
    public String getHeading(){
        return heading;
    }
    public String getMessages(){
        return messages;
    }
}
