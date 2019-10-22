package com.nelo.cryptovote.Domain;

import java.io.Serializable;
import java.util.UUID;

public class ChoiceRecount implements Serializable {
    public UUID choiceId;
    public int votes;
}
