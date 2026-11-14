package app;
import core.*;
import sql.*;
import types.*;

import java.util.*;
import java.io.File;                //для выбора файликов
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main {
    private static String file_name = "";                                       //название файла, с которым мы будем работать
    public static void main(String[] args) {
        String GREEN  = "\u001B[32m";
        String CYAN   = "\u001B[36m";
        String YELLOW = "\u001B[33m";
        String RESET  = "\u001B[0m";
        String RED    = "\u001B[31m";

        Scanner scaner = new Scanner(System.in);                                //штука для принятия строк с клавиатуры
        SqlParser parser = new SqlParser();                                     //создаем парсер, чтоб обрабатывать введенные строки
        DataBase bd = null;                                                     //null, потому что пока ничего не открыто
        File cur_dir = null;                                                    //null, потому что еще ничего не открыто

        while(cur_dir == null){         //запрашиваем у пользователя путь до папки и его обрабатываем
            System.out.println("\n" + CYAN + "Введите корректный путь к папке, где лежат базы данных," + RESET);
            System.out.println("(или нажмите " + CYAN + "Enter" + RESET + " для выбора текущей папки):");
            String input_path = scaner.nextLine().trim();

            if(input_path.isEmpty()){   //если пустая, то выбираем директорию, где лежит файл с программой
                input_path = ".";
            }

            File check_dir = new File(input_path);                              //сразу не записываем в cur_dir, тк пользователь может ввести неусуществующую ерунду
            if(check_dir.exists() && check_dir.isDirectory()){                  //если не ерунда и путь действительно существует, то работаем с путем, иначе выводим сообщение об ошибке и снова запрашиваем путь
                cur_dir = check_dir;
            }
            else{
                System.out.println("Ошибка: выбранной папки не существует,\nВНИМАТЕЛЬНО проверьте введенный путь на правильность написания\nубедитесь, что введенный вами путь действительно путь к папке, а не к файлу");
                System.out.println();
            }
        }

        File[] files = cur_dir.listFiles(((dir, name) -> name.endsWith(".db")));        //лямбда функция, которая отбирает в список только бдшные файлы
        List<String> db_files = new ArrayList<>();

        if(files != null && files.length > 0){              //создаем список названий файлов, если они есть
            System.out.println(GREEN + "Найдены существующие базы данных:" + RESET);
            for(int i = 0; i < files.length; i++){
                db_files.add(files[i].getName());
                System.out.println((i + 1) + ". " + CYAN + files[i].getName() + RESET);
            }
            System.out.println("--------------------------------------------------");
            System.out.println(CYAN + "Выберите номер базы данных" + RESET + " для работы ИЛИ введите " + CYAN + "0" + RESET + " для создания новой");
        }
        else{
            System.out.println(YELLOW + "Не удалось найти существующих баз данных" + RESET);
            System.out.println("Введите" + CYAN + " 0" + RESET + " для создания новой");
        }

        while (bd == null){             //выбираем что делать с веденным числом
            System.out.println("\n" + CYAN + "Введите" + RESET + " номер базы данных: ");
            String choice = scaner.nextLine().trim();

            if(choice.equals("0")){     //если 0, создаем новую бд
                System.out.println(CYAN + "Введите" + RESET + " имя новой базы данных:");
                String newdb_name = scaner.nextLine().trim();

                //+проверка что имя бд не пустое (".db")
                if (newdb_name.isEmpty()) {
                    System.out.println(RED + "Ошибка: имя базы данных не может быть пустым!" + RESET);
                    continue; //возвращаемся в начало и просим выбрать заново пжпж
                }

                //добавляем .db, если нет
                if(!newdb_name.toLowerCase().endsWith(".db")){
                    newdb_name += ".db";
                }

                //нет ли уже файла с таким именем (без учета регистра)
                boolean already_exists = false;
                for (String existing_file : db_files) {
                    if (existing_file.equalsIgnoreCase(newdb_name)) {
                        already_exists = true;
                        break;
                    }
                }

                if (already_exists) {
                    System.out.println(RED + "Ошибка: база данных с именем '" + newdb_name + "' уже существует!" + RESET);
                    System.out.println("\n" + CYAN + "Вы можете:" + RESET);
                    System.out.println(" 1. Ввести " + CYAN + "другое имя" + RESET + " для новой базы данных");
                    System.out.println(" 2. Ввести " + CYAN + "номер" + RESET + " существующей базы из списка выше");

                    //System.out.println("Попробуйте ввести другое имя или выберите существующую базу из списка.\n");
                    continue; //возвращаемся в начало цикла и просим ввести выбор заново
                }

                //если проверка прошла, создаем файл
                File newbd_file = new File(cur_dir, newdb_name);
                file_name = newbd_file.getAbsolutePath();
                bd = new DataBase();
                bd.save(file_name);
                System.out.println(GREEN + "Создана новая база данных " + CYAN + newdb_name + RESET);
            }
            else{                       //остальные циферки и буковки
                try {
                    int file_number = Integer.parseInt(choice);             //пытаемся сделать цифрой
                    if(file_number > 0 && file_number <= db_files.size()){   //проверяем не ввели ли какую-то численную белеберду
                        File load_file = new File(cur_dir, db_files.get(file_number - 1));
                        file_name = load_file.getAbsolutePath();
                        DataBase load_bd = DataBase.load(file_name);                      //пытаемся загрузить бд
                        if(load_bd == null){
                            System.out.println(RED + "Ошибка: не удалось открыть базу данных" + RESET);
                            System.out.println("Возможно файл имеет неверный формат (например SQLite) или поврежден");
                            System.out.println("Откройте файл с правильным форматом ИЛИ создайте новую бд, нажав 0");
                        }
                        else{
                            bd = load_bd;
                        }
                    }
                    else{
                        System.out.println(RED + "Неверный номер, попробуйте еще раз ввести нужное число ИЛИ введите 0 для создания новой бд" + RESET);
                    }
                }
                catch(NumberFormatException e){                             //если буквы
                    System.out.println(RED + "Ошибка: нужно ввести число, а не букву или слово" + RESET );
                }
            }
        }

        System.out.println();
        //выводим список доступных таблиц
        List<String> tableNames = bd.get_table_names();

        if (tableNames.isEmpty()) {
            System.out.println(YELLOW + "В базе данных пока нет таблиц." + RESET);
        } else {
            System.out.println(GREEN + "Доступные таблицы в базе данных:" + RESET);
            for (String tableName : tableNames) {
                System.out.println(" - " + CYAN + tableName + RESET);
            }
        }

        System.out.println(GREEN + "\nВы можете вводить SQL-запросы (например: SELECT * FROM pets;)" + RESET);
        System.out.println("Для выхода из программы введите " + CYAN + "exit" + RESET);
        System.out.println("--------------------------------------------------");

        while(true){                                                                    //пока не ввели exit во всех его возможных вариациях этот цикл будет работать, это основной цикл, который связывает все папки в единое целое
            File f = new File(file_name);
            String short_name = f.getName().replace(".db", "");
            System.out.print(GREEN + "[" + short_name + "]> " + RESET);

            String sql_promt = scaner.nextLine().trim();
            if(sql_promt.equalsIgnoreCase("exit") || sql_promt.equalsIgnoreCase("exit;")){
                System.out.println("\n" + GREEN + "Работа завершена. Хорошего дня!" + RESET);
                break;
            }

            if(sql_promt.isEmpty()){                                                    //если случайно нажали Enter, программа не ложится, а продолжает дальше ждать строку с клавиатуры
                continue;
            }

            try{                                                                        //пробуем распаристь строку и выполнить SQL-запрос, если что-то пошло не так выводится конкретная ошибка, что не так. при этом всем программа продолжает работать, пока не введут стоп слово
                SqlCommand command = parser.parse(sql_promt);                           //получаем список слов нашего запроса
                String table_name = command.getTableName();                             //получаем имя таблички, с которой мы сейчас будем работать
                Table table = bd.get_table(table_name);                                 //получаем саму табличку

                if(command instanceof Create){                  //обработка CREATE
                    if(table != null){                          //защита от уже существующей таблицы
                        throw new IllegalArgumentException("Ошибка: таблица " + table_name + " уже существует");
                    }

                    Create create_cmd = (Create) command;                   //делаем command конкретно классом create, a не sqlcommand
                    TableStruct struct = new TableStruct(table_name);       //получаем все атрибуты класса CREATE
                    List<String> names = create_cmd.getColumnName();
                    List<String> types = create_cmd.getColumnType();

                    for(int i = 0; i < names.size(); i++){                  //полученных из парсинга названия и типа колонок создаем новую таблицу
                        String col_name = names.get(i);
                        String col_type = types.get(i);
                        DataType type = resolveType(col_type);
                        boolean isprimary_key = (i == 0);
                        struct.add_column(new Column(col_name, type, isprimary_key));       //добавляем новую строку в структуру на шей новой таблички
                    }
                    bd.add_table(new Table(struct));                                        //добавляем только что созданную таблицу в базу данных
                    System.out.println("Таблица " + CYAN + table_name + RESET + " успешно" + GREEN + " создана"+ RESET+ "!");
                    bd.save(file_name);                                                     //сохраняем
                    continue;
                }

                if(table == null){                  //проверяем для всех остальных команд, кроме CREATE, что таблица существует и можем с ней без вреда программе работать с другими запросами
                    throw new IllegalArgumentException("Ошибка: таблица " + table_name + " не найдена в базе данных");
                }

                if(command instanceof Insert){      //начинаем выполнение INSERT
                    Insert insert_cmd = (Insert) command;                           //получаем все атрибуты класса INSERT
                    List<String> row_val = insert_cmd.getRawValues();
                    List<String> specific_col = insert_cmd.getColumnsName();
                    Row new_row = new Row();
                    List<Column> all_col = table.get_struct().get_columns();
                    if(specific_col != null && !specific_col.isEmpty()){            //отрабатываем запрос, где задают в какие конкретно столбцы надо добавить значение
                        if(specific_col.size() != row_val.size()){
                            throw new IllegalArgumentException("Ошибка: количество колонок не совпадает с переданными значениями");
                        }
                        for(int i = 0; i < specific_col.size(); i++){               //проверяем существование каждой колонки и записываем в нее переданное пользователем значением
                            String col_name = specific_col.get(i);
                            Column column = table.get_struct().get_column(col_name);
                            if(column == null){
                                throw  new IllegalArgumentException("Ошибка: колонки " + col_name + " не существует");
                            }
                            Object parse_val = column.get_type().parse(row_val.get(i));
                            new_row.put(col_name, parse_val);
                        }
                    }
                    else{                                                           //обрабатываем INSERT, где переданы все значения, без конкретных колонок
                        if(row_val.size() != all_col.size()){
                            throw new IllegalArgumentException("Ошибка: количество колонок не совпадает с переданными значениями");
                        }
                        for(int i = 0; i < all_col.size();  i++){                           //каждой переданной колонке в соответствие ставим переданное значение
                            Column column = all_col.get(i);
                            Object parse_val = column.get_type().parse(row_val.get(i));
                            new_row.put(column.get_name(), parse_val);
                        }
                    }
                    table.insert(new_row);                                          //добавляем в таблицу обработанную строку
                    System.out.println("Запись " + "успешно" + GREEN+ " добавлена" + RESET);
                    bd.save(file_name);                                             //сохраняем
                }

                else if(command instanceof Select){
                    Select select_cmd = (Select) command;
                    List<String> target_col = select_cmd.getFilterColumnName(); //получаем список колонок, которые пользователь хочет увидеть
                    List<Where.SingleCondition> conditions = null;              //по умолчанию условий нет
                    List<String> log_oper = null;                                //по умолчанию логических операторов (and/or) нет
                    if(select_cmd.getCondition() != null){                      //если в запросе был блок where
                        conditions = select_cmd.getCondition().getConditions(); //достаем список условий
                        log_oper = select_cmd.getCondition().getLogicalOperator();  //достаем логические связки
                    }

                    //вызываем метод таблицы, который вернет отфильтрованные строки
                    List<Row> matched_row = table.execute_select(target_col, conditions, log_oper);

                    System.out.println("\n" + "--- Результат запроса " + CYAN + "SELECT" +RESET+ " ---" ); //чутьчуть эстетики

                    if (matched_row.isEmpty()) {                    //если после фильтрации ничего не осталось
                        System.out.println(YELLOW + "Строки не найдены" + RESET);
                    } else {                                        //если строки есть, начинаем их красиво выводить
                        //создаем список колонок, которые будем печатать
                        List<Column> cols_to_print = new ArrayList<>();
                        if (target_col.isEmpty() || target_col.contains("*")) {         //если запросили все колонки (звездочка)
                            cols_to_print = table.get_struct().get_columns();
                        } else {    //если запросили конкретные колонки (например, name, position)
                            for (String colName : target_col) {
                                Column c = table.get_struct().get_column(colName);      //ищем колонку по имени в таблице
                                if (c != null) cols_to_print.add(c);                    //если нашли, добавляем в список на печать
                            }
                        }

                        //печатаем имя колонки, выделяя под него 15 символов
                        for (Column col : cols_to_print) {
                            System.out.printf(CYAN + "%-15s" + RESET, col.get_name());
                        }
                        System.out.println(); //перевод строки

                        //раздельная линия
                        for (int i = 0; i < cols_to_print.size() * 15; i++) System.out.print("-");
                        System.out.println();

                        //перебираем все найденные строки
                        for (Row row : matched_row) {
                            for (Column col : cols_to_print) {
                                //значение из ячейки текущей строки
                                Object val = row.get(col.get_name());
                                String str_val = "";

                                //проверяем, является ли значение датой
                                if (val instanceof java.time.LocalDate) {
                                    //да? переводим её в нужный формат (дд.мм.гггг)
                                    java.time.LocalDate date_val = (java.time.LocalDate) val;
                                    java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy");
                                    str_val = date_val.format(fmt);
                                } else {
                                    //нет? просто превращаем в строку
                                    str_val = String.valueOf(val);
                                }

                                //печатаем значение в поле шириной 15 символов
                                System.out.printf("%-15s", str_val);
                            }

                            //после вывода всех колонок одной строки переходим на новую строку
                            System.out.println();
                        }
                    }
                    System.out.println("Всего найдено строк: " + matched_row.size() + "\n");
                }

                else if(command instanceof Update){                                     //обрабатываем UPDATE
                    Update update_cmd = (Update) command;
                    Map<String, String> update_val = update_cmd.getUpdateValues();
                    List<Where.SingleCondition> conditions = null;                      //null здесь и в следующей строчке, тк блока WHEER может и не быть
                    List<String> log_oper = null;
                    if(update_cmd.getCondition() != null){                              //если есть WHERE
                        conditions = update_cmd.getCondition().getConditions();
                        log_oper = update_cmd.getCondition().getLogicalOperator();
                    }
                    table.update_rows(update_val, conditions, log_oper);                //обновляем
                    bd.save(file_name);
                }

                else if(command instanceof Delete){                                     //обрабатываем DELETE
                    Delete delete_cmd = (Delete) command;
                    List<Where.SingleCondition> conditions = null;                      //null здесь и в следующей строчке, тк блока WHEER может и не быть
                    List<String> log_oper = null;
                    if(delete_cmd.getCondition() != null){                              //если есть WHERE
                        conditions = delete_cmd.getCondition().getConditions();
                        log_oper = delete_cmd.getCondition().getLogicalOperator();
                    }
                    table.execute_delete(conditions, log_oper);                         //удаляем
                    bd.save(file_name);
                }

                else if(command instanceof Drop){                                       //обрабатываем DROP
                    bd.drop_table(table_name);
                    System.out.println("Таблица " + CYAN + table_name + RESET + " успешно" + GREEN+" удалена" + RESET + " из базы данных");
                    bd.save(file_name);

                    //не осталась ли база пустой после удаления?
                    if (bd.get_table_names().isEmpty()) {
                        System.out.println("\n" + YELLOW + "Это была последняя таблица в базе. Теперь база данных пуста." + RESET);
                        System.out.println("Создайте новую таблицу командой " + CYAN + "CREATE TABLE имя (колонки);" + RESET + "\n");
                    }
                }
            }
            catch (Exception e){
                System.out.println(RED + e.getMessage() + RESET);
            }
        }
    }

    private static DataType resolveType(String type){                                       //из строчек делаем тип данных
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
                throw new IllegalArgumentException("Ошибка: неизвестный тип данных");
        }
    }
}