package com.microservices.user.core.dao;

import java.math.BigDecimal;

public class DecimalFormater {
    public static Float round4Dig(Float number){
        return round(number, 4);
    }
    
    public static Float round3Dig(Float number){
        return round(number, 3);
    }
    
    public static Float round2Dig(Float number){
        return round(number, 2);
    }
    
    public static Float round2DigUp(Float number){
        return roundUp(number, 2);
    }
    
    public static Float round2DigDown(Float number){
        return roundUp(number, 2);
    }
    
    public static Float round1Dig(Float number){
        return round(number, 1);
    }
    
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
    
    public static float roundUp(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_UP);
        return bd.floatValue();
    }
    
    public static String makeSumToStringFormat(Float sumFloat) {
        String sumString = String.format("%.2f", sumFloat);
        return sumString.replace(',', '.');
    }
}
