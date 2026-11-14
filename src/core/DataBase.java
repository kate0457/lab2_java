package core;
import java.io.*;           //инструменты для работы с файлами (ввод/вывод)
import java.util.HashMap;   //реализация словаря (хэш-таблица)
import java.util.Map;       //хранит пары имя-объект
import java.util.List;
import java.util.ArrayList;


/**
 * Класс, представляющий базу данных.
 * Хранит в себе таблицы и отвечает за их сохранение в файл (сериализация) и загрузку из файла (десериализация)
 */

public class DataBase implements Serializable{
    private static final String CYAN  = "\u001B[36m";
    private static final String RESET = "\u001B[0m";

    private Map<String, Table> tables; //словарь со всеми таблицами

    //конструктор
    public DataBase() {
        this.tables = new HashMap<>();
    }

    //добавить новую таблицу в базу
    public void add_table(Table table) {
        String name = table.get_struct().get_table_name().trim().toLowerCase();
        tables.put(name, table);
    }

    //получить таблицу по имени
    public Table get_table(String table_name) {
        return tables.get(table_name.trim().toLowerCase());
    }


    /**
     * Сохраняет текущее состояние базы данных (все таблицы и их данные) в указанный файл.
     *
     * @param file_name путь к файлу, в который будет произведена запись
     */
    public void save(String file_name){
        try{
            FileOutputStream file = new FileOutputStream(file_name);    //открываем файлик
            ObjectOutputStream out = new ObjectOutputStream(file);      //преобразует в последовательность байтов и записывает в выходной поток (OutputStream)

            out.writeObject(this);                                      //сохранить объект в файл

            out.close();                                                //теперь все закрываем чтобы ничего не мешало, суету не наводило
            file.close();

            File f = new File(file_name);
            //System.out.println("База данных успешно сохранена в файл " + f.getName());

        } catch (IOException  e){
            System.out.println("Ошибка при сохранении: " + e.getMessage());
        }
    }

    /**
     * Загружает базу данных из указанного файла (десериализация).
     *
     * @param file_name путь к файлу базы данных
     * @return загруженный объект базы данных, либо null, если файл не найден или поврежден
     */
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

            File f = new File(file_name);
            System.out.println("База данных успешно загружена из " + CYAN + f.getName() + RESET);
            return db;
        } catch (FileNotFoundException e){
            System.out.println("Файл базы данных не найден. Будет создана новая база.");
            return null; //полный ноль тк базы нет
        } catch (ClassNotFoundException e) {
            System.out.println("Ошибка при загрузке (класс не найден): " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке (ввод/вывод): " + e.getMessage());
            return null;
        }
    }


    /**
     * Удаляет таблицу из базы данных по её имени.
     *
     * @param table_name имя таблицы, которую нужно удалить
     * @throws IllegalArgumentException если таблицы с таким именем не существует
     */
    public void drop_table(String table_name) {
        if (tables.containsKey(table_name.trim().toLowerCase())) {
            tables.remove(table_name.trim().toLowerCase());
        } else {
            throw new IllegalArgumentException("Ошибка: таблицы " + table_name + " не существует!");
        }
    }

    /**
     * Возвращает список имен всех таблиц, которые в данный момент находятся в базе данных.
     *
     * @return список строк с названиями таблиц
     */
    public List<String> get_table_names() {
        List<String> names = new ArrayList<>();
        for (String table_name : tables.keySet()) {
            names.add(table_name);
        }
        return names;
    }
}
