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

    //загрузить базу из файла
    public static DataBase load(String file_name){
        try{
            //FileInputStream позволяет читать данные из файла в виде последовательности байтов

            FileInputStream file = new FileInputStream(file_name);

            //ObjectInputStream - десериализация, то есть восстановление объектов и примитивных данных из потока, который ранее был записан с помощью ObjectOutputStream
            ObjectInputStream in = new ObjectInputStream(file);

            //читаем файл, приводим к типу датабейз
            Object obj = in.readObject();
            DataBase db = (DataBase) obj;

            //все закрыли без суеты
            in.close();
            file.close();

            System.out.println("База данных успешно загружена из " + file_name);
            return db;
        } catch (FileNotFoundException e){
            System.out.println("Файл базы данных не найден. Будет создана новая база.");
            return null; //полный ноль тк базы нет
        } catch (ClassNotFoundException e) {
            System.out.println("Ошибка при загрузке (класс не найден): " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке (ввод/вывод ): " + e.getMessage());
            return null;
        }
    }


    //удаление таблицы из базы
    public void drop_table(String table_name) {
        if (tables.containsKey(table_name)) {
            tables.remove(table_name);
        } else {
            throw new IllegalArgumentException("Ошибка: таблицы " + table_name + " не существует!");
        }
    }
}
