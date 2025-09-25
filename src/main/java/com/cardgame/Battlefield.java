package com.cardgame;
public class Battlefield {

    private Row backRowB;
    private Row midRowB;
    private Row frontRowB;
    private Row frontRowA;
    private Row midRowA;
    private Row backRowA;

    public Battlefield() {
        this.backRowA = new Row('A', 1, "backRowA");
        this.midRowA = new Row('A', 2, "midRowA");
        this.frontRowA = new Row('A', 3, "frontRowA");
        this.frontRowB = new Row('B', 4, "frontRowB");
        this.midRowB = new Row('B', 5, "midRowB");
        this.backRowB = new Row('B', 6, "backRowB");
    }

    // getters
    public Row getBackRowA() {
        return backRowA;
    }

    public Row getMidRowA() {
        return midRowA;
    }

    public Row getFrontRowA() {
        return frontRowA;
    }

    public Row getFrontRowB() {
        return frontRowB;
    }

    public Row getMidRowB() {
        return midRowB;
    }

    public Row getBackRowB() {
        return backRowB;
    }

    // setters
    public void setBackRowA(Row backRowA) {
        this.backRowA = backRowA;
    }

    public void setMidRowA(Row midRowA) {
        this.midRowA = midRowA;
    }

    public void setFrontRowA(Row frontRowA) {
        this.frontRowA = frontRowA;
    }

    public void setFrontRowB(Row frontRowB){
        this.frontRowB = frontRowB;
    }

    public void setMidRowB(Row midRowB) {
        this.midRowB = midRowB;
    }

    public void setBackRowB(Row backRowB) {
        this.backRowB = backRowB;

    }
}