package com.elijahbosley.textclockwidget;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Method to get the time and convert it into text
 * Created by elibosley on 7/7/16.
 */
public class TimeString {
    private Calendar calendar;
    private String[] textWords; // idea borrowed from http://www.guideforschool.com/553891-program-to-print-a-given-time-in-words-isc-practical-2003/

    public TimeString() {
        textWords = new String[]{"Twelve", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight",
                "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
                "Seventeen", "Eighteen", "Nineteen", "Twenty", "Thirty", "Forty", "Fifty"};
        calendar = new GregorianCalendar();
    }

    public String timeAsString() {
        String timeString = "";
        timeString += parseHour() + "\n" + parseMinute();
        return timeString;
    }

    public String parseHour() {
        String hourText;
        int hour = calendar.get(Calendar.HOUR);
        hourText = numberToText(hour);
        return hourText;
    }

    public String parseMinute() {
        String minuteText;
        int minute = calendar.get(Calendar.MINUTE);

        minuteText = numberToText(minute);
        if (minute == 0) {
            minuteText = "";
        }
        return minuteText;
    }

    public List<String> parseMinutesToArray() {
        String minute = parseMinute();
        List<String> minutes = new ArrayList<>();
        minutes.addAll(Arrays.asList(minute.split("\\n")));
        if (minutes.size() > 1) {
            return minutes;
        } else {
            minutes.add(" ");
            return minutes;
        }
    }

    public String[] getTimeArray() {
        List<String> minutes = parseMinutesToArray();

        String lineOne = parseHour();
        String lineTwo = minutes.get(0);
        String lineThree = minutes.get(1);
        return new String[]{lineOne, lineTwo, lineThree};
    }


    public String numberToText(int number) {
        if (number >= 0 && number <= 60) {
            if (number <= 20) {
                return textWords[number];
            }
            else {
                String textNumber = "";
                switch (number / 10) { // going to be greater than 20
                    case 2:
                        textNumber = textWords[20];
                        break;
                    case 3:
                        textNumber = textWords[21];
                        break;
                    case 4:
                        textNumber = textWords[22];
                        break;
                    case 5:
                        textNumber = textWords[23];
                        break;
                }
                int remainder = number % 10;
                if (remainder != 0) {
                    textNumber += "\n" + textWords[remainder];
                }


                return textNumber;
            }
        }
        return "No Number Generated";
    }

    //todo add second parsing here
}
