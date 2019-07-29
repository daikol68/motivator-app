package de.daikol.acclaim.adapter;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.daikol.acclaim.R;
import de.daikol.acclaim.application.Challenger;
import de.daikol.acclaim.fragment.MessageCreationDialog;
import de.daikol.acclaim.model.Message;
import de.daikol.acclaim.model.user.User;
import de.daikol.acclaim.tasks.ConvertBitmapTask;
import de.daikol.acclaim.util.Constants;

public class MessageAdapter extends AbstractAdapter<Message, MessageAdapter.MessageViewHolder> {

    private final static String LOG_TAG = "MessageAdapter";

    public MessageAdapter(List<Message> items, Challenger challenger, Context context, FragmentManager fragmentManager) {
        super(items, challenger, context, fragmentManager);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public final View foreground;
        public final View background;
        public final View backgroundLeft;
        public final View backgroundRight;
        public final TextView messageId;
        public final TextView messageContent;
        public final ImageView imageSenderPicture;
        public final TextView textSenderName;

        public MessageViewHolder(View view) {
            super(view);
            foreground = view.findViewById(R.id.message_list_foreground);
            background = view.findViewById(R.id.message_list_background);
            backgroundLeft = view.findViewById(R.id.message_list_background_left);
            backgroundRight =         view.findViewById(R.id.message_list_background_right);
            messageId = view.findViewById(R.id.message_list_id);
            messageContent = view.findViewById(R.id.message_list_body);
            imageSenderPicture = view.findViewById(R.id.message_list_sender_picture);
            textSenderName = view.findViewById(R.id.message_list_sender_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + messageContent.getText() + "'";
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_content, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, final int position) {

        final Message message = getItem(holder.getAdapterPosition());

        if (message != null) {
            holder.messageContent.setText(message.getText());
        } else {
            holder.messageContent.setText("Fehler beim Laden der Nachricht!");
            return;
        }

        holder.foreground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new MessageCreationDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.SerializeableKeys.MESSAGE, message);
                dialog.setArguments(bundle);
                getFragmentManager().beginTransaction().add(dialog, "message_creation").commit();
            }
        });
        holder.messageId.setText(String.valueOf(message.getId()));

        final User sender = message.getSender();
        if (sender != null) {
            holder.textSenderName.setText(sender.getName());
            new ConvertBitmapTask(getContext(), holder.imageSenderPicture, sender).execute();
        }
    }
}
