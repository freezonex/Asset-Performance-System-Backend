package com.freezonex.aps.modules.asset.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author penglifr
 * @since 2024/05/10 17:19
 */
public class DateUtils {
    public static List<Date> findDates(Date dBegin, Date dEnd) {
        try {
            List<Date> allDate = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dBegin = sdf.parse(sdf.format(dBegin));
            dEnd = sdf.parse(sdf.format(dEnd));

            allDate.add(dBegin);
            Calendar calBegin = Calendar.getInstance();
            // 使用给定的 Date 设置此 Calendar 的时间
            calBegin.setTime(dBegin);

            // 测试此日期是否在指定日期之后
            while (dEnd.after(calBegin.getTime())) {
                calBegin.add(Calendar.DAY_OF_MONTH, 1);
                allDate.add(calBegin.getTime());
            }
            return allDate;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
