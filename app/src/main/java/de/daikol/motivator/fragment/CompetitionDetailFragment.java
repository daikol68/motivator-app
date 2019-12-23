package de.daikol.motivator.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import de.daikol.motivator.R;
import de.daikol.motivator.activity.CompetitionDetailActivity;
import de.daikol.motivator.activity.CompetitionEditActivity;
import de.daikol.motivator.application.Motivator;
import de.daikol.motivator.model.Achievement;
import de.daikol.motivator.model.AchievementProgressStep;
import de.daikol.motivator.model.AchievementStatus;
import de.daikol.motivator.model.ApplyStatus;
import de.daikol.motivator.model.Competition;
import de.daikol.motivator.model.CompetitionStatus;
import de.daikol.motivator.model.Competitor;
import de.daikol.motivator.model.Message;
import de.daikol.motivator.model.ProgressStepData;
import de.daikol.motivator.model.Reward;
import de.daikol.motivator.model.RewardStatus;
import de.daikol.motivator.model.user.User;
import de.daikol.motivator.tasks.BuyRewardTask;
import de.daikol.motivator.tasks.ConfirmProgressTask;
import de.daikol.motivator.tasks.ConvertBitmapTask;
import de.daikol.motivator.tasks.CreateProgressStepTask;
import de.daikol.motivator.tasks.DeclineProgressTask;
import de.daikol.motivator.tasks.SendMessageTask;
import de.daikol.motivator.util.Constants;

/**
 * A fragment representing a single Competition detail screen.
 * This fragment is either contained in a {@link }
 * in two-pane mode (on tablets) or a {@link CompetitionDetailActivity}
 * on handsets.
 */
public class CompetitionDetailFragment extends Fragment implements Serializable, ProgressDetailDialog.ProgressDetailDialogListener, MessageCreationSupportDialog.CreateMessageDialogListener {

    /**
     * The tag used for logging.
     */
    private static final String LOG_TAG = "CompetitionDetailFrag";

    private static final int MAX_ACHIEVEMENTS = 4;

    private static final int MAX_REWARDS = 4;

    private static final int MAX_PROGRESS = 4;

    private Motivator motivator;

    private Competition competition;

    private View root;

    LinearLayout competitorCategories;

    LinearLayout achievementCategories;

    LinearLayout rewardCategories;

    LinearLayout progressCategories;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CompetitionDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // create motivator
        motivator = (Motivator) getActivity().getApplication();

        // get the root view
        root = inflater.inflate(R.layout.competition_detail, container, false);

        // get the competition
        competition = (Competition) getArguments().getSerializable(Constants.SerializeableKeys.COMPETITION);

        if (competition != null) {
            ((CompetitionDetailActivity) getActivity()).setActionBarTitle(competition.getName());
            createCompetitionLayout(inflater, container, this.root);
        }

