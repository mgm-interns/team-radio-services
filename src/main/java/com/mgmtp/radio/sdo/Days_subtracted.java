package com.mgmtp.radio.sdo;

public enum Days_subtracted {
    DAYS_SUBTRACTED(1L);
    private  Long Days_limited;

    Days_subtracted(Long Days_limited) {
        this.Days_limited=Days_limited;
    }

    public Long getDays_limited() {
        return Days_limited;
    }

    public void setDays_limited(Long days_limited) {
        Days_limited = days_limited;
    }
}
