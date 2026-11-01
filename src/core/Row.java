package core;

import java.io.Serializable; //для сохранения на диск
import java.util.Map;   //хранит пары ключ-значение
import java.util.HashMap;


//класс для одной строки данный
public class Row implements Serializable {
    private Map<String, Object> values; //храниние данных каждой строки в формате словаря

    //конструктор
    public Row() {
        this.values = new HashMap<>(); //создаем пустой словарь
    }

    //добавить значение в строку (в ячейку)
    public void put(String column_name, Object value) {
        values.put(column_name, value);
    }

    //получить значение по имени колонки
    public Object get(String column_name) {
        return values.get(column_name);
    }

    //узнать сколько в строке колонок
    public int size() {
        return values.size();
    }

    @Override //переопределяем метод для нишевого вывода в консоль
    public String toString() {
        return values.toString(); //выведет что-то вроде {id=1, nickname=Шарик}
    }
}
