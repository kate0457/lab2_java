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

    public static void main(String[] args) {
        Scanner scaner = new Scanner(System.in);
        SqlParser parser = new SqlParser();
        DataBase bd = null;
        File cur_dir = null;

        while(cur_dir == null){
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

        if(files != null && files.length > 0){
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
    }
}