package sql;
import java.util.List;

public class Create implements SqlCommand{
    private final String table_name;                                        //название таблицы
    private final List<String> col_name;                                    //название колонок
    private  final List<String> col_type;                                   //типы данных в колонках

    public Create(String table, List<String> name, List<String> type){      //конструктор
        table_name = table;
        col_name = name;
        col_type = type;
    }

    @Override
    public String getTableName(){
        return table_name;
    }                     //переделка функции из SqlCommand

    public List<String> getColumnName(){
        return col_name;
    }                //возвращает значение название колонок
    public List<String> getColumnType(){                                    //возвращает тип данных столбиков
        return col_type;
    }
}
