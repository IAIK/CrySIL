package org.crysil.instance.u2f.nfc;

import android.nfc.tech.IsoDep;
import android.util.Log;

import org.crysil.actor.u2f.U2FDeviceHandler;
import org.crysil.actor.u2f.nfc.APDUError;
import org.crysil.actor.u2f.nfc.NfcU2FDeviceStrategy;
import org.crysil.actor.u2f.nfc.SmartcardHsmNfcU2FDeviceStrategy;
import org.crysil.actor.u2f.nfc.YubikeyNfcU2FDeviceStrategy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.crysil.instance.u2f.utils.CertificateUtils;

/**
 * Handles communication with an NFC tag. Tries to detect which tag it is to select the right implementation (strategy)
 *
 * @see NfcU2FDeviceStrategy
 */
public class NfcU2FDeviceHandler implements U2FDeviceHandler {

    private static final String TAG = NfcU2FDeviceHandler.class.getSimpleName();

    private static final byte[] GET_RESPONSE_COMMAND = {0x00, (byte) 0xc0, 0x00, 0x00, (byte) 0xff};

    private final NfcU2FDeviceStrategy strategy;
    private final IsoDep tag;

    public NfcU2FDeviceHandler(IsoDep tag) throws IOException, APDUError {
        NfcU2FDeviceStrategy tempStrategy = null;
        this.tag = tag;
        tag.setTimeout(5000);
        tag.connect();
        Map<byte[], NfcU2FDeviceStrategy> map = new HashMap<>();
        map.put(YubikeyNfcU2FDeviceStrategy.SELECT_U2F, new YubikeyNfcU2FDeviceStrategy());
        map.put(YubikeyNfcU2FDeviceStrategy.SELECT_YUBICO, new YubikeyNfcU2FDeviceStrategy());
        map.put(SmartcardHsmNfcU2FDeviceStrategy.SELECT, new SmartcardHsmNfcU2FDeviceStrategy());
        for (Map.Entry<byte[], NfcU2FDeviceStrategy> entry : map.entrySet()) {
            try {
                send(entry.getKey());
                tempStrategy = entry.getValue();
                break;
            } catch (APDUError e) {
                // continue with next select statement
            }
        }
        if (tempStrategy == null) {
            throw new IOException("No matching SELECT sequence found for tag");
        }
        strategy = tempStrategy;
    }

    public boolean isConnected() {
        return tag != null && tag.isConnected();
    }

    public String getVersion() throws Exception {
        return strategy.getVersion(this);
    }

    public byte[] registerPlain(byte[] clientParam, byte[] appParam) throws Exception {
        return strategy.registerPlain(clientParam, appParam, this);
    }

    public byte[] signPlain(byte[] keyHandle, byte[] clientParam, byte[] appParam, byte[] counter) throws Exception {
        return strategy.signPlain(keyHandle, clientParam, appParam, counter, this);
    }

    @Override
    public byte[] send(byte[] apdu) throws IOException, APDUError {
        byte[] cmd = apdu;
        int status = 0x6100;
        byte[] data = new byte[0];
        while ((status & 0xff00) == 0x6100) {
            byte[] resp = tag.transceive(cmd);
            Log.d(TAG, String.format("REQ:  %s", CertificateUtils.toHexString(cmd, " ")));
            Log.d(TAG, String.format("RESP: %s", CertificateUtils.toHexString(resp, " ")));
            status = ((0xff & resp[resp.length - 2]) << 8) | (0xff & resp[resp.length - 1]);
            data = concat(data, resp, resp.length - 2);
            cmd = GET_RESPONSE_COMMAND;
        }
        if (status != 0x9000) {
            throw new APDUError(status);
        }
        return data;
    }

    private static byte[] concat(byte[] a, byte[] b, int length) {
        byte[] res = new byte[a.length + length];
        System.arraycopy(a, 0, res, 0, a.length);
        System.arraycopy(b, 0, res, a.length, length);
        return res;
    }

}
