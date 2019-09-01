import exception.MapArraysNotSameSizeException;
import exception.MapBadDataException;
import exception.MapMissingDataException;
import exception.NotSquareMapException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Level {


    //maps are divided into wallTileMaps, floorTileMaps, ceilingTileMaps, staticSpriteTileMaps?
    /*first string of characters must be wallTileMap
    * second string of characters must be floorTileMap
    * third string of characters must be ceilTileMap
    * fourth string of characters must be map information
    * every following string must be Sprite information*/


    public Character[][] wallArray, floorArray, ceilingArray;
    public ArrayList<Sprite> sprites;
    public int mapWidth;
    public int mapHeight;
    public static final String OPEN_SPACE = "0";


    public Level(String fileName) throws MapBadDataException{
        loadMap(fileName);
    }

    private void loadMap(String fileName) throws MapBadDataException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("res/map/" + fileName));
            String data = reader.readLine();
            String[] dataArray = data.split("/");

            if(dataArray.length < 3){
                throw new MapMissingDataException();
            }
            if(dataArray[0].length() / Math.sqrt(dataArray[0].length()) == dataArray[0].length()){
                throw new NotSquareMapException();
            }
            if(dataArray[0].length() != dataArray[1].length()
                    || dataArray[1].length() != dataArray[2].length()){
                throw new MapArraysNotSameSizeException();
            }

            mapWidth = (int) Math.sqrt(dataArray[0].length());
            mapHeight = mapWidth;

            wallArray = new Character[mapWidth][mapHeight];
            floorArray = new Character[mapWidth][mapHeight];
            ceilingArray = new Character[mapWidth][mapHeight];
            loadWalls(dataArray[0]);
            loadFloors(dataArray[1]);
            loadCeils(dataArray[2]);




        } catch (FileNotFoundException e) {
            System.out.println("Couldn't locate the map file.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO load into char[][] wallArray
    private void loadWalls(String wallData){
        for(int i=0; i<mapWidth; i++){
            for(int j=0; j<mapHeight; j++){
                wallArray[j][i] = wallData.charAt(j*mapWidth + i);
            }
        }
    }

    //TODO load into char[][] floorArray
    private void loadFloors(String floorData){

    }

    //TODO load into char[][] ceilArray
    private void loadCeils(String ceilData){

    }

    //TODO load into ArrayList<Sprite> sprites
    private void loadSprites(){

    }


    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public Character[][] getWallArray() {
        return wallArray;
    }

    public Character[][] getFloorArray() {
        return floorArray;
    }

    public Character[][] getCeilingArray() {
        return ceilingArray;
    }

    public ArrayList<Sprite> getSprites() {
        return sprites;
    }
}
