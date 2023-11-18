package net.anax.data;

public enum TimetableLessonType {
    REGULAR("DctInnerTableType10DataTD"),
    EVENT("KuvSkolniAkceHodina"),
    REPLACED("KuvSuplovanaHodina"),
    REPLACEMENT("KuvSuplujiciHodina")
    ;
    public final String identifier;
    TimetableLessonType(String name){
        this.identifier = name;
    }
}
