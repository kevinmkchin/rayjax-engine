import engine.Level;
import engine.Screen;
import engine.Texture;
import entity.Camera;
import exception.MapBadDataException;
import mapeditor.MapEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.HashMap;

public class Main extends JFrame implements Runnable {

    private static final long serialVersionUID = 1L;
    public int renderWidth = 640; //render resolution
    public int renderHeight = 360;
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

    public Character[][] walls, floors, ceilings;


    public Main(){
        thread = new Thread(this);
        image = new BufferedImage(renderWidth, renderHeight, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

        //TODO get camera starting x and y from level data
        camera = new Camera(4.5, 4.5, 1, 0, 0, -1); //TODO play with yp for diff fov
        //if direction vector is same length as plane vector, then FOV is 90 degrees.
        addKeyListener(camera);
        addMouseMotionListener(camera);

        try {
            Level level1 = new Level("test.raymap");
            walls = level1.getWallArray();
            floors = level1.getFloorArray();
            ceilings = level1.getCeilArray();
        } catch (MapBadDataException e) {
            e.printStackTrace();
        }

        //get charMap<String, Texture> from MapEditor JFrame then dispose it
        MapEditor tempMapEditor = new MapEditor();
        HashMap<String, Texture> charMapFromEditor = tempMapEditor.getMasterCharacterMap();
        tempMapEditor.setVisible(false);
        tempMapEditor.dispose();

        //create a new HashMap, replacing the String key with respective Character from String
        textures = new HashMap<>();
        for(HashMap.Entry<String, Texture> entry : charMapFromEditor.entrySet()){
            textures.put(entry.getKey().charAt(0), entry.getValue());
        }
        textures.put("1".charAt(0), Texture.stone);
        textures.put("2".charAt(0), Texture.wood);
        textures.put("3".charAt(0), Texture.brick);
        textures.put("4".charAt(0), Texture.bluestone);


        screen = new Screen(walls, floors, ceilings, textures, renderWidth, renderHeight);

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
        camera.update(walls);
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
