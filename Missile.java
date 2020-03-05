import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Missile {
    public static final double easy_baseSpeed = 1.05;
    public static final double easy_baseSlowSpeed = 0.47;
    public static final double easy_baseFastSpeed = 1.6;
    public static final double easy_baseUndeadSpeed = 0.3;

    public static final double normal_baseSpeed = 1.1;
    public static final double normal_baseSlowSpeed = 0.5;
    public static final double normal_baseFastSpeed = 1.75;
    public static final double normal_baseUndeadSpeed = 0.4;

    public static final double hard_baseSpeed = 1.12;
    public static final double hard_baseSlowSpeed = 0.55;
    public static final double hard_baseFastSpeed = 1.8;
    public static final double hard_baseUndeadSpeed = 0.6;

    public Vector2D pos;
    public Vector2D vel;
    public static double width = 16.875;
    public static double height = 8.1;
    public double maxTurnRate; //degrees
    public double speed;
    public Image img;
    public Area area = new Area();
    public ArrayList<Vector2D[]> trail;
    public int timer_trail = 0;
    public Missile(){
        img = Main.missile_img;
        maxTurnRate = 0.3;
        switch (Main.difficulty){
            case 0: {
                speed = easy_baseSpeed;
                break;
            }
            case 1: {
                speed = normal_baseSpeed;
                break;
            }
            case 2: {
                speed = hard_baseSpeed;
                break;
            }
        }
        trail = new ArrayList<>();
        pos = new Vector2D(100,100);
        vel = new Vector2D(0.01,0.01);
        vel.rotateTo(0);
        double randomX = Math.random()*Main.WIDTH;
        double spawnX = (randomX<Main.WIDTH/2d)?(randomX-Main.WIDTH):(randomX+Main.WIDTH);
        double randomY = Math.random()*Main.HEIGHT;
        double spawnY = (randomY<Main.HEIGHT/2d)?(randomY-Main.HEIGHT):(randomY+Main.HEIGHT);
        pos.set(spawnX,spawnY);
    }
    public boolean isVisible(){
        return (pos.x>0&&pos.x<Main.WIDTH&&pos.y>0&&pos.y<Main.HEIGHT);
    }
    public void show(Graphics2D g2d){
        if (isVisible()) {
            AffineTransform tr = g2d.getTransform();
            tr.translate(pos.x, pos.y);
            tr.rotate(vel.getAngle());
            tr.translate(-width / 2d, -height / 2d);
            g2d.drawImage(img, tr, null);
            tr.translate(-pos.x + width / 2d, -pos.y + height / 2d);

            Rectangle2D boundary = new Rectangle2D.Double(pos.x - width / 2 - 1, pos.y - height / 2 - 1, width + 2, height + 2);
            Shape boundary_shape = tr.createTransformedShape(boundary);
            area = new Area(boundary_shape);
        }

        if (trail.size() > 0) {
            for (int i = 0; i < trail.size() - 1; i++) {
                Vector2D curr = trail.get(i)[0];
                Vector2D next = trail.get(i + 1)[0];
                if (trail.get(i)[1].x == 1) {
                    g2d.setColor(new Color(255, 255, 255, 255 * (i + 1) / trail.size()));
                    g2d.drawLine((int) curr.x, (int) curr.y, (int) next.x, (int) next.y);
                }
            }
        }
    }
    public void move(){
        if (Main.p.isSlowmoActivated){
            if (this.getClass()==Missile.class){
                switch (Main.difficulty){
                    case 0: {
                        speed = easy_baseSpeed/3;
                        break;
                    }
                    case 1: {
                        speed = normal_baseSpeed/2.5;
                        break;
                    }
                    case 2: {
                        speed = hard_baseSpeed/2;
                        break;
                    }
                }
            }
            if (this.getClass()==SlowMissile.class){
                switch (Main.difficulty){
                    case 0: {
                        speed = easy_baseSlowSpeed/3;
                        break;
                    }
                    case 1: {
                        speed = normal_baseSlowSpeed/2.5;
                        break;
                    }
                    case 2: {
                        speed = hard_baseSlowSpeed/2;
                        break;
                    }
                }
            }
            if (this.getClass()==FastMissile.class){
                switch (Main.difficulty){
                    case 0: {
                        speed = easy_baseFastSpeed/3;
                        break;
                    }
                    case 1: {
                        speed = normal_baseFastSpeed/2.5;
                        break;
                    }
                    case 2: {
                        speed = hard_baseFastSpeed/2;
                        break;
                    }
                }
            }
        }else{
            if (this.getClass()==Missile.class){
                switch (Main.difficulty){
                    case 0: {
                        speed = easy_baseSpeed;
                        break;
                    }
                    case 1: {
                        speed = normal_baseSpeed;
                        break;
                    }
                    case 2: {
                        speed = hard_baseSpeed;
                        break;
                    }
                }
            }
            if (this.getClass()==SlowMissile.class){
                switch (Main.difficulty){
                    case 0: {
                        speed = easy_baseSlowSpeed;
                        break;
                    }
                    case 1: {
                        speed = normal_baseSlowSpeed;
                        break;
                    }
                    case 2: {
                        speed = hard_baseSlowSpeed;
                        break;
                    }
                }
            }
            if (this.getClass()==FastMissile.class){
                switch (Main.difficulty){
                    case 0: {
                        speed = easy_baseFastSpeed;
                        break;
                    }
                    case 1: {
                        speed = normal_baseFastSpeed;
                        break;
                    }
                    case 2: {
                        speed = hard_baseFastSpeed;
                        break;
                    }
                }
            }
        }
        timer_trail++;
        if (timer_trail>=15){
            timer_trail = 0;
            if (trail.size()<1) {
                trail.add(new Vector2D[]{new Vector2D(pos.x, pos.y), new Vector2D(1, 1)});
            }else{
                trail.add(new Vector2D[]{new Vector2D(pos.x, pos.y), new Vector2D(1-trail.get(trail.size()-1)[1].x, 1-trail.get(trail.size()-1)[1].y)});
            }
        }
        if (trail.size()>40){
            trail.remove(0);
        }
        Vector2D dir;
        if (Main.p.decoy==null) {
            dir = Vector2D.subtract(Main.p.pos, pos);
        }else{
            dir = Vector2D.subtract(Main.p.decoy.decoyPos, pos);
        }
        dir.normalize();
        Vector2D normalizedVel = vel.getNormalized();
        double angleRadians = Math.acos(dir.x*normalizedVel.x+dir.y*normalizedVel.y);
        double maxTurnRateRadians = Math.toRadians(maxTurnRate);
        double cross = Vector2D.cross(dir,vel);
        int signAngle = (cross>=0)?-1:1;
        angleRadians = signAngle*Math.min(angleRadians, maxTurnRateRadians);
        vel.rotateBy(angleRadians);
        vel.normalize();
        vel.multiply(speed);
        pos.x+=vel.x;
        pos.y+=vel.y;

    }
    public void destroy(){
        Main.missiles.remove(this);
    }
    public static int countSlowMissiles(){
        int result = 0;
        for (int i = 0; i < Main.missiles.size(); i++) {
            Missile m = Main.missiles.get(i);
            if (m.getClass()==SlowMissile.class){
                result++;
            }
        }
        return result;
    }
}

