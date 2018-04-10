package com.basaki.noobchain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * {@code Block} represents a block in a blockchain which has its own digital
 * signature, digital signature of the previous block, and data.
 */
@Slf4j
public class Block {

    @Getter
    private String hash;

    @Getter
    private final String previousHash;

    private final long timeStamp;

    //nonce to modify the hash so that the hash starts with acertain number of 0's
    private int nonce;

    private List<Transaction> transactions = new ArrayList<>();

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    /**
     * Calculates the hash of the current block from the previous block's hash,
     * timestamp, and data. If the previous block’s data is changed then the
     * previous block’s hash will change(since it is calculated in part by
     * the data). This will affect the hashes of the all the blocks which
     * come after it. A blockchain is validated by calculating and comparing the
     * hashes.
     *
     * @return the hash of the current block
     */
    public String calculateHash() {
        return StringUtil.applySha256(previousHash +
                Long.toString(timeStamp) +
                Integer.toString(nonce));
    }

    /**
     * A blockchain mining is required to do `proof of work` by having the
     * block hash starts with a variable number of 0's. The parameter
     * difficulty contains the number of 0's required. Low difficulty such as 1
     * or 2 can be solved nearly instantly on most computers. Use difficulty of
     * 4–6 for testing. At present, Litecoin’s difficulty is around 442,592.
     *
     * @param difficulty number of 0’s that a block hash should start with
     *                   miner must solve to do
     *                   proof-of-work
     */
    public void mineBlock(int difficulty) {
        //Create a string with difficulty * "0"
        String target = new String(new char[difficulty]).replace('\0',
                '0');

        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }

        log.info("Block Mined!!! : " + hash);
    }

    /**
     * Adds a transaction to this block.
     *
     * @param transaction transaction to be added
     * @return true if the transaction is valid, false otherwise
     */
    public boolean addTransaction(Transaction transaction) {
        if (transaction == null) {
            return false;
        }

        // process transaction and check if it's valid
        // ignore checking if the block is a genesis block
        if ((previousHash != "0") && !transaction.processTransaction()) {
            log.info("Transaction failed to process. Discarded.");
            return false;
        }
        transactions.add(transaction);

        log.info("Transaction Successfully added to Block");

        return true;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }
}
