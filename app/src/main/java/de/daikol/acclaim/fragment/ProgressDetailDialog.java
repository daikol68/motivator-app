package de.daikol.acclaim.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import de.daikol.acclaim.R;
import de.daikol.acclaim.model.ProgressStepData;
import de.daikol.acclaim.model.user.User;
import de.daikol.acclaim.tasks.ConvertBitmapTask;
import de.daikol.acclaim.util.Constants;

public class ProgressDetailDialog extends DialogFragment {

    private ProgressDetailDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        final User user = (User) getArguments().getSerializable(Constants.SerializeableKeys.COMPETITOR);
        final ProgressStepData step = (ProgressStepData) getArguments().getSerializable(Constants.SerializeableKeys.PROGRESS);
        final boolean acceptable = getArguments().getBoolean(Constants.SerializeableKeys.PROGRESS_ACCEPTABLE);

        this.listener = (ProgressDetailDialogListener) getArguments().getSerializable(Constants.SerializeableKeys.PROGRESS_LISTENER);

        final View root = getActivity().getLayoutInflater().inflate(R.layout.progress_detail, null);

        final ImageView competitorPicture = root.findViewById(R.id.progress_detail_competitor_picture);
        final ImageView stepPicture = root.findViewById(R.id.progress_detail_achievement_picture);
        final TextView status = root.findViewById(R.id.progress_detail_date);

        // get the pictures
        if (user.getBitmap() != null) {
            competitorPicture.setBackground(new BitmapDrawable(getResources(), user.getBitmap()));
        } else if (user.getPicture() != null) {
            new ConvertBitmapTask(getContext(), competitorPicture, user).execute();
        }

        if (step.getBitmap() != null) {
            stepPicture.setBackground(new BitmapDrawable(getResources(), step.getBitmap()));
        } else if (step.getPicture() != null) {
            new ConvertBitmapTask(getContext(), stepPicture, step).execute();
        }

        status.setText("Gemeldet " + format.format(step.getCreationDate()) + " von " + user.getName());

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true)
                .setTitle(step.getAchievementName())
                .setMessage("Fortschritt von " + user.getName())
                .setView(root);

        if (acceptable) {
            builder.setPositiveButton(R.string.close_reward_detail_accept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    listener.onDialogPositiveClick(step);
                    dialogInterface.dismiss();
                }
            });

            builder.setNegativeButton(R.string.close_reward_detail_decline, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    listener.onDialogNegativeClick(step);
                    dialogInterface.dismiss();
                }
            });

        } else {
            builder.setNeutralButton(R.string.close_reward_detail_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }

        // Create the AlertDialog object and return it
        return builder.create();
    }

    public interface ProgressDetailDialogListener {
        void onDialogPositiveClick(ProgressStepData data);
        void onDialogNegativeClick(ProgressStepData data);
    }


}
