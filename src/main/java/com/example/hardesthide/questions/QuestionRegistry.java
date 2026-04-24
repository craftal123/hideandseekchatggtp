package com.example.hardesthide.questions;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class QuestionRegistry {
    private static final Map<String, HHSQuestion> QUESTIONS = new LinkedHashMap<>();

    static {
        add(new HHSQuestion("east_west", "East or West?", "Reveals which half of the map can be eliminated.", QuestionCategory.DIRECTION, AnswerType.EAST_WEST, 20 * 25));
        add(new HHSQuestion("north_south", "North or South?", "Reveals which half of the map can be eliminated.", QuestionCategory.DIRECTION, AnswerType.NORTH_SOUTH, 20 * 25));
        add(new HHSQuestion("above_below", "Above or below me?", "Compares hider Y-level to the asking hunter.", QuestionCategory.DIRECTION, AnswerType.YES_NO, 20 * 30));
        add(new HHSQuestion("within_100", "Within 100 blocks?", "Eliminates inside or outside a 100-block circle.", QuestionCategory.DISTANCE, AnswerType.YES_NO, 20 * 45));
        add(new HHSQuestion("within_250", "Within 250 blocks?", "Eliminates inside or outside a 250-block circle.", QuestionCategory.DISTANCE, AnswerType.YES_NO, 20 * 60));
        add(new HHSQuestion("in_water", "In water?", "Reveals whether the hider is in water.", QuestionCategory.ENVIRONMENT, AnswerType.YES_NO, 20 * 40));
        add(new HHSQuestion("underground", "Underground?", "Reveals whether the hider is below sea-level/terrain threshold.", QuestionCategory.ENVIRONMENT, AnswerType.YES_NO, 20 * 40));
        add(new HHSQuestion("forest_biome", "In forest biome?", "Reveals if the hider is in a forest-like biome.", QuestionCategory.ENVIRONMENT, AnswerType.YES_NO, 20 * 45));
        add(new HHSQuestion("higher_y80", "Higher than Y=80?", "Reveals vertical information.", QuestionCategory.PRECISION, AnswerType.YES_NO, 20 * 50));
        add(new HHSQuestion("near_hunter", "Near any hunter?", "Checks if a hunter is close to the hider.", QuestionCategory.RISK, AnswerType.YES_NO, 20 * 45));
        add(new HHSQuestion("moving", "Is the hider moving?", "Reveals whether the hider is currently moving.", QuestionCategory.RISK, AnswerType.YES_NO, 20 * 35));
    }

    private static void add(HHSQuestion q) {
        QUESTIONS.put(q.id(), q);
    }

    public static HHSQuestion get(String id) {
        return QUESTIONS.get(id);
    }

    public static Collection<HHSQuestion> all() {
        return QUESTIONS.values();
    }

    private QuestionRegistry() {}
}
