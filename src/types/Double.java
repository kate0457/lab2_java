package types;

public class Double implements DataType {
    @Override //переопределяем метод из интерфейса
    public Str get_name() {
        return "double";
    }

    @Override
    public Object parse(Str str){
        try {
            return Integer.parseInt(str.trim());
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
    public Str print(Object value){
        correct(value); //проверяем
        return value.toString();
    }
}
