package types;

public class Int implements DataType {
    @Override //переопределяем метод из интерфейса
    public String get_name() {
        return "int";
    }

    @Override
    public Object parse(String str){
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ожидалось целое число (int), а мы имеем: " + str);
        }
    }

    @Override
    public void correct(Object value){
        if (value instanceof Integer){

        } else {
            throw new IllegalArgumentException("Ожидался тип int, имеем: " + value.getClass().getSimpleName());
        }
    }

    @Override
    public String print(Object value){
        correct(value); //проверяем
        return value.toString();
    }
}
