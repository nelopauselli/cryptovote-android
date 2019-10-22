package com.nelo.cryptovote.Domain;

import com.nelo.cryptovote.Base58;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class Member extends BlockItem {
    public UUID id;
    public UUID communityId;
    public String name;
    public String address;

    @Override
    public byte[] getData() throws UnsupportedEncodingException {
        byte[] addressAsBytes = Base58.decode(address);
        byte[] nameAsBytes = name.getBytes("utf-8");

        ByteBuffer buffer = ByteBuffer.wrap(new byte[16 + 16 + addressAsBytes.length + nameAsBytes.length]);

        buffer.putLong(id.getMostSignificantBits());
        buffer.putLong(id.getLeastSignificantBits());

        buffer.putLong(communityId.getMostSignificantBits());
        buffer.putLong(communityId.getLeastSignificantBits());

        buffer.put(addressAsBytes);

        buffer.put(nameAsBytes);

        return buffer.array();
    }
}