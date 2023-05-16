package model;

import java.io.Serializable;

public class RouletteBet implements Serializable {
    private Integer id;
    private Integer rollId;
    private String userId;
    private Float moneyBet;
    private String colour;

    public RouletteBet(Integer rollId, String userId, Float moneyBet, String colour){
        this.rollId = rollId;
        this.userId = userId;
        this.moneyBet = moneyBet;
        this.colour = colour;
    }

    public RouletteBet(Integer id, Integer rollId, String userId, Float moneyBet, String colour){
        this.id = id;
        this.rollId = rollId;
        this.userId = userId;
        this.moneyBet = moneyBet;
        this.colour = colour;

    }

    public Integer getId() {
        return this.id;
    }

    public Integer getRollId() {
        return this.rollId;
    }

    public String getUserId() {
        return this.userId;
    }

    public Float getMoneyBet() {
        return this.moneyBet;
    }

    public String getColour() {
        return this.colour;
    }
}
