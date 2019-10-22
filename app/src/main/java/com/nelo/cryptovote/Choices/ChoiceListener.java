package com.nelo.cryptovote.Choices;

import com.nelo.cryptovote.Domain.IssueChoice;

public interface ChoiceListener{
    void onVote(IssueChoice choice);
}
