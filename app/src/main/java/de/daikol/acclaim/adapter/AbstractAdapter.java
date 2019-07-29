package de.daikol.acclaim.adapter;

import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import de.daikol.acclaim.application.Challenger;

public abstract class AbstractAdapter<MODEL extends Serializable, VIEW extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VIEW> {

    private final List<MODEL> items;

    private final Challenger challenger;

    private final Context context;

    private final FragmentManager fragmentManager;

    public AbstractAdapter(List<MODEL> items, Challenger challenger, Context context, FragmentManager fragmentManager) {
        this.items = items;
        this.challenger = challenger;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public void remove(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void insert(MODEL item, int position) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void addAll(Collection<MODEL> items) {
        this.items.addAll(items);
    }

    public MODEL getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    public Context getContext() {
        return this.context;
    }

    public FragmentManager getFragmentManager() {
        return this.fragmentManager;
    }

    public Challenger getChallenger() {
        return this.challenger;
    }
}
