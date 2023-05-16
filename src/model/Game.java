package model;

public class Game {
    private String name;

    public Game(String name) {
        this.name = name;
    }


    @Override
    public String toString(){
        return this.name;
    }
}
