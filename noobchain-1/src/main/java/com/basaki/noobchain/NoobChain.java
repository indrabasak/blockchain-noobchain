package com.basaki.noobchain;

import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * {@code NoobChain} is a simple blockchain containing a chain/list of blocks.
 */
@Slf4j
@SuppressWarnings({"squid:S106"})
public class NoobChain {

    private static final int difficulty = 5;

    private List<Block> blockchain = new ArrayList<>();

    public synchronized void addBlock(String data) {
        Block block;
        if (blockchain.isEmpty()) {
            // genesis block
            block = new Block(data, "0");

        } else {
            block = new Block(data,
                    blockchain.get(blockchain.size() - 1).getHash());
        }

        //add block to the blockchain list
        blockchain.add(block);

        block.mineBlock(difficulty);
    }

    /**
     * Validates the integrity of the blockchain by:
     * <ol>
     * <li>Looping through all the blocks in the chain and comparing their
     * hashes.</li>
     * <li>Checks if the current block's hash is equal to the calculated
     * hash.</li>
     * <li>Checks if the previous block's hash is equal to the current block's
     * previousHash attribute.</li>
     * </ol>
     *
     * @return true if the block chain is valid or false otherwise
     */
    public Boolean isChainValid() {
        //loop through blockchain to check hashes
        for (int i = 1; i < blockchain.size(); i++) {
            Block currentBlock = blockchain.get(i);
            Block previousBlock = blockchain.get(i - 1);
            //compare registered hash and calculated hash
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                log.info("Calulated hash doesn't match block's hash!");
                return false;
            }

            //compare previous hash and registered previous hash
            if (!previousBlock.getHash().equals(
                    currentBlock.getPreviousHash())) {
                log.info(
                        "Previous block hash doesn't match curent block's previous hash attribute!");
                return false;
            }
        }
        return true;
    }

    public String jsonString() {
        return new GsonBuilder().disableHtmlEscaping()
                .setPrettyPrinting()
                .create().toJson(blockchain);
    }

    public static void main(String[] args) {
        NoobChain noob = new NoobChain();

        noob.addBlock("Hi I'm the first block");
        noob.addBlock("Yo I'm the second block");
        noob.addBlock("Hey I'm the third block");

        System.out.println(noob.jsonString());
        System.out.println("valid block chain: " + noob.isChainValid());
    }
}
