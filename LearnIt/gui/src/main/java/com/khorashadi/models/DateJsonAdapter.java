package com.khorashadi.models;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.time.LocalDateTime;

public class DateJsonAdapter {
    @ToJson
    String toJson(LocalDateTime time) {
        return time.toString();
    }

    @FromJson
    LocalDateTime fromJson(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return LocalDateTime.now();
        }
        return LocalDateTime.parse(dateTime);
    }
}
