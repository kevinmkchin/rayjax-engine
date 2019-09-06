package engine;

import entity.Camera;

import java.awt.*;
import java.util.HashMap;

public class Screen {

    public Character[][] walls, floors, ceilings;
    public int width, height;
    private HashMap<Character, Texture> textures;


    public Screen(Character[][] walls,
                  Character[][] floors,
                  Character[][] ceilings,
                  HashMap<Character, Texture> textures, int width, int height){
        this.walls = walls;
        this.floors = floors;
        this.ceilings = ceilings;
        this.textures = textures;
        this.width = width;
        this.height = height;
    }

    public void setMap(Character[][] map) {
        this.walls = map;
    }

    private Color darken(Color c, double factor){
        int cr,cg,cb;
        cr = c.getRed();
        cg = c.getGreen();
        cb = c.getBlue();

        cr *= factor;
        cg *= factor;
        cb *= factor;

        return new Color(cr,cg,cb);
    }

    public int[] update(Camera camera, int[] pixels){

        for(int i=0; i<320*90; i++){
            pixels[i] = Color.BLUE.getRGB();
        }

        for(int x=0; x<width; x++) {

            double cameraX = 2 * x / (double) (width) - 1; //[-1, 1]
            double rayDirX = camera.xDir + camera.xPlane * cameraX; //ray vector x component
            double rayDirY = camera.yDir + camera.yPlane * cameraX; //ray vector y component
            int tileX = (int) camera.xPos; //tile x coordinate
            int tileY = (int) camera.yPos; //tile y coordinate
            double sideDistX; //length of ray from camera pos to next x or y grid line
            double sideDistY;
            //Length of ray from one side to next in map
            double deltaDistX = Math.sqrt(1 + (rayDirY * rayDirY) / (rayDirX * rayDirX));
            double deltaDistY = Math.sqrt(1 + (rayDirX * rayDirX) / (rayDirY * rayDirY));
            double perpWallDist; //perpendicular wall distance (removes fish eye lens effect)
            //Direction to go in x and y
            int stepX, stepY;
            boolean hit = false;//was a wall hit
            int side = 0;//was the wall vertical or horizontal
            boolean thinTile = false;//is this tile a thin wall
            double wallX;//Exact position of where wall was hit

            //Figure out the step direction and initial distance to a side
            if(rayDirX < 0){ stepX = -1; sideDistX = (camera.xPos - tileX) * deltaDistX; }
            else{ stepX = 1; sideDistX = (tileX + 1.0 - camera.xPos) * deltaDistX; }
            if(rayDirY < 0){ stepY = -1; sideDistY = (camera.yPos - tileY) * deltaDistY; }
            else{ stepY = 1; sideDistY = (tileY + 1.0 - camera.yPos) * deltaDistY; }

            //Loop to find where the ray hits a wall
            while(!hit) {
                if(!thinTile){
                    //BLOCK WALLS
                    if (sideDistX < sideDistY) {
                        sideDistX += deltaDistX;
                        tileX += stepX;
                        side = 0;
                    } else {
                        sideDistY += deltaDistY;
                        tileY += stepY;
                        side = 1;
                    }
                }else{
                    //THIN WALLS
                    boolean notThin; //notThin checks if pixel is thin. thinTile checks if tile has a thin wall.
                    if(side==0){ notThin = sideDistY < sideDistX - (deltaDistX / 2); }
                    else{ notThin = sideDistX < sideDistY - (deltaDistY / 2); }

                    //Horizontal thin wall check
                    if(walls[tileX][tileY] == "5".charAt(0)){
                        if(notThin){
                            side = 1;
                            thinTile = false;
                            continue;
                        }else{
                            //TODO implement doors here
                                /* THIS IS HOW YOU DO DOORS HELL YEAH
                                double wallXPos = (camera.xPos + ((tileY - camera.yPos + (1 - stepY) / 2) / rayDirY + (deltaDistY/2)) * rayDirX);
                                wallXPos -= Math.floor(wallXPos);

                                if(wallXPos > 0.8){
                                    side=0;
                                    thin=false;
                                    continue;
                                }*/
                            hit = true;
                            side = 0;
                            thinTile = true;
                            break;
                        }
                    }

                    //Vertical thin wall check
                    if(walls[tileX][tileY] == "6".charAt(0)){
                        if(notThin){
                            side = 0;
                            thinTile = false;
                            continue;
                        }else{
                            hit = true;
                            side = 1;
                            thinTile = true;
                            break;
                        }
                    }

                }

                //find next tile to check
                if(walls[tileX][tileY] == Level.HOR_THIN_WALL.charAt(0)
                        || walls[tileX][tileY] == Level.VER_THIN_WALL.charAt(0)){
                    thinTile = true;
                    hit = false;
                } else if(walls[tileX][tileY] != Level.OPEN_SPACE.charAt(0)){
                    hit = true;
                }
            }

            //Calculate perpendicular distance to the point of impact
            if (side == 0){
                perpWallDist = Math.abs((tileX - camera.xPos + (1 - stepX) / 2) / rayDirX);
                if(thinTile){
                    perpWallDist += 0.5 / Math.abs(rayDirX);
                }
            }else{
                perpWallDist = Math.abs((tileY - camera.yPos + (1 - stepY) / 2) / rayDirY);
                if(thinTile){
                    perpWallDist += 0.5 / Math.abs(rayDirY);
                }
            }

            //Now calculate the height of the wall based on the distance from the camera
            int lineHeight;
            if(perpWallDist > 0){ lineHeight = Math.abs((int)(height / perpWallDist)); }
            else{ lineHeight = height; }
            //calculate lowest and highest pixel to fill in current stripe
            int drawStart = -lineHeight/2 + height/2;
            if(drawStart < 0){ drawStart = 0; }
            int drawEnd = height - drawStart; //faster than lineHeight/2 + height/2;
            if(drawEnd >= height){ drawEnd = height - 1; }

            if (side == 1) {//If its a y-axis wall
                if(thinTile) {
                    wallX = (camera.xPos
                            + ((tileY - camera.yPos + (1 - stepY) / 2) / rayDirY + (deltaDistY/2))
                            * rayDirX);
                }else{
                    wallX = (camera.xPos
                            + ((tileY - camera.yPos + (1 - stepY) / 2) / rayDirY)
                            * rayDirX);
                }
            } else {//X-axis wall
                if(thinTile) {
                    wallX = (camera.yPos
                            + ((tileX - camera.xPos + (1 - stepX) / 2) / rayDirX + (deltaDistX/2))
                            * rayDirY);
                }else{
                    wallX = (camera.yPos
                            + ((tileX - camera.xPos + (1 - stepX) / 2) / rayDirX)
                            * rayDirY);
                }
            }

            //Add a texture
            Character texKey = walls[tileX][tileY]; //key for texture hash map

            double xWithinTile = wallX - Math.floor(wallX);//if actual X is 4.68 then xWithinTile is 0.68
            //x coordinate on the texture
            int texX = (int)(xWithinTile * (textures.get(texKey).SIZE));
            if(side == 0 && rayDirX > 0) texX = textures.get(texKey).SIZE - texX - 1;
            if(side == 1 && rayDirY < 0) texX = textures.get(texKey).SIZE - texX - 1;

            double wallsLightFactor = Math.min(1, Math.pow(3/perpWallDist, 2));
            //calculate y coordinate on texture
            for(int y=drawStart; y<drawEnd; y++) {
                int texY = (((y*2 - height + lineHeight) << 6) / lineHeight) / 2;
                int color;

                //Make y sides darker
                if(side==0) color = textures.get(texKey).pixels[texX + (texY * textures.get(texKey).SIZE)];
                else color = (textures.get(texKey).pixels[texX + (texY * textures.get(texKey).SIZE)]>>1) & 8355711;
                //Convert to type Color
                Color c = new Color(color);

                //Finally set the color of the pixel
                pixels[x + y*(width)] = darken(c, wallsLightFactor).getRGB();
            }


            ///FLOOR & CEILING CASTING
            double floorAtWallX, floorAtWallY;
            double distPlayer, currentDist;

            if(side == 0 && rayDirX > 0){
                floorAtWallX = tileX;
                if(thinTile){floorAtWallX -= 0.5;}
                floorAtWallY = tileY + xWithinTile;
            }else if(side == 0 && rayDirX < 0){
                floorAtWallX = tileX + 1.0;
                if(thinTile){floorAtWallX -= 0.5;}
                floorAtWallY = tileY + xWithinTile;
            }else if(side == 1 && rayDirY > 0){
                floorAtWallX = tileX + xWithinTile;
                floorAtWallY = tileY;
                if(thinTile){floorAtWallY += 0.5;}
            }else{
                floorAtWallX = tileX + xWithinTile;
                floorAtWallY = tileY + 1.0;
                if(thinTile){floorAtWallY -= 0.5;}
            }

            distPlayer = 0.0;

            if(drawEnd < 0){
                drawEnd = height;
            }

            //Draw the floor from drawEnd(of the wall stripe) to bottom of screen
            for(int y=drawEnd; y<height; y++){
                currentDist = height / (2.0 * y - height);

                double weight = (currentDist - distPlayer)/(perpWallDist - distPlayer);

                double currentFloorX = weight * floorAtWallX + (1.0 - weight) * camera.xPos;
                double currentFloorY = weight * floorAtWallY + (1.0 - weight) * camera.yPos;


                int mapWidth = walls.length;
                int cx = Math.max(0, Math.min(mapWidth-1, (int) currentFloorX));
                int cy = Math.max(0, Math.min(mapWidth-1, (int) currentFloorY));
                Texture floorTex = textures.get(floors[cx][cy]);
                Texture ceilTex = textures.get(ceilings[cx][cy]);
                int tileSize = floorTex.SIZE;

                int floorTextureX, floorTextureY;
                floorTextureX = (int) (currentFloorX * tileSize) % tileSize;
                floorTextureY = (int) (currentFloorY * tileSize) % tileSize;

                //floor
                double floorsLightFactor = Math.min(1, Math.pow(3/currentDist, 2));
                Color c = new Color(floorTex.pixels[Math.abs(tileSize * floorTextureY + floorTextureX)]);
                pixels[y*width + x] = darken(c, floorsLightFactor).getRGB();

                //ceiling
                if(ceilings[cx][cy] != Level.OPEN_SPACE.charAt(0)){
                    c = new Color(ceilTex.pixels[Math.abs(tileSize * floorTextureY + floorTextureX)]);
                    pixels[(height-y)*width + x] = darken(c, floorsLightFactor).getRGB();
                }
            }


        }

        return pixels;

    }

}
