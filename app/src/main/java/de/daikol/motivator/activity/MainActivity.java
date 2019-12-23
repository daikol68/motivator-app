package de.daikol.motivator.activity;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import de.daikol.motivator.R;
import de.daikol.motivator.adapter.ChallengeAdapter;
import de.daikol.motivator.adapter.ChallengeTouchHelper;
import de.daikol.motivator.adapter.MessageAdapter;
import de.daikol.motivator.adapter.MessageTouchHelper;
import de.daikol.motivator.application.Motivator;
import de.daikol.motivator.fragment.LeaveCompetitionDialog;
import de.daikol.motivator.fragment.MessageCreationDialog;
import de.daikol.motivator.listener.OnLeaveCompetition;
import de.daikol.motivator.listener.OnListChallengesComplete;
import de.daikol.motivator.listener.OnListUnreadComplete;
import de.daikol.motivator.model.Competition;
import de.daikol.motivator.model.Message;
import de.daikol.motivator.model.user.User;
import de.daikol.motivator.tasks.DeclineCompetitionTask;
import de.daikol.motivator.tasks.ListChallengesTask;
import de.daikol.motivator.tasks.ListUnreadMessagesTask;
import de.daikol.motivator.tasks.ReadMessageTask;
import de.daikol.motivator.tasks.SendMessageTask;
import de.daikol.motivator.util.Constants;

