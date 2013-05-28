package Actor;

import java.util.UUID;

/**
 * User: Chris
 * Date: 5/28/13
 * Time: 11:31 PM
 */
class QueueMessage {
    private UUID id;
    private Object message;
    private Actor sender;

    QueueMessage(Object message, Actor sender) {
        this.message = message;
        this.sender = sender;
        this.id = UUID.randomUUID();
    }

    UUID getId() {
        return id;
    }

    Object getMessage() {
        return message;
    }

    Actor getSender() {
        return sender;
    }
}
