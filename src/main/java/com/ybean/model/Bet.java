package com.ybean.model;

import java.math.BigDecimal;

public class Bet {

    private final int userId;
    private final int betId;
    private final int stake;

    public Bet(int userId, int betId, int stake) {
        this.userId = userId;
        this.betId = betId;
        this.stake = stake;
    }

    public int getUserId() {
        return userId;
    }

    public int getBetId() {
        return betId;
    }

    public int getStake() {
        return stake;
    }
}
