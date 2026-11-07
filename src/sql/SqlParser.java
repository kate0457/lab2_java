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
        System.out.println(tokens);
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
                for (int i = 5;  i < tokens.size(); i++){
                    String token = tokens.get(i);
                    if (token.equals(")")){
                        break;
                    }
                    raw_val.add(token);
                }
                System.out.println(table_name);
                System.out.print(raw_val);
                return new Insert(table_name, raw_val);
            }
            case "SELECT":{
                if (tokens.get(1).equals("*")){
                    String table_name = tokens.get(3);
                    System.out.println(table_name);
                    return new Select(table_name);
                }
                else{
                    List<String> col = new ArrayList<>();
                    int index = 0;
                    for (int i = 1; i < tokens.size(); i++){
                        String token = tokens.get(i);
                        if (token.equals("FROM")){
                            index = i;
                            break;
                        }
                        col.add(token);
                    }
                    System.out.println(tokens.get(index + 1));
                    System.out.println(col);
                    return new Select(tokens.get(index + 1), col);
                }
            }
            case "UPDATE":{
                String table_name = tokens.get(1);
                Map<String, String> update_val = new HashMap<>();
                int i = 3;
                while (i < tokens.size()){
                    String token = tokens.get(i);
                    if (token.toUpperCase().equals("WHERE")){
                        break;
                    }
                    String col_name = token;
                    if(i + 2 < tokens.size() && tokens.get(i + 1).equals("=")){
                        String val = tokens.get(i + 2);
                        update_val.put(col_name, val);
                        i += 2;
                    }
                    i++;
                }
                System.out.println(table_name);
                System.out.println(update_val);
                return new Update(table_name, update_val);
            }
            case "DELETE":
                throw new UnsupportedOperationException("Команда еще не реализована");
            case "CREATE":
                throw new UnsupportedOperationException("Команда еще не реализована");
            case "DROP":
                throw new UnsupportedOperationException("Команда еще не реализована");
            default:
                throw new UnsupportedOperationException("Команда еще не реализована");
        }
    }
}
