package com.zui.notes.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.text.format.DateFormat;
import com.zui.notes.NotesApplication;
import com.zui.notes.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by huangfei on 2016/11/17.
 */

public class Utils {

    private static String getChineseAmPm(Context context, Calendar calendar) {
        String AmPm;
        int i = calendar.get(Calendar.HOUR_OF_DAY);
        if (((i >= 0) && (i <= 5)) || (i == 24)) {
            AmPm = context.getResources().getString(R.string.day_dawn);
        } else if ((i >= 6) && (i <= 11)) {
            AmPm = context.getResources().getString(R.string.day_morning);
        } else if ((i >= 12) && (i <= 12)) {
            AmPm = context.getResources().getString(R.string.day_midnoon);
        } else if ((i >= 13) && (i <= 18)) {
            AmPm = context.getResources().getString(R.string.day_afternoon);
        } else if ((i >= 19) && (i <= 23)) {
            AmPm = context.getResources().getString(R.string.day_evening);
        } else {
            AmPm = "";
        }
        return AmPm;
    }


    private static String getTimeAmPm(Context context, long TimeInMillis) {
        String timeAmPm;
        if (DateFormat.is24HourFormat(context)) {
            timeAmPm = "";
        } else {
            Calendar localCalendar = Calendar.getInstance();
            localCalendar.setTimeInMillis(TimeInMillis);
            int r = localCalendar.get(Calendar.AM_PM);
            if (isChinese()) {
                timeAmPm = getChineseAmPm(context, localCalendar);
            } else if (r == Calendar.AM) {
                timeAmPm = "AM";
            } else {
                timeAmPm = "PM";
            }
        }
        return timeAmPm;
    }

    private static boolean isChinese() {
        boolean bool = false;
        String str = Resources.getSystem().getConfiguration().locale.toString();
        if (("zh_CN".equals(str)) || ("zh_TW".equals(str)) || ("zh_HK".equals(str))) {
            bool = true;
        }
        return bool;
    }


    public static String getHourMinute(long paramLong) {
        if(isSystem24Hour()){
        SimpleDateFormat localSimpleDateFormat;
       localSimpleDateFormat = new SimpleDateFormat("HH:mm",Locale.getDefault());
        return localSimpleDateFormat.format(new Date(paramLong));}
        else {
            return getHourMinuteIn12Hour(paramLong);
        }
    }

    private static String getHourMinuteIn12Hour(long paramLong) {
        String yearMonthDayHourMinuteIn12Hour;
        SimpleDateFormat simpleDateFormat;
        String str = getTimeAmPm(NotesApplication.getInstance(), paramLong);
        if (isChinese()) {
            simpleDateFormat = new SimpleDateFormat(str + " hh:mm",Locale.getDefault());
            yearMonthDayHourMinuteIn12Hour = simpleDateFormat.format(new Date(paramLong));
        } else {
            simpleDateFormat = new SimpleDateFormat(" hh:mm",Locale.getDefault());
            yearMonthDayHourMinuteIn12Hour = simpleDateFormat.format(new Date(paramLong)) + " " + str;
        }
        return yearMonthDayHourMinuteIn12Hour;
    }

    private static boolean isSystem24Hour() {
        return DateFormat.is24HourFormat(NotesApplication.getInstance());
    }

    public static String getYearMonthDay(long paramLong)
    {
        SimpleDateFormat localSimpleDateFormat;
        if (isChinese()) {
            localSimpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日",Locale.getDefault());}
        else{
            localSimpleDateFormat = new SimpleDateFormat(" MMM d, yyyy", Locale.getDefault());
        }
        return localSimpleDateFormat.format(new Date(paramLong));
    }

    public static String getYear(long paramLong)
    {
        SimpleDateFormat localSimpleDateFormat;
        if (isChinese()) {
            localSimpleDateFormat = new SimpleDateFormat("yyyy年");
        }else{
            localSimpleDateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        }
        return localSimpleDateFormat.format(new Date(paramLong));
    }

    public static String getMonthDay(long paramLong)
    {
        SimpleDateFormat localSimpleDateFormat;
        if (isChinese()) {
            localSimpleDateFormat = new SimpleDateFormat("MM月dd日",Locale.getDefault());
        }else{
            localSimpleDateFormat = new SimpleDateFormat("MMM d", Locale.getDefault());
        }
        return localSimpleDateFormat.format(new Date(paramLong));
    }

    public static PackageInfo getPackageInfo(Context context, String packageName){
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName,0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            packageInfo = null;
        }
        return packageInfo;
    }
}
