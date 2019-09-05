package mapeditor;

import engine.Level;
import engine.Texture;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EditorPanel extends JPanel {

    private Level level;
    private int editorW;
    private Character[][] arrayForDisplay;
    private int tileSize;

    public EditorPanel(int editorW){
        this.editorW = editorW;
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        this.setBackground(Color.LIGHT_GRAY);

        if(level != null){
            int mapW = level.getMapWidth();
            int mapH = level.getMapHeight();

            tileSize = editorW / mapW; //rounds down cuz integer
            //for zoom, just double tileSize

            for(int i=0; i<mapW; i++){
                for(int j=0; j<mapH; j++){
                    BufferedImage img = getImageFromCharacter(arrayForDisplay[i][j]);
                    //System.out.println(img);
                    g.drawImage(img, j*tileSize, i*tileSize, tileSize, tileSize, null);
                }
            }
        }

    }

    private BufferedImage getImageFromCharacter(Character c){
        switch(c.toString()){
            case "1":
                return Texture.stone.getImage();
            case "2":
                return Texture.wood.getImage();
            case "3":
                return Texture.brick.getImage();
            case "4":
                return Texture.bluestone.getImage();
            case "5":
                return Texture.brick.getImage();
            case "6":
                return Texture.brick.getImage();
            case "a":
                return Texture.wood.getImage();
            default:
                return null;
        }
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
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
