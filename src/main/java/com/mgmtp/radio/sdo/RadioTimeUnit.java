package com.mgmtp.radio.sdo;

public enum RadioTimeUnit {
    DAY("DT", 86400),
    HOUR("H", 3600),
    MINUTE("M", 60),
    SECOND("S", 1),
    MILLISECONDS("MS", 1000);

    private String key;
    private int period;

    RadioTimeUnit(String key, int period) {
        this.key = key;
        this.period = period;
    }

    public String getKey() {
        return this.key;
    }

    public int getPeriod() {
        return this.period;
    }
}
