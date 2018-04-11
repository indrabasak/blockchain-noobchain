package com.basaki.noobchain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * {@code Wallet} represents a a digital wallet which manages noobcoins.
 */
@SuppressWarnings({"squid:S00112"})
@Slf4j
public class Wallet {

    @Getter
    private PrivateKey privateKey;

    @Getter
    private PublicKey publicKey;

    // unspent transactions
    private Map<String, TransactionOutput> utxo = new HashMap<>();

    public Wallet() {
        generateKeyPair();
    }

    /**
     * Returns the current balance of the wallet by summing all the unspent
     * transaction outputs which has the same public key as the wallet. All
     * the unspent trasactions are added to the wallet's unspent transaction.
     *
     * @return the current wallet balance
     */
    public float getBalance() {
        return NoobChain.UTXO.values()
                .stream()
                .filter(t -> {
                    boolean mine = t.isMine(publicKey);
                    // if the output belongs to the current wallet
                    if (mine) {
                        // add it to the list of unspent transactions
                        utxo.put(t.getId(), t);
                    }
                    return mine;
                })
                .collect(Collectors.summingDouble(TransactionOutput::getValue))
                .floatValue();
    }

    public Transaction sendFunds(PublicKey recipient, float value) {
        if (getBalance() < value) {
            log.info("Transaction discarded as there isn't enough fund...");
            return null;
        }

        List<TransactionInput> inputs = new ArrayList<>();

        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : utxo.entrySet()) {
            TransactionOutput unspentTxn = item.getValue();
            total += unspentTxn.getValue();
            inputs.add(new TransactionInput(unspentTxn.getId()));
            if (total > value) {
                break;
            }
        }

        for (TransactionInput input : inputs) {
            utxo.remove(input.getTransactionOutputId());
        }

        Transaction txn = new Transaction(publicKey, recipient, value, inputs);
        txn.generateSignature(privateKey);

        return txn;
    }

    private void generateKeyPair() {
        try {
            //generate key using
            // Elliptic Curve Digital Signature Algorithm (ECDSA) algorithm
            KeyPairGenerator keyGen =
                    KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec,
                    random);   //256 bytes provides an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();
            // Set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
