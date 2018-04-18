package com.mgmtp.radio.sdo;

public enum HistoryLimitation {
    first(50),
    next(10);

    private int limit;

    HistoryLimitation(Integer limit){
        this.limit = limit;
    }
    public Integer getLimit(){
        return this.limit;
    }
}
