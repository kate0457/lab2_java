package core;
import sql.*;
import java.io.Serializable; //для сохранения на диск (сериализация)
import java.util.ArrayList; //динамический массив
import java.util.List;  //для хранения колонок
import java.util.function.Predicate; //для фильтрации через where
import java.util.Map;

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


    //фильтровать строки по условию
    public List<Row> select_where(Predicate<Row> condition){
        List<Row> rows_where = new ArrayList<>();

        for (int i=0; i < rows.size(); i++){
            Row row = rows.get(i);

            //если строка подходит под условие (condition вернул true) - добавляем её
            if (condition.test(row)) {
                rows_where.add(row);
            }
        }
        return rows_where;
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

    public List<Row> execute_select(List<String> targetColumns, List<Where.SingleCondition> conditions, List<String> logicalOperators) {
        List<Row> resultList = new ArrayList<>();

        // Если блок WHERE пустой, то фильтровать нечего — возвращаем все строки таблицы целиком
        if (conditions == null || conditions.isEmpty()) {
            return rows;
        }

        // Перебираем абсолютно все строки, накопленные в нашей таблице
        for (int i = 0; i < rows.size(); i++) {
            Row row = rows.get(i);

            // Вычисляем результат самого первого (базового) условия для текущей строки
            boolean currentResult = evaluate_single_condition(row, conditions.get(0));

            // Если в запросе есть дополнительные связки AND / OR, идем по ним по цепочке
            for (int j = 0; j < logicalOperators.size(); j++) {
                String op = logicalOperators.get(j).toUpperCase();

                // Защитная проверка: проверяем, что для следующего оператора есть соответствующее условие
                if (j + 1 < conditions.size()) {
                    boolean nextResult = evaluate_single_condition(row, conditions.get(j + 1));

                    if (op.equals("AND")) {
                        currentResult = currentResult && nextResult;
                    } else if (op.equals("OR")) {
                        currentResult = currentResult || nextResult;
                    }
                }
            }

            // Если после всех логических связок строка нам подошла — добавляем её в итоговый список
            if (currentResult) {
                resultList.add(row);
            }
        }

        return resultList;
    }

    /**
     * Вспомогательный метод: проверяет одну ячейку конкретной строки по одному правилу (например, price > 20000)
     */
    private boolean evaluate_single_condition(Row row, Where.SingleCondition single) {
        String colName = single.getColumnName();
        Column column = struct.get_column(colName); // Ищем колонку в "паспорте" таблицы

        if (column == null) {
            throw new IllegalArgumentException("Ошибка выполнения: колонка '" + colName + "' не найдена в структуре таблицы.");
        }

        Object cellValue = row.get(colName); // Достаем живой объект из ячейки текущей строки
        if (cellValue == null) return false;

        // Важнейший шаг: прогоняем сырой строковый текст фильтра из SQL через метод parse() нужного типа данных
        Object filterValue = column.get_type().parse(single.getValue());
        String operator = single.getOperator();

        // Универсальное сравнение любых объектов в Java (чисел, строк) через интерфейс Comparable
        if (cellValue instanceof Comparable && filterValue instanceof Comparable) {
            Comparable c1 = (Comparable) cellValue;
            Comparable c2 = (Comparable) filterValue;
            int compare = c1.compareTo(c2);

            switch (operator) {
                case "=":  return compare == 0;
                case "!=": return compare != 0;
                case ">":  return compare > 0;
                case "<":  return compare < 0;
                case ">=": return compare >= 0;
                case "<=": return compare <= 0;
                default: throw new IllegalArgumentException("Неизвестный оператор сравнения в условии: " + operator);
            }
        }

        // Для типов, которые нельзя сравнивать через "больше/меньше" (например, boolean)
        if (operator.equals("=")) {
            return cellValue.equals(filterValue);
        } else if (operator.equals("!=")) {
            return !cellValue.equals(filterValue);
        }

        return false;
    }


    //обновление строк по условию
    public int update_where(Predicate<Row> condition, Map<String, Object> new_values){
        int updated_count = 0;
        for (int i = 0; i < rows.size(); i++){
            Row row = rows.get(i);

            //проверяем что строка подходит под условие
            if (condition.test(row)){
                //идем по всем подходящим под условие значениям
                for (Map.Entry<String, Object> entry  : new_values.entrySet()){
                    String column_name = entry.getKey();
                    Object new_value = entry.getValue();
                    row.put(column_name, new_value); //меняем значение в строке
                }
                updated_count++;
            }
        }
        return updated_count; // Возвращаем, сколько строк обновили
    }

    //удалить строку по условию
    public int delete_where(Predicate<Row> condition){
        int deleted_count = 0;

        //идем с конца
        for (int i = rows.size() - 1; i >= 0; i--) {
            Row row = rows.get(i);
            if (condition.test(row)) {
                rows.remove(i);
                deleted_count++;
            }
        }
        return deleted_count;
    }


}
