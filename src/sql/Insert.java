package sql;
import java.util.List;

public class Insert implements SqlCommand{
    private final String table_name;
    private final List<String> raw_val;

    public Insert(String table_name, List<String> raw_val){
        this.table_name = table_name;
        this.raw_val = raw_val;
    }

    @Override
    public String getTableName(){
        return table_name;
    }

    public List<String> getRawvalues(){
        return raw_val;
    }
}
