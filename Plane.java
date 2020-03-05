import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
public class Plane {
    public Vector2D pos;
    public static double size = 40;
    public Vector2D vel;
    public Image img;
    public Plane.Decoy decoy;
    public double decoyTimer = 0;

    public double speed=1;
    public double rotateAmount = 0.5;

    public double maxBoostFuel;
    public double boostFuel;

    public int isRotating = 0;
    public boolean isBoosted = false;
    public boolean isInvincible;
    public int invincibilityTimer = 0;
    public boolean isRadarActivated = false;
    public double radarTimer = 0;
    public boolean isSlowmoActivated = false;
    public double slowmoTimer = 0;

    public ArrayList<double[]> path_smoke;
    public int path_delay = 40;

    public Area area = new Area();

    public Plane() {
        img = Main.plane_img;
        path_smoke = new ArrayList<>();
        decoy = null;
        pos = new Vector2D(Main.WIDTH/2d,Main.HEIGHT/2d);
        vel = new Vector2D(0,0);
        vel.rotateTo(Math.random()*Math.PI*2);
        maxBoostFuel = 100;
        boostFuel = maxBoostFuel;
        isInvincible = true;
    }
    public void show(Graphics2D g2d){
        if (decoy!=null){
            decoy.showDecoy(g2d);
        }
        if (isInvincible){
            g2d.setColor(Color.orange);
            g2d.drawOval((int)(pos.x-size*Math.sqrt(2)/2),(int)(pos.y-size*Math.sqrt(2)/2),(int)(size*Math.sqrt(2)),(int)(size*Math.sqrt(2)));
        }

        if (path_smoke.size()>0){
            for (int i = path_smoke.size()-2; i >= 0; i--) {
                double bubbleSize = 20*((i+1d)/ path_smoke.size());
                g2d.setColor(new Color(50,50,50,75));
                if (bubbleSize>1.5) {
                    g2d.fillOval((int) (path_smoke.get(i)[0] - bubbleSize / 2), (int) (path_smoke.get(i)[1] - bubbleSize / 2), (int) bubbleSize, (int) bubbleSize);
                }else{
                    path_smoke.remove(i);
                }
            }
        }
        if (isRadarActivated){
            for (int i = 0; i < Main.missiles.size(); i++) {
                Missile m = Main.missiles.get(i);
                double angle = Math.atan2(m.pos.y-pos.y, m.pos.x-pos.x);
                AffineTransform tr = g2d.getTransform();
                tr.translate(pos.x+75*Math.cos(angle),pos.y+75*Math.sin(angle));
                tr.rotate(angle);
                tr.translate(-Powerup.size/2.5d,-Powerup.size/2.5d);
                g2d.drawImage(Main.powerup_radar_arrow_img, tr, null);
            }
        }
        AffineTransform tr = g2d.getTransform();
        tr.translate(pos.x,pos.y);
        tr.rotate(vel.getAngle());
        tr.translate(-size/2d,-size/2d);
        g2d.drawImage(img, tr, null);
        tr.translate(-pos.x+size/2d, -pos.y+size/2d);

        Rectangle2D boundary = new Rectangle2D.Double(pos.x-size/2, pos.y-size/2, size, size);
        Shape boundary_shape = tr.createTransformedShape(boundary);
        area = new Area(boundary_shape);
    }
    public void move(){

        vel.set(speed*Math.cos(vel.getAngle()), speed*Math.sin(vel.getAngle()));
        path_delay++;
        if (path_delay >= 20){
            path_delay = 0;
            path_smoke.add(new double[]{pos.x+(Math.random()*2-1)*10,pos.y+(Math.random()*2-1)*10});
        }
        if (isRotating==1){
           vel.rotateBy(Math.toRadians(-rotateAmount));
        }
        if (isRotating==2){
            vel.rotateBy(Math.toRadians(rotateAmount));
        }

        if (isBoosted){
            boostFuel -= 0.03;
            if (boostFuel > 0) {
                speed = 2;
                rotateAmount = 0.15;
            } else {
                isBoosted = false;
            }
        }
        if (!isBoosted){
            speed = 1;
            rotateAmount = 0.5;
        }
        pos.x+=vel.x;
        pos.y+=vel.y;
        boostFuel = Math.max(Math.min(boostFuel, maxBoostFuel),0);
        checkPowerupActivations();
        checkBorderCrosses();
        checkPowerupCollisions();
    }
    public void checkPowerupActivations(){
        if (decoy!=null){
            decoyTimer++;
            if (decoyTimer>= DecoyPU.decoyDuration){
                decoyTimer = 0;
                decoy = null;
            }
        }
        if (isSlowmoActivated){
            slowmoTimer++;
            if (slowmoTimer>=Slowmo.slowmoDuration){
                slowmoTimer = 0;
                isSlowmoActivated = false;
            }

        }
        if (isInvincible){
            invincibilityTimer++;
            if (invincibilityTimer>=300){
                invincibilityTimer = 0;
                isInvincible = false;
            }
        }
        if (isRadarActivated){
            radarTimer++;
            if (radarTimer>=Radar.radarDuration){
                isRadarActivated = false;
                radarTimer = 0;
            }
        }
    }
    public void checkBorderCrosses(){
        if (pos.x>Main.WIDTH) {
            pos.x = 0;
            for (int i = 0; i < path_smoke.size(); i++) {
                path_smoke.get(i)[0]-=Main.WIDTH;
            }
            for (int i = 0; i < Main.missiles.size(); i++) {
                Missile m = Main.missiles.get(i);
                for (int j = 0; j < m.trail.size(); j++) {
                    m.trail.get(j)[0].x-=Main.WIDTH;
                }
                m.pos.x-=Main.WIDTH;
            }
            for (int i = 0; i < Main.clouds.size(); i++) {
                Cloud c = Main.clouds.get(i);
                c.x-=Main.WIDTH;
            }
            for (int i = 0; i < Main.powerups.size(); i++) {
                Powerup pu = Main.powerups.get(i);
                pu.pos.x-=Main.WIDTH;
            }
            Cloud.generateAdditionalClouds();
        }
        if (pos.y>Main.HEIGHT) {
            pos.y = 0;
            for (int i = 0; i < path_smoke.size(); i++) {
                path_smoke.get(i)[1]-=Main.HEIGHT;
            }
            for (int i = 0; i < Main.missiles.size(); i++) {
                Missile m = Main.missiles.get(i);
                for (int j = 0; j < m.trail.size(); j++) {
                    m.trail.get(j)[0].y-=Main.HEIGHT;
                }
                m.pos.y-=Main.HEIGHT;
            }
            for (int i = 0; i < Main.clouds.size(); i++) {
                Cloud c = Main.clouds.get(i);
                c.y-=Main.HEIGHT;
            }
            for (int i = 0; i < Main.powerups.size(); i++) {
                Powerup pu = Main.powerups.get(i);
                pu.pos.y-=Main.HEIGHT;
            }
            Cloud.generateAdditionalClouds();
        }
        if (pos.x<0) {
            for (int i = 0; i < path_smoke.size(); i++) {
                path_smoke.get(i)[0]+=Main.WIDTH;
            }
            pos.x = Main.WIDTH;
            for (int i = 0; i < Main.missiles.size(); i++) {
                Missile m = Main.missiles.get(i);
                for (int j = 0; j < m.trail.size(); j++) {
                    m.trail.get(j)[0].x+=Main.WIDTH;
                }
                m.pos.x+=Main.WIDTH;
            }
            for (int i = 0; i < Main.clouds.size(); i++) {
                Cloud c = Main.clouds.get(i);
                c.x+=Main.WIDTH;
            }
            for (int i = 0; i < Main.powerups.size(); i++) {
                Powerup pu = Main.powerups.get(i);
                pu.pos.x+=Main.WIDTH;
            }
            Cloud.generateAdditionalClouds();
        }
        if (pos.y<0) {
            pos.y = Main.HEIGHT;
            for (int i = 0; i < path_smoke.size(); i++) {
                path_smoke.get(i)[1]+=Main.HEIGHT;
            }
            for (int i = 0; i < Main.missiles.size(); i++) {
                Missile m = Main.missiles.get(i);
                for (int j = 0; j < m.trail.size(); j++) {
                    m.trail.get(j)[0].y+=Main.HEIGHT;
                }
                m.pos.y+=Main.HEIGHT;
            }
            for (int i = 0; i < Main.clouds.size(); i++) {
                Cloud c = Main.clouds.get(i);
                c.y+=Main.HEIGHT;
            }
            for (int i = 0; i < Main.powerups.size(); i++) {
                Powerup pu = Main.powerups.get(i);
                pu.pos.y+=Main.HEIGHT;
            }
            Cloud.generateAdditionalClouds();
        }
    }
    public void checkPowerupCollisions(){
        for (int i = 0; i < Main.powerups.size(); i++) {
            Powerup pu = Main.powerups.get(i);
            if (area.intersects(pu.pos.x-Powerup.size/2,pu.pos.y-Powerup.size/2, Powerup.size,Powerup.size)){
                pu.activate();
                Main.powerups.remove(pu);
            }
        }
    }
    public class Decoy{
        public Vector2D decoyPos;
        public Vector2D decoyVel;
        public Image decoyImg;
        public double decoySpeed;
        public Decoy(){
            decoyImg = Main.decoy_img;
            decoyPos = new Vector2D(pos.x,pos.y);
            decoyVel = new Vector2D(vel);
            decoySpeed = 1.5*speed;
            decoyVel.set(decoySpeed*Math.cos(decoyVel.getAngle()), decoySpeed*Math.sin(decoyVel.getAngle()));
        }
        public void showDecoy(Graphics2D g2d){
            AffineTransform tr = g2d.getTransform();
            tr.translate(decoyPos.x,decoyPos.y);
            tr.rotate(decoyVel.getAngle());
            tr.translate(-size/2d,-size/2d);
            g2d.drawImage(decoyImg, tr, null);
            tr.translate(-decoyPos.x+size/2d, -decoyPos.y+size/2d);
            decoyPos.x+=decoyVel.x;
            decoyPos.y+=decoyVel.y;
        }
    }
}
