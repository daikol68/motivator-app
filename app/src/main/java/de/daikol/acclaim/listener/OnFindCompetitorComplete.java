package de.daikol.acclaim.listener;

import java.util.List;

import de.daikol.acclaim.model.Competitor;
import de.daikol.acclaim.model.user.User;

public interface OnFindCompetitorComplete {
    void onFindCompleted(List<User> users);
}
