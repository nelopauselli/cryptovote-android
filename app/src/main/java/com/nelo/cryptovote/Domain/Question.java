package com.nelo.cryptovote.Domain;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Question extends BlockItem implements Serializable {
    public UUID id;
    public UUID communityId;
    public String name;
    public byte type;
    public long endTime;
    public List<QuestionChoice> choices = new ArrayList<>();

    @Override
    public byte[] getData() throws UnsupportedEncodingException {
        List<byte[]> temp = new ArrayList<>();
        int tempLength = 0;

        for (QuestionChoice choice : choices) {
            byte[] choiceAsBytes = choice.getData();

            temp.add(choiceAsBytes);
            tempLength += choiceAsBytes.length;
        }

        byte[] nameAsBytes = name.getBytes("utf-8");
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16 + 16 + nameAsBytes.length + 1 + 8 + 8 + tempLength]);

        buffer.putLong(id.getMostSignificantBits());
        buffer.putLong(id.getLeastSignificantBits());

        buffer.putLong(communityId.getMostSignificantBits());
        buffer.putLong(communityId.getLeastSignificantBits());

        buffer.put(nameAsBytes);

        buffer.put(type);

        buffer.putLong(endTime);

        for (byte[] choiceAsBytes : temp) {
            buffer.put(choiceAsBytes);
        }

        return buffer.array();
    }
}
