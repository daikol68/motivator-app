package de.daikol.motivator.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import de.daikol.motivator.R;
import de.daikol.motivator.application.Motivator;
import de.daikol.motivator.model.Achievement;
import de.daikol.motivator.model.AchievementStatus;
import de.daikol.motivator.model.Competition;
import de.daikol.motivator.model.CompetitionStatus;
import de.daikol.motivator.model.Competitor;
import de.daikol.motivator.model.Reward;
import de.daikol.motivator.model.RewardStatus;
import de.daikol.motivator.model.user.User;
import de.daikol.motivator.tasks.ConvertBitmapTask;
import de.daikol.motivator.tasks.CreateCompetitionTask;
import de.daikol.motivator.tasks.UpdateCompetitionTask;
import de.daikol.motivator.util.BitmapUtility;
import de.daikol.motivator.util.Constants;

public class CompetitionEditActivity extends AppCompatActivity {

    private static final String LOG_TAG = "CompetitionEditActivity";

    private Motivator motivator;

    private EditText competitionName;

    private ImageView competitionPicture;

    private Competition competition;

    private LinearLayout competitors;

    private LinearLayout achievements;

    private LinearLayout rewards;

    private ImageView competitorAdd;

    private ImageView achievementAdd;

    private ImageView rewardAdd;

    private FloatingActionButton update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        motivator = (Motivator) getApplication();
        if (!motivator.isLoggedIn()) {
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
        }

