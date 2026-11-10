package sql;

public class Drop implements SqlCommand{
    private final String table_name;                        //название таблицы

    public Drop(String name){
        table_name = name;
    }         //конструктор

    @Override
    public String getTableName(){                           //переопределение метода из SqlCommand
        return table_name;
    }
}
