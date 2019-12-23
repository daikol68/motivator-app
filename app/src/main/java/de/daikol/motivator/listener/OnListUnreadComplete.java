package de.daikol.motivator.listener;

import java.util.List;

import de.daikol.motivator.model.Message;

public interface OnListUnreadComplete {
    void onListUnreadCompleted(List<Message> messages);
}
