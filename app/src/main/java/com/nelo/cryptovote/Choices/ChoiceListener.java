package com.nelo.cryptovote.Choices;

import com.nelo.cryptovote.Domain.QuestionChoice;

public interface ChoiceListener{
    void onVote(QuestionChoice choice);
}
