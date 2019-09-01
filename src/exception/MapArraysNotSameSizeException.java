package exception;

public class MapArraysNotSameSizeException extends MapBadDataException {

    public MapArraysNotSameSizeException(){
        super();
        System.out.println("Map file's walls, floors, ceiling arrays are not the same size.");
    }

}
