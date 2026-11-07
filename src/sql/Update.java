package sql;
import java.util.Map;

public class Update implements SqlCommand{
    private final String table_name;
    private final Map<String, String> update_val;
    private final Where condition;

    public Update (String table, Map<String, String> val, Where condition){
        table_name = table;
        update_val = val;
        this.condition = condition;
    }

    @Override
    public String getTableName(){
        return table_name;
    }

    public Map<String, String> getUpdateValues(){
        return update_val;
    }

    public  Where getCondition(){
        return condition;
    }
}
