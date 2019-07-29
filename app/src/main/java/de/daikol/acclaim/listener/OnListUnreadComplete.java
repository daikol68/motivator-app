package de.daikol.acclaim.listener;

import java.util.List;

import de.daikol.acclaim.model.Message;

public interface OnListUnreadComplete {
    void onListUnreadCompleted(List<Message> messages);
}
