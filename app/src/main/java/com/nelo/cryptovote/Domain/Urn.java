package com.nelo.cryptovote.Domain;

import android.util.Log;

import com.nelo.cryptovote.Base58;
import com.nelo.cryptovote.Signer;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Urn implements Serializable {
    public UUID id;
    public UUID issueId;
    public String name;
    public List<byte[]> authorities = new ArrayList<>();

    public String publicKey;
    public String signature;

    public byte[] getData() throws UnsupportedEncodingException {
        int authoritiesLength = 0;
        for (byte[] authority : authorities) {
            authoritiesLength += authority.length;
        }
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16 + 16 + name.length() + authoritiesLength]);

        buffer.putLong(id.getMostSignificantBits());
        buffer.putLong(id.getLeastSignificantBits());

        buffer.putLong(issueId.getMostSignificantBits());
        buffer.putLong(issueId.getLeastSignificantBits());

        byte[] nameAsBytes = name.getBytes("utf-8");
        buffer.put(nameAsBytes);

        for (byte[] authority : authorities) {
            buffer.put(authority);
        }

        return buffer.array();
    }

    public boolean isValid() throws UnsupportedEncodingException {
        Signer signer = new Signer();
        byte[] publicKey = Base58.decode(this.publicKey);
        byte[] signature = Base58.decode(this.signature);

        byte[] data = this.getData();
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b)).append(" ");
        }
        Log.d(getClass().getSimpleName(), "data: " + sb.toString());

        return signer.verifySignature(data, publicKey, signature);
    }
}