public class MainActivity extends AppCompatActivity implements
        Serializable,
        BottomNavigationView.OnNavigationItemSelectedListener,
        MessageTouchHelper.Listener,
        ChallengeTouchHelper.Listener,
        OnListUnreadComplete,
        OnListChallengesComplete,
        MessageCreationDialog.CreateMessageDialogListener,
        SwipeRefreshLayout.OnRefreshListener,
        OnLeaveCompetition {

    private Motivator motivator;

    private BottomNavigationView navigation;

    private RecyclerView recycler;

    private MessageAdapter messageAdapter;

    private ChallengeAdapter challengeAdapter;

    private ProgressDialog loader;

    private SwipeRefreshLayout refresh;

    private int viewSwitch;

    private ItemTouchHelper messageHelper;

    private ItemTouchHelper challengeHelper;

    private FloatingActionButton addButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        viewSwitch = R.id.nav_messages;
        motivator = (Motivator) getApplication();

        if (!motivator.isLoggedIn()) {
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
        }

        // set the content view
        setContentView(R.layout.activity_main);
        navigation = findViewById(R.id.main_bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        refresh = findViewById(R.id.main_swipe_container);
        refresh.setOnRefreshListener(this);
        refresh.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        recycler = findViewById(R.id.main_recycler_view);
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                refresh.setEnabled(topRowVerticalPosition >= 0);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        ItemTouchHelper.SimpleCallback messageCallback = new MessageTouchHelper(0, ItemTouchHelper.RIGHT, this);
        messageHelper = new ItemTouchHelper(messageCallback);

        ItemTouchHelper.SimpleCallback challengeCallback = new ChallengeTouchHelper(0, ItemTouchHelper.RIGHT, this);
        challengeHelper = new ItemTouchHelper(challengeCallback);

        addButton = findViewById(R.id.main_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        switchViewTo(R.id.nav_messages);
    }

    private void addItem() {
        if (viewSwitch == R.id.nav_messages) {
            Intent search = new Intent(getApplicationContext(), CompetitorSearchActivity.class);
            startActivityForResult(search, 0);
        } else if (viewSwitch == R.id.nav_challenges) {
            Intent edit = new Intent(getApplicationContext(), CompetitionEditActivity.class);
            startActivity(edit);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }

    private void update() {
        if (!motivator.isLoggedIn()) {
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
        }

        if (viewSwitch == R.id.nav_messages) {
            showLoader();
            new ListUnreadMessagesTask(motivator, this).execute();
        } else if (viewSwitch == R.id.nav_challenges) {
            showLoader();
            new ListChallengesTask(motivator, this).execute();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (viewSwitch == id) {
            return true;
        }
        if (switchViewTo(id)) {
            update();
            return true;
        }
        return false;
    }

    private boolean switchViewTo(int id) {

        if (id == R.id.nav_messages) {
            this.viewSwitch = id;
            recycler.setAdapter(messageAdapter);
            messageHelper.attachToRecyclerView(recycler);
            challengeHelper.attachToRecyclerView(null);
            return true;
        } else if (id == R.id.nav_challenges) {
            this.viewSwitch = id;
            recycler.setAdapter(challengeAdapter);
            messageHelper.attachToRecyclerView(null);
            challengeHelper.attachToRecyclerView(recycler);
            return true;
        } else if (id == R.id.nav_settings) {
            Intent edit = new Intent(getApplicationContext(), UserEditActivity.class);
            startActivity(edit);
            return false;
        }
        return false;
    }

    @Override
    public void onSwipedMessage(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        final MessageAdapter.MessageViewHolder holder = (MessageAdapter.MessageViewHolder) viewHolder;
        new ReadMessageTask(Long.valueOf(holder.messageId.getText().toString()), motivator).execute();
        messageAdapter.remove(position);
    }

    @Override
    public void onSwipedChallenge(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        final ChallengeAdapter adapter = (ChallengeAdapter) recycler.getAdapter();
        final Competition competition = adapter.getItem(position);

        DialogFragment dialog = new LeaveCompetitionDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.SerializeableKeys.COMPETITION, competition);
        bundle.putSerializable(Constants.SerializeableKeys.COMPETITION_LISTENER, this);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "leaveCompetition");
    }


    @Override
    public void onListUnreadCompleted(List<Message> messages) {
        messageAdapter = new MessageAdapter(messages, motivator, getApplicationContext(), getFragmentManager());
        recycler.setAdapter(messageAdapter);
        recycler.invalidate();
        hideLoader();
    }

    @Override
    public void onListChallengesCompleted(List<Competition> competitions) {
        challengeAdapter = new ChallengeAdapter(competitions, motivator, getApplicationContext(), getFragmentManager());
        recycler.setAdapter(challengeAdapter);
        recycler.invalidate();
        hideLoader();
    }

    @Override
    public void onLeaveCompetitionPositive(Competition competition) {
        final ChallengeAdapter adapter = (ChallengeAdapter) recycler.getAdapter();
        int index = -1;
        for (int position = 0; position < adapter.getItemCount(); position++) {
            if (adapter.getItem(position).getId() == competition.getId()) {
                index = position;
                break;
            }
        }
        if (index > -1) {
            adapter.remove(index);
        }
        new DeclineCompetitionTask(competition.getId(), motivator).execute();
        Snackbar.make(recycler, "Die Challenge wurde verlassen.", Snackbar.LENGTH_LONG).show();
        update();
    }

    @Override
    public void onLeaveCompetitionNegative(Competition competition) {
        update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_upper_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_logout) {
            motivator.logout();
            update();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void showLoader() {
        loader = new ProgressDialog(this);
        loader.setMessage("Lade Daten...");
        loader.setCancelable(false);
        loader.setInverseBackgroundForced(false);
        loader.show();
    }

    private void hideLoader() {
        if (loader != null) {
            loader.hide();
            loader.dismiss();
            loader = null;
            refresh.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        update();
    }

    @Override
    public void onCreateMessageClick(Message message) {
        new SendMessageTask(message, motivator).execute();
        Toast.makeText(getApplicationContext(), "Die Nachricht wurde verschickt!", Toast.LENGTH_LONG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final User competitor = (User) data.getSerializableExtra(Constants.SerializeableKeys.COMPETITOR);
        final Message message = new Message();
        message.setReceiver(motivator.getUser());
        message.setSender(competitor);

        DialogFragment dialog = new MessageCreationDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.SerializeableKeys.MESSAGE, message);
        dialog.setArguments(bundle);
        getFragmentManager().beginTransaction().add(dialog, "message_creation").commit();
    }

}
