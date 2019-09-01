package exception;

public class MapMissingDataException extends MapBadDataException {

    public MapMissingDataException(){
        super();
        System.out.println("Map is missing one or more of wallData, floorData, ceilData, mapInfo.");
    }

}
