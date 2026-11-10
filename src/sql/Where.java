package sql;
import java.util.List;
import java.util.ArrayList;

public class Where{
    public static class SingleCondition{                                            //одно конкретное условие
        private final String col_name;                                              //название столбца, где должны что-то поменять
        private final String operator;                                              //какая операция происходит
        private final String value;                                                 //на что конкретно меняем

        public SingleCondition(String column, String operator, String value){       //конструктор
            col_name = column;
            this.operator = operator;
            this.value = value;
        }

        public String getColumnName(){
            return col_name;
        }                          //возвращает название столбца

        public String getOperator(){
            return operator;
        }                            //возвращает операцию

        public String getValue(){
            return value;
        }                                  //возвращает значение
    }

    private final List<SingleCondition> conditions = new ArrayList<>();             //список всех одиночных условий
    private final List<String> log_operator = new ArrayList<>();                    //список логических операторов AND и OR, которые соединяют условия

    public Where(List<SingleCondition> conditions, List<String> log_operator){      //конструктор
        this.conditions.addAll(conditions);
        this.log_operator.addAll(log_operator);
    }

    public List<SingleCondition> getConditions(){
        return conditions;
    }             //возвращает список условий

    public List<String> getLogicalOperator(){
        return log_operator;
    }               //возвращает список логических операторов
}
