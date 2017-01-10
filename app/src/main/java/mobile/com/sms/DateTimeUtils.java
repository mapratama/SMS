package mobile.com.sms;

import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.Date;


public class DateTimeUtils {

    public static Date fromTimeInMillis(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }

    public static String getRelativeTimeSpanString(Date time) {
        return (String) DateUtils.getRelativeTimeSpanString(time.getTime(), new Date().getTime(), 0);
    }
}
