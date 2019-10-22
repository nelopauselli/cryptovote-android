package com.nelo.cryptovote.Domain;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class QuestionChoice implements Serializable {
    public UUID id;
    public int color;
    public String text;
    public String guardianAddress;

    public byte[] getData() throws UnsupportedEncodingException {
        byte[] guardianAddressAsBytes = guardianAddress.getBytes("utf-8");
        byte[] textAsBytes = text.getBytes("utf-8");

        ByteBuffer buffer = ByteBuffer.wrap(new byte[16 + 4 + textAsBytes.length + guardianAddressAsBytes.length]);

        buffer.putLong(id.getMostSignificantBits());
        buffer.putLong(id.getLeastSignificantBits());

        buffer.putInt(color);

        buffer.put(textAsBytes);
        buffer.put(guardianAddressAsBytes);

        return buffer.array();
    }
}
