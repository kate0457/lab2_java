package types;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateT implements DataType {
    //переопределяем методы из интерфейса DataType
    @Override
    public String get_name() {
        return "date";
    }

    @Override
    public Object parse(String str){
        String s = str.trim();

        try{
            //день.месяц.год
            String pattern = "dd.MM.yyyy";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDate.parse(s, formatter);
        } catch (Exception e){
            throw new IllegalArgumentException("Ожидалась дата в формате DD.MM.YYYY, а мы имеем: " + str);
        }
    }

    @Override
    public void correct(Object value){
        if (value instanceof LocalDate){
            //если нам передали дату, то все ок
        } else{
            throw new IllegalArgumentException("Ожидался тип LocalDate, имеем: " + value.getClass().getSimpleName());
        }
    }

    @Override
    public String print(Object value){
        correct(value); //check
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        //берем объект даты и превращаем его в строку с точками
        return "'" + ((LocalDate) value).format(formatter) + "'";
    }
}
