package com.nelo.cryptovote.Domain;

import android.util.Log;

import com.nelo.cryptovote.Base58;
import com.nelo.cryptovote.Signer;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Recount extends BlockItem implements Serializable {
    public UUID id;
    public UUID urnId;
    public List<ChoiceRecount> results = new ArrayList<>();

    @Override
    public byte[] getData() {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16 + 16 + results.size() * (16 + 4)]);

        buffer.putLong(urnId.getMostSignificantBits());
        buffer.putLong(urnId.getLeastSignificantBits());

        buffer.putLong(id.getMostSignificantBits());
        buffer.putLong(id.getLeastSignificantBits());

        for (ChoiceRecount recount : results) {
            buffer.putLong(recount.choiceId.getMostSignificantBits());
            buffer.putLong(recount.choiceId.getLeastSignificantBits());
            buffer.putInt(recount.votes);
        }

        return buffer.array();
    }

    public boolean isValid() {
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
