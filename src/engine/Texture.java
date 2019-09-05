package engine;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Texture {


    public static Texture wood = new Texture("res/texture/wood.png", 64);
    public static Texture brick = new Texture("res/texture/redbrick.png", 64);
    public static Texture bluestone = new Texture("res/texture/bluestone.png", 64);
    public static Texture stone = new Texture("res/texture/greystone.png", 64);


    public final int SIZE;
    public int[] pixels;
    private String loc;
    private BufferedImage image;


    public Texture(String location, int size){
        this.loc = location;
        this.SIZE = size;
        pixels = new int[SIZE * SIZE];
        load();
    }

    private void load(){
        try {
            image = ImageIO.read(new File(loc));
            int w = image.getWidth();
            int h = image.getHeight();
            image.getRGB(0,0,w,h,pixels,0,w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage getImage(){
        return image;
    }


}