class SlowMissile extends Missile{
    public SlowMissile(){
        super();
        img = Main.missile_slow_img;
        maxTurnRate = 0.1;
        switch (Main.difficulty){
            case 0: {
                speed = easy_baseSlowSpeed;
                break;
            }
            case 1: {
                speed = normal_baseSlowSpeed;
                break;
            }
            case 2: {
                speed = hard_baseSlowSpeed;
                break;
            }
        }
    }
    @Override
    public void move(){
        speed = 0.5+0.03*countSlowMissiles();
        super.move();
    }
}
class FastMissile extends Missile{
    public FastMissile(){
        super();
        img = Main.missile_fast_img;
        maxTurnRate = 0.12;
        switch (Main.difficulty){
            case 0: {
                speed = easy_baseFastSpeed;
                break;
            }
            case 1: {
                speed = normal_baseFastSpeed;
                break;
            }
            case 2: {
                speed = hard_baseFastSpeed;
                break;
            }
        }
    }
}
class UndeadMissile extends Missile{
    public int lives;
    public UndeadMissile(){
        super();
        lives = 1;
        img = Main.missile_undead_img;
        maxTurnRate = 0.42;
        switch (Main.difficulty){
            case 0: {
                speed = easy_baseUndeadSpeed;
                break;
            }
            case 1: {
                speed = normal_baseUndeadSpeed;
                break;
            }
            case 2: {
                speed = hard_baseUndeadSpeed;
                break;
            }
        }
    }
    @Override
    public void destroy(){
        if (lives<=0){
            Main.missiles.remove(this);
        }else{
            lives--;
            vel.rotateBy(Math.random()*Math.PI);
        }
    }
}