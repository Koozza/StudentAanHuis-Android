package com.thijsdev.studentaanhuis;

public class GeneralFunctions {
    public static String fixDate(String date) {
        date = date.replace("januari", "1");
        date = date.replace("februari", "2");
        date = date.replace("maart", "3");
        date = date.replace("april", "4");
        date = date.replace("mei", "5");
        date = date.replace("juni", "6");
        date = date.replace("juli", "7");
        date = date.replace("augustus", "8");
        date = date.replace("september", "9");
        date = date.replace("oktober", "10");
        date = date.replace("november", "11");
        date = date.replace("december", "12");

        date = date.replace("jan", "1");
        date = date.replace("feb", "2");
        date = date.replace("mrt", "3");
        date = date.replace("apr", "4");
        date = date.replace("mei", "5");
        date = date.replace("jun", "6");
        date = date.replace("jul", "7");
        date = date.replace("aug", "8");
        date = date.replace("sep", "9");
        date = date.replace("okt", "10");
        date = date.replace("nov", "11");
        date = date.replace("dec", "12");

        date = date.replace("maa ", "");
        date = date.replace("din ", "");
        date = date.replace("woe ", "");
        date = date.replace("don ", "");
        date = date.replace("vri ", "");
        date = date.replace("zat ", "");
        date = date.replace("zon ", "");

        return date;
    }
}
