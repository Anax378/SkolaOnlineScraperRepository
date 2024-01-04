package net.anax.data.timetable;

public enum ETimetableLessonType {
    REGULAR("DctInnerTableType10DataTD"),
    EVENT("KuvSkolniAkceHodina"),
    REPLACED("KuvSuplovanaHodina"),
    REPLACEMENT("KuvSuplujiciHodina")
    ;
    public final String identifier;
    ETimetableLessonType(String name){
        this.identifier = name;
    }
}
