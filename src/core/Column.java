package core;

import types.DataType; //импортируем наш интерфейс
import java.io.Serializable; //для сохранения на диск

/**
 * Класс представляет описание одного столбца (колонки) в таблице базы данных.
 * Хранит имя колонки, её тип данных и признак того, является ли она первичным ключом.
 */
public class Column implements Serializable{
    private String name;        //имя колонки
    private DataType type;      //тип данных
    private boolean primary_key;//проверяем уникальность

    public Column(String name, DataType type, boolean primary_key){
        this.name = name;
        this.type = type;
        this.primary_key = primary_key;
    }

    public String get_name(){
        return name;
    }

    public DataType get_type(){
        return type;
    }

    public boolean is_primary_key(){
        return primary_key;
    }

    @Override
    public String toString(){
        return name + " (" + type.get_name() + (primary_key ? ", primary key" : "") + ")";
    }


}
