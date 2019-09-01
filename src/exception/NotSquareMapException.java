package exception;

public class NotSquareMapException extends MapBadDataException {

    public NotSquareMapException(){
        super();
        System.out.println("Map's height does not match its width.");
    }

}
