package sql;

import java.util.List;
import java.util.ArrayList;
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
}
