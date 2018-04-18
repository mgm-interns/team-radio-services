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

    protected boolean validatesEventId = true;
    protected String associatedEventId;

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

    abstract boolean canHandleMessage(Map<String, Object> messageData);

    protected abstract void process(Map<String, Object> message);
}
