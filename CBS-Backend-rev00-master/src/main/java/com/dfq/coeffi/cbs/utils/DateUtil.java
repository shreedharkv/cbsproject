package com.dfq.coeffi.cbs.utils;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

public class DateUtil implements Serializable {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private static final long serialVersionUID = -3224601291474978013L;

    public static Date addMonthsToDate(int month) {
        Calendar calender = Calendar.getInstance();
        calender.add(Calendar.MONTH, month);
        Date dateAsObjectAfterMonth = calender.getTime();
        return dateAsObjectAfterMonth;
    }

    public static Date getTodayDate() {

        Date currentDate = null;
        String dateStr = "";
        String day = "";
        String month = "";
        int year = 0;

        Calendar cal = new GregorianCalendar();

        if (cal != null) {
            int dd = cal.get(Calendar.DAY_OF_MONTH);
            day = getDoubleDigits(dd);

            int mm = cal.get(Calendar.MONTH) + 1;
            month = getDoubleDigits(mm);

            year = cal.get(Calendar.YEAR);
        }

        dateStr = day + "/" + month + "/" + year;

        currentDate = convertToDate(dateStr);

        return currentDate;
    }

    private static String getDoubleDigits(int inputValue) {
        String returnValue = "";

        if (inputValue < 10) {
            returnValue = "0" + inputValue;
        } else {
            returnValue = "" + inputValue;
        }
        return returnValue;
    }

    public static Date convertToDate(String date) {
        Date convertedDate = null;
        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException pe) {
        }
        return convertedDate;
    }

    public static Date convertDateToFormat(Date date) {
        try {
            date = dateFormat.parse(dateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getYesterdayDate() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public static String getDay(Date date) {
        return new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime());
    }

    public static String convertToDateString(Date date) {
        String convertedDate = null;

        if (date != null) {
            convertedDate = dateFormat.format(date);
        } else {
        }

        return convertedDate;
    }

    public static int calculateAge(Date birthDate, Date currentDate) {
        LocalDate birthLocal = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentLocalDate = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthLocal, currentLocalDate).getYears();
        } else {
            return 0;
        }
    }

    public static int getCurrentYear() {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        return year;
    }

    public static String getCurrentMonth() {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
        String monthName = monthFormat.format(date);
        return monthName;
    }

    public static Date addYearsToDate(int year) {
        Calendar calender = Calendar.getInstance();
        Long y = new Long(year);
        int inputYear = y.intValue();
        calender.add(Calendar.YEAR, inputYear);
        Date dateAsObjectAfterYear = calender.getTime();
        return dateAsObjectAfterYear;
    }

    public static String getTodayDateAndTime() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String strDate = formatter.format(date);
        return strDate;
    }

    public static int calculateYearsBetweenDate(Date fromDate, Date currentDate) {
        LocalDate birthLocal = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentLocalDate = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if ((fromDate != null) && (currentDate != null)) {
            return Period.between(birthLocal, currentLocalDate).getYears();
        } else {
            return 0;
        }
    }

    public static Date addMonthsToGivenDate(Date inputDate, int month) {
        Calendar calender = Calendar.getInstance();
        calender.setTime(inputDate);
        month = month - 1;
        calender.add(Calendar.MONTH, month);
        Date dateAsObjectAfterMonth = calender.getTime();
        return dateAsObjectAfterMonth;
    }

    public static double calculateDaysBetweenDate(Date fromDate, Date currentDate) {
        LocalDate birthLocal = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentLocalDate = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if ((fromDate != null) && (currentDate != null)) {
            return Period.between(birthLocal, currentLocalDate).getDays();
        } else {
            return 0;
        }
    }

    public static String getMonthName(Date inputDate) {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
        String monthName = monthFormat.format(inputDate);
        return monthName;
    }

    public static Date getFirstDateOfCurrentMonth(Date date) {

        Date monthM = date;

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);

        int day = 1;
        c.set(year, month, day);
        int numOfDaysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        return c.getTime();
    }

    public static Date getLastDateOfCurrentMonth(Date date) {

        Date monthM = date;

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);

        int day = 1;
        c.set(year, month, day);
        int numOfDaysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.add(Calendar.DAY_OF_MONTH, numOfDaysInMonth - 1);
        return c.getTime();

    }

    public static Date getSubtractedDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.getTodayDate());
        calendar.add(Calendar.DATE, -days);
        Date subtractedDate = calendar.getTime();

        return subtractedDate;
    }

    public static BigInteger convertToAscii(String s){
        StringBuilder stringBuilder = new StringBuilder();
        s = s.toUpperCase();
        for (char c : s.toCharArray()) {
            stringBuilder.append((int) c);
        }
        BigInteger asciiNumber = new BigInteger(stringBuilder.toString());
        return asciiNumber;
    }

    public static int calculateMonthsBetweenDate(Date fromDate, Date currentDate) {
        LocalDate birthLocal = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentLocalDate = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if ((fromDate != null) && (currentDate != null)) {
            return Period.between(birthLocal, currentLocalDate).getMonths();
        } else {
            return 0;
        }
    }

}