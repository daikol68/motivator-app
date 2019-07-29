package de.daikol.acclaim.listener;

import de.daikol.acclaim.model.Competition;

public interface OnLeaveCompetition {
    void onLeaveCompetitionPositive(Competition competition);
    void onLeaveCompetitionNegative(Competition competition);
}
