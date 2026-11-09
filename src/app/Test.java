package app;
import core.Column;
import core.Row;
import core.Table;
import core.TableStruct;
import types.Int;
import types.Str;

//тестовый класс для проверки кода
public class Test {
    public static void main(String[] args) {
        TableStruct pets_struct = new TableStruct("pets"); //создали новую сборку колонок с названием
        //добавили две колонки и задали им имена, типы данных и boolean на трушность
        pets_struct.add_column(new Column("id", new Int(), true));
        pets_struct.add_column(new Column("nickname", new Str(), false));

        //создаем таблицу на основе структуры pets_struct
        Table pets_table = new Table(pets_struct);

        //добавляем первую строку (Шарик, id=1)
        Row row1 = new Row();
        row1.put("id", 1);
        row1.put("nickname", "Шарик");
        pets_table.insert(row1);
        System.out.println("Шарик добавлен. Всего строк: " + pets_table.get_rows().size());

        //try добавить второго питомца с таким же id=1
        Row row2 = new Row();
        row2.put("id", 1); //такой id уже есть
        row2.put("nickname", "Мурка");

        try {
            pets_table.insert(row2);
        } catch (IllegalArgumentException e) {
            System.out.println("Поймали ошибку: " + e.getMessage());
        }

        System.out.println("Строк в таблице: " + pets_table.get_rows().size());

        //try добавить нового питомца с id=2
        Row row3 = new Row();
        row3.put("id", 2); //такой id уже есть
        row3.put("nickname", "Шантик");
        pets_table.insert(row3);

        System.out.println("Строк в таблице: " + pets_table.get_rows().size());
    }
}