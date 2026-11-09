package app;
import core.*;
import sql.*;
import types.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
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
                            System.out.println("ошибка, неудалось открыть базу данных");
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
    }
}