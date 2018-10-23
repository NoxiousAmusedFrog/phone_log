package com.jiajiabingcheng.phonelog;

import java.util.HashMap;

class CallRecord {

    CallRecord() {}

    String formattedNumber;
    String number;
    String callType;
    String cashed_name;
    int dateYear;
    int dateMonth;
    int dateDay;
    int dateHour;
    int dateMinute;
    int dateSecond;
    long duration;
    String cashed_number_type;
    String phone_account_id;
    String cashed_photo_uri;

    HashMap<String, Object> toMap() {
        HashMap<String, Object> recordMap = new HashMap<>();
        recordMap.put("formattedNumber", formattedNumber);
        recordMap.put("number", number);
        recordMap.put("callType", callType);
        recordMap.put("cashed_name", cashed_name);
        recordMap.put("dateYear", dateYear);
        recordMap.put("dateMonth", dateMonth);
        recordMap.put("dateDay", dateDay);
        recordMap.put("dateHour", dateHour);
        recordMap.put("dateMinute", dateMinute);
        recordMap.put("dateSecond", dateSecond);
        recordMap.put("duration", duration);
        recordMap.put("cashed_number_type", cashed_number_type);
        recordMap.put("phone_account_id", phone_account_id);
        recordMap.put("cashed_photo_uri", cashed_photo_uri);

        return recordMap;
    }
}
