package types;

public class Doub implements DataType {
    @Override //переопределяем метод из интерфейса
    public String get_name() {
        return "double";
    }

    @Override
    public Object parse(String str){
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ожидалось дробное число (double), а мы имеем: " + str);
        }
    }

    @Override
    public void correct(Object value){
        if (value instanceof Double){

        } else {
            throw new IllegalArgumentException("Ожидался тип double, имеем: " + value.getClass().getSimpleName());
        }
    }

    @Override
    public String print(Object value){
        correct(value); //проверяем
        return value.toString();
    }
}
