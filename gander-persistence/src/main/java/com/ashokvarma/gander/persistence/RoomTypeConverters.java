package com.ashokvarma.gander.persistence;


import androidx.room.TypeConverter;

import com.ashokvarma.gander.internal.data.HttpHeader;
import com.ashokvarma.gander.internal.support.TextUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 03/06/18
 */
class RoomTypeConverters {
    @TypeConverter
    public static Date fromLongToDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long fromDateToLong(Date value) {
        return value == null ? null : value.getTime();
    }

    private static final String NAME_VALUE_SEPARATOR = "__:_:__";
    private static final String LIST_SEPARATOR = "__,_,__";

    @TypeConverter
    public static List<HttpHeader> fromStringToHeaderList(String value) {
        if (value == null || TextUtil.isNullOrWhiteSpace(value)) {
            return new ArrayList<>();
        }

        String[] nameValuePairArray = value.split(LIST_SEPARATOR);
        List<HttpHeader> list = new ArrayList<>(nameValuePairArray.length);

        for (String nameValuePair : nameValuePairArray) {
            String[] nameValue = nameValuePair.split(NAME_VALUE_SEPARATOR);
            if (nameValue.length == 2) {
                list.add(new HttpHeader(nameValue[0], nameValue[1]));
            } else if (nameValue.length == 1) {
                list.add(new HttpHeader(nameValue[0], ""));
            }
        }
        return list;
    }

    @TypeConverter
    public static String fromHeaderListToString(List<HttpHeader> value) {
        if (value == null || value.size() == 0) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        boolean isFirst = true;
        for (HttpHeader header : value) {
            if (!isFirst) {
                stringBuilder.append(LIST_SEPARATOR);
            }
            stringBuilder
                    .append(header.getName())
                    .append(NAME_VALUE_SEPARATOR)
                    .append(header.getValue());
            isFirst = false;
        }
        return stringBuilder.toString();
    }
}
