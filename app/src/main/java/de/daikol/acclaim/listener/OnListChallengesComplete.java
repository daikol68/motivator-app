package de.daikol.acclaim.listener;

import java.util.List;

import de.daikol.acclaim.model.Competition;

public interface OnListChallengesComplete {
    void onListChallengesCompleted(List<Competition> competitions);
}
