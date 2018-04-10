package com.basaki.noobchain;

import lombok.Getter;
import lombok.Setter;

public class TransactionInput {

    // reference to transactionId of TransactionOutput
    @Getter
    private String transactionOutputId;

    // unspent transaction output
    @Getter
    @Setter
    private TransactionOutput unspentTxnOutput;

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