        // set the content view
        setContentView(R.layout.activity_edit);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            this.competition = (Competition) savedInstanceState.getSerializable(Constants.SerializeableKeys.COMPETITION);
        }

        // check if we have a competition coming from outside
        this.competition = (Competition) getIntent().getSerializableExtra(Constants.SerializeableKeys.COMPETITION);

        final User user = motivator.getUser();

        // check if competition is still null and create a new one
        if (this.competition == null) {
            this.competition = new Competition();
            this.competition.setCompetitors(new ArrayList<Competitor>());
            this.competition.setAchievements(new ArrayList<Achievement>());
            this.competition.setRewards(new ArrayList<Reward>());

            // directly add the user
            Competitor competitor = new Competitor();
            competitor.setCompetition(competition);
            competitor.setUser(user);
        }

        competitionName = findViewById(R.id.competition_create_name);
        competitionPicture = findViewById(R.id.competition_create_picture);

        competitionName.setText(competition.getName());
        if (competition.getBitmap() != null) {
            new ConvertBitmapTask(getApplicationContext(), competitionPicture, competition).execute();
        }

        competitionPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 0);
            }
        });

        competitors = findViewById(R.id.competition_create_competitors);
        achievements = findViewById(R.id.competition_create_achievements);
        rewards = findViewById(R.id.competition_create_rewards);

        update = findViewById(R.id.competition_update_fab);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean meIsCompetitor = false;
                if (competition.getCompetitors() != null) {
                    for (Competitor competitor : competition.getCompetitors()) {
                        if (competitor.getUser().getId() == user.getId()) {
                            meIsCompetitor = true;
                        }
                    }
                }

                if (!meIsCompetitor) {
                    Competitor me = new Competitor();
                    me.setCompetition(competition);
                    me.setStatus(CompetitionStatus.CONFIRMED);
                    me.setUser(user);
                    competition.getCompetitors().add(me);
                }

                copyToCompetition();

                if (competition.getName() == null || competition.getName().isEmpty()) {
                    Snackbar.make(view, "Jeder Wettbewerb benötigt einen Namen.", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (competition.getCompetitors() == null || competition.getCompetitors().size() < 2) {
                    Snackbar.make(view, "Ein Wettbewerb benötigt mindestens zwei Teilnehmer.", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (competition.getAchievements() == null || competition.getAchievements().isEmpty()) {
                    Snackbar.make(view, "Eine Herausforderung muss mindestens einen Erfolg haben.", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (competition.getRewards() == null || competition.getRewards().isEmpty()) {
                    Snackbar.make(view, "Eine Herausforderung muss mindestens eine Belohnung haben.", Snackbar.LENGTH_LONG).show();
                    return;
                }
                for (int i = 0; i < competition.getAchievements().size(); i++) {

                    Achievement achievement = competition.getAchievements().get(i);
                    LinearLayout row = (LinearLayout) achievements.getChildAt(i);

                    if (achievement.getName() == null || achievement.getName().isEmpty()) {
                        EditText achievementName = row.findViewById(R.id.achievement_create_name);
                        achievementName.requestFocus();
                        Snackbar.make(view, "Jeder Erfolg muss einen Namen haben.", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    if (achievement.getProgressStepsFinish() <= 0) {
                        EditText achievementFinish = row.findViewById(R.id.achievement_create_finish);
                        achievementFinish.requestFocus();
                        Snackbar.make(view, "Jeder Erfolg muss mindestens einmal ausgeführt werden.", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    if (achievement.getPoints() <= 0) {
                        EditText achievementPoints = row.findViewById(R.id.achievement_create_points);
                        achievementPoints.requestFocus();
                        Snackbar.make(view, "Jeder Erfolg sollte Punkte haben die einem gutgeschrieben werden.", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                }
                for (int i = 0; i < competition.getRewards().size(); i++) {

                    Reward reward = competition.getRewards().get(i);
                    LinearLayout row = (LinearLayout) rewards.getChildAt(i);

                    if (reward.getName() == null || reward.getName().isEmpty()) {
                        EditText rewardName = row.findViewById(R.id.reward_create_name);
                        rewardName.requestFocus();
                        Snackbar.make(view, "Jede Belohnung muss einen Namen haben.", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    if (reward.getPoints() <= 0) {
                        EditText rewardPoints = row.findViewById(R.id.reward_create_points);
                        rewardPoints.requestFocus();
                        Snackbar.make(view, "Jede Belohnung sollte Kosten haben.", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                }

                if (competition.getId() == 0) {
                    new CreateCompetitionTask(competition, motivator).execute();
                } else {
                    new UpdateCompetitionTask(competition, motivator).execute();
                }

                onBackPressed();
            }
        });

        competitorAdd = findViewById(R.id.competition_create_competitor_add);
        competitorAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent competitorAddIntent = new Intent(getApplicationContext(), CompetitorSearchActivity.class);
                competitorAddIntent.putExtra(Constants.SerializeableKeys.COMPETITION, competition);
                final int index = competitors.getChildCount();
                if (index >= 99) {
                    Toast.makeText(getApplicationContext(), "Das Limit an Teilnehmern ist leider erreicht!", Toast.LENGTH_SHORT);
                    return;
                }
                startActivityForResult(competitorAddIntent, 300 + index);
            }
        });

        achievementAdd = findViewById(R.id.competition_create_achievement_add);
        achievementAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the index of the current row
                final int index = achievements.getChildCount();

                if (index >= 99) {
                    Toast.makeText(getApplicationContext(), "Das Limit an Erfolgen ist leider erreicht!", Toast.LENGTH_SHORT);
                    return;
                }

                // create row
                final LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.achievement_row_create, achievements, false);

                final ImageView achievementPicture = row.findViewById(R.id.achievement_create_picture);
                final EditText achievementName = row.findViewById(R.id.achievement_create_name);
                final EditText achievementPoints = row.findViewById(R.id.achievement_create_points);
                final EditText achievementFinish = row.findViewById(R.id.achievement_create_finish);
                final FloatingActionButton achievementDelete = row.findViewById(R.id.achievement_create_delete);

                achievementPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, 100 + index);
                    }
                });

                achievements.addView(row);

                // add listener to remove entry
                achievementDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        competition.getAchievements().remove(index);
                        View child = achievements.getChildAt(index);
                        achievements.removeView(child);
                        copyToCompetition();
                        copyFromCompetition();
                    }
                });

                // add also achievement
                Achievement achievement = new Achievement();
                competition.getAchievements().add(achievement);
            }
        });

        rewardAdd = findViewById(R.id.competition_create_reward_add);
        rewardAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get the index of the current row
                final int index = rewards.getChildCount();

                // create row
                final LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.reward_row_create, rewards, false);

                final ImageView rewardPicture = row.findViewById(R.id.reward_create_picture);
                final EditText rewardName = row.findViewById(R.id.reward_create_name);
                final EditText rewardPoints = row.findViewById(R.id.reward_create_points);
                final FloatingActionButton rewardDelete = row.findViewById(R.id.reward_create_delete);

                rewardPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, 200 + index);
                    }
                });

                rewards.addView(row);

                // add listener to remove entry
                rewardDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        competition.getRewards().remove(index);
                        View child = rewards.getChildAt(index);
                        rewards.removeView(child);
                        copyToCompetition();
                        copyFromCompetition();
                    }
                });

                // add also reward
                Reward reward = new Reward();
                competition.getRewards().add(reward);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        copyToCompetition();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!motivator.isLoggedIn()) {
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
        }

        copyFromCompetition();
    }

    private void copyFromCompetition() {

        competitionName.setText(competition.getName());
        if (competition.getBitmap() != null) {
            competitionPicture.setBackground(new BitmapDrawable(getResources(), competition.getBitmap()));
        }

        competitors.removeAllViews();
        if (competition.getCompetitors() != null) {
            int i = 0;

            for (Competitor competitor : competition.getCompetitors()) {

                final int index = i;
                // create row
                final LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.competitor_row, competitors, false);

                final TextView id = row.findViewById(R.id.competitor_id);
                final ImageView picture = row.findViewById(R.id.competitor_picture);
                final TextView name = row.findViewById(R.id.competitor_name);
                final TextView points = row.findViewById(R.id.competitor_balance);
                final FloatingActionButton competitorDelete = row.findViewById(R.id.competitor_delete);

                new ConvertBitmapTask(getBaseContext(), picture, competitor.getUser()).execute();
                id.setText(String.valueOf(competitor.getUser().getId()));
                name.setText(competitor.getUser().getName());
                points.setText(String.valueOf(competitor.getPoints()));

                // add listener to remove entry
                competitorDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        competition.getCompetitors().remove(index);
                        View child = competitors.getChildAt(index);
                        competitors.removeView(child);
                        copyToCompetition();
                        copyFromCompetition();
                    }
                });

                competitors.addView(row, index);
                i++;
            }
        }

        achievements.removeAllViews();
        if (competition.getAchievements() != null) {

            int i = 0;

            for (Achievement achievement : competition.getAchievements()) {

                if (achievement.getStatus() == AchievementStatus.CLOSED) {
                    continue;
                }

                // get the index for the row
                final int index = i;

                // create row
                final LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.achievement_row_create, achievements, false);

                final ImageView achievementPicture = row.findViewById(R.id.achievement_create_picture);
                final EditText achievementName = row.findViewById(R.id.achievement_create_name);
                final EditText achievementPoints = row.findViewById(R.id.achievement_create_points);
                final EditText achievementFinish = row.findViewById(R.id.achievement_create_finish);
                final FloatingActionButton achievementDelete = row.findViewById(R.id.achievement_create_delete);

                new ConvertBitmapTask(getBaseContext(), achievementPicture, achievement).execute();
                achievementPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, 100 + index);
                    }
                });

                // add listener to remove entry
                achievementDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        competition.getAchievements().remove(index);
                        View child = achievements.getChildAt(index);
                        achievements.removeView(child);
                        copyToCompetition();
                        copyFromCompetition();
                    }
                });

                achievementName.setText(achievement.getName());
                if (achievement.getPoints() != 0) {
                    achievementPoints.setText(String.valueOf(achievement.getPoints()));
                }
                if (achievement.getProgressStepsFinish() != 0) {
                    achievementFinish.setText(String.valueOf(achievement.getProgressStepsFinish()));
                }

                achievements.addView(row, index);
                i++;
            }
        }

        rewards.removeAllViews();
        if (competition.getRewards() != null) {

            int i = 0;

            for (Reward reward : competition.getRewards()) {

                if (reward.getStatus() == RewardStatus.CLOSED) {
                    continue;
                }

                // get the index for the row
                final int index = i;

                // create row
                final LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.reward_row_create, rewards, false);

                final ImageView rewardPicture = row.findViewById(R.id.reward_create_picture);
                final EditText rewardName = row.findViewById(R.id.reward_create_name);
                final EditText rewardPoints = row.findViewById(R.id.reward_create_points);
                final FloatingActionButton rewardDelete = row.findViewById(R.id.reward_create_delete);

                new ConvertBitmapTask(getBaseContext(), rewardPicture, reward).execute();
                rewardPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, 200 + index);
                    }
                });

                // add listener to remove entry
                rewardDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        competition.getRewards().remove(index);
                        View child = rewards.getChildAt(index);
                        rewards.removeView(child);
                        copyToCompetition();
                        copyFromCompetition();
                    }
                });

                rewardName.setText(reward.getName());
                if (reward.getPoints() != 0) {
                    rewardPoints.setText(String.valueOf(reward.getPoints()));
                }

                rewards.addView(row, index);
                i++;
            }
        }
    }

    private void copyToCompetition() {

        User me = motivator.getUser();

        if (!competitionName.getText().toString().isEmpty()) {
            competition.setName(competitionName.getText().toString());
        }
        if (competitionPicture.getBackground() != null && competitionPicture.getBackground() instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) competitionPicture.getBackground()).getBitmap();
            competition.setBitmap(bitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] userPicture = baos.toByteArray();
            competition.setPicture(userPicture);
        }
        for (int i = 0; i < competitors.getChildCount(); i++) {

            final LinearLayout row = (LinearLayout) competitors.getChildAt(i);
            final Competitor competitor = competition.getCompetitors().get(i);
            User user = competitor.getUser();

            if (user.getId() == me.getId()) {
                row.setVisibility(View.GONE);
            }

            final TextView competitorId = row.findViewById(R.id.competitor_id);
            final ImageView competitorPicture = row.findViewById(R.id.competitor_picture);
            final TextView competitorName = row.findViewById(R.id.competitor_name);
            final TextView competitorPoints = row.findViewById(R.id.competitor_balance);

            user.setId(Integer.valueOf(competitorId.getText().toString()));
            if (competitorPicture.getBackground() != null && competitorPicture.getBackground() instanceof BitmapDrawable) {
                user.setBitmap(((BitmapDrawable) competitorPicture.getBackground()).getBitmap());
            }
            user.setName(competitorName.getText().toString());
            if (!competitorPoints.getText().toString().equals("")) {
                competitor.setPoints(Integer.parseInt(competitorPoints.getText().toString()));
            }
        }

        for (int i = 0; i < achievements.getChildCount(); i++) {

            final LinearLayout row = (LinearLayout) achievements.getChildAt(i);
            final Achievement achievement = competition.getAchievements().get(i);

            final ImageView achievementPicture = row.findViewById(R.id.achievement_create_picture);
            final EditText achievementName = row.findViewById(R.id.achievement_create_name);
            final EditText achievementPoints = row.findViewById(R.id.achievement_create_points);
            final EditText achievementFinish = row.findViewById(R.id.achievement_create_finish);

            if (achievementPicture.getBackground() != null && achievementPicture.getBackground() instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) achievementPicture.getBackground()).getBitmap();
                achievement.setBitmap(bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] userPicture = baos.toByteArray();
                achievement.setPicture(userPicture);
            }
            achievement.setName(achievementName.getText().toString());
            if (!achievementPoints.getText().toString().equals("")) {
                achievement.setPoints(Integer.parseInt(achievementPoints.getText().toString()));
            }
            if (!achievementFinish.getText().toString().equals("")) {
                achievement.setProgressStepsFinish(Integer.parseInt(achievementFinish.getText().toString()));
            }
        }

        for (int i = 0; i < rewards.getChildCount(); i++) {

            final LinearLayout row = (LinearLayout) rewards.getChildAt(i);
            final Reward reward = competition.getRewards().get(i);

            final ImageView rewardPicture = row.findViewById(R.id.reward_create_picture);
            final EditText rewardName = row.findViewById(R.id.reward_create_name);
            final EditText rewardPoints = row.findViewById(R.id.reward_create_points);

            if (rewardPicture.getBackground() != null && rewardPicture.getBackground() instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) rewardPicture.getBackground()).getBitmap();
                reward.setBitmap(bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] userPicture = baos.toByteArray();
                reward.setPicture(userPicture);
            }
            reward.setName(rewardName.getText().toString());
            if (!rewardPoints.getText().toString().equals("")) {
                reward.setPoints(Integer.parseInt(rewardPoints.getText().toString()));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (this.competition != null) {
            // Save the user's current game state
            savedInstanceState.putSerializable(Constants.SerializeableKeys.COMPETITION, this.competition);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode / 100 == 0 && resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                final Bitmap bitmap = BitmapUtility.scaleDownBitmap(selectedImage, BitmapUtility.HEIGHT, this);
                competitionPicture.setBackground(new BitmapDrawable(getResources(), bitmap));
            } catch (FileNotFoundException e) {
                Log.w(LOG_TAG, "Exception [" + e.getClass().getName() + "] raised while loading image due to [" + e.getMessage() + "].");
            }
            copyToCompetition();
        } else if (requestCode / 100 == 1 && resultCode == RESULT_OK) {
            final int index = requestCode % 100;

            if (index + 1 > competition.getAchievements().size()) {
                throw new IllegalStateException("This should not happen.");
            }

            final Achievement achievement = competition.getAchievements().get(index);

            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                final Bitmap bitmap = BitmapUtility.scaleDownBitmap(selectedImage, BitmapUtility.HEIGHT, this);
                achievement.setBitmap(bitmap);
                copyFromCompetition();
            } catch (FileNotFoundException e) {
                Log.w(LOG_TAG, "Exception [" + e.getClass().getName() + "] raised while loading image due to [" + e.getMessage() + "].");
            }
        } else if (requestCode / 100 == 2 && resultCode == RESULT_OK) {
            final int index = requestCode % 100;

            if (index + 1 > competition.getRewards().size()) {
                throw new IllegalStateException("This should not happen.");
            }

            final Reward reward = competition.getRewards().get(index);

            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                final Bitmap bitmap = BitmapUtility.scaleDownBitmap(selectedImage, BitmapUtility.HEIGHT, this);
                reward.setBitmap(bitmap);
                copyFromCompetition();
            } catch (FileNotFoundException e) {
                Log.w(LOG_TAG, "Exception [" + e.getClass().getName() + "] raised while loading image due to [" + e.getMessage() + "].");
            }
        } else if (requestCode / 100 == 3 && resultCode == RESULT_OK) {
            final User user = (User) data.getSerializableExtra(Constants.SerializeableKeys.COMPETITOR);
            final Competitor competitor = new Competitor();
            competitor.setPoints(0);
            competitor.setStatus(CompetitionStatus.CONFIRMED);
            competitor.setCompetition(competition);
            competitor.setUser(user);
            competition.getCompetitors().add(competitor);
            copyFromCompetition();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            if (competition.getId() != 0) {
                Intent intent = new Intent(this, CompetitionDetailActivity.class);
                intent.putExtra(Constants.SerializeableKeys.COMPETITION, competition);
                navigateUpTo(intent);
            } else {
                navigateUpTo(new Intent(this, MainActivity.class));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (competition.getId() != 0) {
            Intent intent = new Intent(this, CompetitionDetailActivity.class);
            intent.putExtra(Constants.SerializeableKeys.COMPETITION, competition);
            navigateUpTo(intent);
        } else {
            navigateUpTo(new Intent(this, MainActivity.class));
        }
    }

}
