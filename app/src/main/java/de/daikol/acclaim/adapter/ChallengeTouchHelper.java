package de.daikol.acclaim.adapter;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

public class ChallengeTouchHelper extends ItemTouchHelper.SimpleCallback {

    private Listener listener;

    public ChallengeTouchHelper(int dragDirs, int swipeDirs, Listener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            final View foregroundView = ((ChallengeAdapter.ChallengeViewHolder) viewHolder).container;
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((ChallengeAdapter.ChallengeViewHolder) viewHolder).container;
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = ((ChallengeAdapter.ChallengeViewHolder) viewHolder).container;
        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        final View foregroundView = ((ChallengeAdapter.ChallengeViewHolder) viewHolder).foreground;
        final View backgroundView = ((ChallengeAdapter.ChallengeViewHolder) viewHolder).background;

        float dXRelative = dX / foregroundView.getWidth() * 4;
        // check size boundaries
        if (dXRelative > 1) {
            dXRelative = 1;
        }
        if (dXRelative < 0) {
            dXRelative = 0;
        }
        // animate the icon with scaling on both dimensions
        backgroundView.animate().scaleX(dXRelative).scaleY(dXRelative).setDuration(0).start();
        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwipedChallenge(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    public interface Listener {
        void onSwipedChallenge(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}