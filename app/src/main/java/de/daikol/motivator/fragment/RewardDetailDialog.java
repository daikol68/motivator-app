package de.daikol.motivator.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.daikol.motivator.R;
import de.daikol.motivator.model.Competition;
import de.daikol.motivator.model.Competitor;
import de.daikol.motivator.model.Reward;
import de.daikol.motivator.tasks.ConvertBitmapTask;
import de.daikol.motivator.util.Constants;

public class RewardDetailDialog extends DialogFragment {

    private Competition competition;

    private Competitor competitor;

    private Reward reward;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        this.competition = (Competition) getArguments().getSerializable(Constants.SerializeableKeys.COMPETITION);
        this.competitor = (Competitor) getArguments().getSerializable(Constants.SerializeableKeys.COMPETITOR);
        this.reward = (Reward) getArguments().getSerializable(Constants.SerializeableKeys.REWARD);

        final View root = getActivity().getLayoutInflater().inflate(R.layout.reward_detail, null);

        final ImageView picture = root.findViewById(R.id.reward_detail_picture);
        final TextView points = root.findViewById(R.id.reward_detail_points);

        // get the picture
        if (reward.getBitmap() != null) {
            picture.setBackground(new BitmapDrawable(getResources(), reward.getBitmap()));
        } else if (reward.getPicture() != null) {
            new ConvertBitmapTask(getContext(), picture, reward).execute();
        }

        points.setText(String.valueOf(reward.getPoints()));

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true)
                .setTitle(this.reward.getName())
                .setView(root);

        builder.setNeutralButton(R.string.close_reward_detail_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        if (competitor.getPoints() >= reward.getPoints()) {
            builder.setMessage("Lange gedrückt halten um die Belohnung zu kaufen.");
        } else {
            builder.setMessage("Leider haben sie nicht genug Punkte um die Belohnung zu kaufen.");
        }


        // Create the AlertDialog object and return it
        return builder.create();
    }
}
