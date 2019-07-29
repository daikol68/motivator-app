package de.daikol.acclaim.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import de.daikol.acclaim.R;
import de.daikol.acclaim.model.Message;
import de.daikol.acclaim.util.Constants;

/**
 * Created by daikol on 16.12.2016.
 */
public class DeclineProgressDialog extends DialogFragment {

    /**
     * The competition to be declined.
     */
    private Message message;

    /**
     * The comment used to decline the progress.
     */
    private String comment;

    /**
     * The position.
     */
    private int position;

    /**
     * The listener to tell that a button has been clicked.
     */
    DeclineProgressDialogListener listener;

    public DeclineProgressDialog() {
        // nothing to do
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.message = (Message) getArguments().getSerializable(Constants.SerializeableKeys.MESSAGE);
        this.position = getArguments().getInt(Constants.SerializeableKeys.POSITION);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set up the input
        final EditText input = new EditText(getActivity());
        input.setHint(getResources().getString(R.string.decline_progress_hint_comment));
        // Specify the type of input expected; this, for example, sets the input as a normal string
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);
        builder.setMessage(R.string.decline_progress_question)
                .setPositiveButton(R.string.decline_progress_option_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        comment = input.getText().toString();
                        if (listener != null) {
                            listener.onDeclineProgressPositive(message, comment, position);
                        }
                    }
                })
                .setNegativeButton(R.string.decline_progress_option_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onDeclineProgressNegative(message);
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
        listener = (DeclineProgressDialogListener) activity;
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface DeclineProgressDialogListener {
        void onDeclineProgressPositive(Message message, String comment, int position);
        void onDeclineProgressNegative(Message message);
    }

}
