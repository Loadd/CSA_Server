package model;

import java.io.Serializable;

public class RouletteRoll implements Serializable {
    private Integer id;
    private String rollResult;

    public RouletteRoll(){
        this.id = -1;
        this.rollResult = "";
    }

    public RouletteRoll(Integer id, String rollResult){
        this.id = id;
        this.rollResult = rollResult;
    }

    public RouletteRoll(String rollResult){
        this.id = -1;
        this.rollResult = rollResult;
    }

    public Integer getRollId() {return id;}

    public String getRollResult() {return rollResult;}

}
