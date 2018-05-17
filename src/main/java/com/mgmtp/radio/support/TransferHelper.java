package com.mgmtp.radio.support;

import com.mgmtp.radio.exception.RadioException;
import com.mgmtp.radio.sdo.RadioTimeUnit;
import org.springframework.stereotype.Component;

@Component
public class TransferHelper {

    public long transferVideoDuration(String youTubeDuration) {
        String time = youTubeDuration.substring(2);
        if (youTubeDuration.contains(RadioTimeUnit.DAY.getKey())) {
            time = youTubeDuration.substring(1);
        }
        RadioTimeUnit []timers = {
            RadioTimeUnit.DAY,
            RadioTimeUnit.HOUR,
            RadioTimeUnit.MINUTE,
            RadioTimeUnit.SECOND
        };

        long duration = 0L;
        int index;
        String value;
        try {
            for (RadioTimeUnit timer: timers) {
                index = time.indexOf(timer.getKey());
                if (index != -1) {
                    value = time.substring(0, time.indexOf(timer.getKey()));
                    time = time.substring(value.length() + timer.getKey().length());
                    duration += Integer.parseInt(value) * timer.getPeriod();
                }
            }
        } catch (NumberFormatException exception) {
            throw new RadioException("There is something wrong with the video!");
        }

        return duration * RadioTimeUnit.MILLISECONDS.getPeriod();
    }

//    public LocalDate convertToLocalDate(Date dateToConvert) {
//        return dateToConvert.toInstant()
//                .atZone(ZoneId.systemDefault())
//                .toLocalDate();
//    }
}
