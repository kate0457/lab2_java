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


    //сериализация
    public void save(String file_name){
        try{
            //открываем файлик
            FileOutputStream file = new FileOutputStream(file_name);
            //преобразует в последовательность байтов и записывает в выходной поток (OutputStream)
            ObjectOutputStream out = new ObjectOutputStream(file);

            //сохранить объект в файл
            out.writeObject(this);

            //теперь все закрываем чтобы ничего не мешало, суету не наводило
            out.close();
            file.close();
            System.out.println("База данных успешно сохранена в " + file_name);

        } catch (IOException  e){
            System.out.println("Ошибка при сохранении: " + e.getMessage());
        }
    }

}
