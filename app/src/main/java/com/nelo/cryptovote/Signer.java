package com.nelo.cryptovote;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.nelo.cryptovote.Domain.BlockItem;
import com.nelo.cryptovote.Domain.Member;

import org.spongycastle.asn1.sec.SECNamedCurves;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.generators.ECKeyPairGenerator;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECKeyGenerationParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.jce.spec.ECParameterSpec;
import org.spongycastle.jce.spec.ECPrivateKeySpec;
import org.spongycastle.jce.spec.ECPublicKeySpec;
import org.spongycastle.math.ec.ECPoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

public class Signer {
    private static final ECDomainParameters CURVE;
    private static final ECParameterSpec CURVE_SPEC;
    private static final BigInteger HALF_CURVE_ORDER;

    public static Pair<byte[], byte[]> pair = null;
    private static CharSequence currentUserName;

    static {
        X9ECParameters params = SECNamedCurves.getByName("secp256k1");
        CURVE = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH(), params.getSeed());
        CURVE_SPEC = new ECParameterSpec(params.getCurve(), params.getG(), params.getN(), params.getH(), params.getSeed());
        HALF_CURVE_ORDER = params.getN().shiftRight(1);

        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    public static boolean exists(Context context, CharSequence userName) {
        File privateKeyFile = new File(context.getFilesDir(), userName + ".dat");
        Log.d("Signer", "Verificando si existe el usuario");

        return privateKeyFile.exists();
    }

    public static void remove(Context context,String username) {
        File privateKeyFile = new File(context.getFilesDir(), username + ".dat");
        if(privateKeyFile.exists())
            privateKeyFile.delete();
    }


    public static boolean create(Context context, CharSequence userName, byte[] password) {
        try {
            File privateKeyFile = new File(context.getFilesDir(), userName + ".dat");
            if (!privateKeyFile.exists()) {
                Log.d("Signer", "Creando hash del password");
                MessageDigest sha256 = MessageDigest.getInstance("sha256");
                byte[] key = sha256.digest(password);
                SecretKeySpec sks = new SecretKeySpec(key, "AES");

                Log.d("Signer", "Creando archivo");
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, sks);

                FileOutputStream fos = new FileOutputStream(privateKeyFile);
                CipherOutputStream cos = new CipherOutputStream(fos, cipher);

                Log.d("Signer", "key [" + key.length + "]: " + Base58.encode(key));
                cos.write(key);

                pair = create();
                Log.d("Signer", "PrivateKey [" + pair.first.length + "]: " + Base58.encode(pair.first));
                Log.d("Signer", "PublicKey [" + pair.second.length + "]: " + Base58.encode(pair.second));

                cos.write(pair.first);
                cos.flush();
                cos.close();

                return true;
            }
        } catch (Exception e) {
            Log.e("Signer.load", e.getMessage(), e);
        }

