package sql;

import java.util.ArrayList;
import java.util.List;

public class Select implements SqlCommand{
    private final String table_name;
    private final List<String> col_name;
    private final Where condition;

    public Select(String table, List<String> filter_col, Where condition){
        table_name = table;
        col_name = filter_col;
        this.condition = condition;
    }

    @Override
    public String getTableName(){
        return table_name;
    }

    public List<String> getFilterColumnName(){
        return col_name;
    }

    public  Where getCondition(){
        return condition;
    }
}
