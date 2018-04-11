package com.basaki.noobchain;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

@Slf4j
@SuppressWarnings({"squid:S3776", "squid:S3008"})
public class NoobChain {

    public static final float MINIMUM_TRANSACTION_VALUE = 0.1f;

    private static final int DIFFICULTY = 3;

    // unspent transactions
    protected static Map<String, TransactionOutput> UTXO = new HashMap<>();

    private Transaction genesisTransaction;

    private List<Block> blockchain = new ArrayList<>();

    public synchronized void addBlock(Transaction transaction) {
        Block block;

        if (blockchain.isEmpty()) {
            // genesis block
            genesisTransaction = transaction;
            //manually set the transaction id
            genesisTransaction.setTransactionId("0");

            List<TransactionOutput> outputs = transaction.getOutputs();
            if (!outputs.isEmpty()) {
                TransactionOutput output = outputs.get(0);

                // it's important to store the first
                // transaction in the UTXO list
                UTXO.put(output.getId(), output);
            }

            block = new Block("0");
        } else {
            block = new Block(blockchain.get(blockchain.size() - 1).getHash());
        }

        block.addTransaction(transaction);
        block.mineBlock(DIFFICULTY);

        //add block to the blockchain list
        blockchain.add(block);
    }

    public Boolean isChainValid() {
        if (blockchain.isEmpty()) {
            return true;
        }

        String hashTarget = new String(new char[DIFFICULTY]).replace('\0', '0');
        HashMap<String, TransactionOutput> tempUTXOs =
                new HashMap<>(); //a temporary working list of unspent transactions at a given block state.
        tempUTXOs.put(genesisTransaction.getOutputs().get(0).getId(),
                genesisTransaction.getOutputs().get(0));

        //loop through the blockchain to check hashes
        for (int i = 1; i < blockchain.size(); i++) {
            Block currentBlock = blockchain.get(i);
            Block previousBlock = blockchain.get(i - 1);

            // compare the current block's hash with calculated hash
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                log.error("Calculated hash doesn't match block's hash.");
                return false;
            }

            // compare the previous hash with current block's previous hash
            if (!previousBlock.getHash().equals(
                    currentBlock.getPreviousHash())) {
                log.error(
                        "Previous block hash doesn't match curent block's previous hash.");
                return false;
            }

            //check if current block is mined
            if (!currentBlock.getHash().substring(0, DIFFICULTY).equals(
                    hashTarget)) {
                log.error("Current block hasn't been mined.");
                return false;
            }

            //loop through blockchains transactions
            TransactionOutput tempOutput;
            List<Transaction> transactions = currentBlock.getTransactions();
            for (int t = 0; t < transactions.size(); t++) {
                Transaction currentTxn = transactions.get(t);

                if (!currentTxn.verifySignature()) {
                    log.error("Transaction({}) signature is invalid.", t);
                    return false;
                }

                if (currentTxn.getInputSum() != currentTxn.getOutputSum()) {
                    log.error("Transaction({}) input doesn't match output.", t);
                    return false;
                }

                for (TransactionInput input : currentTxn.getInputs()) {
                    tempOutput = tempUTXOs.get(input.getTransactionOutputId());

                    if (tempOutput == null) {
                        log.error("Input on transaction({}) is missing.", t);
                        return false;
                    }

                    if (input.getUnspentTxnOutput().getValue()
                            != tempOutput.getValue()) {
                        log.error("Input on transaction({}) is invalid.", t);
                        return false;
                    }

                    tempUTXOs.remove(input.getTransactionOutputId());
                }

                for (TransactionOutput output : currentTxn.getOutputs()) {
                    tempUTXOs.put(output.getId(), output);
                }

                if (currentTxn.getOutputs().get(0).getRecipient()
                        != currentTxn.getRecipient()) {
                    log.error("Mismatch output recipient in transaction({})",
                            t);
                    return false;
                }
                if (currentTxn.getOutputs().get(1).getRecipient()
                        != currentTxn.getSender()) {
                    log.error(
                            "Transaction({}) output recipient is not the sender.",
                            t);
                    return false;
                }

            }

        }

        return true;
    }

    public static void main(String[] args) {
        //Setup Bouncey castle as a Security Provider
        Security.addProvider(new BouncyCastleProvider());

        //Create wallets:
        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();
        Wallet coinbase = new Wallet();

        //create genesis transaction, which sends 100 NoobCoin to walletA
        Transaction txn =
                new Transaction(coinbase.getPublicKey(), walletA.getPublicKey(),
                        100f, null);

        // sign the genesis transaction
        txn.generateSignature(coinbase.getPrivateKey());

        TransactionOutput outputTxn =
                new TransactionOutput(txn.getRecipient(),
                        txn.getValue(),
                        txn.getTransactionId());

        // add the transactions output
        txn.addTransactionOutput(outputTxn);

        log.info("Creating and Mining Genesis block... ");
        NoobChain chain = new NoobChain();
        chain.addBlock(txn);

        log.info("WalletA's balance is: " + walletA.getBalance());
        log.info("WalletA is Attempting to send funds (40) to WalletB...");
        chain.addBlock(walletA.sendFunds(walletB.getPublicKey(), 40f));
        log.info("WalletA's balance is: {}", walletA.getBalance());
        log.info("WalletB's balance is: {}", walletB.getBalance());

        log.info("WalletA Attempting to send more funds (1000) than it has...");
        chain.addBlock(walletA.sendFunds(walletB.getPublicKey(), 1000f));
        log.info("WalletA's balance is: {}", walletA.getBalance());
        log.info("WalletB's balance is: {}", walletB.getBalance());

        log.info("WalletB is Attempting to send funds (20) to WalletA...");
        chain.addBlock(walletB.sendFunds(walletA.getPublicKey(), 20));
        log.info("WalletA's balance is {}", walletA.getBalance());
        log.info("WalletB's balance is {}", walletB.getBalance());

        log.info("Block Chain Valid: {}", chain.isChainValid());
    }
}
