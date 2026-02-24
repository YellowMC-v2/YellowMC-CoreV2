package de.emn4tor.modules.global.daily.enums;

/*
 *  @author: Emn4tor
 *  @created: 12.06.2025
 */

public enum RankType {
    DEFAULT(100, 1), //INFO Free users
    BRONZE(200, 2), //INFO 6.5 months to break even (4.99€ rank ≈ 428 rubies)
    SILVER(300, 7), //INFO 6.1 months to break even (14.99€ rank ≈ 1285 rubies)
    GOLD(400, 12), //INFO 6.0 months to break even (24.99€ rank ≈ 2142 rubies)
    PLATIN(500, 21); //INFO 6.0 months to break even (49.99€ rank ≈ 3856 rubies)

    private final int coins;
    private final int rubies;


    RankType(int coins, int rubies) {
        this.coins = coins;
        this.rubies = rubies;
    }

    public int getCoins() {
        return coins;
    }

    public int getRubies() {
        return rubies;
    }

    //get RankType by

}
