package com.kevin.prodapp.service;


public class MQTTMessage {
    private String message;
    public MQTTMessage(){

    }

    public MQTTMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
