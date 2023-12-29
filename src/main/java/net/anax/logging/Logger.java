package net.anax.logging;

public class Logger {
    public static void LOG(String message){
        System.out.println(message);
    }

    public static void LOGBLOCK(String body, String title){
        System.out.println("-------------------" + title + " start-------------------------");
        System.out.println(body);
        System.out.println("-------------------" + title + " end-------------------------");
    }

    public static void LOGFOLDEDSECTION(String section){
        String[] lines = section.split("\n");
        for(String line : lines){
            System.out.println("-fold-\t" + line);
        }
    }

}
