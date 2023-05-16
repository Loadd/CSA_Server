package model;

import java.io.Serializable;
import java.util.Date;

public class Account implements Serializable {
    private String username;
    private String password;
    private byte[] profilePicture;
    private String accountType;
    private Boolean ban;
    private Date timeout;
    private Float money;

    public Account() {
        this.username = "";
        this.password = "";
        this.profilePicture = null;
        this.accountType = "";
        this.ban = false;
        this.timeout = null;
        this.money = (float) 0;
    }

    //Default settings when registering
    public Account(String username, String password) {
        this.username = username;
        this.password = password;
        this.profilePicture = null;
        this.accountType = "User";
        this.ban = false;
        this.timeout = null;
        this.money = 100F;
    }

    public Account(String username, String password, byte[] profilePicture, String accountType, Boolean ban, Date timeout, Float money){
        this.username = username;
        this.password = password;
        this.profilePicture = profilePicture;
        this.accountType = accountType;
        this.ban = ban;
        this.timeout = timeout;
        this.money = money;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public byte[] getProfilePicture(){
        return this.profilePicture;
    }

    public String getAccountType() {
        return this.accountType;
    }

    public Boolean getBan() {
        return this.ban;
    }

    public Date getTimeout() {
        return this.timeout;
    }

    public Float getMoney() {
        return this.money;
    }
}
