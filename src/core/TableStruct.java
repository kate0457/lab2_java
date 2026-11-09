package core;

import java.io.Serializable; //для сохранения на диск (сериализация)
import java.util.ArrayList; //динамический массив
import java.util.List;  //для хранения колонок

public class TableStruct implements Serializable{
    private String table_name;      //имя таблицы
    private List<Column> columns;   //список колонок

    //конструктор
    public TableStruct(String table_name) {
        this.table_name = table_name;      //имя сохраняем
        this.columns = new ArrayList<>(); //создаем пустой список
    }

    //добавляем колонку в список columns
    public void add_column(Column column) {
        columns.add(column);
    }

    //публичный метод, чтобы получить приватное нахвание таблицы
    public String get_table_name() {
        return table_name;
    }

    public List<Column> get_columns() {
        return columns;
    }

    //ищем колонку по имени, если совпали - возвращ
    public Column get_column(String name) {
        for (int i = 0; i < columns.size(); i++) { //идем по списку от 0 до конца (размер списка - columns.size())
            Column col = columns.get(i); //берём колонку по индексу i

            if (col.get_name().equalsIgnoreCase(name)) { //сравниваем имена
                return col; //нашли ура победа
            }
        }
        return null; //не нашли, грусть печаль
    }

    //ищем колонку которая primary key
    public Column get_primary_key() {
        for (int i = 0; i < columns.size(); i++) { //идем по списку от 0 до конца (размер списка - columns size)
            Column col = columns.get(i); //берём колонку по индексу i

            if (col.is_primary_key()) { //смотрим явялется ли праймари
                return col; //нашли ура победа
            }
        }
        return null; //не нашли, грусть печаль
    }

    @Override //переопределяем стандратный метод из Object  для эстетичного вывода таблицы
    public String toString() {
        String result = "Таблица: " + table_name + "\n";
        for (int i = 0; i < columns.size(); i++) {
            Column col = columns.get(i);
            String colText = col.toString(); //превращаем колонку в строку
            result = result + "  " + colText + "\n";
        }
        return result;
    }
}
