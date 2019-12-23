package de.daikol.motivator.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import de.daikol.motivator.R;
import de.daikol.motivator.listener.OnLeaveCompetition;
import de.daikol.motivator.model.Competition;
import de.daikol.motivator.model.Message;
import de.daikol.motivator.util.Constants;

public class LeaveCompetitionDialog extends DialogFragment {

    private Competition competition;

    private Message message;

    private OnLeaveCompetition listener;

    public LeaveCompetitionDialog() {
        // nothing to do
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.competition = (Competition) getArguments().getSerializable(Constants.SerializeableKeys.COMPETITION);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.leave_competition_question)
                .setPositiveButton(R.string.leave_competition_option_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onLeaveCompetitionPositive(competition);
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.leave_competition_option_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onLeaveCompetitionNegative(competition);
                        }
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        listener = (OnLeaveCompetition) activity;
    }
}
