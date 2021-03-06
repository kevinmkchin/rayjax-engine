package mapeditor;

import engine.Level;
import engine.Texture;
import exception.MapBadDataException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import static java.awt.Font.PLAIN;

public class MapEditor extends JFrame implements ActionListener, MouseMotionListener, MouseListener {


    private Level level;

    public int winW = 1600;
    public int winH = 900;
    public int editorW = 800;
    public int editorH = 800;

    public Color bgColor = Color.DARK_GRAY;
    public Color textColor = Color.WHITE;
    public Font font1 = new Font("Consolas", PLAIN, 18);

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newFile, saveFile, loadFile;
    private JFileChooser fc = new JFileChooser();
    private int menuBarHeight = 25;

    private HashMap<String, Texture> masterCharacterMap;
    private JList charList;
    private EditorPanel editorPanel;
    private JPanel toolPanel, bottomPanel, zoomPanel, texturePanel;
    private JLabel tpLabel;
    ButtonGroup editorSelect, zoomSelect;
    private JRadioButton wallEdit, floorEdit, ceilEdit, zoom1, zoom2, zoom3, zoom4, nozoom;
    private JButton fillScreenButton;
    public boolean wallEditing, floorEditing, ceilEditing, zoomed1, zoomed2, zoomed3, zoomed4, notzoomed;

    class TextureEntry {
        private String character;
        private Texture texture;

        TextureEntry(String c, Texture t){
            character = c;
            texture = t;
        }

        public String getCharacter() {
            return character;
        }

        public Texture getTexture() {
            return texture;
        }

        @Override
        public String toString() {
            if(texture == null){
                return "Texture: " + character + " | " + "empty";
            }else {
                return "Texture: " + character + " | " + texture.toString();
            }
        }
    }

    public MapEditor(){

        makeMasterCharacterMap();

        initializeGUI();

        addMouseListener(this);
        addMouseMotionListener(this);

        newFile(30,30); //By default, start a new map
        update();

        this.setJMenuBar(menuBar);
        this.setLayout(null);
        this.setSize(winW, winH);
        this.setResizable(false);
        this.setTitle("Rayjax Map Editor");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

    }

// ================================================================

    /** ALL TILE CHARACTER & TEXTURE DATA GO HERE
     *  creates a HashMap with tile character as key
     *  and corresponding Texture object as value.*/
    private void makeMasterCharacterMap(){
        masterCharacterMap = new HashMap<>();
        masterCharacterMap.put(Level.OPEN_SPACE, null);
        masterCharacterMap.put(Level.HOR_THIN_WALL, Texture.bluestone);
        masterCharacterMap.put(Level.VER_THIN_WALL, Texture.bluestone);
        masterCharacterMap.put("a", Texture.stone);
        masterCharacterMap.put("b", Texture.wood);
        masterCharacterMap.put("c", Texture.brick);
        masterCharacterMap.put("d", Texture.bluestone);
    }

// ================================================================

