package com.zf.qqcy.dataService.customer.service;

import com.cea.core.mapper.CustomConvertUtils;
import com.cea.core.modules.common.DateUtil;
import com.cea.core.modules.persistence.PageUtils;
import com.cea.identity.remote.dto.DictionaryDto;
import com.zf.qqcy.dataService.common.constants.Constants;
import com.zf.qqcy.dataService.customer.remote.dto.CustomerPageResultDto;
import com.zf.qqcy.dataService.customer.service.imp.CustomerPageImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerUtils {

    private static final Logger logger = LoggerFactory.getLogger(CustomerUtils.class);
    private static final String NONE = "-";
    private static final String ZERO = "0";
    private static final String ONE = "1";

    /**
     * 根据日期时间返回日期时间的起始和结束时间<br/>
     * 参数"2015-06-17"
     * 返回Date[0]("2015-06-17 00:00:00"),Date[1]("2015-06-17 23:59:59")<br/>
     * <!--参数"201506"
     * 返回Date[0]("2015-06-01 00:00:00"),Date[1]("2015-06-30 23:59:59")-->
     *
     * @param startDateString
     * @param endDateString
     * @return
     */
    public static Date[] getStartEndTimeDate(String startDateString, String endDateString) {
        Date[] date = null;
        if (null != startDateString && !"".equals(startDateString) && startDateString.length() == 10
                && null != endDateString && !"".equals(endDateString) && endDateString.length() == 10) {
            // yyyy-MM-dd格式
            date = new Date[2];
            try {
                date[0] = DateUtil.getDate(startDateString + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
                date[1] = DateUtil.getDate(endDateString + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
            } catch (ParseException e) {
            }
        }
        return date;
    }

    public static Date[] getStartEndTimeDate(Date date) {
        Date[] dates = null;
        if (null != date) {
            String dateString = new SimpleDateFormat("yyyy-MM-dd").format(date);
            // yyyy-MM-dd格式
            dates = new Date[2];
            try {
                dates[0] = DateUtil.getDate(dateString + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
                dates[1] = DateUtil.getDate(dateString + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
            } catch (ParseException e) {
            }
        }
        return dates;
    }

    /**
     * 获取当月的 天数
     */
    public static int getCurrentMonthDay() {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 获取昨天String
     *
     * @return
     */
    public static String getYesterdayDateString(String pattern) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday = new SimpleDateFormat(pattern).format(cal.getTime());
        return yesterday;
    }

    /**
     * 获取上个月的期数
     *
     * @return
     */
    public static String getLastMonthPeriod() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return new SimpleDateFormat("yyyyMM").format(calendar.getTime());
    }

    /**
     * 获取明天
     *
     * @return
     */
    public static Date getTomorrowDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, +1);
        return cal.getTime();
    }

    /**
     * 获取昨天
     *
     * @return
     */
    public static Date getYesterdayDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public static Date getYesterday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    /**
     * 判断date3在日期date1和date2之间
     *
     * @param date1
     * @param date2
     * @param date3
     * @return
     */
    public static boolean hasDayInDates(Date date1, Date date2, Date date3) {
        long t = date3.getTime();
        return date1.getTime() < t && date2.getTime() > t;
    }

    public static int getMonthLastDay(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        cal.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
        int maxDate = cal.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 判断两个日期间隔天数(在基准日期之前为正数，在基准日期之后为负数)
     *
     * @param date1 基准日期
     * @param date2 比较日期
     * @return
     */
    public static int compareIntervalDate(Date date1, Date date2) {
        // 将转换的两个时间对象转换成Calendard对象
        Calendar can1 = Calendar.getInstance();
        can1.setTime(date1);
        long t = can1.getTimeInMillis();
        Calendar can2 = Calendar.getInstance();
        can2.setTime(date2);
        long t2 = can2.getTimeInMillis();
        int abs = 1;
        if (t < t2) {
            abs = -1;
        }
        // 拿出两个年份
        int year1 = can1.get(Calendar.YEAR);
        int year2 = can2.get(Calendar.YEAR);
        // 天数
        int days = 0;
        Calendar can = null;
        // 如果can1 < can2
        // 减去小的时间在这一年已经过了的天数
        // 加上大的时间已过的天数
        if (can1.before(can2)) {
            days -= can1.get(Calendar.DAY_OF_YEAR);
            days += can2.get(Calendar.DAY_OF_YEAR);
            can = can1;
        } else {
            days -= can2.get(Calendar.DAY_OF_YEAR);
            days += can1.get(Calendar.DAY_OF_YEAR);
            can = can2;
        }
        for (int i = 0; i < Math.abs(year2 - year1); i++) {
            // 获取小的时间当前年的总天数
            days += can.getActualMaximum(Calendar.DAY_OF_YEAR);
            // 再计算下一年。
            can.add(Calendar.YEAR, 1);
        }
        int d = abs * days;
        String d1 = DateUtil.getString(date1, "yyyy-MM-dd");
        String d2 = DateUtil.getString(date2, "yyyy-MM-dd");
        // logger.debug("日期比较，" + d1 + " 与 " + d2 + " 天数差：" + d);
        return d;
    }

    /**
     * 获取客户接待客户数量等级
     *
     * @param totalCustomerObject
     * @return
     */
    public static String getCustomerCountRank(Object totalCustomerObject) {
        if (null != totalCustomerObject) {
            if (!"-".equals(totalCustomerObject)) {
                int totalCustomer = Integer.parseInt(String.valueOf(totalCustomerObject));
                if (totalCustomer <= 5) {
                    return Constants.KeyValueEnum.CUSTOMER_COUNT_RANK_INTRODUCTION.getValue();
                } else if (6 <= totalCustomer && totalCustomer <= 15) {
                    return Constants.KeyValueEnum.CUSTOMER_COUNT_RANK_CLASS_ONE.getValue();
                } else if (16 <= totalCustomer && totalCustomer <= 25) {
                    return Constants.KeyValueEnum.CUSTOMER_COUNT_RANK_CLASS_TWO.getValue();
                } else if (26 <= totalCustomer && totalCustomer <= 40) {
                    return Constants.KeyValueEnum.CUSTOMER_COUNT_RANK_CLASS_THREE.getValue();
                } else if (41 <= totalCustomer && totalCustomer <= 45) {
                    return Constants.KeyValueEnum.CUSTOMER_COUNT_RANK_CLASS_FOUR.getValue();
                } else if (totalCustomer >= 46) {
                    return Constants.KeyValueEnum.CUSTOMER_COUNT_RANK_CLASS_FIVE.getValue();
                }
            }
        }
        return "-";
    }

    /**
     * 获取客户成交数量等级
     *
     * @param totalVictoryObject
     * @return
     */
    public static String getCustomerVolumeRank(Object totalVictoryObject) {
        if (null != totalVictoryObject) {
            if (!"-".equals(totalVictoryObject)) {
                int totalVictory = Integer.parseInt(String.valueOf(totalVictoryObject));
                if (totalVictory <= 3) {
                    return Constants.KeyValueEnum.CUSTOMER_VOLUME_RANK_CLASS_ONE.getValue();
                } else if (4 <= totalVictory && totalVictory <= 7) {
                    return Constants.KeyValueEnum.CUSTOMER_VOLUME_RANK_CLASS_TWO.getValue();
                } else if (8 <= totalVictory && totalVictory <= 12) {
                    return Constants.KeyValueEnum.CUSTOMER_VOLUME_RANK_CLASS_THREE.getValue();
                } else if (13 <= totalVictory && totalVictory <= 17) {
                    return Constants.KeyValueEnum.CUSTOMER_VOLUME_RANK_CLASS_FOUR.getValue();
                } else if (totalVictory >= 18) {
                    return Constants.KeyValueEnum.CUSTOMER_VOLUME_RANK_CLASS_FIVE.getValue();
                }
            }
        }
        return "-";
    }


    public static Date[] getCustomerPersonMessageDates() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1); // 得到昨天
        Date theDayBeforeYesterday = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -7); // 得到昨天的7天前
        Date sevenDaysAgo = cal.getTime();
        return new Date[]{sevenDaysAgo, theDayBeforeYesterday};
    }

    public static Date[] getCustomerBusinessMessageDates() {
        Date[] dates = new Date[2];
        List<Date[]> datesList = getCustomerStatisticsMonthList(new Date()); // 获取2015年时间List
        if (null != datesList && !datesList.isEmpty()) {
            dates[0] = datesList.get(0)[0]; // 起始日期取第一个List的开始日期
            dates[1] = datesList.get(datesList.size() - 1)[1]; // 结束日期取最后一个List的结束日期
        }
        return dates;
    }


    /**
     * 按月客户统计消息
     * TODO 2016年底过期
     *
     * @param date
     * @return
     */
    public static List<Date[]> getCustomerStatisticsMonthList(Date date) {
        List<Date[]> list = new ArrayList<Date[]>();
        try {
            Date[] june = new Date[]{DateUtil.getDate("2015-06-01 00:00:00", "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.getDate("2015-06-30 23:59:59", "yyyy-MM-dd HH:mm:ss")};
            Date[] july = new Date[]{DateUtil.getDate("2015-07-01 00:00:00", "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.getDate("2015-07-31 23:59:59", "yyyy-MM-dd HH:mm:ss")};
            Date[] august = new Date[]{DateUtil.getDate("2015-08-01 00:00:00", "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.getDate("2015-08-31 23:59:59", "yyyy-MM-dd HH:mm:ss")};
            Date[] september = new Date[]{DateUtil.getDate("2015-09-01 00:00:00", "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.getDate("2015-09-30 23:59:59", "yyyy-MM-dd HH:mm:ss")};
            Date[] october = new Date[]{DateUtil.getDate("2015-10-01 00:00:00", "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.getDate("2015-10-31 23:59:59", "yyyy-MM-dd HH:mm:ss")};
            Date[] november = new Date[]{DateUtil.getDate("2015-11-01 00:00:00", "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.getDate("2015-11-30 23:59:59", "yyyy-MM-dd HH:mm:ss")};
            Date[] december = new Date[]{DateUtil.getDate("2015-12-01 00:00:00", "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.getDate("2015-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss")};
            Date[] jan2016 = new Date[]{DateUtil.getDate("2016-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.getDate("2016-01-31 23:59:59", "yyyy-MM-dd HH:mm:ss")};
            Date[] feb2016 = new Date[]{DateUtil.getDate("2016-02-01 00:00:00", "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.getDate("2016-02-29 23:59:59", "yyyy-MM-dd HH:mm:ss")};
            Date[] mar2016 = new Date[]{DateUtil.getDate("2016-03-01 00:00:00", "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.getDate("2016-03-31 23:59:59", "yyyy-MM-dd HH:mm:ss")};
            Date[] apr2016 = new Date[]{DateUtil.getDate("2016-04-01 00:00:00", "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.getDate("2016-04-30 23:59:59", "yyyy-MM-dd HH:mm:ss")};
            Date[] may2016 = new Date[]{DateUtil.getDate("2016-05-01 00:00:00", "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.getDate("2016-05-31 23:59:59", "yyyy-MM-dd HH:mm:ss")};
            Date[] jun2016 = new Date[]{DateUtil.getDate("2016-06-01 00:00:00", "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.getDate("2016-06-31 23:59:59", "yyyy-MM-dd HH:mm:ss")};
            Date[] jul2016 = new Date[]{DateUtil.getDate("2016-07-01 00:00:00", "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.getDate("2016-07-31 23:59:59", "yyyy-MM-dd HH:mm:ss")};
            Date[] aug2016 = new Date[]{DateUtil.getDate("2016-08-01 00:00:00", "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.getDate("2016-08-31 23:59:59", "yyyy-MM-dd HH:mm:ss")};
            Date[] sept2016 = new Date[]{DateUtil.getDate("2016-09-01 00:00:00", "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.getDate("2016-09-30 23:59:59", "yyyy-MM-dd HH:mm:ss")};
            Date[] oct2016 = new Date[]{DateUtil.getDate("2016-10-01 00:00:00", "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.getDate("2016-10-31 23:59:59", "yyyy-MM-dd HH:mm:ss")};
            Date[] nov2016 = new Date[]{DateUtil.getDate("2016-11-01 00:00:00", "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.getDate("2016-11-30 23:59:59", "yyyy-MM-dd HH:mm:ss")};
            Date[] dec2016 = new Date[]{DateUtil.getDate("2016-12-01 00:00:00", "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.getDate("2016-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss")};
            if (CustomerUtils.compareIntervalDate(date, june[0]) >= 0) {
                list.add(june);
            }
            if (CustomerUtils.compareIntervalDate(date, july[0]) >= 0) {
                list.add(july);
            }
            if (CustomerUtils.compareIntervalDate(date, august[0]) >= 0) {
                list.add(august);
            }
            if (CustomerUtils.compareIntervalDate(date, september[0]) >= 0) {
                list.add(september);
            }
            if (CustomerUtils.compareIntervalDate(date, october[0]) >= 0) {
                list.add(october);
            }
            if (CustomerUtils.compareIntervalDate(date, november[0]) >= 0) {
                list.add(november);
            }
            if (CustomerUtils.compareIntervalDate(date, december[0]) >= 0) {
                list.add(december);
            }
            if (CustomerUtils.compareIntervalDate(date, jan2016[0]) >= 0) {
                list.add(jan2016);
            }
            if (CustomerUtils.compareIntervalDate(date, feb2016[0]) >= 0) {
                list.add(feb2016);
            }
            if (CustomerUtils.compareIntervalDate(date, mar2016[0]) >= 0) {
                list.add(mar2016);
            }
            if (CustomerUtils.compareIntervalDate(date, apr2016[0]) >= 0) {
                list.add(apr2016);
            }
            if (CustomerUtils.compareIntervalDate(date, may2016[0]) >= 0) {
                list.add(may2016);
            }
            if (CustomerUtils.compareIntervalDate(date, jun2016[0]) >= 0) {
                list.add(jun2016);
            }
            if (CustomerUtils.compareIntervalDate(date, jul2016[0]) >= 0) {
                list.add(jul2016);
            }
            if (CustomerUtils.compareIntervalDate(date, aug2016[0]) >= 0) {
                list.add(aug2016);
            }
            if (CustomerUtils.compareIntervalDate(date, sept2016[0]) >= 0) {
                list.add(sept2016);
            }
            if (CustomerUtils.compareIntervalDate(date, oct2016[0]) >= 0) {
                list.add(oct2016);
            }
            if (CustomerUtils.compareIntervalDate(date, nov2016[0]) >= 0) {
                list.add(nov2016);
            }
            if (CustomerUtils.compareIntervalDate(date, dec2016[0]) >= 0) {
                list.add(dec2016);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取从2015-06-01到参数日期的每天起始时间List
     *
     * @param endDate
     * @return
     */
    public static List<Date[]> getCustomerStatisticsDaysList(Date endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = null;
        try {
            beginDate = sdf.parse("2015-06-01");
        } catch (ParseException e) {
        }
        List<Date[]> lDate = new ArrayList<Date[]>();
        lDate.add(getStartEndTimeDate(beginDate));// 把开始时间加入集合
        Calendar cal = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        cal.setTime(beginDate);
        boolean bContinue = true;
        while (bContinue) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cal.add(Calendar.DAY_OF_MONTH, 1);
            // 测试此日期是否在指定日期之后
            if (endDate.after(cal.getTime())) {
                lDate.add(getStartEndTimeDate(cal.getTime()));
            } else {
                break;
            }
        }
        // lDate.add(getStartEndTimeDate(endDate));// 把结束时间加入集合
        return lDate;
    }

    /**
     * 如果参数一个月的第一天和最后一天，返回上个月的第一天和最后一天
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static Date[] getLastMonthStartEndDays(Date startDate, Date endDate) {
        if (null != startDate && null != endDate) {
            Calendar startCalendar = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();
            startCalendar.setTime(startDate);
            endCalendar.setTime(endDate);
            int startYear = startCalendar.get(Calendar.YEAR);
            int endYear = endCalendar.get(Calendar.YEAR);
            // 判断第一个日期的年份和第二个日期年份相等
            if (startYear == endYear) {
                // 得到第一个日期的月份
                int startMonth = startCalendar.get(Calendar.MONTH);
                int endMonth = endCalendar.get(Calendar.MONTH);
                // 判断第一个日期的月份和第二个日期月份相等
                if (startMonth == endMonth) {
                    int startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
                    // 判断第一个日期是1号
                    if (startDay == 1) {
                        int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
                        int thisMonthEndDay = endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        // 判断第二个日期是这个月最后一天
                        if (endDay == thisMonthEndDay) {
                            startCalendar.add(Calendar.MONTH, -1);
                            startCalendar.set(Calendar.HOUR_OF_DAY, 0);
                            startCalendar.set(Calendar.MINUTE, 0);
                            startCalendar.set(Calendar.SECOND, 0);
                            startCalendar.set(Calendar.MILLISECOND, 0);
                            endCalendar.add(Calendar.MONTH, -1);
                            endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                            endCalendar.set(Calendar.HOUR_OF_DAY, 23);
                            endCalendar.set(Calendar.MINUTE, 59);
                            endCalendar.set(Calendar.SECOND, 59);
                            endCalendar.set(Calendar.MILLISECOND, 999);
                            Date[] dates = new Date[2];
                            dates[0] = startCalendar.getTime();
                            dates[1] = endCalendar.getTime();
                            return dates;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 返回本月第一天和最后一天
     *
     * @return
     */
    public static Date[] getCurrentMonthDates() {
        Date[] dates = new Date[2];
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.DAY_OF_MONTH, 1);
        dates[0] = cal.getTime();
        cal.roll(Calendar.DAY_OF_MONTH, -1);
        dates[1] = cal.getTime();
        return dates;
    }

    /**
     * 返回上个月第一天和最后一天
     *
     * @return
     */
    public static Date[] getLastMonthDates() {
        Date[] dates = new Date[2];
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        dates[0] = calendar.getTime();
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        dates[1] = calendar.getTime();
        return dates;
    }

    public static Date getStartDate(String dateString) {
        try {
            return DateUtil.getDate(dateString + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
        }
        return null;
    }

    public static Date getEndDate(String dateString) {
        try {
            return DateUtil.getDate(dateString + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
        }
        return null;
    }

    public static Map<String, String> getDictionaryMap(List<DictionaryDto> list) {
        Map<String, String> map = new HashMap<String, String>();
        list = CustomConvertUtils.covert(list, DictionaryDto.class);
        if (null != list && !list.isEmpty()) {
            for (DictionaryDto dictionaryDto : list) {
                map.put(dictionaryDto.getId(), dictionaryDto.getName());
            }
        }
        return map;
    }

    public static DictionaryDto getDictionaryDto(String id, String name) {
        DictionaryDto d = new DictionaryDto();
        d.setId(id);
        d.setName(name);
        return d;
    }

    /**
     * 将Date转换为201506字符串
     *
     * @param date
     * @param split 年月之间是否加分隔符-
     * @return
     */
    public static String getPeriodByDate(Date date, boolean split) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        StringBuffer buffer = new StringBuffer(String.valueOf(cal.get(Calendar.YEAR)));
        if (split) {
            buffer.append("-");
        }
        int month = cal.get(Calendar.MONTH) + 1;
        String m = String.valueOf(month);
        if (m.length() == 1) {
            buffer.append("0");
        }
        buffer.append(m);
        return buffer.toString();
    }

    /**
     * @param pattern
     * @return
     */
    public static String getDateStringByPattern(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 根据期数获取本月日期起始
     *
     * @param period 201507
     * @return Date[]
     */
    public static Date[] getMonthStartEndDatesByPeriod(String period) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyyMM").parse(period);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        startCalendar.setTime(date);
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);
        endCalendar.setTime(date);
        endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);
        endCalendar.set(Calendar.MILLISECOND, 999);
        Date[] dates = new Date[2];
        dates[0] = startCalendar.getTime();
        dates[1] = endCalendar.getTime();
        return dates;
    }

    private static Date getBeforeDate(int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -i);
        return calendar.getTime();
    }

    /**
     * 客户级别统计时间
     *
     * @return
     */
    public static Date[][] getCustomerLevelDates() {
        Date dates[][] = new Date[4][2];
        for (int i = 0; i < dates.length; i++) {
            Date[] datess = new Date[2];
            Date[] tmpDate = new Date[2];
            switch (i) {
                case 0:
                    tmpDate = getStartEndTimeDate(getBeforeDate(2));
                    datess[0] = tmpDate[0];
                    tmpDate = getStartEndTimeDate(new Date());
                    datess[1] = tmpDate[1];
                    break;
                case 1:
                    tmpDate = getStartEndTimeDate(getBeforeDate(6));
                    datess[0] = tmpDate[0];
                    tmpDate = getStartEndTimeDate(getBeforeDate(3));
                    datess[1] = tmpDate[1];
                    break;
                case 2:
                    tmpDate = getStartEndTimeDate(getBeforeDate(14));
                    datess[0] = tmpDate[0];
                    tmpDate = getStartEndTimeDate(getBeforeDate(7));
                    datess[1] = tmpDate[1];
                    break;
                case 3:
                    tmpDate = getStartEndTimeDate(getBeforeDate(15));
                    datess[0] = tmpDate[0];
                    break;
                default:
                    break;
            }
            dates[i] = datess;
        }
        return dates;
    }

    public static <T> CustomerPage<T> pageResultToPage(CustomerPageResultDto<T> pageResult) {
        PageRequest pageable = PageUtils.buildPageRequest(pageResult.getFilter().getPage(), pageResult.getFilter().getSize(), pageResult.getFilter().getSortField(), pageResult.getFilter().getSortDir());
        Object result = pageResult.getResult() == null?new ArrayList():pageResult.getResult();
        CustomerPageImp<T> page = new CustomerPageImp((List)result, pageable, pageResult.getCount(), pageResult.getDataMap());
        return page;
    }

    /**
     * 除法，百分比
     *
     * @param num
     * @param divisor
     * @return
     */
    public static String divisionPercentData(Object num, Object divisor) {
        if (null == num || NONE.equals(num) || null == divisor || NONE.equals(divisor)
                || ZERO.equals(String.valueOf(divisor))) {
            return NONE;
        }
        BigDecimal data = new BigDecimal(String.valueOf(num));
        BigDecimal divisorData = new BigDecimal(String.valueOf(divisor));
        BigDecimal s = data.divide(divisorData, 2, BigDecimal.ROUND_HALF_EVEN);
        NumberFormat percent = NumberFormat.getPercentInstance();
        percent.setMaximumFractionDigits(2);
        String ss = percent.format(s.doubleValue());
        logger.info("divisionPercentData===> num=" + num + ", divisor=" + divisor + ", rate=" + ss);
        return ss;
    }

    /**
     * 除法，小数
     *
     * @param num
     * @param divisor
     * @return
     */
    public static String divisionDecimalData(Object num, Object divisor) {
        if (null == num || NONE.equals(num) || null == divisor || NONE.equals(divisor)
                || ZERO.equals(String.valueOf(divisor))) {
            return NONE;
        }
        BigDecimal data = new BigDecimal(String.valueOf(num));
        BigDecimal divisorData = new BigDecimal(String.valueOf(divisor));
        BigDecimal s = data.divide(divisorData, 2, BigDecimal.ROUND_HALF_EVEN);
        logger.info("divisionDecimalData===> num=" + num + ", divisor=" + divisor + ", rate=" + s.toString());
        return s.toString();
    }

    /**
     * 除法，小数乘以100
     *
     * @param num
     * @param divisor
     * @return
     */
    public static double divisionDecimalOneHundredData(Object num, Object divisor) {
        if (null == num || NONE.equals(num) || null == divisor || NONE.equals(divisor)
                || ZERO.equals(String.valueOf(divisor))) {
            return 0d;
        }
        BigDecimal data = new BigDecimal(String.valueOf(num));
        BigDecimal divisorData = new BigDecimal(String.valueOf(divisor));
        BigDecimal s = data.divide(divisorData, 4, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(100),
                new MathContext(4));
        logger.info("divisionDecimalData===> num=" + num + ", divisor=" + divisor + ", rate=" + s.doubleValue());
        return s.doubleValue();
    }

    /**
     * 数据增加
     *
     * @param s
     * @return
     */
    public static int getTotalData(int total, String s) {
        if (null == s) {
            return total;
        }
        if (ZERO.equals(s) || NONE.equals(s)) {
            return total;
        } else {
            int i = Integer.parseInt(s);
            return total + i;
        }
    }

    /**
     * 数据+1
     *
     * @param o
     * @return
     */
    public static Integer increaseOne(Object o) {
        if (null == o) {
            return null;
        }
        Integer i = Integer.parseInt(String.valueOf(o));
        return i + 1;
    }

    public static String increaseOne(String s) {
        if (null == s) {
            return null;
        }
        if (ZERO.equals(s) || NONE.equals(s)) {
            return ONE;
        } else {
            int i = Integer.parseInt(s);
            return String.valueOf(i + 1);
        }
    }

    public static void main(String[] args) {
//        System.out.println(CustomerUtils.getMonthStartEndDatesByPeriod("201505"));
        System.out.println(CustomerUtils.getCustomerStatisticsMonthList(new Date()));
    }
}
