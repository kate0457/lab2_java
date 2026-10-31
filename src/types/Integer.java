package types;

public class Integer implements DataType {
    @Override //переопределяем метод из интерфейса
    public String get_name() {
        return "int";
    }

    @Override
    public Object parse(String str){
        try {
            return Integer.parseInt(str.trim())
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ожидалось целое число (int), а мы имеем: " + str);
        }
    }

    @Override
    public void validate(Object value){
        if (value instanceof Integer){

        } else {
            throw new IllegalArgumentException("Ожидался тип int, имеем: " + value.getClass().getSimpleName());
        }
    }
}
