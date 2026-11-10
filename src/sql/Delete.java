package sql;

public class Delete implements SqlCommand{
    private final String table_name;
    private final Where condition;

    public Delete(String table, Where condition){
        table_name = table;
        this.condition = condition;
    }

    @Override
    public String getTableName(){
        return table_name;
    }

    public Where getCondition(){
        return condition;
    }
}