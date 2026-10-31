package types;

public class Str implements DataType {
    @Override //переопределяем метод из интерфейса
    public String get_name() {
        return "string";
    }

    @Override
    public Object parse(String str){
        try {

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ожидалось целое число (int), а мы имеем: " + str);
        }
    }

    @Override
    public void correct(Object value){
        if (value instanceof String){

        } else {
            throw new IllegalArgumentException("Ожидался тип string, имеем: " + value.getClass().getSimpleName());
        }
    }

    @Override
    public String print(Object value){
        correct(value); //проверяем
        return value.toString();
    }
}
