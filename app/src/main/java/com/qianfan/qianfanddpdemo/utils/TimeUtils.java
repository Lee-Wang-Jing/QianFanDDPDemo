package com.qianfan.qianfanddpdemo.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * TimeUtils
 *
 * @author WangJing on 2016/12/28 0028 09:07
 * @e-mail wangjinggm@gmail.com
 * @see [相关类/方法](可选)
 */

public class TimeUtils {

    private static long lastClickTime;


    /**
     * 将时间戳转为时间字符串
     * <p>格式为pattern</p>
     *
     * @param millis  毫秒时间戳
     * @param pattern 时间格式
     * @return 时间字符串
     */
    public static String millis2String(long millis, String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date(millis));
    }


    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static String getTimestampString(long time) {
        String str = null;
        String language = Locale.getDefault().getLanguage();
        boolean var3 = language.startsWith("zh");
        Date date = new Date(time);
        if (isSameDay(time)) {
            str = "HH:mm";
        } else if (isYesterday(time)) {
            if (!var3) {
                return "Yesterday " + (new SimpleDateFormat("HH:mm", Locale.ENGLISH)).format(date);
            }
            str = "昨天 HH:mm";
        } else if (var3) {
            str = "M月d日 HH:mm";
        } else {
            str = "MMM dd HH:mm";
        }

        return var3 ? (new SimpleDateFormat(str, Locale.CHINESE)).format(date) : (new SimpleDateFormat(str, Locale.ENGLISH)).format(date);
    }

    /**
     * 判断是否为今天(效率比较高)
     *
     * @param time 传入的 时间
     * @return true今天 false不是
     */
    private static boolean isSameDay(long time) {
        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);
        Calendar cal = Calendar.getInstance();
        Date date = new Date(time);
        cal.setTime(date);
        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为昨天
     *
     * @param time 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true昨天 false不是
     */
    private static boolean isYesterday(long time) {
        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = new Date(time);
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取年
     *
     * @return
     */
    public static int getYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }


    /**
     * 毫秒转化时分秒毫秒
     */
    public static String formatTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
//        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if (day > 0) {
            sb.append(day + "天");
        }
        if (hour > 0) {
            sb.append(hour + "小时");
        }
        if (minute > 0) {
            sb.append(minute + "分");
        } else {
            sb.append("0:");
        }
        if (second > 0) {
//            sb.append(second + "秒");
            sb.append(second);
        } else {
            sb.append("0");
        }
//        if (milliSecond > 0) {
//            sb.append(milliSecond + "毫秒");
//        }
        return sb.toString();
    }

    /**
     * 毫秒转化时分秒毫秒
     */
    public static String formatTime1(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long hour = ms / hh;
        Long minute = (ms - hour * hh) / mi;
        Long second = (ms - hour * hh - minute * mi) / ss;

//        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if (hour > 0) {
            sb.append(hour + ":");
        }
        if (minute >= 10) {
            sb.append(minute + ":");
        } else if (minute > 0) {
            sb.append("0" + minute + ":");
        } else {
            sb.append("00:");
        }
        if (second >= 10) {
            sb.append(second);
        } else if (second > 0) {
            sb.append("0" + second);
        } else {
            sb.append("00");
        }
//        if (milliSecond > 0) {
//            sb.append(milliSecond + "毫秒");
//        }
        return sb.toString();
    }


    /**
     * 毫秒转化时分秒毫秒
     */
    public static String formatTime2(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
//        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if (day > 0) {
            sb.append(day + "天");
        }
        if (hour > 0) {
            sb.append(hour + ":");
        }
        if (minute > 0) {
            sb.append(minute + ":");
        } else {
            sb.append("0:");
        }
        if (second > 0) {
//            sb.append(second + "秒");
            if (second <= 9) {
                sb.append("0" + second);
            } else {
                sb.append(second);
            }
        } else {
            sb.append("00");
        }
//        if (milliSecond > 0) {
//            sb.append(milliSecond + "毫秒");
//        }
        return sb.toString();
    }

    /**
     * 毫秒转化时分秒毫秒
     */
    public static String formatTimeExam(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;

        Long minute = ms / mi;
        Long second = (ms - minute * mi) / ss;

        StringBuffer sb = new StringBuffer();
        if (minute > 0) {
            sb.append(minute + ":");
        } else {
            sb.append("00:");
        }
        if (second > 0) {
            if (second > 9) {
                sb.append(second);
            } else {
                sb.append("0" + second);
            }
        } else {
            sb.append("00");
        }
        return sb.toString();
    }

    /**
     * 毫秒转化时分秒毫秒
     */
    public static String formatTimeExam2(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Long minute = ms / mi;

        StringBuffer sb = new StringBuffer();
        if (minute > 0) {
            sb.append("" + minute);
        } else {
            sb.append("1");
        }

        return sb.toString();
    }


    /**
     * 判断时间和当前时间是否超过48小时
     */
    public static boolean isThan48Hour(Long startTime) {

        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        long time = System.currentTimeMillis() - startTime;
        Long day = time / dd;
        Long hour = (time - day * dd) / hh;

        LogUtil.e("startTime", "" + startTime);
        LogUtil.e("currentTimeMillis", "" + System.currentTimeMillis());
        LogUtil.e("isThan48Hour", "" + (day * 24 + hour));
        LogUtil.e("hour", "" + hour);
        LogUtil.e("day", "" + day);
        if (day * 24 + hour > 48) {
            return true;
        }
        return false;
    }
}
