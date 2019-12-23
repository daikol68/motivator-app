package de.daikol.motivator.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;

import de.daikol.motivator.R;
import de.daikol.motivator.application.Motivator;
import de.daikol.motivator.model.Achievement;
import de.daikol.motivator.model.AchievementProgressStep;
import de.daikol.motivator.model.ApplyStatus;
import de.daikol.motivator.model.Competition;
import de.daikol.motivator.model.Competitor;
import de.daikol.motivator.model.user.User;
import de.daikol.motivator.tasks.ConvertBitmapTask;
import de.daikol.motivator.util.Constants;

public class AchievementDetailDialog extends DialogFragment {

    private Motivator motivator;

    private Competition competition;

    private Achievement achievement;

    private boolean locked;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        this.motivator = (Motivator) getActivity().getApplication();
        this.competition = (Competition) getArguments().getSerializable(Constants.SerializeableKeys.COMPETITION);
        this.achievement = (Achievement) getArguments().getSerializable(Constants.SerializeableKeys.ACHIEVEMENT);
        this.locked = getArguments().getBoolean(Constants.SerializeableKeys.ACHIEVEMENT_LOCKED);

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        final View root = layoutInflater.inflate(R.layout.achievement_detail, null);

        final ImageView picture = root.findViewById(R.id.achievement_detail_picture);
        final TextView points = root.findViewById(R.id.achievement_detail_points);
        final TextView repeat = root.findViewById(R.id.achievement_detail_repeat);

        final User meUser = motivator.getUser();

        // get the picture
        if (achievement.getBitmap() != null) {
            picture.setBackground(new BitmapDrawable(getResources(), achievement.getBitmap()));
        } else if (achievement.getPicture() != null) {
            new ConvertBitmapTask(getContext(), picture, achievement).execute();
        }

        points.setText(String.valueOf(achievement.getPoints()));
        repeat.setText(String.valueOf(achievement.getProgressStepsFinish()));

        if (competition != null && competition.getCompetitors() != null) {

            final HashMap<Long, Integer> competitorCounts = new HashMap<>();

            if (achievement.getProgressSteps() != null) {
                for (AchievementProgressStep step : achievement.getProgressSteps()) {
                    if (step.getApplied() == ApplyStatus.CONFIRMED) {
                        if (competitorCounts.containsKey(step.getUserId())) {
                            competitorCounts.put(step.getUserId(), competitorCounts.get(step.getUserId()));
                        } else {
                            competitorCounts.put(step.getUserId(), 1);
                        }
                    }
                }
            }

            final LinearLayout competitors = root.findViewById(R.id.achievement_detail_competitors);
            for (Competitor competitor : competition.getCompetitors()) {
                final LinearLayout competitorDetail = (LinearLayout) layoutInflater.inflate(R.layout.achievement_detail_competitor, competitors, false);
                final ImageView competitorPicture = competitorDetail.findViewById(R.id.achievement_detail_competitor_picture);
                final TextView competitorName = competitorDetail.findViewById(R.id.achievement_detail_competitor_name);
                final ProgressBar competitorProgress = competitorDetail.findViewById(R.id.achievement_detail_competitor_progress);

                int count = 0;
                if (competitorCounts.containsKey(competitor.getUser().getId())) {
                    count = competitorCounts.get(competitor.getUser().getId());
                }

                if (competitor.getUser().getBitmap() != null) {
                    competitorPicture.setBackground(new BitmapDrawable(getResources(), competitor.getUser().getBitmap()));
                } else if (competitor.getUser().getPicture() != null) {
                    new ConvertBitmapTask(getContext(), competitorPicture, competitor.getUser()).execute();
                }
                competitorName.setText(competitor.getUser().getName());
                competitorProgress.setProgress(count);

                competitors.addView(competitorDetail);
            }
        }

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setCancelable(true)
                .setTitle(this.achievement.getName())
                .setView(root)
                .setNeutralButton(R.string.close_achievement_detail_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        if (this.locked) {
            builder.setMessage("Leider muss der Erfolg erst bestätigt werden ehe sie ihn erneut melden können.");
        } else {
            builder.setMessage("Lange gedrückt halten um den Erfolg zu melden.");
        }


        // Create the AlertDialog object and return it
        return builder.create();
    }
}
