package at.iaik.skytrust.element.actors.smcc;

import at.gv.egiz.smcc.*;
import at.gv.egiz.smcc.pin.gui.PINGUI;
import at.gv.egiz.smcc.util.SmartCardIO;
import at.iaik.skytrust.element.SkytrustElement;
import at.iaik.skytrust.element.actors.Actor;
import at.iaik.skytrust.element.actors.common.BasicCommand;
import at.iaik.skytrust.element.actors.common.CmdType;
import at.iaik.skytrust.element.authentication.KeyBean;
import at.iaik.skytrust.element.skytrustprotocol.old.CMDHeader;
import at.iaik.skytrust.element.skytrustprotocol.old.CMDPacket;
import at.iaik.skytrust.element.skytrustprotocol.old.CMDPayload;
import at.iaik.skytrust.element.receiver.Receiver;
import com.google.gson.Gson;
import iaik.security.provider.IAIK;
import iaik.utils.Base64Exception;
import iaik.utils.Util;
import iaik.x509.X509Certificate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardTerminal;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.util.*;



public class SmartCardProvider implements Actor {
    protected Map<CmdType, BasicCommand> _providedCommands;
    protected HashMap<String, X509Certificate> _certificateMap = new HashMap<String, X509Certificate>();
    protected List<KeyBean> _keyIdentifiers = new ArrayList<KeyBean>();
    protected HashMap<String, SignatureCard> _cardMap = new HashMap<String, SignatureCard>();
    protected List<SignatureCard> _signatureCards;

    protected SmartCardIO smartCardIO;

	public SmartCardProvider() {
        Security.addProvider(new IAIK());
	    init();
    }

    protected void init() {
        _providedCommands = new HashMap<CmdType, BasicCommand>();
        _certificateMap = new HashMap<String, X509Certificate>();
        _keyIdentifiers = new ArrayList<KeyBean>();
        _cardMap = new HashMap<String, SignatureCard>();
        _signatureCards = new ArrayList<SignatureCard>();


        createCommandEntries();
        smartCardIO = new SmartCardIO();
        smartCardIO.getCardTerminals();

        loadSignatureCards();
    }

    protected void createCommandEntries(){
        System.out.println("Create Commands");
        _providedCommands = new HashMap<CmdType, BasicCommand>();
        BasicCommand basicCommand = new SMCCCommandGetCertificate(this);
        _providedCommands.put(basicCommand.getCommandType(), basicCommand);
    }



    protected Vector<SignatureCard> getSignatureCards() {
        Map<CardTerminal, Card> cards = smartCardIO.getCards();
        SignatureCardFactory factory = SignatureCardFactory.getInstance();
        Vector<SignatureCard> signatureCardVector = new Vector<SignatureCard>();

        for (CardTerminal cardTerminal : cards.keySet()) {
            try {
                Card c = cards.get(cardTerminal);
                if (c == null) {
                    throw new CardNotSupportedException();
                }
                SignatureCard signatureCard = factory.createSignatureCard(c, cardTerminal);
                signatureCardVector.add(signatureCard);
                ATR atr = cards.get(cardTerminal).getATR();
///	            logger.debug("Found supported card (" + signatureCard.toString() + ") "
//	                + "in terminal '" + cardTerminal.getName());
            } catch (CardNotSupportedException e) {
                Card c = cards.get(cardTerminal);
                if (c != null) {
                    ATR atr = c.getATR();
//	              	log.info("Found unsupported card" + " in terminal '"
//	                  + cardTerminal.getName() + "', ATR = "
//	                  + toString(atr.getBytes()) + ".");
                } else {
                    //logger.info("Found unsupported card in terminal '"
                    //        + cardTerminal.getName() + "' without ATR");
                }
            }
        }
        return signatureCardVector;
    }

    synchronized protected void loadSignatureCards() {
        _signatureCards = getSignatureCards();
        int cardIndex = 0;
        for (SignatureCard signatureCard:_signatureCards) {
            //loadCertificatesFrom(signatureCard, SignatureCard.KeyboxName.SECURE_SIGNATURE_KEYPAIR,cardIndex);
            loadCertificatesFrom(signatureCard, SignatureCard.KeyboxName.CERITIFIED_KEYPAIR,cardIndex);
            cardIndex++;
        }

	}

