package core;

import types.DataType; //импортируем наш интерфейс
import java.io.Serializable; //для сохранения на диск

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

    @Override //переопределяем метод из базового класса Object
    //выводим данные про колокну, ее название, тип данных которые она хранит и явлеятся ли праймари
    public String toString(){
        return name + " (" + type.get_name() + (primary_key ? ", primary key" : "") + ")";
    }


}
