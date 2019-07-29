package de.daikol.acclaim.adapter;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.daikol.acclaim.R;
import de.daikol.acclaim.activity.CompetitionDetailActivity;
import de.daikol.acclaim.application.Challenger;
import de.daikol.acclaim.model.Competition;
import de.daikol.acclaim.model.user.User;
import de.daikol.acclaim.tasks.ConvertBitmapTask;
import de.daikol.acclaim.util.Constants;

public class ChallengeAdapter extends AbstractAdapter<Competition, ChallengeAdapter.ChallengeViewHolder> {

    private final static String LOG_TAG = "MessageAdapter";

    public ChallengeAdapter(List<Competition> items, Challenger challenger, Context context, FragmentManager fragmentManager) {
        super(items, challenger, context, fragmentManager);
    }

    public class ChallengeViewHolder extends RecyclerView.ViewHolder {
        public final View container;
        public final View foreground;
        public final View background;
        public final ImageView challengePicture;
        public final TextView challengeName;

        public ChallengeViewHolder(View view) {
            super(view);
            container = view;
            foreground = view.findViewById(R.id.competition_list_foreground);
            background = view.findViewById(R.id.competition_list_background);
            challengePicture = view.findViewById(R.id.competition_list_competition_picture);
            challengeName = view.findViewById(R.id.competition_list_competition_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + challengeName.getText() + "'";
        }
    }

    @Override
    public ChallengeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.competition_list_content, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChallengeViewHolder holder, final int position) {

        final User me = getChallenger().getUser();
        final Competition competition = getItem(holder.getAdapterPosition());
        final boolean isSolo = competition.isSoloByUserId(me.getId());

        holder.challengeName.setText(competition.getName());
        new ConvertBitmapTask(getContext(), holder.challengePicture, competition).execute();

        if (isSolo) {
            holder.challengeName.setTextColor(Color.RED);
            holder.challengeName.setHint("Sie sind der letzte Teilnehmer.");
        }

        holder.challengePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, CompetitionDetailActivity.class);
                intent.putExtra(Constants.SerializeableKeys.COMPETITION, competition);
                context.startActivity(intent);
            }
        });
        holder.challengeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, CompetitionDetailActivity.class);
                intent.putExtra(Constants.SerializeableKeys.COMPETITION, competition);
                context.startActivity(intent);
            }
        });
    }
}
