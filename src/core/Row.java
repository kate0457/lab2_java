package core;

import java.io.Serializable; //для сохранения на диск
import java.util.Map;   //хранит пары ключ-значение
import java.util.LinkedHashMap;
import java.time.LocalDate;                  //импорт для даты
import java.time.format.DateTimeFormatter;  //импорт для форматирования


//класс для одной строки данный
public class Row implements Serializable {
    private Map<String, Object> values; //храниние данных каждой строки в формате словаря

    //конструктор
    public Row() {
        this.values = new LinkedHashMap<>(); //создаем пустой словарь
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
        String result = "{";
        boolean first = true; //нужен, чтобы не ставить запятую перед первым элементом

        //перебираем все пары (имя колонки -> значение)
        for (String key : values.keySet()){
            if (!first){
                result = result + ", "; //если это не первый элемент, ставим запятую
            }

            Object val = values.get(key);
            String val_str;

            if (val instanceof LocalDate) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                val_str = ((LocalDate) val).format(formatter);
            } else {
                val_str = String.valueOf(val); //остальное превращаем в строку
            }

            result = result + key + "=" + val_str;
            first = false;
        }

        result = result + "}";
        return result;
    }
}
