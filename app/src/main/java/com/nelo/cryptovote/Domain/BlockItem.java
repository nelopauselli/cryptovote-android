package com.nelo.cryptovote.Domain;

import java.io.UnsupportedEncodingException;

public abstract class BlockItem {
    public String publicKey;
    public String signature;

    public abstract byte[] getData() throws UnsupportedEncodingException;
}
