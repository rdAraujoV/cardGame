package com.cardgame;
public class Battlefield {

    private Row backRowB;
    private Row midRowB;
    private Row frontRowB;
    private Row frontRowA;
    private Row midRowA;
    private Row backRowA;

    public Battlefield() {
        this.backRowA = new Row();
        this.midRowA = new Row();
        this.frontRowA = new Row();
        this.frontRowB = new Row();
        this.midRowB = new Row();
        this.backRowB = new Row();
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