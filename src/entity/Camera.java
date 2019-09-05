package entity;

import engine.Level;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class Camera implements KeyListener, MouseMotionListener {
    /*stores player coordinates on 2d map
    * xPos, yPos are x,y coordinates on 2d map
    * xDir, yDir are x,y components of direction vector
    * */

    public double xPos, yPos, xDir, yDir, xPlane, yPlane;
    public boolean left, right, forward, back;
    public final double MOVE_SPEED = 0.08; //movement speed
    public double characterWidth = 3.0; //character radius is MOVE_SPEED * characterWidth

    private Robot robot;
    private int accumulatedDeltaX = 0;
    public int oldMouseX = 640;

    public Camera(double x, double y, double xd, double yd, double xp, double yp){
        xPos = x;
        yPos = y;
        xDir = xd;
        yDir = yd;
        xPlane = xp;
        yPlane = yp;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        checkWasdPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        checkWasdReleased(e);
    }

    /**Update method runs during each tick.
     * Checks for player input.
     * forward and backwards movement + strafing
     *
     * mouseMoved accumulates deltaX until each tick.
     * Thus, the variable accumulatedDeltaX is the
     * deltaX for each tick. accumulatedDeltaX must
     * be reset to 0 at the end of the update method.
     * accumulatedDeltaX is processed into the double
     * mdxFactor which can be used in our vector calculations*/
    public void update(Character[][] map){
        if(forward) {
            if(map[(int)(xPos + xDir * MOVE_SPEED * characterWidth)][(int)yPos] == Level.OPEN_SPACE.charAt(0)) {
                xPos += xDir * MOVE_SPEED;
            }
            if(map[(int)xPos][(int)(yPos + yDir * MOVE_SPEED * characterWidth)] == Level.OPEN_SPACE.charAt(0))
                yPos += yDir * MOVE_SPEED;
        }
        if(back) {
            if(map[(int)(xPos - xDir * MOVE_SPEED * characterWidth)][(int)yPos] == Level.OPEN_SPACE.charAt(0))
                xPos -= xDir * MOVE_SPEED;
            if(map[(int)xPos][(int)(yPos - yDir * MOVE_SPEED * characterWidth)]== Level.OPEN_SPACE.charAt(0))
                yPos -= yDir * MOVE_SPEED;
        }
        if(left) {
            if(map[(int)(xPos - yDir * MOVE_SPEED * characterWidth)][(int)yPos] == Level.OPEN_SPACE.charAt(0)) {
                xPos -= yDir * MOVE_SPEED;
            }
            if(map[(int)xPos][(int)(yPos + xDir * MOVE_SPEED * characterWidth)] == Level.OPEN_SPACE.charAt(0))
                yPos += xDir * MOVE_SPEED;
        }
        if(right) {
            if(map[(int)(xPos + yDir * MOVE_SPEED * characterWidth)][(int)yPos] == Level.OPEN_SPACE.charAt(0))
                xPos += yDir*MOVE_SPEED;
            if(map[(int)xPos][(int)(yPos - xDir * MOVE_SPEED * characterWidth)] == Level.OPEN_SPACE.charAt(0))
                yPos -= xDir*MOVE_SPEED;
        }


        //TODO play around with this number to find good sensitivity
        double mdxFactor = (double)accumulatedDeltaX/500.0;//mouse delta x factor

        if(mdxFactor != 0) {
            double oldxDir = xDir;
            xDir = xDir * Math.cos(mdxFactor) - yDir * Math.sin(mdxFactor);
            yDir = oldxDir * Math.sin(mdxFactor) + yDir * Math.cos(mdxFactor);
            double oldxPlane = xPlane;
            xPlane = xPlane * Math.cos(mdxFactor) - yPlane * Math.sin(mdxFactor);
            yPlane = oldxPlane * Math.sin(mdxFactor) + yPlane * Math.cos(mdxFactor);
        }
        accumulatedDeltaX = 0;

    }

    /**Checks if W A S D keys are pressed, and if so,
     * sets forward left back right to true respectively.*/
    private void checkWasdPressed(KeyEvent e){
        if (e.getKeyCode() == KeyEvent.VK_W) forward = true;
        if (e.getKeyCode() == KeyEvent.VK_A) left = true;
        if (e.getKeyCode() == KeyEvent.VK_S) back = true;
        if (e.getKeyCode() == KeyEvent.VK_D) right = true;
    }
    /**Checks if W A S D keys are pressed, and if so,
     * sets forward left back right to false respectively.*/
    private void checkWasdReleased(KeyEvent e){
        if (e.getKeyCode() == KeyEvent.VK_W) forward = false;
        if (e.getKeyCode() == KeyEvent.VK_A) left = false;
        if (e.getKeyCode() == KeyEvent.VK_S) back = false;
        if (e.getKeyCode() == KeyEvent.VK_D) right = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        calculateMouseDeltaX(e);
    }

    /**calculateMouseDeltaX simply finds the
     * difference between last mouse position
     * and current/new mouse position and adds
     * it to accumulatedDeltaX. This method runs
     * independent of tick/update rate.*/
    private void calculateMouseDeltaX(MouseEvent e){
        int cx = e.getX();
        int cy = e.getY();
        if(cx <= 100 || cx >= 540 || cy <= 100 || cy >= 260){ //Lock mouse to window
            robot.mouseMove(e.getXOnScreen()+(320-cx), e.getYOnScreen()+(180-cy));
        }
        int dx = oldMouseX - cx; //deltaX with positive being mouse movement towards left side of screen
        oldMouseX = cx;
        if(Math.abs(dx) >= 20){ //check if dx is unreasonable
            if(dx <= 0){
                dx = -1;
            }else{
                dx = 1;
            }
        }

        accumulatedDeltaX += dx;
    }


}
