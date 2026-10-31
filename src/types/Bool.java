package types;

public class Bool implements DataType{
    @Override //переопределяем метод из интерфейса
    public String get_name() {
        return "boolean";
    }

    @Override
    public Object parse(String str){
        String s = str.trim().toUpperCase(); //к верхнему регистру
        if (s.equals("TRUE") || s.equals("1")) {
            return true;
        }
        if (s.equals("FALSE") || s.equals("0")) {
            return false;
        }

        throw new IllegalArgumentException("Ожидался тип boolean, а мы имеем: " + str);
    }

    @Override
    public void correct(Object value){
        if (value instanceof Boolean){

        } else {
            throw new IllegalArgumentException("Ожидался тип boolean, имеем: " + value.getClass().getSimpleName());
        }
    }

    @Override
    public String print(Object value){
        correct(value); //проверяем
        return value.toString();  //выведет true или false
    }
}
