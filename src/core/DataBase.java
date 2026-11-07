package core;
import java.io.*; //инструменты для работы с файлами (ввод/вывод)
import java.util.HashMap; //реализация словаря (хэш-таблица)
import java.util.Map; //хранит пары имя-объект


//который хранит в себе все таблицы
//отвечает за сохранение в файл и загрузку из файла
public class DataBase implements Serializable{
    private Map<String, Table> tables; //словарь со всеми таблицами

    //конструктор
    public DataBase() {
        this.tables = new HashMap<>();
    }


    //добавить новую таблицу в базу
    public void add_table(Table table) {
        String name = table.get_struct().get_table_name();
        tables.put(name, table);
    }

    //получить таблицу по имени
    public Table get_table(String table_name) {
        return tables.get(table_name);
    }



}