        return false;

    }

    public static boolean load(Context context, CharSequence userName, byte[] password) {
        try {
            File privateKeyFile = new File(context.getFilesDir(), userName + ".dat");

            Log.d("Signer", "Creando hash del password");
            MessageDigest sha256 = MessageDigest.getInstance("sha256");
            byte[] key = sha256.digest(password);
            SecretKeySpec sks = new SecretKeySpec(key, "AES");

            if (privateKeyFile.exists()) {
                Log.d("Signer", "Leyendo archivo existente");
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, sks);

                FileInputStream fis = new FileInputStream(privateKeyFile);
                CipherInputStream cis = new CipherInputStream(fis, cipher);


                byte[] key2 = new byte[32];
                cis.read(key2, 0, 32);
                Log.d("Signer", "key [" + key.length + "]: " + Base58.encode(key));
                Log.d("Signer", "key2 [" + key2.length + "]: " + Base58.encode(key2));
                if (!Arrays.equals(key, key2)) {
                    Log.w("Signer", "PIN inválido");
                    cis.close();
                    return false;
                }

                byte[] privateKey = new byte[32];
                cis.read(privateKey, 0, 32);
                pair = new Pair<>(privateKey, getPublicKeyFromPrivateKeyEx(privateKey));

                Log.d("Signer", "PrivateKey [" + pair.first.length + "]: " + Base58.encode(pair.first));
                Log.d("Signer", "PublicKey [" + pair.second.length + "]: " + Base58.encode(pair.second));

                cis.close();
            }

            setUserName(userName);
            return true;
        } catch (Exception e) {
            Log.e("Signer.load", e.getMessage(), e);
        }

        return false;
    }

    private static Pair<byte[], byte[]> create() {
        Log.d("Signer", "generando pair de llaves");

        ECKeyGenerationParameters keyGenParams = new ECKeyGenerationParameters(CURVE, new SecureRandom());

        ECKeyPairGenerator generator = new ECKeyPairGenerator();
        generator.init(keyGenParams);
        AsymmetricCipherKeyPair pair = generator.generateKeyPair();

        ECPrivateKeyParameters privateKey = (ECPrivateKeyParameters) pair.getPrivate();
        byte[] privateKeyBytes = privateKey.getD().toByteArray();
        ECPublicKeyParameters publicKey = (ECPublicKeyParameters) pair.getPublic();
        byte[] publicKeyBytes = publicKey.getQ().getEncoded(false);

        Signer.pair = new Pair<>(privateKeyBytes, publicKeyBytes);
        return Signer.pair;
    }

    public static byte[] getPublicKeyFromPrivateKeyEx(byte[] privateKey) {
        BigInteger d = new BigInteger(privateKey);
        ECPoint q = CURVE.getG().multiply(d);

        ECPublicKeyParameters publicKey = new ECPublicKeyParameters(q, CURVE);

        return publicKey.getQ().getEncoded(false);
    }

    public static List<CharSequence> listIdentities(Context context) {
        List<CharSequence> identities = new ArrayList<>();

        File[] files = context.getFilesDir().listFiles();
        for(File file : files) {
            String name = file.getName();

            Log.d("Signer", "Archivo: " + name);
            if(name.endsWith(".dat")) {
                identities.add(name.substring(0, name.length() - 4));
            }
        }
        return identities;
    }

    private static void setUserName(CharSequence userName) {
        Signer.currentUserName = userName;
    }

    public static CharSequence getUserName() {
        return Signer.currentUserName;
    }

    private byte[] getSignature(byte[] privateKey, byte[] data) {
        try {
            BigInteger s = new BigInteger(privateKey);
            ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(s, CURVE_SPEC);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PrivateKey pk = keyFactory.generatePrivate(privateKeySpec);

            final Signature signer = Signature.getInstance("SHA256withECDSA");
            signer.initSign(pk);
            signer.update(data);
            return signer.sign();

        } catch (Exception e) {
            Log.e("Signer", e.getMessage(), e);
        }
        return null;

    }

    public boolean verifySignature(byte[] data, byte[] publicKey, byte[] signature) {
        try {
            ECPoint q = CURVE_SPEC.getCurve().decodePoint(publicKey);
            ECPublicKeySpec publicKeyParameters = new ECPublicKeySpec(q, CURVE_SPEC);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PublicKey pk = keyFactory.generatePublic(publicKeyParameters);

            final Signature verifier = Signature.getInstance("SHA256withECDSA");
            verifier.initVerify(pk);
            verifier.update(data);
            return verifier.verify(signature);

        } catch (Exception e) {
            Log.e("Signer", e.getMessage(), e);
        }
        return false;
    }

    public byte[] getPrivateKey() {
        return pair.first;
    }

    public byte[] getPublicKey() {
        return pair.second;
    }

    public void sign(BlockItem item) throws UnsupportedEncodingException {
        byte[] data = item.getData();
        Log.d("Signer", "Raw: " + bytesToHex(data));

        byte[] signature = getSignature(Signer.pair.first, data);
        item.signature = Base58.encode(signature);
        item.publicKey = Base58.encode(Signer.pair.second);

        Log.d("Signer", "Signature: " + item.signature);
        Log.d("Signer", "PublicKey: " + item.publicKey);

    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}