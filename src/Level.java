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


    public Character[][] wallArray, floorArray, ceilArray;
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

            if(dataArray.length < 3){//TODO change 3 to 4 after implementing map info
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
            ceilArray = new Character[mapWidth][mapHeight];
            loadWalls(dataArray[0]);
            loadFloors(dataArray[1]);
            loadCeils(dataArray[2]);

            //TODO deal w sprites here

        } catch (FileNotFoundException e) {
            System.out.println("Couldn't locate the map file.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadWalls(String wallData){
        loadToArray(wallArray, wallData);
    }

    private void loadFloors(String floorData){
        loadToArray(floorArray, floorData);
    }

    private void loadCeils(String ceilData){
        loadToArray(ceilArray, ceilData);
    }

    private void loadToArray(Character[][] mapArray, String arrayData){
        for(int i=0; i<mapWidth; i++){
            for(int j=0; j<mapHeight; j++){
                mapArray[j][i] = arrayData.charAt(j*mapWidth + i);
            }
        }
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

    public Character[][] getCeilArray() {
        return ceilArray;
    }

    public ArrayList<Sprite> getSprites() {
        return sprites;
    }
}
