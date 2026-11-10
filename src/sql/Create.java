package sql;
import java.util.List;

public class Create implements SqlCommand{
    private final String table_name;
    private final List<String> col_name;
    private  final List<String> col_type;

    public Create(String table, List<String> name, List<String> type){
        table_name = table;
        col_name = name;
        col_type = type;
    }

    @Override
    public String getTableName(){
        return table_name;
    }

    public List<String> getColumnName(){
        return col_name;
    }
    public List<String> getColumnType(){
        return col_type;
    }
}