    private void initializeGUI(){

        // === Panels ===
        editorPanel = new EditorPanel(editorW, this);
        editorPanel.setSize(editorW,editorH);

        toolPanel = new JPanel();
        toolPanel.setBackground(bgColor);
        toolPanel.setLayout(new BorderLayout());
        toolPanel.setBounds(editorW,0,winW-editorW,editorH);

        bottomPanel = new JPanel();
        bottomPanel.setBackground(bgColor);
        bottomPanel.setBounds(0,editorH,winW,winH);

        zoomPanel = new JPanel();
        zoomPanel.setBackground(bgColor);
        zoomPanel.setLayout(new GridLayout(3, 2));

        texturePanel = new JPanel();
        texturePanel.setBackground(bgColor);

        // === Radio Buttons ===
        wallEdit = new JRadioButton("Edit WALLS", true);
        wallEditing = true;
        floorEdit = new JRadioButton("Edit FLOORS", false);
        ceilEdit = new JRadioButton("Edit CEILINGS", false);
        wallEdit.setFont(font1);
        wallEdit.setForeground(Color.WHITE);
        wallEdit.setBackground(bgColor);
        ceilEdit.setFont(font1);
        ceilEdit.setForeground(Color.WHITE);
        ceilEdit.setBackground(bgColor);
        floorEdit.setFont(font1);
        floorEdit.setForeground(Color.WHITE);
        floorEdit.setBackground(bgColor);
        wallEdit.addActionListener(this);
        floorEdit.addActionListener(this);
        ceilEdit.addActionListener(this);
        wallEdit.addActionListener(this);
        floorEdit.addActionListener(this);
        ceilEdit.addActionListener(this);
        editorSelect = new ButtonGroup();
        editorSelect.add(wallEdit);
        editorSelect.add(floorEdit);
        editorSelect.add(ceilEdit);
        bottomPanel.add(wallEdit);
        bottomPanel.add(floorEdit);
        bottomPanel.add(ceilEdit);

        zoom1 = new JRadioButton("Top-Left Zoom", false);
        zoom2 = new JRadioButton("Top-Right Zoom", false);
        zoom3 = new JRadioButton("Bottom-Left Zoom", false);
        zoom4 = new JRadioButton("Bottom-Right Zoom", false);
        nozoom = new JRadioButton("No Zoom", true);
        notzoomed = true;
        zoom1.setFont(font1);
        zoom2.setFont(font1);
        zoom3.setFont(font1);
        zoom4.setFont(font1);
        nozoom.setFont(font1);
        zoom1.setForeground(Color.WHITE);
        zoom2.setForeground(Color.WHITE);
        zoom3.setForeground(Color.WHITE);
        zoom4.setForeground(Color.WHITE);
        nozoom.setForeground(Color.WHITE);
        zoom1.setBackground(bgColor);
        zoom2.setBackground(bgColor);
        zoom3.setBackground(bgColor);
        zoom4.setBackground(bgColor);
        nozoom.setBackground(bgColor);
        zoom1.addActionListener(this);
        zoom2.addActionListener(this);
        zoom3.addActionListener(this);
        zoom4.addActionListener(this);
        nozoom.addActionListener(this);
        zoomSelect = new ButtonGroup();
        zoomSelect.add(zoom1);
        zoomSelect.add(zoom2);
        zoomSelect.add(zoom3);
        zoomSelect.add(zoom4);
        zoomSelect.add(nozoom);
        zoomPanel.add(zoom1);
        zoomPanel.add(zoom2);
        zoomPanel.add(zoom3);
        zoomPanel.add(zoom4);
        zoomPanel.add(nozoom);

        tpLabel = new JLabel("Select textures & Tools");
        tpLabel.setFont(font1);
        tpLabel.setForeground(textColor);
        toolPanel.add(tpLabel, BorderLayout.PAGE_START);
        toolPanel.add(zoomPanel, BorderLayout.PAGE_END);

        toolPanel.add(texturePanel, BorderLayout.CENTER);

        this.add(editorPanel);
        this.add(toolPanel);
        this.add(bottomPanel);

        //=== MENU BAR ===
        menuBar = new JMenuBar();
        menuBar.setPreferredSize(new Dimension(winW, menuBarHeight));
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.getAccessibleContext().setAccessibleDescription("File menu");
        newFile = new JMenuItem("New Map");
        newFile.addActionListener(this);
        saveFile = new JMenuItem("Save Map As...");
        saveFile.addActionListener(this);
        loadFile = new JMenuItem("Load Map");
        loadFile.addActionListener(this);
        fileMenu.add(newFile);
        fileMenu.add(saveFile);
        fileMenu.add(loadFile);
        menuBar.add(fileMenu);

        //Texture/Character selector List
        Vector<TextureEntry> textureEntries = new Vector<>();
        for(HashMap.Entry<String, Texture> entry : masterCharacterMap.entrySet()){
            textureEntries.add(new TextureEntry(entry.getKey(), entry.getValue()));
        }
        charList = new JList(textureEntries);
        charList.setFont(font1);
        charList.setSelectedIndex(0);
        texturePanel.add(charList);

        fillScreenButton = new JButton("Fill Map with Texture");
        fillScreenButton.addActionListener(this);
        texturePanel.add(fillScreenButton);

    }

    public static void main(String[] args){
        new MapEditor();
    }

