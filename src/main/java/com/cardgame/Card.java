package com.cardgame;

enum Type {
    MELEE(1),
    RANGED(2),
    STRUCTURE(1);

    private final int range;

    Type(int range) {
        this.range = range;
    }

    public int getRange() {
        return range;
    }
}

public class Card {
    private int range;
    private Player owner;
    private String description;
    private Type type;
    private int hp;
    private int damage;
    private Row position;
    private String name;
    private String design;
    private String set;

    // getters
    public int getRange() {
        return range;
    }

    public Player getOwner() {
        return owner;
    }

    public String getDescription() {
        return description;
    }

    public Type getType() {
        return type;
    }

    public int getHp() {
        return hp;
    }

    public int getDamage() {
        return damage;
    }

    public Row getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public String getDesign() {
        return design;
    }

    public String getSet() {
        return set;
    }

    // setters
    public void setRange(int range) {
        this.range = range;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setPosition(Row position) {
        this.position = position;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesign(String design) {
        this.design = design;
    }

    public void setSet(String set) {
        this.set = set;
    }

    public Card() {
        // Default constructor
    }

    public Card(String name, String description, Type type, int hp, int damage, String design, String set) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.hp = hp;
        this.damage = damage;
        this.design = design;
        this.set = set;
    }

    public Card(Card template) {
        this(template.name, template.description, template.type, template.hp, template.damage, template.design,
                template.set);
    }
}