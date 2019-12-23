package de.daikol.motivator.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.daikol.motivator.R;
import de.daikol.motivator.application.Motivator;
import de.daikol.motivator.listener.OnFindCompetitorComplete;
import de.daikol.motivator.model.user.User;
import de.daikol.motivator.tasks.ConvertBitmapTask;
import de.daikol.motivator.tasks.FindCompetitorTask;
import de.daikol.motivator.util.Constants;

public class CompetitorSearchActivity extends AppCompatActivity implements OnFindCompetitorComplete {

    private static final String LOG_TAG = "CompetitorSearchAct";

    private Motivator motivator;

    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        motivator = (Motivator) getApplication();

        if (!motivator.isLoggedIn()) {
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
        }

        users = new ArrayList<>();
        setContentView(R.layout.activity_competitor_search);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        EditText filter = findViewById(R.id.competitor_name);

        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // nothing to do
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() >= 3) {
                    new FindCompetitorTask(s.toString(), users, motivator, CompetitorSearchActivity.this).execute();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // nothing to do
            }
        });

        setupRecyclerView(users);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");

        if (!motivator.isLoggedIn()) {
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
        }

        setupRecyclerView(users);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupRecyclerView(List<User> users) {
        RecyclerView recyclerView = findViewById(R.id.competitor_list);
        recyclerView.setAdapter(new UserViewAdapter(users));
    }

    @Override
    public void onFindCompleted(List<User> users) {
        setupRecyclerView(users);
    }

    public class UserViewAdapter extends RecyclerView.Adapter<UserViewAdapter.ViewHolder> {

        private final List<User> users;

        public UserViewAdapter(List<User> items) {
            users = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.competitor_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            if (users != null) {
                holder.mItem = users.get(position);
                final User item = holder.mItem;
                final LinearLayout linear = holder.mLinear;
                final ImageView pictureView = linear.findViewById(R.id.competitor_overview_picture);
                final TextView nameView = linear.findViewById(R.id.competitor_overview_name);

                nameView.setText(item.getName());
                new ConvertBitmapTask(getApplicationContext(), pictureView, item).execute();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra(Constants.SerializeableKeys.COMPETITOR, item);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            if (users == null) {
                return 0;
            }
            return users.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final LinearLayout mLinear;
            public User mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mLinear = view.findViewById(R.id.competitor_overview);
            }
        }
    }
}
