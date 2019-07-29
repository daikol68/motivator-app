package de.daikol.acclaim.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import de.daikol.acclaim.R;
import de.daikol.acclaim.model.Competition;
import de.daikol.acclaim.model.Message;
import de.daikol.acclaim.util.Constants;

public class DeclineCompetitionDialog extends DialogFragment {

    private Competition competition;

    private Message message;

    DeclineCompetitionDialogListener listener;

    public DeclineCompetitionDialog() {
        // nothing to do
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.competition = (Competition) getArguments().getSerializable(Constants.SerializeableKeys.COMPETITION);
        this.message = (Message) getArguments().getSerializable(Constants.SerializeableKeys.MESSAGE);
        this.listener = (DeclineCompetitionDialogListener) getArguments().getSerializable(Constants.SerializeableKeys.COMPETITION_LISTENER);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.decline_competition_question)
                .setPositiveButton(R.string.decline_competition_option_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onDeclineCompetitionPositive(competition, message);
                        }
                    }
                })
                .setNegativeButton(R.string.decline_competition_option_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onDeclineCompetitionNegative(competition, message);
                        }
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        listener = (DeclineCompetitionDialogListener) activity;
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface DeclineCompetitionDialogListener {
        void onDeclineCompetitionPositive(Competition competition, Message message);
        void onDeclineCompetitionNegative(Competition competition, Message message);
    }

}
