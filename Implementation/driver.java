import java.util.ArrayList;
public class driver {
    public static void drive(ArrayList array){
        if (array.get(0) == "commit"){
            //call commit
        }
        if (array.get(0) == "checkout"){
            Object filename = array.get(1);
            Object version = array.get(2);
            //call checkout with args
        }
        if (array.get(0) == "merge"){
            Object filename = array.get(1);
            Object version = array.get(2);
            //call merge with args
        }
        //DAG;
    }
}
