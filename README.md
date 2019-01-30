# JBIP 39

[![Build Status](https://travis-ci.org/nightcode/jbip39.svg?branch=master)](https://travis-ci.org/nightcode/jbip39)
[![Maven Central](https://img.shields.io/maven-central/v/org.nightcode/jbip39.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3Aorg.nightcode%20AND%20a%3Ajbip39)

Java implementation of the BIP 39 specification.

How to use
----------

_Create a new mnemonic (a seed phrase)_
```java
  Dictionary dictionary = EnglishDictionary.instance();

  Bip39 bip39 = new Bip39(dictionary);

  byte[] entropy = bip39.generateEntropy(EntropyDesc.ENT_128);

  String mnemonic = bip39.createMnemonic(entropy);
```


_Convert a mnemonic to a seed_
```java
  String mnemonic = "legal winner thank year wave sausage worth useful legal winner thank yellow";
  String passphrase = "TREZOR";

  byte[] seed = bip39.createSeed(mnemonic, passphrase);
```

Download
--------

Download [the latest jar][1] via Maven:
```xml
<dependency>
  <groupId>org.nightcode</groupId>
  <artifactId>jbip39</artifactId>
  <version>0.1</version>
</dependency>
```

Credits
-------
Wordlists are from the [BIP 0039]([2]).



Feedback is welcome. Please don't hesitate to open up a new [github issue]([3]) or simply drop me a line at <dmitry@nightcode.org>.


 [1]: http://oss.sonatype.org/service/local/artifact/maven/redirect?r=releases&g=org.nightcode&a=jbip39&v=LATEST
 [2]: https://github.com/bitcoin/bips/tree/master/bip-0039
 [3]: https://github.com/nightcode/jbip39/issues