    private void fillScreenWithCharacter(String c){
        for(int i=0; i<editorPanel.getLevel().getMapWidth(); i++){
            for(int j=0; j<editorPanel.getLevel().getMapHeight(); j++){
                if(wallEditing) {
                    editorPanel.getLevel().getWallArray()[i][j] = c.charAt(0);
                }else if(floorEditing){
                    editorPanel.getLevel().getFloorArray()[i][j] = c.charAt(0);
                }else if(ceilEditing){
                    editorPanel.getLevel().getCeilArray()[i][j] = c.charAt(0);
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == fillScreenButton){
            TextureEntry selectedEntry = (TextureEntry) charList.getSelectedValue();
            String c = selectedEntry.getCharacter();
            fillScreenWithCharacter(c);
        }

        //sets wallEditing, floorEditing, ceilEditing true or false
        if(e.getSource() == wallEdit){
            wallEditing = true;
            floorEditing = false;
            ceilEditing = false;
        }
        if(e.getSource() == floorEdit){
            wallEditing = false;
            floorEditing = true;
            ceilEditing = false;
        }
        if(e.getSource() == ceilEdit){
            wallEditing = false;
            floorEditing = false;
            ceilEditing = true;
        }

        //sets zoomed1 to notzoomed true or false
        if(e.getSource() == zoom1){
            zoomed1 = true;
            zoomed2 = false;
            zoomed3 = false;
            zoomed4 = false;
            notzoomed = false;
        }
        if(e.getSource() == zoom2){
            zoomed1 = false;
            zoomed2 = true;
            zoomed3 = false;
            zoomed4 = false;
            notzoomed = false;
        }
        if(e.getSource() == zoom3){
            zoomed1 = false;
            zoomed2 = false;
            zoomed3 = true;
            zoomed4 = false;
            notzoomed = false;
        }
        if(e.getSource() == zoom4){
            zoomed1 = false;
            zoomed2 = false;
            zoomed3 = false;
            zoomed4 = true;
            notzoomed = false;
        }
        if(e.getSource() == nozoom){
            zoomed1 = false;
            zoomed2 = false;
            zoomed3 = false;
            zoomed4 = false;
            notzoomed = true;
        }

        if(e.getSource() == newFile){
            String width = JOptionPane.showInputDialog(this,
                    "Enter the width of the new map",
                    "New Map (width)", JOptionPane.PLAIN_MESSAGE);
            int w = Integer.parseInt(width);
            String height = JOptionPane.showInputDialog(this,
                    "Enter the height of the new map",
                    "New Map (height)", JOptionPane.PLAIN_MESSAGE);
            int h = Integer.parseInt(height);

            newFile(w, h);
        }
        if(e.getSource() == saveFile){
            saveFile();
        }
        if(e.getSource() == loadFile){
            loadFile();
        }

        update();
    }

    public void update(){
        if(wallEditing){
            editorPanel.setLevel(level);
            editorPanel.setArrayForDisplay(level.getWallArray());
        }else if(floorEditing){
            editorPanel.setLevel(level);
            editorPanel.setArrayForDisplay(level.getFloorArray());
        }else if(ceilEditing){
            editorPanel.setLevel(level);
            editorPanel.setArrayForDisplay(level.getCeilArray());
        }

        displayArray();
    }

    private void displayArray(){
        repaint();
    }

    private void loadTextures(){
        //loads textures from res/texture/
    }

    //new empty level
    private void newFile(int w, int h){
        level = new Level(w, h);
    }

    //sets level
    private void loadFile(){
        int returnVal = fc.showOpenDialog(MapEditor.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            String extension = getExtension(file);
            if(!extension.equals("raymap")){
                JOptionPane.showMessageDialog(this, "File isn't a .raymap");
                return;
            }

            try {
                level = new Level(file);
                System.out.println("Loaded: " + file.getName());
            } catch (MapBadDataException e1) {
                System.out.println("couldn't load file properly");
                e1.printStackTrace();
            }
        }
    }
    private String getExtension(File file){
        String ext = null;
        String s = file.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    private void saveFile(){
        Character[][] wallArrayToSave = editorPanel.getLevel().getWallArray();
        Character[][] floorArrayToSave = editorPanel.getLevel().getFloorArray();
        Character[][] ceilArrayToSave = editorPanel.getLevel().getCeilArray();

        String mapData = "";

        //TODO change saving after adding sprites
        mapData = writeFromArray(mapData, wallArrayToSave);
        mapData += "/";
        mapData = writeFromArray(mapData, floorArrayToSave);
        mapData += "/";
        mapData = writeFromArray(mapData, ceilArrayToSave);

        fc.setDialogTitle("Save raymap as... (extension must be .raymap");
        int userSelection = fc.showSaveDialog(this);
        if(userSelection == JFileChooser.APPROVE_OPTION){
            File fileToSave = fc.getSelectedFile();
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave));
                writer.write(mapData);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private String writeFromArray(String string, Character[][] array){
        for(int i=0; i<editorPanel.getLevel().getMapWidth(); i++){
            for(int j=0; j<editorPanel.getLevel().getMapHeight(); j++){
                string += array[i][j].toString();
            }
        }
        return string;
    }

    private void drawTileAtMouse(MouseEvent e){
        TextureEntry selectedEntry = (TextureEntry) charList.getSelectedValue();
        String c = selectedEntry.getCharacter();
        editorPanel.drawTile(c, e.getX(), e.getY() - menuBarHeight - this.getInsets().top);
        update();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        drawTileAtMouse(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        drawTileAtMouse(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    public HashMap<String, Texture> getMasterCharacterMap() {
        return masterCharacterMap;
    }
}
