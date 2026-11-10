package sql;
import java.util.Map;

public class Update implements SqlCommand{
    private final String table_name;                                                //название таблицы
    private final Map<String, String> update_val;                                   //словарь типа: в каком столбце надо поменять - на что меняем
    private final Where condition;                                                  //блок WHERE

    public Update (String table, Map<String, String> val, Where condition){         //конструктор
        table_name = table;
        update_val = val;
        this.condition = condition;
    }

    @Override
    public String getTableName(){
        return table_name;
    }                           //переопределение метода из SqlCommand

    public Map<String, String> getUpdateValues(){
        return update_val;
    }           //возвращает словарь

    public  Where getCondition(){                                                 //возвращает блок WHERE
        return condition;
    }
}
