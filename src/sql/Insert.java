package sql;
import java.util.List;

public class Insert implements SqlCommand{
    private final String table_name;
    private final List<String> raw_val;
    private final List<String> col_name;

    public Insert(String table_name, List<String> raw_val, List<String> col_name){
        this.table_name = table_name;
        this.raw_val = raw_val;
        this.col_name = col_name;
    }

    @Override
    public String getTableName(){
        return table_name;
    }

    public List<String> getRawValues(){
        return raw_val;
    }

    public List<String> getColumnsName(){
        return col_name;
    }
}
