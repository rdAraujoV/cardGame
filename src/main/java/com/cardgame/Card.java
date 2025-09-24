package com.cardgame;

enum Type {
    MELEE,
    RANGED,
    STRUCTURE;
}

public class Card {
    private Player owner;
    private String description;
    private Type type;
    private int hp;
    private int damage;
    private Row position;
    private String name;
    private String design; // image path
    private String set;

    // getters
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

    /**
     * Copy constructor to create a new instance of a card from a template.
     * Note: Runtime state like owner and position are not copied.
     */
    public Card(Card template) {
        this(template.name, template.description, template.type, template.hp, template.damage, template.design, template.set);
    }


    // ===========================================
    // ------------------ CARDS ------------------
    // ===========================================

    // ------------------ MELEE ------------------
    // ANNA
    /* 
    public static Card createAnna() {
        return new Card("Anna", "A fierce melee warrior.", Type.MELEE, 5, 3, null);
    }

    // JORDAN
    public static Card createJordan() {
        return new Card("Jordan", "A powerful melee fighter.", Type.MELEE, 8, 8,
                "\\assets\\cards\\melee\\jordan\\card.png");
    }

    // GEORGE
    public static Card createGeorge() {
        return new Card("George", "A sturdy melee combatant.", Type.MELEE, 5, 5, null);
    }

    // ------------------ RANGED -----------------
    // PAT
    public static Card createPat() {
        return new Card("Pat", "Notorious assassin of the Secret Clan of Assassins of Metropolis City", Type.RANGED, 3,
                5, null);
    }

    // PIRATE
    public static Card createPirate() {
        return new Card("Pirate", "A cunning ranged pirate.", Type.RANGED, 4, 6, null);
    }

    // JOHN
    public static Card createJohn() {
        return new Card("John", "A skilled ranged attacker.", Type.RANGED, 3, 8, null);
    }

    // ---------------- STRUCTURE ----------------

    // PIRATE SHIP
    public static Card createPirateShip() {
        return new Card("Pirate Ship", "A formidable pirate ship.", Type.STRUCTURE, 10, 1, null);
    }

    // LIGHTHOUSE
    public static Card createLighthouse() {
        return new Card("Lighthouse", "A guiding lighthouse structure.", Type.STRUCTURE, 12, 0, null);
    }

    // FORT
    public static Card createFort() {
        return new Card("Fort", "A strong defensive fort.", Type.STRUCTURE, 15, 2, null);
    }*/
}