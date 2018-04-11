package com.basaki.noobchain;

import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * {@code StringUtil} contains cryptography related utility functions.
 */
@SuppressWarnings({"squid:S00112"})
public class StringUtil {

    private static final int MAGIC_NUMBER = 0xff;

    /**
     * Creates a SHA-256 hash of the input string.
     *
     * @param input string to be hashed
     * @return a hexadecimal string representation of a SHA-256 hash
     */
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            //Applies sha256 to our input,
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString =
                    new StringBuilder(); // This will contain hash as hexidecimal
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(MAGIC_NUMBER & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a signature from the private key and the input string.
     *
     * @param privateKey the private key used in signing
     * @param input      the input string to be used in the signature
     * @return the ECDSA signature
     */
    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output = new byte[0];
        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }


    /**
     * Verfies a signature.
     *
     * @param publicKey the corresponding public key of the private key used to
     *                  create the signature
     * @param data      data which is part of the signature
     * @param signature the signature to be verified
     * @return true if the signature is valid
     */
    public static boolean verifyECDSASig(PublicKey publicKey, String data,
            byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * A {@code Merkle tree} is constructed by pairing the transaction ids and
     * hashing them and hashing the results until a single hash remains. The
     * remaining single hash is called the {@code Merkle Root}.
     *
     * @param transactions all transactions within a block
     * @return a Merkle root
     */
    public static String getMerkleRoot(List<Transaction> transactions) {
        int count = transactions.size();

        List<String> previousTreeLayer = new ArrayList<>();
        for (Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.getTransactionId());
        }

        List<String> treeLayer = previousTreeLayer;

        while (count > 1) {
            treeLayer = new ArrayList<>();
            for (int i = 1; i < previousTreeLayer.size(); i += 2) {
                treeLayer.add(applySha256(previousTreeLayer.get(i - 1)
                        + previousTreeLayer.get(i)));
            }

            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }

        return (treeLayer.size() == 1) ? treeLayer.get(0) : "";
    }

    private StringUtil() {
    }
}
