package sql;

public class Drop implements SqlCommand{
    private final String table_name;

    public Drop(String name){
        table_name = name;
    }

    @Override
    public String getTableName(){
        return table_name;
    }
}
