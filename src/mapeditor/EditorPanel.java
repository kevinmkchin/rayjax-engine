package mapeditor;

import engine.Level;
import engine.Texture;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EditorPanel extends JPanel {

    private Level level;
    private Character[][] arrayForDisplay;
    private int editorW;
    private int tileSize;
    MapEditor frame;

    public EditorPanel(int editorW, MapEditor god){
        this.editorW = editorW;
        frame = god;
    }

    //replaces the clicked tile with the selected character
    public void drawTile(String character, int x, int y){
        int i = x / tileSize;
        int j = y / tileSize;
        arrayForDisplay[j][i] = character.charAt(0);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        this.setBackground(Color.LIGHT_GRAY);

        if(level != null){
            int mapW = level.getMapWidth();
            int mapH = level.getMapHeight();

            for(int i=0; i<mapW; i++){
                for(int j=0; j<mapH; j++){
                    String currentCharacter = arrayForDisplay[i][j].toString();
                    Texture matchingTexture = frame.getMasterCharacterMap().get(currentCharacter);
                    if(matchingTexture == null){ continue; }

                    BufferedImage image = matchingTexture.getImage();

                    g.drawImage(image, j*tileSize, i*tileSize, tileSize, tileSize, null);
                }
            }
        }

    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
        tileSize = editorW / level.getMapWidth(); //rounds down cuz integer
        //for zoom, just double tileSize
    }

    public Character[][] getArrayForDisplay() {
        return arrayForDisplay;
    }

    public void setArrayForDisplay(Character[][] arrayForDisplay) {
        this.arrayForDisplay = arrayForDisplay;
    }

    public int getTileSize() {
        return tileSize;
    }

    public void setTileSize(int tileSize) {
        this.tileSize = tileSize;
    }
}
