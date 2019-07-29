package de.daikol.acclaim.util;

import java.io.Serializable;

public class RunnableHolder implements Serializable {

    private final Runnable onSuccess;
    private final Runnable onFailure;

    public RunnableHolder(Runnable onSuccess, Runnable onFailure) {
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;
    }

    public void executeSuccess() {
        this.onSuccess.run();
    }

    public void executeFailure() {
        this.onFailure.run();
    }
}

