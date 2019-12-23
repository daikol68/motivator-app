package de.daikol.motivator.listener;

import de.daikol.motivator.model.Competition;

public interface OnLeaveCompetition {
    void onLeaveCompetitionPositive(Competition competition);
    void onLeaveCompetitionNegative(Competition competition);
}
