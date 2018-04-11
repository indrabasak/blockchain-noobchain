package com.basaki.noobchain;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings({"squid:S00112", "squid:S2696"})
@Slf4j
public class Transaction {

    // hash of the transaction
    @Getter
    @Setter
    private String transactionId;

    // sender's public key
    @Getter
    private PublicKey sender;

    // recipient's public key
    @Getter
    private PublicKey recipient;

    @Getter
    private float value;

    //  prevents anybody else from spending funds in the wallet
    private byte[] signature;

    private List<TransactionInput> inputs = new ArrayList<>();

    private List<TransactionOutput> outputs = new ArrayList<>();

    // a rough count of how many transactions have been generated
    private static int sequence = 0;

    public Transaction(PublicKey from, PublicKey to, float value,
            List<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    public void addTransactionOutput(TransactionOutput txn) {
        outputs.add(txn);
    }

    public List<TransactionOutput> getOutputs() {
        return Collections.unmodifiableList(outputs);
    }

    public List<TransactionInput> getInputs() {
        return Collections.unmodifiableList(inputs);
    }


    /**
     * Calculates the transaction hash which is used as the transaction id.
     *
     * @return
     */
    private String calculateHash() {
        // increase the sequence to avoid two identical transactions
        // having the same hash
        sequence++;

        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(recipient) +
                        Float.toString(value)
                        + sequence);
    }

    /**
     * Signs a transaction so that it can't be tampered.
     *
     * @param privateKey the private key used for signing
     */
    public void generateSignature(PrivateKey privateKey) {
        if (signature != null) {
            throw new RuntimeException("The transaction is already signed!");
        }

        String data = StringUtil.getStringFromKey(sender) +
                StringUtil.getStringFromKey(recipient) +
                Float.toString(value);

        signature = StringUtil.applyECDSASig(privateKey, data);
    }


    /**
     * Verifies the signature of the transaction to confirm that the transaction
     * hasn't been tampered with.
     *
     * @return true if the signature is valid, false otherwise
     */
    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) +
                StringUtil.getStringFromKey(recipient) +
                Float.toString(value);

        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    /**
     * Processes a transaction.
     *
     * @return true if the transaction can be created
     */
    public boolean processTransaction() {

        if (!verifySignature()) {
            log.info("Failed to verify transaction signature.");

            return false;
        }

        // gather transaction inputs (Make sure they are unspent):
        for (TransactionInput txn : inputs) {
            txn.setUnspentTxnOutput(
                    NoobChain.UTXO.get(txn.getTransactionOutputId()));
        }

        // check if transaction is valid
        if (getInputSum() < NoobChain.MINIMUM_TRANSACTION_VALUE) {
            log.info("Transaction input, {}, is less than minimum amount",
                    getInputSum());
            return false;
        }

        // generate transaction outputs

        // get value of inputs then the left over change:
        float leftOver = getInputSum() - value;
        transactionId = calculateHash();

        //send value to the recipient
        outputs.add(new TransactionOutput(recipient, value, transactionId));

        // send the remaining amount back to the sender
        outputs.add(new TransactionOutput(sender, leftOver, transactionId));

        // add outputs to unspent list
        for (TransactionOutput txn : outputs) {
            NoobChain.UTXO.put(txn.getId(), txn);
        }

        // remove transaction inputs from UTXO lists as spent
        for (TransactionInput txn : inputs) {
            if (txn.getUnspentTxnOutput() != null) {
                NoobChain.UTXO.remove(txn.getUnspentTxnOutput().getId());
            }
        }

        return true;
    }

    /**
     * Returns the sum of all input transaction.
     *
     * @return sum all input transactions
     */
    public float getInputSum() {
        float total = 0;
        for (TransactionInput txn : inputs) {
            if (txn.getUnspentTxnOutput() != null) {
                total += txn.getUnspentTxnOutput().getValue();
            }
        }

        return total;
    }

    /**
     * Returns the sum of all output transactions.
     *
     * @return sum of all output transactions
     */
    public float getOutputSum() {
        float total = 0;
        for (TransactionOutput txn : outputs) {
            total += txn.getValue();
        }

        return total;
    }
}
