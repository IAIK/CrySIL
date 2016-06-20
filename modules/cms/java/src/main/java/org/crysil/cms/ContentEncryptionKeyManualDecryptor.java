package org.crysil.cms;

import javax.crypto.SecretKey;

import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CryptoException;

public interface ContentEncryptionKeyManualDecryptor {


  SecretKey decrypt(KeyTransRecipientInfo kInfo, AlgorithmIdentifier contentEncryptionAlgorithm)
      throws CryptoException;

}
