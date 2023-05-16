package model;

import java.io.Serializable;
import java.util.Date;

public class ChatMessage implements Serializable {
    private Integer id;
    private String userId;
    private String message;
    private Date date;
    private Boolean removed;

    public ChatMessage(Integer id, String userId, String message, Date date, Boolean removed){

        this.id = id;
        this.userId = userId;
        this.message = message;
        this.date = date;
        this.removed = removed;

    }

    public ChatMessage(String userId, String message){

        this.id = null;
        this.userId = userId;
        this.message = message;
        this.date = new Date();
        this.removed = false;

    }

    public Integer getId() {
        return id;
    }

    public String getUserId() {return userId;}

    public String getMessage() {return message;}

    public Date getDate() {return date;}

    public Boolean getRemoved() {return removed;}
}
