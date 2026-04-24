package com.example.hardesthide.questions;

public enum QuestionCategory {
    DIRECTION(1),
    DISTANCE(2),
    ENVIRONMENT(2),
    PRECISION(3),
    STRONG_REVEAL(4),
    RISK(2);

    public final int tokenReward;

    QuestionCategory(int tokenReward) {
        this.tokenReward = tokenReward;
    }
}
