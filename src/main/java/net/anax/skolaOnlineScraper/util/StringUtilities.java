package net.anax.skolaOnlineScraper.util;

public class StringUtilities {
    public static boolean isInteger(String text){
        if(text == null){return false;}
        try{
            Integer.parseInt(text);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

}
