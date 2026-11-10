package app;
import core.*;
import sql.*;
import types.*;

import java.util.*;
import java.io.File;                //для выбора файликов
public class Main {
    private static String file_name = "";
    public static void main(String[] args) {
        Scanner scaner = new Scanner(System.in);
        SqlParser parser = new SqlParser();
        DataBase bd = null;
        File cur_dir = null;

        while(cur_dir == null){         //запрашиваем у пользователя путь до папки и его обрабатываем
            System.out.println("введите корректный путь к папке, где лежат базы данных,\n(или нажмите Enter для выбора текущей папки):");
            String input_path = scaner.nextLine().trim();

            if(input_path.isEmpty()){
                input_path = ".";
            }

            File check_dir = new File(input_path);
            if(check_dir.exists() && check_dir.isDirectory()){
                cur_dir = check_dir;
            }
            else{
                System.out.println("ошибка: выбранной папки не существует,\nВНИМАТЕЛЬНО проверьте введеный путь на правильность написания\nубедитесь, что введенный вами путь действительно путь к папке, а не к файлу");
                System.out.println();
            }
        }

        File[] files = cur_dir.listFiles(((dir, name) -> name.endsWith(".db")));        //лямбда функция, которая отбирает в список только бдшные файлы
        List<String> db_files = new ArrayList<>();

        if(files != null && files.length > 0){              //создаем список названий файлов, если они есть
            System.out.println("найдены существующие базы данных:");
            for(int i = 0; i < files.length; i++){
                db_files.add(files[i].getName());
                System.out.println((i + 1) + ". " + files[i].getName());
            }
            System.out.println("выберите номер базы данных для работы или введите 0 для создания новой");
        }
        else{
            System.out.println("не нашлось существующих баз данных\nвведите 0 для создания новой");
        }

        while (bd == null){             //выбираем что делать с веденным числом
            System.out.println("ваш выбор: ");
            String choice = scaner.nextLine().trim();

            if(choice.equals("0")){     //если 0, создаем новую бд
                System.out.println("введите имя новой базы данных:");
                String newdb_name = scaner.nextLine().trim();
                if(!newdb_name.endsWith(".db")){
                    newdb_name += ".db";
                }
                File newbd_file = new File(cur_dir, newdb_name);
                file_name = newbd_file.getAbsolutePath();
                bd = new DataBase();
                bd.save(file_name);
                System.out.println("создана новая база данных " + newdb_name);
            }
            else{                       //остальные циферки и буковки
                try {
                    int file_number = Integer.parseInt(choice);             //пытаемся сделать цифрой
                    if(file_number > 0 && file_number <= db_files.size()){   //проверяем не ввели ли какую-то численную белеберду
                        File load_file = new File(cur_dir, db_files.get(file_number - 1));
                        file_name = load_file.getAbsolutePath();
                        DataBase load_bd = DataBase.load(file_name);                      //пытаемся загрузить бд
                        if(load_bd == null){
                            System.out.println("ошибка, не удалось открыть базу данных");
                            System.out.println("возможно файл имеет неверный формат (например SQLite) или поврежден");
                            System.out.println("откройте файл с правильным форматом или создайте новую бд, нажав 0");
                        }
                        else{
                            bd = load_bd;
                        }
                    }
                    else{
                        System.out.println("неверный номер, попробуйте еще раз ввести нужное число или введите 0 для создания новой бд");
                    }
                }
                catch(NumberFormatException e){                             //если буквы
                    System.out.println("нужно ввести число, а не букву или слова");
                }
            }
        }

        System.out.println("можете вводить SQL запросы.\nдля выхода введите exit");
        while(true){
            System.out.println(file_name.replace(".db", ""));
            String sql_promt = scaner.nextLine().trim();
            if(sql_promt.equalsIgnoreCase("exit") || sql_promt.equalsIgnoreCase("exit;")){
                System.out.println("работа завершена");
                break;
            }

            if(sql_promt.isEmpty()){
                continue;
            }

            try{
                SqlCommand command = parser.parse(sql_promt);
                String table_name = command.getTableName();
                Table table = bd.get_table(table_name);

                if(command instanceof Create){                  //обработка CREATE
                    if(table != null){
                        throw new IllegalArgumentException("ошибка: таблица " + table_name + " уже существует");
                    }

                    Create create_cmd = (Create) command;       //делаем command конкретно классом create, a не sqlcommand
                    TableStruct struct = new TableStruct(table_name);
                    List<String> names = create_cmd.getColumnName();
                    List<String> types = create_cmd.getColumnType();

                    for(int i = 0; i < names.size(); i++){
                        String col_name = names.get(i);
                        String col_type = types.get(i);
                        DataType type = resolveType(col_type);
                        boolean isprimary_key = (i == 0);
                        struct.add_column(new Column(col_name, type, isprimary_key));
                    }
                    bd.add_table(new Table(struct));
                    System.out.println("таблица " + table_name + " успешно создана");
                    bd.save(file_name);
                    continue;
                }

                if(table == null){                  //проверяем для всех остальных команд, кроме CREATE, что таблица существует и можем с ней без вреда программе работать с другими запросами
                    throw new IllegalArgumentException("ошибка: таблица " + table_name + " не найдена в базе данных");
                }

                if(command instanceof Insert){      //начинаем выполнение INSERT
                    Insert insert_cmd = (Insert) command;
                    List<String> row_val = insert_cmd.getRawValues();
                    List<String> specific_col = insert_cmd.getColumnsName();
                    Row new_row = new Row();
                    List<Column> all_col = table.get_struct().get_columns();
                    if(specific_col != null && !specific_col.isEmpty()){
                        if(specific_col.size() != row_val.size()){
                            throw new IllegalArgumentException("ошибка: количество колонок не совпадает с переданными значениями");
                        }
                        for(int i = 0; i < specific_col.size(); i++){
                            String col_name = specific_col.get(i);
                            Column column = table.get_struct().get_column(col_name);
                            if(column == null){
                                throw  new IllegalArgumentException("ошибка: колонки " + col_name + " не существует");
                            }
                            Object parse_val = column.get_type().parse(row_val.get(i));
                            new_row.put(col_name, parse_val);
                        }
                    }
                    else{
                        if(row_val.size() != all_col.size()){
                            throw new IllegalArgumentException("ошибка: количество колонок не совпадает с переданными значениями");
                        }
                        for(int i = 0; i < all_col.size();  i++){
                            Column column = all_col.get(i);
                            Object parse_val = column.get_type().parse(row_val.get(i));
                            new_row.put(column.get_name(), parse_val);
                        }
                    }
                    table.insert(new_row);
                    System.out.println("запись успешно добавлена");
                    bd.save(file_name);
                }

                else if(command instanceof Select){
                    Select select_cmd = (Select) command;
                    List<String> target_col = select_cmd.getFilterColumnName();
                    List<Where.SingleCondition> conditions = null;
                    List<String> log_oper = null;
                    if(select_cmd.getCondition() != null){
                        conditions = select_cmd.getCondition().getConditions();
                        log_oper = select_cmd.getCondition().getLogicalOperator();
                    }
                    List<Row> matched_row = table.execute_select(target_col, conditions, log_oper);
                    System.out.println("результат выполнения SELECT");
                    if(target_col.isEmpty() || target_col.contains("*")){
                        for(Row row : matched_row){
                            System.out.println(row);
                        }
                    }
                    else{
                        for(Row row : matched_row){
                            StringBuilder row_output = new StringBuilder("{");      //для красивой строчки
                            for(int i = 0; i < target_col.size(); i++){
                                String col_name = target_col.get(i);
                                if(table.get_struct().get_column(col_name) == null){
                                    throw new IllegalArgumentException("ошибка: колонка " + col_name + " не найдена");
                                }
                                row_output.append(col_name).append("=").append(row.get(col_name));
                                if(i < target_col.size() - 1){
                                    row_output.append(", ");
                                }
                            }
                            row_output.append("}");
                            System.out.println(row_output);
                        }
                    }
                    System.out.println("всего найдено строк: " + matched_row.size());
                }
            }
            catch (Exception e){
                System.out.println("пока не доделала" + e.getMessage());
            }
        }
    }

    private static DataType resolveType(String type){
        switch(type.toLowerCase()){
            case "int":
                return new Int();
            case "string":
                return new Str();
            case "str":
                return new Str();
            case "double":
                return new Doub();
            case "bool":
                return new Bool();
            case "data":
                return new DateT();
            default:
                throw new IllegalArgumentException("ошибка: неизвестный тип данных");
        }
    }
}