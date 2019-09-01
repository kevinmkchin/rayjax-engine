package exception;

public class MapBadDataException extends Exception {

    public MapBadDataException(){
        super();
        System.out.println("Some data in this map file is bad.");
    }

}
