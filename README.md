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
 - Every block contains a hash (digital signature)of the previous block. 
 - A block hash is computed from the hash of its previous block.
 - Each block is guaranteed to come after the previous block chronologically 
 since a block's hash is based on the previous block's hash.
 
A block in a blockchain is computationally impractical to modify since very block
after it has to be regenerated.


A **genesis block** is the first block of a blockchain.


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
