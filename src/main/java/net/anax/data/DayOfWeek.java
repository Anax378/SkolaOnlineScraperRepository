package net.anax.data;

public enum DayOfWeek {
    SUNDAY("Sunday", 0),
    MONDAY("Monday", 1),
    TUESDAY("Tuesday", 2),
    WEDNESDAY("Wednesday", 3),
    THURSDAY("Thursday", 4),
    FRIDAY("Friday", 5),
    SATURDAY("Saturday", 6);

    String name;
    int index;
    DayOfWeek(String name, int index){
        this.name = name;
        this.index = index;
    }
}
