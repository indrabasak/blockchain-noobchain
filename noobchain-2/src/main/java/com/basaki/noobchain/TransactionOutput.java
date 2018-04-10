package com.basaki.noobchain;

import java.security.PublicKey;
import lombok.Getter;

public class TransactionOutput {

    @Getter
    private String id;

    // owner of new noob coins
    @Getter
    private PublicKey recipient;

    // amount of noob coins
    @Getter
    private float value;

    // id of the parent transaction
    @Getter
    private String parentTransactionId;

    public TransactionOutput(PublicKey reciepient, float value,
            String parentTransactionId) {
        this.recipient = reciepient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(
                StringUtil.getStringFromKey(reciepient) + Float.toString(
                        value) + parentTransactionId);
    }

    //Check if coin belongs to you

    /**
     * Check if a transaction belongs to a public key
     *
     * @param publicKey public key to be tested against
     * @return true if the transaction belongs to the public
     * key passed as the parameter
     */
    public boolean isMine(PublicKey publicKey) {
        return (publicKey == recipient);
    }
}
