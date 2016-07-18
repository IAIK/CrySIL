package org.crysil.instance.u2f;

import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.crysil.actor.u2f.U2FActivityHandler;
import org.crysil.actor.u2f.U2FDeviceHandler;
import org.crysil.actor.u2f.nfc.APDUError;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.crysil.instance.u2f.nfc.NfcU2FDeviceHandler;

/**
 * Handles management of NFC tokens: Display messages to swipe the token
 */
public class NFCHandlerFragment extends Fragment implements U2FActivityHandler {

    private static final String TAG = NFCHandlerFragment.class.getSimpleName();

    private NfcAdapter nfcAdapter;
    private PendingIntent nfcPendingIntent;
    private IntentFilter[] nfcIntentFilter;
    private String[][] nfcTechLists;
    private NfcU2FDeviceHandler nfcHandler = null;
    private Lock nfcLock = new ReentrantLock();
    private Condition nfcCondition = nfcLock.newCondition();
    private TextView tvMessage;

    public NFCHandlerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
        nfcPendingIntent = PendingIntent.getActivity(getActivity(), 0,
                getActivity().getIntent().addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        nfcIntentFilter = new IntentFilter[]{new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)};
        nfcTechLists = new String[][]{new String[]{IsoDep.class.getName()}};
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nfchandler, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvMessage = (TextView) view.findViewById(R.id.tvNfcMessage);
        tvMessage.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    public void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(getActivity(), nfcPendingIntent, nfcIntentFilter, nfcTechLists);
        showMessage(!nfcAdapter.isEnabled() ? getString(R.string.nfc_disabled) : "", false);
    }

    @Override
    public void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(getActivity());
    }

    public void onNFCPresent(IsoDep tag) {
        try {
            nfcHandler = new NfcU2FDeviceHandler(tag);
            nfcLock.lock();
            nfcCondition.signalAll();
            nfcLock.unlock();
        } catch (IOException e) {
            Log.e(TAG, "IOException on NFC", e);
        } catch (APDUError apduError) {
            Log.e(TAG, "APDUError on NFC", apduError);
        }
    }

    @Override
    public U2FDeviceHandler activateNFC() {
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(getActivity());
        if (adapter == null) {
            showMessage(getString(R.string.error_nfc_null), true);
        } else if (adapter.isEnabled()) {
            showMessage(getString(R.string.nfc_swipe), true);
            if (nfcHandler != null && nfcHandler.isConnected()) {
                return nfcHandler;
            }
            try {
                nfcLock.lock();
                nfcHandler = null;
                nfcAdapter.enableForegroundDispatch(getActivity(), nfcPendingIntent, nfcIntentFilter, nfcTechLists);
                nfcCondition.await(60, TimeUnit.SECONDS);
                nfcLock.unlock();
                showMessage("", false);
                return nfcHandler;
            } catch (InterruptedException e) {
                Log.e(TAG, "Interrupted on waiting for NFC", e);
            }
        } else {
            showMessage(getString(R.string.nfc_disabled), true);
        }
        return null;
    }

    private void showMessage(final String text, final boolean showToast) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvMessage.setText(text);
                if (showToast) {
                    Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
