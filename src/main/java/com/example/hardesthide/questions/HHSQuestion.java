package com.example.hardesthide.questions;

public record HHSQuestion(
        String id,
        String title,
        String tooltip,
        QuestionCategory category,
        AnswerType answerType,
        int cooldownTicks
) {}
