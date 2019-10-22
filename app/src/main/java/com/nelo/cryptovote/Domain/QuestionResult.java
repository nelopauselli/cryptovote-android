package com.nelo.cryptovote.Domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuestionResult {
    public UUID questionId;
    public byte type;
    public List<ChoiceResult> choices = new ArrayList<>();
}
