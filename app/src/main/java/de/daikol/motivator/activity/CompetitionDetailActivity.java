package de.daikol.motivator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import de.daikol.motivator.R;
import de.daikol.motivator.application.Motivator;
import de.daikol.motivator.fragment.CompetitionDetailFragment;
import de.daikol.motivator.fragment.MessageCreationSupportDialog;
import de.daikol.motivator.model.Message;
import de.daikol.motivator.tasks.SendMessageTask;
import de.daikol.motivator.util.Constants;

public class CompetitionDetailActivity extends AppCompatActivity implements MessageCreationSupportDialog.CreateMessageDialogListener {

    private static final String LOG_TAG = "CompetitionDetailAct";

    private Motivator motivator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        motivator = (Motivator) getApplication();

        if (!motivator.isLoggedIn()) {
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
        }

        setContentView(R.layout.activity_competition_detail);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putSerializable(Constants.SerializeableKeys.COMPETITION, getIntent().getSerializableExtra(Constants.SerializeableKeys.COMPETITION));
            CompetitionDetailFragment fragment = new CompetitionDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.competition_detail_container, fragment)
                    .commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!motivator.isLoggedIn()) {
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
        }

        // Create the detail fragment and add it to the activity
        // using a fragment transaction.
        Bundle arguments = new Bundle();
        arguments.putSerializable(Constants.SerializeableKeys.COMPETITION, getIntent().getSerializableExtra(Constants.SerializeableKeys.COMPETITION));
        CompetitionDetailFragment fragment = new CompetitionDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.competition_detail_container, fragment)
                .commitAllowingStateLoss();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onCreateMessageClick(Message message) {
        Toast.makeText(getApplicationContext(), "Die Nachricht wurde verschickt!", Toast.LENGTH_SHORT);
        new SendMessageTask(message, motivator).execute();
    }

}
