package de.daikol.motivator.listener;

import java.util.List;

import de.daikol.motivator.model.Competition;

public interface OnListChallengesComplete {
    void onListChallengesCompleted(List<Competition> competitions);
}
