package com.nelo.cryptovote.WebApiAdapters;

import com.nelo.cryptovote.Domain.Question;

public interface QuestionGetListener {
    void onComplete(Question question);
}
