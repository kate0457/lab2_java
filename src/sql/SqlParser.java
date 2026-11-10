package sql;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlParser {
    List<String> tokenize(String promt){
        final Pattern token_patern = Pattern.compile("'[^']*'|\"[^\"]*\"|[a-zA-Z0-9_.]+|[<>=!]+|[()*]");

        List<String> tokens = new ArrayList<>();
        Matcher matcher = token_patern.matcher(promt.trim());

        while (matcher.find()){
            String token = matcher.group();
            if ((token.startsWith("'") && token.endsWith("'")) || token.startsWith("\"") && token.endsWith("\"")){
                token = token.substring(1, token.length() - 1);
            }
            tokens.add(token);
        }
        return tokens;
    }

    public SqlCommand parse(String promt){
        List<String> tokens = tokenize(promt);
        if (tokens.isEmpty()){
            throw new UnsupportedOperationException("вы ввели пустоту");
        }
        String first_word = tokens.getFirst().toUpperCase();
        switch (first_word){
            case "INSERT": {
                String table_name = tokens.get(2);
                List<String> raw_val = new ArrayList<>();
                List<String> col_name = null;
                int values_index = 0;
                for (int i = 3; i < tokens.size(); i++){
                    if (tokens.get(i).toUpperCase().equals("VALUES")){
                        values_index = i;
                        break;
                    }
                }

                if (values_index == 0){
                    throw new IllegalArgumentException("Ошибка синтаксиса INSERT: пропущено ключевое слово VALUES");
                }

                if (tokens.get(3).equals("(")){
                    col_name = new ArrayList<>();
                    for (int i = 4; i < values_index; i++){
                        String token = tokens.get(i);
                        if (token.equals(")")){
                            break;
                        }
                        col_name.add(token);
                    }
                }

                for (int i = (values_index + 2); i < tokens.size(); i++){
                    String token = tokens.get(i);
                    if (token.equals(")")){
                        break;
                    }
                    raw_val.add(token);
                }
                return new Insert(table_name, raw_val, col_name);
            }
            case "SELECT":{
                if (tokens.get(1).equals("*")){
                    String table_name = tokens.get(3);
                    Where final_cond = null;
                    if (tokens.size() > 4 && tokens.get(4).toUpperCase().equals("WHERE")){
                        List<Where.SingleCondition> cond = new ArrayList<>();
                        List<String> log_op = new ArrayList<>();
                        String col_name = tokens.get(5);
                        String operator = tokens.get(6);
                        String value = tokens.get(7);
                        cond.add(new Where.SingleCondition(col_name, operator, value));
                        if (tokens.size() > 9) {
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
                                        throw new IllegalArgumentException("Неполное условие после оператора ");
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
                    List<String> col_name = new ArrayList<>();
                    return new Select(table_name, col_name, final_cond);
                }
                else{
                    List<String> col = new ArrayList<>();
                    Where final_cond = null;
                    int index = 0;
                    for (int i = 1; i < tokens.size(); i++){
                        String token = tokens.get(i);
                        if (token.equals("FROM")){
                            index = i;
                            break;
                        }
                        col.add(token);
                    }

                    if (tokens.size() > (index + 2) && tokens.get(index + 2).toUpperCase().equals("WHERE")){
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
                                        throw new IllegalArgumentException("Неполное условие после оператора ");
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
                    return new Select(tokens.get(index + 1), col, final_cond);
                }
            }
            case "UPDATE":{
                String table_name = tokens.get(1);
                Map<String, String> update_val = new HashMap<>();
                Where final_cond = null;
                int i = 3;
                while (i < tokens.size()){
                    String token = tokens.get(i);
                    if (token.toUpperCase().equals("WHERE") && (i + 3) < tokens.size()){
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
                                        throw new IllegalArgumentException("Неполное условие после оператора ");
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
                    if(i + 2 < tokens.size() && tokens.get(i + 1).equals("=") && !(tokens.get(i).equals("WHERE"))){
                        String val = tokens.get(i + 2);
                        update_val.put(col_name, val);
                        i += 2;
                    }
                    i++;
                }
                return new Update(table_name, update_val, final_cond);
            }
            case "DELETE": {
                String table_name = tokens.get(2);
                Where final_cond = null;
                if (tokens.get(3).toUpperCase().equals("WHERE") && tokens.size() > 6){
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
                                    throw new IllegalArgumentException("Неполное условие после оператора ");
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
                return new Delete(table_name, final_cond);
            }
            case "CREATE":{
                String table_name = tokens.get(2);
                List<String> col_name = new ArrayList<>();
                List<String> col_type = new ArrayList<>();
                int i = 4;
                while (i < tokens.size()){
                    String token = tokens.get(i);
                    if (token.equals(")")){
                        break;
                    }
                    col_name.add(token);
                    col_type.add(tokens.get(i + 1));
                    i += 2;
                }
                return new Create(table_name, col_name, col_type);
            }
            case "DROP":
                String table_name = tokens.get(2);
                return new Drop(table_name);
            default:
                throw new UnsupportedOperationException("Команда еще не реализована");
        }
    }
}