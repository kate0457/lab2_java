package types;
import java.io.Serializable;

public interface DataType extends Serializable{
    String get_name();                                          //возвращ название типа
    Object parse(String str) throws IllegalArgumentException;   //из sql в объект
    void correct(Object value) throws IllegalArgumentException; //проверка подходит ли для этого типа
    String print(Object value);                                 //вывод
}
