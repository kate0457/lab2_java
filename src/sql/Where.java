package sql;
import java.util.List;
import java.util.ArrayList;

public class Where{
    public static class SingleCondition{
        private final String col_name;
        private final String operator;
        private final String value;

        public SingleCondition(String column, String operator, String value){
            col_name = column;
            this.operator = operator;
            this.value = value;
        }

        public String getColumnName(){
            return col_name;
        }

        public String getOperator(){
            return operator;
        }

        public String getValue(){
            return value;
        }

        void print(){
            System.out.println(getColumnName());
            System.out.println(getOperator());
            System.out.println(getValue());
        }
    }

    private final List<SingleCondition> conditions = new ArrayList<>();
    private final List<String> log_operator = new ArrayList<>();

    public Where(List<SingleCondition> conditions, List<String> log_operator){
        this.conditions.addAll(conditions);
        this.log_operator.addAll(log_operator);
    }

    public List<SingleCondition> getConditions(){
        return conditions;
    }

    public List<String> getLogicalOperator(){
        return log_operator;
    }

    void print(){
        for (SingleCondition cond : conditions){
            cond.print();
        }
        System.out.println(getLogicalOperator());
    }
}
