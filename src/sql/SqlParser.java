package sql;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlParser {
    List<String> tokenize(String promt){                                                                              //разделяем запрос на отдельные слова
        final Pattern token_patern = Pattern.compile("'[^']*'|\"[^\"]*\"|[a-zA-Z0-9_.]+|[<>=!]+|[()*]");        //маска для поиска слов в запросе, при чем если передать имя фамилию в кавычках, это распознается как одно слово

        List<String> tokens = new ArrayList<>();
        Matcher matcher = token_patern.matcher(promt.trim());

        while (matcher.find()){                                                                                       //ищем совпадения в полученной строчке
            String token = matcher.group();                                                                           //если что-то нашли, то записываем в переменную
            if ((token.startsWith("'") && token.endsWith("'")) || token.startsWith("\"") && token.endsWith("\"")){    //удаляем кавычки, чтоб они не добавлялись как часть строки
                token = token.substring(1, token.length() - 1);
            }
            tokens.add(token);
        }
        return tokens;
    }

    public SqlCommand parse(String promt){                                                                            //функция, которая конкретно осознает что за запрос и что с ним надо делать, чтоб дальше в core и в app все работало
        List<String> tokens = tokenize(promt);
        if (tokens.isEmpty()){
            throw new UnsupportedOperationException("Ошибка: Вы ввели пустоту");
        }
        String first_word = tokens.getFirst().toUpperCase();                                                          //берем первое слово, тк у всех наших команд оно разное и по нему оч удобно различать команды
        switch (first_word){
            case "INSERT": {                                                                                          //разбираем insert
                if (tokens.size() < 2 || !tokens.get(1).toUpperCase().equals("INTO")) {
                    throw new IllegalArgumentException("Ошибка: пропущено ключевое слово INTO");
                }

                if (tokens.size() < 3) {
                    throw new IllegalArgumentException("Ошибка: пропущено имя таблицы");
                }

                String table_name = tokens.get(2).trim();                                                                    //берем имя таблицы, так как это единственный обязательный атрибут дял всех команд
                if (table_name.toUpperCase().equals("VALUES")) {
                    throw new IllegalArgumentException("Ошибка: пропущено имя таблицы");
                }

                List<String> raw_val = new ArrayList<>();
                List<String> col_name = null;
                int values_index = 0;
                for (int i = 3; i < tokens.size(); i++){                                                              //ищем где у нас находится VALUES, тк существует два вида INSERT и от этого мы пляшем как и что мы добавляем в таблицу
                    if (tokens.get(i).toUpperCase().equals("VALUES")){
                        values_index = i;
                        break;
                    }
                }

                if (values_index == 0){
                    throw new IllegalArgumentException("Ошибка: пропущено ключевое слово VALUES");
                }

                if (tokens.get(3).equals("(")){                                                                       //если у нас сначала идут столбики куда добавить значения, а потом VALUES и значения
                    col_name = new ArrayList<>();
                    for (int i = 4; i < values_index; i++){
                        String token = tokens.get(i);
                        if (token.equals(")")){
                            break;
                        }
                        col_name.add(token);                                                                          //добавляем в список, чтоб потом по нему искать куда что добавлять и есть ли вообще такие столбики
                    }
                }

                for (int i = (values_index + 2); i < tokens.size(); i++){                                             //отбираем сами значения, которые нужно добавить в таблицу, values_index + 2, тк +0 это само слово VALUES, +1 открывающаяся скобка
                    String token = tokens.get(i);
                    if (token.equals(")")){
                        break;
                    }
                    raw_val.add(token);
                }
                return new Insert(table_name, raw_val, col_name);                                                     //возвращаем объект типа INSERT
            }
            case "SELECT":{                                                                                           //разбираем SELECT
                if (tokens.size() < 2) {
                    throw new IllegalArgumentException("Ошибка: пропущены колонки или символ *");
                }

                if (tokens.get(1).toUpperCase().equals("FROM")) {
                    throw new IllegalArgumentException("Ошибка: не указано, что именно выбрать (символ * или имена колонок)");
                }
                if (tokens.get(1).equals("*")){                                                                       //если нужно достать все столбцы
                    if (tokens.size() < 4) {
                        throw new IllegalArgumentException("Ошибка: пропущено имя таблицы после FROM");
                    }
                    String table_name = tokens.get(3).trim();
                    Where final_cond = null;                                                                          //блок WHERE по-умолчанию делаем null, тк не везде это условие может быть
                    if (tokens.size() > 4 && tokens.get(4).toUpperCase().equals("WHERE")){                            //если все-таки условия на отбор есть
                        List<Where.SingleCondition> cond = new ArrayList<>();
                        List<String> log_op = new ArrayList<>();
                        String col_name = tokens.get(5);                                                              //вручную вводим первое условие, потому что оно может быть одно
                        String operator = tokens.get(6);
                        String value = tokens.get(7);
                        cond.add(new Where.SingleCondition(col_name, operator, value));
                        if (tokens.size() > 9) {                                                                      //если больше двух условий, то пока не дойдем до конца строчки, не остановимся
                            int i = 8;
                            while(i < tokens.size()){
                                if (tokens.get(i).toUpperCase().equals("AND") || tokens.get(i).toUpperCase().equals("OR")){
                                    log_op.add(tokens.get(i));
                                    if (i + 3 < tokens.size()){
                                        String next_col_name = tokens.get(i + 1);
                                        String next_operator = tokens.get(i + 2);
                                        String next_value = tokens.get(i + 3);
                                        cond.add(new Where.SingleCondition(next_col_name, next_operator, next_value));
                                    }
                                    else{
                                        throw new IllegalArgumentException("Неполное условие после оператора " + tokens.get(i));      //если не дописали условие, то вылезает вот эта ошибка
                                    }
                                }
                                else{
                                    break;
                                }
                                i += 4;
                            }
                        }
                        final_cond = new Where(cond, log_op);                                                         //заполняем объект класса WHERE
                    }
                    List<String> col_name = new ArrayList<>();
                    return new Select(table_name, col_name, final_cond);                                              //обработка SELECT готова
                }
                else{                                                                                                 //если в SELECT передали конкретные столбики для вывода
                    List<String> col = new ArrayList<>();
                    Where final_cond = null;
                    int index = 0;
                    for (int i = 1; i < tokens.size(); i++){                                                          //забираем столбики для вывода плюсом находим индекс слова FROM, чтоб понять где у нас название таблицы и есть ли WHERE
                        String token = tokens.get(i);
                        if (token.equals("FROM")){
                            index = i;
                            break;
                        }
                        col.add(token);
                    }

                    if (index == 0) {
                        throw new IllegalArgumentException("Ошибка: пропущено ключевое слово FROM");
                    }

                    if (index + 1 >= tokens.size()) {
                        throw new IllegalArgumentException("Ошибка: пропущено имя таблицы после FROM");
                    }

                    if (tokens.size() > (index + 2) && tokens.get(index + 2).toUpperCase().equals("WHERE")){          //та же самая логика как и сверху, только немного поменяны индексы
                        List<Where.SingleCondition> cond = new ArrayList<>();
                        List<String> log_op = new ArrayList<>();
                        String col_name = tokens.get(index + 3);
                        String operator = tokens.get(index + 4);
                        String value = tokens.get(index + 5);
                        cond.add(new Where.SingleCondition(col_name, operator, value));
                        if (tokens.size() > (index + 9)) {
                            int i = index + 6;
                            while(i < tokens.size()){
                                if (tokens.get(i).toUpperCase().equals("AND") || tokens.get(i).toUpperCase().equals("OR")){
                                    log_op.add(tokens.get(i));
                                    if (i + 3 < tokens.size()){
                                        String next_col_name = tokens.get(i + 1);
                                        String next_operator = tokens.get(i + 2);
                                        String next_value = tokens.get(i + 3);
                                        cond.add(new Where.SingleCondition(next_col_name, next_operator, next_value));
                                    }
                                    else{
                                        throw new IllegalArgumentException("Неполное условие после оператора " + tokens.get(i));
                                    }
                                }
                                else{
                                    break;
                                }
                                i += 4;
                            }
                        }
                        final_cond = new Where(cond, log_op);
                    }
                    return new Select(tokens.get(index + 1).trim(), col, final_cond);
                }
            }
            case "UPDATE":{                                                                                           //обрабатываем UPDATE
                if (tokens.size() < 2) {
                    throw new IllegalArgumentException("Ошибка: пропущено имя таблицы");
                }
                String table_name = tokens.get(1).trim();
                if (table_name.toUpperCase().equals("SET")) {
                    throw new IllegalArgumentException("Ошибка: пропущено имя таблицы");
                }

                if (tokens.size() < 3 || !tokens.get(2).toUpperCase().equals("SET")) {
                    throw new IllegalArgumentException("Ошибка: пропущено ключевое слово SET");
                }

                Map<String, String> update_val = new HashMap<>();
                Where final_cond = null;
                int i = 3;                                                                                            //с 3 индекса, потому что скипаем UPDATE название_таблицы SET
                while (i < tokens.size()){
                    String token = tokens.get(i);
                    if (token.toUpperCase().equals("WHERE") && (i + 3) < tokens.size()){                              //если есть блок WHERE обрабатываем его как и до этого
                        List<Where.SingleCondition> cond = new ArrayList<>();
                        List<String> log_op = new ArrayList<>();
                        String col_name = tokens.get(i + 1);
                        String operator = tokens.get(i + 2);
                        String value = tokens.get(i + 3);
                        cond.add(new Where.SingleCondition(col_name, operator, value));
                        if (tokens.size() > (i + 4)) {
                            int index = i + 4;
                            while(index < tokens.size()){
                                if (tokens.get(index).toUpperCase().equals("AND") || tokens.get(index).toUpperCase().equals("OR")){
                                    log_op.add(tokens.get(index));
                                    if (index + 3 < tokens.size()){
                                        String next_col_name = tokens.get(index + 1);
                                        String next_operator = tokens.get(index + 2);
                                        String next_value = tokens.get(index + 3);
                                        cond.add(new Where.SingleCondition(next_col_name, next_operator, next_value));
                                    }
                                    else{
                                        throw new IllegalArgumentException("Неполное условие после оператора " + tokens.get(i));
                                    }
                                }
                                else{
                                    break;
                                }
                                index += 4;
                            }
                        }
                        final_cond = new Where(cond, log_op);
                    }
                    String col_name = token;
                    if(i + 2 < tokens.size() && tokens.get(i + 1).equals("=") && !(tokens.get(i).equals("WHERE"))){   //если действительно есть хотя бы одно значение на update
                        String val = tokens.get(i + 2);
                        update_val.put(col_name, val);
                        i += 2;
                    }
                    i++;
                }
                return new Update(table_name, update_val, final_cond);                                                //получаем готовый объект Uodate, с которым можно дальше работать
            }
            case "DELETE": {                                                                                          //разбираем запрос с DELETE
                if (tokens.size() < 2 || !tokens.get(1).toUpperCase().equals("FROM")) {
                    throw new IllegalArgumentException("Ошибка: пропущено ключевое слово FROM");
                }
                if (tokens.size() < 3) {
                    throw new IllegalArgumentException("Ошибка: пропущено имя таблицы");
                }
                String table_name = tokens.get(2).trim();
                Where final_cond = null;
                if (tokens.get(3).toUpperCase().equals("WHERE") && tokens.size() > 6){                                //как и везде разбираем запрос с блоком WHERE
                    List<Where.SingleCondition> cond = new ArrayList<>();
                    List<String> log_op = new ArrayList<>();
                    String col_name = tokens.get(4);
                    String operator = tokens.get(5);
                    String value = tokens.get(6);
                    cond.add(new Where.SingleCondition(col_name, operator, value));
                    if (tokens.size() > 9) {
                        int i = 7;
                        while(i < tokens.size()){
                            if (tokens.get(i).toUpperCase().equals("AND") || tokens.get(i).toUpperCase().equals("OR")){
                                log_op.add(tokens.get(i));
                                if (i + 3 < tokens.size()){
                                    String next_col_name = tokens.get(i + 1);
                                    String next_operator = tokens.get(i + 2);
                                    String next_value = tokens.get(i + 3);
                                    cond.add(new Where.SingleCondition(next_col_name, next_operator, next_value));
                                }
                                else{
                                    throw new IllegalArgumentException("Неполное условие после оператора " + tokens.get(i));
                                }
                            }
                            else{
                                break;
                            }
                            i += 4;
                        }
                    }
                    final_cond = new Where(cond, log_op);
                }
                return new Delete(table_name, final_cond);                                                            //получаем объект класса DELETE
            }
            case "CREATE":{                                                                                           //работает с запросом CREATE
                if (tokens.size() < 2 || !tokens.get(1).toUpperCase().equals("TABLE")) {
                    throw new IllegalArgumentException("Ошибка: пропущено ключевое слово TABLE");
                }
                if (tokens.size() < 3) {
                    throw new IllegalArgumentException("Ошибка: пропущено имя таблицы");
                }

                String table_name = tokens.get(2).trim();
                List<String> col_name = new ArrayList<>();
                List<String> col_type = new ArrayList<>();
                int i = 4;                                                                                            //начинаем с 4, тк пропускаем CREATE TABLE название_таблицы (
                while (i < tokens.size()){
                    String token = tokens.get(i);
                    if (token.equals(")")){
                        break;
                    }
                    col_name.add(token);
                    col_type.add(tokens.get(i + 1));
                    i += 2;                                                                                           // +=2 тк мы сразу разбираем значение: название колонки - тип данных
                }

                if (col_name.isEmpty()) {
                    throw new IllegalArgumentException("Ошибка: таблица должна содержать хотя бы одну колонку!");
                }

                return new Create(table_name, col_name, col_type);                                                    //готовый разобранный CREATE
            }
            case "DROP":                                                                                              //разбираем запрос с командой DROP
                if (tokens.size() < 2) {
                    throw new IllegalArgumentException("Ошибка: пропущено ключевое слово TABLE");
                }
                if (!tokens.get(1).toUpperCase().equals("TABLE")) {
                    throw new IllegalArgumentException("Ошибка: пропущено ключевое слово TABLE");
                }
                if (tokens.size() < 3) {
                    throw new IllegalArgumentException("Ошибка: пропущено имя таблицы");
                }

                String table_name = tokens.get(2).trim();
                return new Drop(table_name);                                                                          //для нее нужно лишь название таблицы
            default:                                                                                                  //если какие-то другие команды, для которых мы не написали реализации, выводится сообщение
                throw new UnsupportedOperationException("Команда еще не реализована, следите за обновлениями!:)");
        }
    }
}