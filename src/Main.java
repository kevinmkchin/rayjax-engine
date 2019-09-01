import exception.MapBadDataException;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.HashMap;

public class Main extends JFrame implements Runnable {

    private static final long serialVersionUID = 1L;
    public int renderWidth = 320; //render resolution
    public int renderHeight = 180;
    public int screenWidth = 1280; //display resolution
    public int screenHeight = 720;
    public final int FPS = 60;
    private Thread thread;
    private boolean running;
    private BufferedImage image;
    public int[] pixels;
    public HashMap<Character, Texture> textures;
    public Camera camera;
    public Screen screen;


    public Character[][] map;// = {
//            {1,1,1,1,1,1,1,1,2,2,2,2,2,2,2},
//            {1,0,0,0,0,0,0,0,2,0,0,0,0,0,2},
//            {1,0,3,3,5,3,3,0,0,0,0,0,0,0,2},
//            {1,0,3,0,0,0,3,0,2,0,0,0,0,0,2},
//            {1,0,3,0,0,0,6,0,2,2,2,0,2,2,2},
//            {1,0,3,0,0,0,3,0,2,0,0,0,0,0,2},
//            {1,0,3,3,0,3,3,0,2,0,0,0,0,0,2},
//            {1,0,0,0,0,0,0,0,2,0,0,0,0,0,2},
//            {1,1,1,1,1,1,1,1,4,4,4,0,4,4,4},
//            {1,0,0,0,0,0,1,4,0,0,0,0,0,0,4},
//            {1,0,0,0,0,0,1,4,0,0,0,0,0,0,4},
//            {1,0,0,2,0,0,1,4,0,3,3,3,3,0,4},
//            {1,0,0,0,0,0,1,4,0,3,3,3,3,0,4},
//            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
//            {1,1,1,1,1,1,1,4,4,4,4,4,4,4,4}
//    };

    public Main(){
        thread = new Thread(this);
        image = new BufferedImage(renderWidth, renderHeight, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

        camera = new Camera(4.5, 4.5, 1, 0, 0, -0.84); //TODO play with yp for diff fov
        //if direction vector is same length as plane vector, then FOV is 90 degrees.
        addKeyListener(camera);
        addMouseMotionListener(camera);

        textures = new HashMap<>();

        textures.put("1".charAt(0), Texture.stone);
        textures.put("2".charAt(0), Texture.wood);
        textures.put("3".charAt(0), Texture.brick);
        textures.put("4".charAt(0), Texture.bluestone);
        textures.put("5".charAt(0), Texture.brick);
        textures.put("6".charAt(0), Texture.brick);

        try {
            Level level1 = new Level("map1.rajmap");
            map = level1.getWallArray();
        } catch (MapBadDataException e) {
            e.printStackTrace();
        }

        screen = new Screen(map, textures, renderWidth, renderHeight);

        Toolkit tk= getToolkit();
        Cursor transparent = tk.createCustomCursor(tk.getImage(""), new Point(), "trans");
        setCursor(transparent);

        setSize(screenWidth, screenHeight);
        setResizable(false);
        setTitle("Raycasting Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.BLUE); //default unity background color
        setLocationRelativeTo(null);
        setVisible(true);
        start();
    }

    private synchronized void start(){
        running = true;
        thread.start();
    }

    private synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime(); //when first ran, it is simply the time when it is ran
        final double ns = 1000000000.0 / (double)FPS; //this is how many nanoseconds for 1 tick during 60 ticks per sec
        double delta = 0;
        requestFocus();

        while(running){
            long now = System.nanoTime();
            delta = delta + ((now - lastTime) / ns);
            lastTime = now;

            while(delta >= 1){ //runs when time interval between ticks >= tick interval for given FPS
                /* Last line of this loop should be 'delta--' to increment delta by -1.
                 * We increments delta by -1 instead of setting it to 0 because if (for
                 * whatever reason) we ticked slower than 60 ticks per second for a given
                 * tick, the next tick will happen a little faster to accommodate that.*/
                tick();
                delta--;
            }

            render();
        }

    }

    /**THIS IS THE UPDATE METHOD (RUNNING AT GIVEN FPS/TICKS PER SECOND)
     * Handles all logic that occur at every tick.*/
    private void tick(){
        screen.update(camera, pixels);
        camera.update(map);
    }

    public void render(){
        BufferStrategy bs = getBufferStrategy();
        if(bs == null){
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, screenWidth, screenHeight, null);
        bs.show();
    }

    public static void main(String[] args){
        Main main = new Main();
    }

}
