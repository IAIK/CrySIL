package org.crysil.receiver.jcereceiver.jceprovider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.crysil.errorhandling.CrySILException;
import org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilKey;
import org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilKeyHandle;
import org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilKeyInternalCertificate;
import org.crysil.receiver.jcereceiver.crysilhighlevelapi.KeyRepresentation;

/**
 * The Class CrysilKeyStore.
 */
public class CrysilKeyStore extends KeyStoreSpi {
	protected CrysilProvider provider;
    
    /** The table. */
    private Hashtable<String, CrysilKey> table = new Hashtable<>();
    
    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineLoad(java.security.KeyStore.LoadStoreParameter)
     */
    @Override
    public void engineLoad(KeyStore.LoadStoreParameter param) throws IOException, NoSuchAlgorithmException, CertificateException {
        if (param!=null) {
            if (!(param instanceof CommandIdParameters)) {
                throw new IOException(param.getClass() + " parameters not supported");
            }
        }
        this.engineLoad(null,null);
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineAliases()
     */
    @Override
    public Enumeration<String> engineAliases() {
        return table.keys();
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineContainsAlias(java.lang.String)
     */
    @Override
    public boolean engineContainsAlias(String alias) {
        return table.get(alias) != null;
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineDeleteEntry(java.lang.String)
     */
    @Override
    public void engineDeleteEntry(String alias) throws KeyStoreException {
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineGetCertificate(java.lang.String)
     */
    @Override
    public Certificate engineGetCertificate(String alias) {
        CrysilKey k = table.get(alias);
        if (k == null)
            return null;

        return ((CrysilKeyInternalCertificate)k).getX509Certificate();
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineGetCertificateAlias(java.security.cert.Certificate)
     */
    @Override
    public String engineGetCertificateAlias(Certificate cert) {
        return null;
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineGetCertificateChain(java.lang.String)
     */
    @Override
    public Certificate[] engineGetCertificateChain(String alias) {
        return null;
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineGetCreationDate(java.lang.String)
     */
    @Override
    public Date engineGetCreationDate(String alias) {
        return null;
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineGetKey(java.lang.String, char[])
     */
    @Override
    public Key engineGetKey(String alias, char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        return table.get(alias);
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineIsCertificateEntry(java.lang.String)
     */
    @Override
    public boolean engineIsCertificateEntry(String alias) {
        return false;
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineIsKeyEntry(java.lang.String)
     */
    @Override
    public boolean engineIsKeyEntry(String alias) {
        return false;
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineLoad(java.io.InputStream, char[])
     */
    @Override
    public void engineLoad(InputStream stream, char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
        table.clear();

        try {
			List<CrysilKey> crysilKeys = provider.getApi().discoverKeys(KeyRepresentation.CERTIFICATE);
            for (CrysilKey crysilKey : crysilKeys) {
                CrysilKeyHandle handle = (CrysilKeyHandle)crysilKey;
				table.put(handle.getId() + " - " + handle.getSubId(), crysilKey);
            }
        } catch (CrySILException e) {
            throw new IOException(e);
        }
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineSetCertificateEntry(java.lang.String, java.security.cert.Certificate)
     */
    @Override
    public void engineSetCertificateEntry(String alias, Certificate cert) throws KeyStoreException {
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineSetKeyEntry(java.lang.String, byte[], java.security.cert.Certificate[])
     */
    @Override
    public void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain) throws KeyStoreException {
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineSetKeyEntry(java.lang.String, java.security.Key, char[], java.security.cert.Certificate[])
     */
    @Override
    public void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain) throws KeyStoreException {
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineSize()
     */
    @Override
    public int engineSize() {
        return table.size();
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineStore(java.io.OutputStream, char[])
     */
    @Override
    public void engineStore(OutputStream stream, char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
    }
}
