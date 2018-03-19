package com.mgmtp.radio.event;

import com.mgmtp.radio.sdo.EventDataKeys;
import lombok.extern.log4j.Log4j2;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
public abstract class BaseEventMessageEndpoint {

    private boolean validatesEventId = true;
    private String associatedEventId;

    protected BaseEventMessageEndpoint() {
        validatesEventId = false;
    }

    public BaseEventMessageEndpoint(String associatedEventId) {
        this.associatedEventId = associatedEventId;
    }

    List<String> notLogEventIds = new ArrayList<String>() {{

    }};

    @ServiceActivator
    public void receive(Map<String, Object> message) {

        if (!canHandleMessage(message)) {
            return;
        }

        // Hand off to concrete sub class
        long start = System.currentTimeMillis();
        try {
            process(message);
        } finally {
            long end = System.currentTimeMillis();
            String eventId = message.containsKey(EventDataKeys.event_id.name()) ? (String)message.get(EventDataKeys.event_id.name()) : "unknown";
            if (!notLogEventIds.contains(eventId)) {
                log.debug("Message: {} for userId: {} processed in {} milliseconds in class: {}",
                        eventId,
                        message.containsKey(EventDataKeys.user_id.name()) ? message.get(EventDataKeys.user_id.name()) : "unknown",
                        end - start,
                        this.getClass().toString()
                );
            }

        }
    }

    private boolean canHandleMessage(Map<String, Object> messageData) {

        if (messageData == null ||
                messageData.get(EventDataKeys.user_id.name()) == null ||
                messageData.get(EventDataKeys.event_id.name()) == null) {
            log.error("Invalid event message data received {}", messageData);
            return false;
        }

        String userId = (String) messageData.get(EventDataKeys.user_id.name());
        String eventId = (String) messageData.get(EventDataKeys.event_id.name());

        if (StringUtils.isEmpty(userId)) {
            log.info("ignoring invalid userId: {}", userId);
            return false;
        }

        if (eventId == null) {
            log.info("null eventId found");
            return false;
        }

        if (validatesEventId) {
            if (!eventId.equals(associatedEventId)) {
                log.debug("receive() - Associated event id: {}. I cannot handle events of cost: {}", associatedEventId, eventId);
                return false;
            }
        }

        if (!notLogEventIds.contains(eventId)) {
            log.info("processing event '{}' for userId {}", eventId, userId);
        }
        return true;
    }

    protected abstract void process(Map<String, Object> message);
}
