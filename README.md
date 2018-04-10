[![Build Status][travis-badge]][travis-badge-url]
[![Quality Gate][sonarqube-badge]][sonarqube-badge-url] 
[![Technical debt ratio][technical-debt-ratio-badge]][technical-debt-ratio-badge-url] 
[![Coverage][coverage-badge]][coverage-badge-url]

![](./img/bockchain-noobchain.svg)

Blockchain Noobchain Example
==================================
The noobchain examples are based on the following blogs:

1. [Creating Your First Blockchain with Java. Part 1](https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa)

1. [Creating Your First Blockchain with Java. Part 2 — Transactions.](https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce)


Blockchain Glossary
=======================

A **blockchain** is a chain/list of blocks where:
 - Every block contains a hash (digital signature) of the previous block. 
 - A block hash is computed from the hash of its previous block.
 - Each block is guaranteed to come after the previous block chronologically 
 since a block's hash is based on the previous block's hash.
 
A block in a blockchain is computationally impractical to modify since every 
block after it has to be regenerated.

A **genesis block** is the first block of a blockchain.

**Proof of Work (POW)** is a way of ensuring that a new block is difficult to build by
making the block creation process costly and time consuming. However it must be 
relatively trivial to check if a blockchain satifies these requriiments. This 
helps to avoid blockchain tampering.

**Hashcash** is the **Proof of Work** used by Bitcoin. 

A blockchain **wallet** is a digital wallet that allows users to 
manage crypto-currencies such as bitcoin. Coin ownership in a blockchain is 
transferred as transactions. Every participant in a blockchain has a unique
address for sending and receiving crypto-currencies.

Noobchain Example 1
====================

![](./img/blockchain.svg)

Noobchain example one includes the following:

- A simple block (`Block.java`) containing `previous block's hash`, its own 
`hash`, and simple `data`.

```java
public String calculateHash() {
    String calculatedhash = StringUtil.applySha256(
        previousHash +
        Long.toString(timeStamp) +
        Integer.toString(nonce) +
        data);

    return calculatedhash;
}
```

- The `hash` is generated using `SHA-256` cryptograhic hash algorithm. The code
can be found in `StringUtil.java`.

- A blockchain, `NoobChain.java`, built with the aforementioned blocks.

- The first block is called the `genesis block`. The previous hash of the 
 genesis block is defaulted to `0`.

- Checking the `validity` of the `noobchain` by iterating over all the blocks and
comparing the current block hash with the calculated hash. It also checks the 
current block's reference to the previous block hash with the previous block hash.
Any tampering of a block's hash is easily detected.

```java
public Boolean isChainValid() {
    // loop through blockchain to check hashes
    for (int i = 1; i < blockchain.size(); i++) {
        Block currentBlock = blockchain.get(i);
        Block previousBlock = blockchain.get(i - 1);

        // compare current block's hash with calculated hash
        if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
            log.info("Calculated hash doesn't match block's hash.");
            return false;
        }

        // compare previous hash with current block's previous hash
        if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
            log.info(
                "Previous block hash doesn't match curent block's previous hash.");
            return false;
        }
    }
    return true;
}
```

- The `Proof of Work` in this example is measured by having a certain number of 
`0s` at the beginning of the `hash`. This is done by introducing a `nonce` 
(number used once) in the hash claculation. The `nonce` is modified until the 
required result is achieved. You can find the code in the `mineBlock` method
of `Block` class. The `mineBlock` takes a parameter named `difficulty` which
specifies the number of starting `0s` required for a hash.

```java
public void mineBlock(int difficulty) {
   //Create a string with difficulty * "0"
   String target = new String(new char[difficulty]).replace('\0', '0');

   while (!hash.substring(0, difficulty).equals(target)) {
       nonce++;
       hash = calculateHash();
   }
}
```

![](./img/noobchain-class-dia.svg)

Noobchain Example 2
====================
Noobchain example two includes the following:

- Create a simple `wallet` which can send and receive transactions of `noobcoins`.

- Every `wallet` has a `public key` and a `private key`. The `public key` acts
as the wallet's address. The `private key` is used to `sign transactions`. 
Signing prevents tampering with a owner's `noobcoins`. A sender's `public key` 
is sent along every transaction for verification.

- A private and public keys are generated as a key pair`. `Elliptic Curve Digital 
Signature Algorithm (ECDSA)` signature is used to generate the key pair in
method `generateKeyPair` of `Wallet` class.

- `Transactions` are sent using `NoobChain`. Each transaction contains the
following information:
  - a unique `transaction id`
  - `public keys` of the sender and the receiver of funds
  - `amount` to be transferred. 
  - previous transactions (`inputs`) which helps in proving that there is 
  enough fund to send. 
  - outgoing transactions (`outputs`) with the amount to be transferred 
  - a `crytographic signature` to prove that that the transaction hasn't been
tampered with


### Build
To build the JAR, execute the following command from the parent directory:

```
mvn clean install
```

### Usage

 
[travis-badge]: https://travis-ci.org/indrabasak/blockchain-noobchain.svg?branch=master
[travis-badge-url]: https://travis-ci.org/indrabasak/blockchain-noobchain/

[sonarqube-badge]: https://sonarcloud.io/api/badges/gate?key=com.basaki:blockchain-noobchain
[sonarqube-badge-url]: https://sonarcloud.io/dashboard/index/com.basaki:blockchain-noobchain 

[technical-debt-ratio-badge]: https://sonarcloud.io/api/badges/measure?key=com.basaki:blockchain-noobchain&metric=sqale_debt_ratio
[technical-debt-ratio-badge-url]: https://sonarcloud.io/dashboard/index/com.basaki:blockchain-noobchain

[coverage-badge]: https://sonarcloud.io/api/badges/measure?key=com.basaki:blockchain-noobchain&metric=coverage
[coverage-badge-url]: https://sonarcloud.io/dashboard/index/com.basaki:blockchain-noobchain
