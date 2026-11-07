package sql;

public class Delete implements SqlCommand{
    private final String table_name;

    public Delete(String table){
        table_name = table;
    }

    @Override
    public String getTableName(){
        return table_name;
    }
}