    protected void loadCertificatesFrom(SignatureCard card,SignatureCard.KeyboxName keyboxName,int cardIndex) {
        try {
            byte[] encodedCertificate = card.getCertificate(keyboxName);
            if (encodedCertificate!=null) {
                X509Certificate x509Certificate = new iaik.x509.X509Certificate(encodedCertificate);
                String keyIdentifier =  SkytrustElement.getSkytrustElement().getName() + ",SMCC,Card " + cardIndex + "," + keyboxName.getKeyboxName();
                  //TODO
                KeyBean keyInfo = new KeyBean();
                keyInfo.setType(x509Certificate.getPublicKey().getAlgorithm());
                keyInfo.setID(keyIdentifier);
                keyInfo.setFingerprint(Util.toBase64String(x509Certificate.getFingerprintSHA()));

                _certificateMap.put(keyIdentifier, x509Certificate);
                _keyIdentifiers.add(keyInfo);
                _cardMap.put(keyIdentifier,card);
            }
        } catch (SignatureCardException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (CertificateException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public X509Certificate getCertificate(Key key) {
        return null;      //TODO
       // return _certificateMap.get(key.getId());      //TODO
    }



    public void take(byte[] command, Receiver receiver) {
        // TODO Auto-generated method stub
        // ------------------DEBUG INFO--------------------------------------------
        System.out.println("Command received at: "
                + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                .format(new Date()) + " #################");
        System.out.println(new String(command));
        System.out
                .println("#######################################################");
        // ------------------DEBUG INFO--------------------------------------------
//        receiver.se(forwardCommand(command));

    }

    /**
     * Handles incomming command and creates response to caller
     * @param command to be header
     * @return response for caller
     */
    public byte[] forwardCommand(byte[] command) {
        // Convert transport package to class
        Gson gson = new Gson();
        //get Command
        //Command cmd = gson.fromJson(new String(command), Command.class);
        CMDPacket cmd = gson.fromJson(new String(command), CMDPacket.class);
        System.out.println("Command - " + cmd.getPayload().getCommand());
        //Proof Header and Payload
        if(cmd.getHeader().getCommandid() != null){
            // forward to appropriate method
            byte[] resp = _providedCommands.get(cmd.getPayload().getCommand()).handle(command);
            if (resp != null){
                return resp;
            }
        }
        CMDPacket response = new CMDPacket();
        CMDHeader resp_header = new CMDHeader();
        resp_header.setCommandid(cmd.getHeader().getCommandid());
        if(!cmd.getHeader().getPath().isEmpty()){
            ArrayList<String> rev = new ArrayList<String>(cmd.getHeader().getPath());
            Collections.reverse(rev);
            resp_header.setPath(rev);
        }
        response.setHeader(resp_header);
        CMDPayload resp_payload = new CMDPayload();
        resp_payload.setCode(400);
        resp_payload.setType("response");
        return response.getBytes();
    }

    @Override
    public byte[] take(byte[] command) {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<CmdType> getProvidedCommands() {
        return _providedCommands.keySet();
    }


    //TODO!!
    @Override
    public List<KeyBean> getAvailableKeys(String userId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    //TODO!!
    @Override
    public boolean isKeyAvailable(String userId, String keyId) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

//    @Override
//    public List<KeyBean> getAvailableKeySlots() {
//        return _keyIdentifiers;
//    }
//
//    @Override
//    public boolean isKeyAvailable(String friendlyname) {
//        return _certificateMap.keySet().contains(friendlyname);
//    }

    public byte[] decrypt(byte[] encrypted,String alias, String algorithm) {
        SignatureCard signatureCard = _cardMap.get(alias);
        byte[] decrypted = null;
        try {
            PINGUI pingui = new PINGUI() {
                @Override
                public void enterPINDirect(PINSpec spec, int retries) throws CancelledException, InterruptedException {

                }

                @Override
                public void enterPIN(PINSpec spec, int retries) throws CancelledException, InterruptedException {

                }

                @Override
                public void validKeyPressed() {

                }

                @Override
                public void correctionButtonPressed() {

                }

                @Override
                public void allKeysCleared() {

                }

                @Override
                public char[] providePIN(PINSpec pinSpec, int retries) throws CancelledException, InterruptedException {
                    return "1234".toCharArray();
                }
            };
            decrypted = signatureCard.decrypt(encrypted, pingui);
        } catch (SignatureCardException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return decrypted;
    }

    public byte[] encrypt(byte[] data, String alias, String algorithm) {
        //Proof if algorithm is supported
        if(algorithm == null)
            return null;
        if(!algorithm.equals("RSAES-PKCS1-v1_5"))
            return null;
        byte[] enc = null;

        Cipher rsacipher = null;
        try {
            rsacipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "IAIK");

            X509Certificate certificate = _certificateMap.get(alias);
            rsacipher.init(Cipher.ENCRYPT_MODE, certificate, SecureRandom.getInstance("SHA1PRNG", "IAIK"));
            enc = rsacipher.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchProviderException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (BadPaddingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidKeyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return enc;
    }


//	@Override
//	public byte[] deriveSharedSecret(PublicKey publicKey) throws SecurityDeviceException {
//		ASN1 publicKeyASN1;
//		byte[] sharedSecret = null;
//		try {
//			publicKeyASN1 = new ASN1(publicKey.getEncoded());
//			ASN1Object pointASN1 = publicKeyASN1.getComponentAt(1);
//			byte[] encodedECKey = (byte[])pointASN1.getValue();
//
//			SignatureCard signatureCard = existingCards.values().toArray(new SignatureCard[0])[0];
//			sharedSecret = signatureCard.deriveSharedSecret(encodedECKey, securityDeviceUnlocker.getPINGUI());
//		} catch (CodingException e) {
//			logger.error(e);
//			throw new APDUCitizenCardException(e);
//		} catch (SignatureCardException e) {
//			logger.error(e);
//			throw new APDUCitizenCardException(e);
//		} catch (InterruptedException e) {
//			logger.error(e);
//			throw new APDUCitizenCardException(e);
//		} catch (IOException e) {
//			logger.error(e);
//			throw new APDUCitizenCardException(e);
//		}
//		return sharedSecret;
//	}

//	@Override
//	public byte[] xmlDecryptRsa(RecipientInfo recipientInfo,IssuerAndSerialNumber ias) throws SecurityDeviceException {
//		try {
//			byte[] encryptedCEK = recipientInfo.getEncryptedKey(ias);
//			SignatureCard signatureCard = existingCards.values().toArray(new SignatureCard[0])[0];
//			byte[] decrypted = signatureCard.decrypt(encryptedCEK, securityDeviceUnlocker.getPINGUI());
//
//
//
//			return decrypted;
//		} catch (CMSException e) {
//			logger.error("Cannot get encrypted cek for IAS: " + ias.toString(),e);
//			throw new APDUCitizenCardException(e);
//		} catch (SignatureCardException e) {
//			logger.error(e);
//			throw new APDUCitizenCardException(e);
//		} catch (InterruptedException e) {
//			logger.error(e);
//			throw new APDUCitizenCardException(e);
//		} catch (IOException e) {
//			logger.error(e);
//			throw new APDUCitizenCardException(e);
//		}
//	}


    public abstract static class BasicSMCCCommand extends BasicCommand {

      protected SmartCardProvider _smartCardProvider;

    }

    public static class SMCCCommandDecrypt extends BasicSMCCCommand {

        public SMCCCommandDecrypt(SmartCardProvider apduCitizenCard) {
            _smartCardProvider = apduCitizenCard;
        }

        @Override
        protected void handleCommand(CMDPacket requestCmdPacket, CMDPacket responseCmdPacket) {
            CMDPayload responsePayload = responseCmdPacket.getPayload();

            byte[] decrypted = null;
            if(requestCmdPacket.getPayload().getLoad() != null){
                try {
                    decrypted = _smartCardProvider.decrypt(Util.fromBase64String(requestCmdPacket.getPayload().getLoad()),requestCmdPacket.getPayload().getKey().getId(),requestCmdPacket.getPayload().getAlgorithm().getName());
    //                decrypted = _iaikjce.decrypt(Util.fromBase64String(decrypt.getPayload().getLoad()),
    //                        decrypt.getPayload().getKey().getId(),
    //                        decrypt.getPayload().getAlgorithm().getName());
                } catch (Base64Exception e) {
                    System.err.println("Error while decode Load!");
                }
                if(decrypted != null){
                    responsePayload.setCode(200);
                    responsePayload.setLoad(Util.toBase64String(decrypted));
                }
                else {
                    responsePayload.setCode(400);
                }
            } else{
                responsePayload.setCode(400);
            }
        }

        @Override
        public CmdType getCommandType() {
            return CmdType.decrypt;
        }



    }

    public static class SMCCCommandEncrypt extends BasicSMCCCommand {

      public SMCCCommandEncrypt(SmartCardProvider smartCardProvider) {
       _smartCardProvider = smartCardProvider;
      }

        @Override
        protected void handleCommand(CMDPacket cmdPacket, CMDPacket responseCmdPacket) {
            CMDPayload responsePayload = responseCmdPacket.getPayload();

            byte[] encrypted = null;
            if (cmdPacket.getPayload().getLoad() != null) {
                System.out.println("Payloadset");
                try {
                    encrypted = _smartCardProvider.encrypt(Util.fromBase64String(cmdPacket.getPayload().getLoad()),
                            cmdPacket.getPayload().getKey().getId(),
                            cmdPacket.getPayload().getAlgorithm().getName());

                } catch (Base64Exception e) {
                    System.err.println("Error while decode Load!");
                }
                if(encrypted != null){
                    responsePayload.setCode(200);
                    responsePayload.setLoad(Util.toBase64String(encrypted));
                }
                else {
                    responsePayload.setCode(400);
                }
            } else {
                responsePayload.setCode(400);
            }
        }

        @Override
        public CmdType getCommandType() {
            return CmdType.encrypt;
        }

    }

    /**
     * Created with IntelliJ IDEA.
     * User: pteufl
     * Date: 5/22/13
     * Time: 7:47 AM
     * To change this template use File | Settings | File Templates.
     */
    public static class SMCCCommandGetCertificate extends BasicSMCCCommand {

        public SMCCCommandGetCertificate(SmartCardProvider apduCitizenCard) {
            _smartCardProvider = apduCitizenCard;
        }

        @Override
        protected void handleCommand(CMDPacket requestCmdPacket, CMDPacket responseCmdPacket) {
            CMDPayload responsePayload = responseCmdPacket.getPayload();
            if(requestCmdPacket.getPayload().getKey().getId() != null) {
                X509Certificate cert = _smartCardProvider.getCertificate(requestCmdPacket.getPayload().getKey());
                //X509Certificate cert = null;
                if(cert != null){
                    responsePayload.setCode(200);
                    try {
                        responsePayload.setLoad(Util.toBase64String(cert.getEncoded()));
                    } catch (CertificateEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                else {
                    responsePayload.setCode(400);
                }
            } else {
                responsePayload.setCode(400);
            }
        }

        @Override
        public CmdType getCommandType() {
            return CmdType.getCertificate;
        }

    }

    public static class SMCCCommandGetKeys extends BasicSMCCCommand {

      public SMCCCommandGetKeys(SmartCardProvider smartCardProvider) {
        _smartCardProvider = smartCardProvider;
      }

        @Override
        protected void handleCommand(CMDPacket cmdPacket, CMDPacket responseCmdPacket) {
            CMDPayload responsePayload = responseCmdPacket.getPayload();
            List<KeyBean> keyInfo = _smartCardProvider.getAvailableKeySlots();
            if(keyInfo != null){
                for(KeyBean key : keyInfo){
                    System.out.println("Key: " + key.toString());

                }
                responsePayload.setCode(200);
            }else{
                responsePayload.setCode(400);
            }
        }

        @Override
        public CmdType getCommandType() {
            return CmdType.discoverKeys;
        }


    }
}