        return this.root;
    }

    private void createCompetitionLayout(final LayoutInflater inflater, final ViewGroup container, final View rootView) {

        // find additional views
        final FloatingActionButton copyButton = rootView.findViewById(R.id.competition_detail_copy);
        final FloatingActionButton updateButton = rootView.findViewById(R.id.competition_detail_update);
        final ImageView pictureView = rootView.findViewById(R.id.competition_detail_picture);
        final TextView nameView = rootView.findViewById(R.id.competition_detail_name);

        competitorCategories = rootView.findViewById(R.id.competition_detail_competitors);
        achievementCategories = rootView.findViewById(R.id.competition_detail_achievements);
        rewardCategories = rootView.findViewById(R.id.competition_detail_rewards);
        progressCategories = rootView.findViewById(R.id.competition_detail_progress);

        // Show the survey content.
        if (competition != null) {

            // get the reporter
            final User me = motivator.getUser();
            final Competitor meCompetitor = competition.findCompetitorByUserId(me.getId());
            final ArrayList<ProgressStepData> progress = new ArrayList<>();

            nameView.setText(competition.getName());
            new ConvertBitmapTask(getContext(), pictureView, competition).execute();

            // set on click listener for copy function
            copyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, CompetitionEditActivity.class);
                    intent.putExtra(Constants.SerializeableKeys.COMPETITION, competition.clone());
                    context.startActivity(intent);
                }
            });

            // set on click listener for copy function
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, CompetitionEditActivity.class);
                    intent.putExtra(Constants.SerializeableKeys.COMPETITION, competition);
                    context.startActivity(intent);
                }
            });

            if (competition.getCompetitors() != null) {
                for (final Competitor other : competition.getCompetitors()) {
                    LinearLayout row = (LinearLayout) inflater.inflate(R.layout.competitor_row, container, false);

                    ImageView competitorPicture = row.findViewById(R.id.competitor_picture);
                    TextView competitorName = row.findViewById(R.id.competitor_name);

                    final TextView balanceView = row.findViewById(R.id.competitor_balance);

                    if (other.getUser().getId() == me.getId()) {
                        row.setVisibility(View.GONE);
                        continue;
                    } else {
                        row.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Fragment dialog = new MessageCreationSupportDialog();
                                Bundle bundle = new Bundle();
                                final Message message = new Message();
                                message.setType(Message.MessageType.CHAT);
                                message.setSender(other.getUser());
                                message.setReceiver(me);
                                bundle.putSerializable(Constants.SerializeableKeys.MESSAGE, message);
                                dialog.setArguments(bundle);
                                android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                                fragmentManager.beginTransaction().add(dialog, "message_creation").commit();
                            }
                        });
                    }

                    competitorName.setText(other.getUser().getName());
                    new ConvertBitmapTask(getContext(), competitorPicture, other.getUser()).execute();
                    balanceView.setText(String.valueOf(other.getPoints()));

                    FloatingActionButton delete = row.findViewById(R.id.competitor_delete);
                    delete.setVisibility(View.GONE);

                    competitorCategories.addView(row);
                }
            }

            // for each achievement add a row to the table
            if (competition.getAchievements() != null) {

                int count = 0;
                boolean first = true;
                LinearLayout row = null;

                for (final Achievement achievement : competition.getAchievements()) {

                    // skip closed achievements
                    if (achievement.getStatus() == AchievementStatus.CLOSED) {
                        continue;
                    }

                    count++;

                    // create row
                    if (first) {
                        row = (LinearLayout) inflater.inflate(R.layout.achievement_row, container, false);
                        first = false;
                        achievementCategories.addView(row);
                    }

                    LinearLayout entry = (LinearLayout) inflater.inflate(R.layout.achievement_entry, row, false);

                    // create nameView
                    final ImageView achievementPictureView = entry.findViewById(R.id.achievement_entry_picture);

                    // get the reporter data
                    final ProgressBar progressBar = entry.findViewById(R.id.achievement_entry_me_progress);

                    int progressCount = 0;
                    boolean tmpLocked = false;
                    if (achievement.getProgressSteps() != null) {
                        for (AchievementProgressStep step : achievement.getProgressSteps()) {
                            if (step.getUserId() == me.getId() && step.getApplied() == ApplyStatus.CONFIRMED) {
                                progressCount++;
                            }
                            if (step.getUserId() == me.getId() && step.getApplied() == ApplyStatus.OPEN) {
                                tmpLocked = true;
                            }
                        }
                    }
                    final boolean locked = tmpLocked;

                    progressBar.setMax(achievement.getProgressStepsFinish());
                    progressBar.setProgress(progressCount);
                    progressBar.invalidate();

                    achievementPictureView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Fragment dialog = new AchievementDetailDialog();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Constants.SerializeableKeys.COMPETITION, competition);
                            bundle.putSerializable(Constants.SerializeableKeys.ACHIEVEMENT, achievement);
                            bundle.putBoolean(Constants.SerializeableKeys.ACHIEVEMENT_LOCKED, locked);
                            dialog.setArguments(bundle);
                            android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().add(dialog, "achievement_detail").commit();
                        }
                    });

                    if (!locked) {

                        new ConvertBitmapTask(getContext(), achievementPictureView, achievement).execute();
                        achievementPictureView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                AchievementProgressStep step = new AchievementProgressStep();
                                step.setAchievementId(achievement.getId());
                                step.setApplied(ApplyStatus.OPEN);
                                step.setUserId(me.getId());
                                new CreateProgressStepTask(achievement.getId(), step, motivator).execute();
                                Snackbar.make(v, "Der Fortschritt wurde gemeldet und muss erst bestätigt werden ehe er gezählt wird.", Snackbar.LENGTH_LONG).show();

                                final ProgressStepData progressStep = new ProgressStepData();
                                progressStep.setCreationDate(new Date());
                                progressStep.setApplied(ApplyStatus.OPEN);
                                progressStep.setUserid(me.getId());
                                progressStep.setCompetition(competition.getId());
                                progressStep.setAchievementName(achievement.getName());
                                progressStep.setPicture(achievement.getPicture());
                                progressStep.setBitmap(achievement.getBitmap());

                                // create row
                                LinearLayout row = (LinearLayout) progressCategories.getChildAt(progressCategories.getChildCount() - 1);

                                if (row == null || row.getChildCount() % MAX_PROGRESS == 0) {
                                    row = (LinearLayout) inflater.inflate(R.layout.reward_row, container, false);
                                    progressCategories.addView(row);
                                }

                                final LinearLayout entry = (LinearLayout) inflater.inflate(R.layout.progress_entry, row, false);
                                final ImageView progressPicture = entry.findViewById(R.id.progress_detail_achievement_picture);
                                progressPicture.setBackground(new BitmapDrawable(getResources(), progressStep.getBitmap()));

                                progressPicture.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Fragment dialog = new ProgressDetailDialog();
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable(Constants.SerializeableKeys.PROGRESS, progressStep);
                                        bundle.putSerializable(Constants.SerializeableKeys.COMPETITOR, me);
                                        bundle.putBoolean(Constants.SerializeableKeys.PROGRESS_ACCEPTABLE, false);
                                        bundle.putSerializable(Constants.SerializeableKeys.PROGRESS_LISTENER, CompetitionDetailFragment.this);
                                        dialog.setArguments(bundle);
                                        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                                        fragmentManager.beginTransaction().add(dialog, "progress_detail").commit();
                                    }
                                });

                                row.addView(entry);
                                achievementPictureView.setBackground(getResources().getDrawable(R.drawable.ic_unbestaetigt));
                                achievementPictureView.setOnLongClickListener(null);
                                return true;
                            }
                        });
                    } else {
                        achievementPictureView.setBackground(getResources().getDrawable(R.drawable.ic_unbestaetigt));
                    }

                    row.addView(entry);
                    if (count % MAX_ACHIEVEMENTS == 0) {
                        first = true;
                        row = null;
                    }

                    for (AchievementProgressStep progressStep : achievement.getProgressSteps()) {
                        if (progressStep.getApplied() == ApplyStatus.OPEN) {
                            ProgressStepData stepData = new ProgressStepData();
                            stepData.setId(progressStep.getId());
                            stepData.setUserid(progressStep.getUserId());
                            stepData.setCompetition(competition.getId());
                            stepData.setCreationDate(progressStep.getCreationDate());
                            stepData.setApplied(progressStep.getApplied());
                            stepData.setPicture(achievement.getPicture());
                            stepData.setBitmap(achievement.getBitmap());
                            progress.add(stepData);
                        }
                    }
                }
            }

            // for each achievement add a row to the table
            if (competition.getRewards() != null) {

                int count = 0;
                boolean first = true;
                LinearLayout row = null;

                for (final Reward reward : competition.getRewards()) {

                    // skip closed rewards
                    if (reward.getStatus() == RewardStatus.CLOSED) {
                        continue;
                    }

                    count++;

                    // create row
                    if (first) {
                        row = (LinearLayout) inflater.inflate(R.layout.reward_row, container, false);
                        first = false;
                        rewardCategories.addView(row);
                    }

                    final LinearLayout entry = (LinearLayout) inflater.inflate(R.layout.reward_entry, row, false);

                    // create nameView
                    final ImageView rewardPictureView = entry.findViewById(R.id.reward_detail_picture);
                    // get the picture
                    if (reward.getBitmap() != null) {
                        rewardPictureView.setBackground(new BitmapDrawable(getResources(), reward.getBitmap()));
                    } else if (reward.getPicture() != null) {
                        new ConvertBitmapTask(getContext(), rewardPictureView, reward).execute();
                    }

                    final boolean isBuyable = meCompetitor.getStatus() == CompetitionStatus.CONFIRMED && meCompetitor.getPoints() >= reward.getPoints();

                    rewardPictureView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Fragment dialog = new RewardDetailDialog();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Constants.SerializeableKeys.COMPETITION, competition);
                            bundle.putSerializable(Constants.SerializeableKeys.COMPETITOR, meCompetitor);
                            bundle.putSerializable(Constants.SerializeableKeys.REWARD, reward);
                            bundle.putBoolean(Constants.SerializeableKeys.REWARD_BUYABLE, isBuyable);
                            dialog.setArguments(bundle);
                            android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().add(dialog, "reward_detail").commit();
                        }
                    });

                    if (isBuyable) {
                        rewardPictureView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                new BuyRewardTask(reward.getId(), motivator).execute();
                                Snackbar.make(v, "Die Belohnung wurde gekauft.", Snackbar.LENGTH_LONG).show();
                                meCompetitor.setPoints(meCompetitor.getPoints() - reward.getPoints());

                                final int index = competition.getCompetitors().indexOf(meCompetitor);
                                final TextView balanceView = competitorCategories.getChildAt(index).findViewById(R.id.competitor_balance);
                                balanceView.setText(String.valueOf(meCompetitor.getPoints()));

                                for (int i = 0; i < rewardCategories.getChildCount(); i++) {
                                    View row = rewardCategories.getChildAt(i);
                                    Reward reward = competition.getRewards().get(i);

                                    if (reward.getPoints() > meCompetitor.getPoints()) {
                                        row.findViewById(R.id.reward_detail_picture).setOnLongClickListener(null);
                                    }
                                }
                                return true;
                            }
                        });
                    }

                    row.addView(entry);

                    if (count % MAX_REWARDS == 0) {
                        first = true;
                        row = null;
                    }

                }
            }

            if (progress != null) {

                int count = 0;
                boolean first = true;
                LinearLayout row = null;

                for (final ProgressStepData stepData : progress) {

                    // skip closed rewards
                    if (stepData.getApplied() != ApplyStatus.OPEN) {
                        continue;
                    }

                    count++;

                    // create row
                    if (first) {
                        row = (LinearLayout) inflater.inflate(R.layout.progress_row, container, false);
                        first = false;
                        progressCategories.addView(row);
                    }

                    final LinearLayout entry = (LinearLayout) inflater.inflate(R.layout.progress_entry, row, false);
                    final ImageView progressPicture = entry.findViewById(R.id.progress_detail_achievement_picture);

                    new ConvertBitmapTask(getContext(), progressPicture, stepData).execute();

                    boolean tmpAcceptable = true;
                    if (stepData.getUserid() == me.getId()) {
                        tmpAcceptable = false;
                    }

                    final boolean acceptable = tmpAcceptable;
                    progressPicture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Fragment dialog = new ProgressDetailDialog();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Constants.SerializeableKeys.PROGRESS, stepData);
                            bundle.putSerializable(Constants.SerializeableKeys.COMPETITOR, competition.findCompetitorByUserId(stepData.getUserid()).getUser());
                            bundle.putBoolean(Constants.SerializeableKeys.PROGRESS_ACCEPTABLE, acceptable);
                            bundle.putSerializable(Constants.SerializeableKeys.PROGRESS_LISTENER, CompetitionDetailFragment.this);
                            dialog.setArguments(bundle);
                            android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().add(dialog, "progress_detail").commit();
                        }
                    });

                    row.addView(entry);

                    if (count % MAX_PROGRESS == 0) {
                        first = true;
                        row = null;
                    }
                }
            }

        }
    }

    @Override
    public void onDialogPositiveClick(ProgressStepData data) {
        new ConfirmProgressTask(data, motivator).execute();
    }

    @Override
    public void onDialogNegativeClick(ProgressStepData data) {
        new DeclineProgressTask(data, motivator).execute();
    }

    @Override
    public void onCreateMessageClick(Message message) {
        new SendMessageTask(message, motivator).execute();
    }
}
