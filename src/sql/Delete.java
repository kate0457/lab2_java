package sql;

public class Delete implements SqlCommand{
    private final String table_name;                                //название таблицы
    private final Where condition;                                  //блок WHERE

    public Delete(String table, Where condition){                   //конструктор
        table_name = table;
        this.condition = condition;
    }

    @Override
    public String getTableName(){
        return table_name;
    }             //переопределение метода из SqlCommand

    public Where getCondition(){                                    //возвращает блок WHERE
        return condition;
    }
}