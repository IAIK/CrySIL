package org.crysil.instance.u2f;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

/**
 * Shows the dialog for the user to trust a certificate from a webservice
 */
public class WebsocketCertificateDialog extends DialogFragment {

    public static final String CERTIFICATE = "certificate";

    public interface WebsocketCertificateDialogListener {
        void onDialogNegativeClick(DialogFragment dialog);

        void onDialogOnceClick(DialogFragment dialog);

        void onDialogAlwaysClick(DialogFragment dialog);
    }

    private WebsocketCertificateDialogListener listener;

    public static WebsocketCertificateDialog newInstance(WebsocketCertificateDialogListener listener) {
        WebsocketCertificateDialog dialog = new WebsocketCertificateDialog();
        dialog.listener = listener;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (listener != null)
            listener.onDialogNegativeClick(WebsocketCertificateDialog.this);
        super.onDismiss(dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String certificate = "ERROR";
        if (getArguments().containsKey(CERTIFICATE))
            certificate = getArguments().getString(CERTIFICATE);
        View view = inflater.inflate(R.layout.dialog_websocket_certificate, container, false);
        ((EditText) view.findViewById(R.id.etCertificate)).setKeyListener(null);
        ((EditText) view.findViewById(R.id.etCertificate)).setText(certificate);
        view.findViewById(R.id.btAlways).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onDialogAlwaysClick(WebsocketCertificateDialog.this);
                dismiss();
            }
        });
        view.findViewById(R.id.btOnce).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onDialogOnceClick(WebsocketCertificateDialog.this);
                dismiss();
            }
        });
        view.findViewById(R.id.btNegative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onDialogNegativeClick(WebsocketCertificateDialog.this);
                dismiss();
            }
        });
        return view;
    }
}
