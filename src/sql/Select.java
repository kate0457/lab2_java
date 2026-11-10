package sql;

import java.util.ArrayList;
import java.util.List;

public class Select implements SqlCommand{
    private final String table_name;                                            //название таблицы
    private final List<String> col_name;                                        //список колонок, которые надо будет потом вывести
    private final Where condition;                                              //блок WHERE

    public Select(String table, List<String> filter_col, Where condition){      //конструктор
        table_name = table;
        col_name = filter_col;
        this.condition = condition;
    }

    @Override
    public String getTableName(){
        return table_name;
    }                         //переопределение метода из SqlCommand

    public List<String> getFilterColumnName(){
        return col_name;
    }              //возвращает список колонок

    public  Where getCondition(){
        return condition;
    }                          //возвращает блок WHERE
}
