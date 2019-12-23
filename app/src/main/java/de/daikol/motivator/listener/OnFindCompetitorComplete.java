package de.daikol.motivator.listener;

import java.util.List;

import de.daikol.motivator.model.user.User;

public interface OnFindCompetitorComplete {
    void onFindCompleted(List<User> users);
}
