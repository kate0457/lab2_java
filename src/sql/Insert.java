package sql;
import java.util.List;

public class Insert implements SqlCommand{
    private final String table_name;                                                    //название таблицы
    private final List<String> raw_val;                                                 //список добавляемых значений
    private final List<String> col_name;                                                //список столбиков куда надо записать значения

    public Insert(String table_name, List<String> raw_val, List<String> col_name){      //конструктор
        this.table_name = table_name;
        this.raw_val = raw_val;
        this.col_name = col_name;
    }

    @Override
    public String getTableName(){                                                       //переопределение метода из SqlCommand
        return table_name;
    }

    public List<String> getRawValues(){
        return raw_val;
    }                              //получаем вставляемые значения

    public List<String> getColumnsName(){                                               //получаем столбцы куда надо вставить
        return col_name;
    }
}
