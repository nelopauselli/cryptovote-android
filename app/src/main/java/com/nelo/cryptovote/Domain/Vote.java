package com.nelo.cryptovote.Domain;

import android.util.Log;

import com.nelo.cryptovote.Base58;
import com.nelo.cryptovote.Signer;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.UUID;

public class Vote extends BlockItem implements Serializable {
    public UUID issueId;
    public UUID choiceId;
    public long time;

    @Override
    public byte[] getData() {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16 + 16 + 8]);

        buffer.putLong(issueId.getMostSignificantBits());
        buffer.putLong(issueId.getLeastSignificantBits());

        buffer.putLong(choiceId.getMostSignificantBits());
        buffer.putLong(choiceId.getLeastSignificantBits());

        buffer.putLong(time);

        return buffer.array();
    }

    public boolean isValid() {
        Signer signer = new Signer();
        byte[] publicKey = Base58.decode(this.publicKey);
        byte[] signature = Base58.decode(this.signature);

        byte[] data = this.getData();
        StringBuilder sb =new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b)).append(" ");
        }
        Log.d(getClass().getSimpleName(), "data: " + sb.toString());

        return signer.verifySignature(data, publicKey, signature);
    }
}
