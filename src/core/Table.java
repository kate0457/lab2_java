package core;

import java.io.Serializable; //для сохранения на диск (сериализация)
import java.util.ArrayList; //динамический массив
import java.util.List;  //для хранения колонок

//сама таблица с данными
//тут хранятся строки и проверяется уникальность id
public class Table implements Serializable{
    private TableStruct struct;        //какие есть колонки
    private List<Row> rows;            //список всех строк

    //конструктор
    public Table(TableStruct struct) {
        this.struct = struct;
        this.rows = new ArrayList<>(); //создаем пустой список строк
    }


    public TableStruct get_struct() {
        return struct;
    }

    public List<Row> get_rows() {
        return rows;
    }


}
