package org.wikipedia.page.chat;

import java.util.Date;

public class Message {
    private String user;
    private String message;
    private Date timeStamp;

    public Message(String user, String message, Date timeStamp) {
        this.user = user;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Message withUser(String user) {
        this.user = user;
        return this;
    }

    public Message withMessage(String message) {
        this.message = message;
        return this;
    }

    public Message withTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }
}
