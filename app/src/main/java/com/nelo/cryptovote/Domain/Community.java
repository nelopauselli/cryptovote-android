package com.nelo.cryptovote.Domain;

import java.nio.ByteBuffer;
import java.util.UUID;

public class Community extends BlockItem {
    public UUID id;
    public String name;
    public long createAt;

    @Override
    public byte[] getData() {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16 + 20 + 8]);
        buffer.putLong(id.getMostSignificantBits());
        buffer.putLong(id.getLeastSignificantBits());

        byte[] name20 = String.format("%1$-" + 20 + "s", name).getBytes();
        buffer.put(name20);

        buffer.putLong(createAt);

        return buffer.array();
    }
}
