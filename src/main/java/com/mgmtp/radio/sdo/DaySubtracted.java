package com.mgmtp.radio.sdo;

public enum DaySubtracted {
    DAYS_SUBTRACTED(1L);
    private Long DAYS_LIMITED;

    DaySubtracted(Long DAYS_LIMITED) {
        this.DAYS_LIMITED = DAYS_LIMITED;
    }

    public Long getDAYS_LIMITED() {
        return DAYS_LIMITED;
    }

    public void setDAYS_LIMITED(Long DAYS_LIMITED) {
        this.DAYS_LIMITED = DAYS_LIMITED;
    }
}
