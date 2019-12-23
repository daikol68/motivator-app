package de.daikol.motivator.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import de.daikol.motivator.R;
import de.daikol.motivator.model.Message;
import de.daikol.motivator.util.Constants;

public class MessageCreationDialog extends DialogFragment {

    private Message message;

    CreateMessageDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        this.message = (Message) getArguments().getSerializable(Constants.SerializeableKeys.MESSAGE);
        final LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        final View root = layoutInflater.inflate(R.layout.message_creation, null);
        final TextView name = root.findViewById(R.id.message_create_receiver_name);
        final ImageView picture = root.findViewById(R.id.message_create_receiver_picture);
        final EditText text = root.findViewById(R.id.message_create_text);

        name.setText(message.getSender().getName());
        if (message.getSender().getBitmap() != null) {
            picture.setBackground(new BitmapDrawable(getResources(), message.getSender().getBitmap()));
        }

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true)
                .setView(root);

        builder.setPositiveButton(R.string.send_message_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Message reply = new Message();
                reply.setRead(false);
                reply.setType(Message.MessageType.CHAT);
                reply.setSender(message.getReceiver());
                reply.setReceiver(message.getSender());
                reply.setText(text.getText().toString());
                dialogInterface.dismiss();
                listener.onCreateMessageClick(reply);
            }
        });

        builder.setNegativeButton(R.string.send_message_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        listener = (MessageCreationDialog.CreateMessageDialogListener) activity;
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface CreateMessageDialogListener {
        void onCreateMessageClick(Message message);
    }


}
