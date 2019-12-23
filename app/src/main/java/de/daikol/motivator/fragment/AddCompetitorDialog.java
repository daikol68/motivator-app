package de.daikol.motivator.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import de.daikol.motivator.R;
import de.daikol.motivator.model.Competition;
import de.daikol.motivator.model.user.User;
import de.daikol.motivator.util.Constants;

public class AddCompetitorDialog extends DialogFragment {

    private Competition competition;

    private User competitor;

    AddCompetitorDialogListener listener;

    public AddCompetitorDialog() {
        // nothing to do
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        this.competition = (Competition) getArguments().getSerializable(Constants.SerializeableKeys.COMPETITION);
        this.competitor = (User) getArguments().getSerializable(Constants.SerializeableKeys.COMPETITOR);


        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.add_competitor_question)
                .setPositiveButton(R.string.close_competition_option_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onDialogPositiveClick(competitor);
                        }
                    }
                })
                .setNegativeButton(R.string.close_competition_option_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onDialogNegativeClick(competitor);
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
        listener = (AddCompetitorDialogListener) activity;
    }

    public interface AddCompetitorDialogListener {
        void onDialogPositiveClick(User user);
        void onDialogNegativeClick(User user);
    }


}
