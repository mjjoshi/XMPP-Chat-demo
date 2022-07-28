//package com.example.xmppchat;
//
//import com.google.gson.Gson;
//
//import org.jxmpp.jid.EntityJid;
//
//public class ChatMessage {
//
//  public ChatMessage() {
//  }
//
//  public ChatMessage(String Sender, String Receiver, String messageString, String ID,
//                     boolean isMINE) {
//    body = messageString;
//    isMine = isMINE;
//    sender = Sender;
//    msgid = ID;
//    receiver = Receiver;
//    senderName = sender;
//  }
//
//  public static ChatMessage instanceOf(String messageString) {
//    if (messageString == null) {
//      return new ChatMessage();
//    } else {
//      return new Gson().fromJson(messageString, ChatMessage.class);
//    }
//  }
//
//  public String getBody() {
//    return body;
//  }
//
//  public void setBody(String body) {
//    this.body = body;
//  }
//
//  public String getSender() {
//    return sender;
//  }
//
//  public void setSender(String sender) {
//    this.sender = sender;
//  }
//
//  public EntityJid getReceiver() {
//    return receiver;
//  }
//
//  public void setReceiver(String receiver) {
//    this.receiver = receiver;
//  }
//
//  public String getSenderName() {
//    return senderName;
//  }
//
//  public void setSenderName(String senderName) {
//    this.senderName = senderName;
//  }
//
//  public String getDate() {
//    return Date;
//  }
//
//  public void setDate(String date) {
//    Date = date;
//  }
//
//  public String getTime() {
//    return Time;
//  }
//
//  public void setTime(String time) {
//    Time = time;
//  }
//
//  public String getMsgid() {
//    return msgid;
//  }
//
//  public void setMsgid(String msgid) {
//    this.msgid = msgid;
//  }
//
//  public boolean isMine() {
//    return isMine;
//  }
//
//  public void setMine(boolean mine) {
//    isMine = mine;
//  }
//
//  public String body, sender, receiver, senderName;
//  public String Date, Time;
//  public String msgid;
//  public boolean isMine;// Did I send the message.
//
//
//}