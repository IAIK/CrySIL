package org.crysil.instance.u2f;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.crysil.commons.Module;
import org.crysil.communications.websocket.interfaces.ActionPerformedCallback;
import org.crysil.communications.websocket.interfaces.ActorChooser;

import java.util.Map;

/**
 * Handles management of the actor to choose for a request.
 */
public class ActorChooserFragment extends Fragment implements ActorChooser {

    private static final String TAG = ActorChooserFragment.class.getSimpleName();

    public ActorChooserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_actor_chooser, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void chooseActor(final Map<String, Module> map, final ActionPerformedCallback callback) {
        if (map.size() == 0)
            callback.actionPerformed(null);
        final int[] selectedModule = {0};
        final CharSequence[] singleChoiceItems = new CharSequence[map.size()];
        for (int i = 0; i < map.size(); ++i) {
            singleChoiceItems[i] = map.keySet().toArray()[i].toString();
        }
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(ActorChooserFragment.this.getActivity()).setTitle(
                        getActivity().getString(R.string.crysil_actor_choose_auth)).setSingleChoiceItems(
                        singleChoiceItems, selectedModule[0], new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedModule[0] = which;
                            }
                        }).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.actionPerformed(
                                selectedModule[0] != -1 ? (Module) map.values().toArray()[selectedModule[0]] : null);
                    }
                }).show();
            }
        });
    }
}
