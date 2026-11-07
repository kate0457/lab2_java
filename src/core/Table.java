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

    //вовращ копию списка всех строк таблицы
    public List<Row> select_all() {
        return rows;
    }

    //тут щас сложно
    //добавление новой строки в таблицу с проверкой уникальности

    public void insert(Row new_row){
        Column primary_key_column = struct.get_primary_key(); //методом из Table ищем среди всех колонок таблицы первичный ключ

        //проверяем ифом нашли или нет
        if (primary_key_column != null) {
            //если все топ и мы нашли, то для этой колонки сохраняем имя методом get_name из Column
            String primary_key_name = primary_key_column.get_name();

            //сохраняем в перемнную значение ключа из НОВОЙ строки которую хотим добавить
            Object new_primary_key_value = new_row.get(primary_key_name);

            //теперь идем по всем уже строчкам которые есть, и проверяем на совпадение с новым ключом в new_primary_key_value
            for (int i = 0; i < rows.size(); i++){
                //получаем старую строку по номеру i
                Row old_row = rows.get(i);

                //get - получить значение по имени колонки
                //смотрим значение id из старой строчки old_row
                Object old_primary_key = old_row.get(primary_key_name);

                //и теперь уже сравниваем значение старого ключа и нового с помощью equals
                if (old_primary_key != null && old_primary_key.equals(new_primary_key_value)) {
                    throw new IllegalArgumentException("Ошибка: запись с таким " + primary_key_name + " = " + new_primary_key_value + " уже существует!");
                }
            }
        }
        //все норм, поэтому наконец добавляем строку в таблицу
        rows.add(new_row);
    }

}
