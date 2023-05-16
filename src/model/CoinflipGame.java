package model;

import java.io.Serializable;

public class CoinflipGame implements Serializable {
    private Integer id;
    private String playerOneId;
    private String playerTwoId;
    private Float bet;
    private String winner;
    private Boolean done;

    public CoinflipGame(){
        this.id = -1;
        this.playerOneId = "";
        this.playerTwoId = "";
        this.bet = (float) 0;
        this.winner = "";
        this.done = false;
    }

    public CoinflipGame(String playerOneId, Float bet){
        this.id = null;
        this.playerOneId = playerOneId;
        this.playerTwoId = null;
        this.bet = bet;
        this.winner = null;
        this.done = false;
    }

    public CoinflipGame(Integer id, String playerOneId, String playerTwoId, Float bet, String winner, Boolean done){
        this.id = id;
        this.playerOneId = playerOneId;
        this.playerTwoId = playerTwoId;
        this.bet = bet;
        this.winner = winner;
        this.done = done;
    }

    public Integer getId() {return this.id;}
    public String getPlayerOneId() {return this.playerOneId;}
    public String getPlayerTwoId() {return this.playerTwoId;}
    public Float getBet() {return this.bet;}
    public String getWinner() {return this.winner;}
    public Boolean getDone() {return this.done;}

    public void setWinner(String winner) {this.winner = winner;}
}
