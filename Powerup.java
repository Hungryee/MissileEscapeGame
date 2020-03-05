import java.awt.*;

public class Powerup {
    Vector2D pos;
    public Image img;
    public static double size = 30;
    public Powerup(){
        pos = new Vector2D(19+Math.random()*(Main.WIDTH-19), 19+Math.random()*(Main.HEIGHT-19));
    }
    public void show(Graphics2D g2d) {
        if (isVisible()){
            g2d.drawImage(img, (int) pos.x, (int) pos.y, null);
            if (pos.x <= -2 * Main.WIDTH || pos.x >= 2 * Main.WIDTH || pos.y <= -2 * Main.HEIGHT || pos.y >= 2 * Main.HEIGHT) {
                Main.powerups.remove(this);
            }
        }
    }
    public void activate(){

    }
    public boolean isVisible(){
        return (pos.x>0&&pos.x<Main.WIDTH&&pos.y>0&&pos.y<Main.HEIGHT);
    }
}
class Fuel extends Powerup{
    public double fuelAmount;
    public Fuel(){
        super();
        fuelAmount = 10+Math.random()*15;
        img = Main.powerup_fuel_img;
    }
    @Override
    public void activate(){
        Main.p.boostFuel+=fuelAmount;
    }
}
class Radar extends Powerup{
    public static double radarDuration = 5000;
    public Radar(){
        super();
        img = Main.powerup_radar_img;
    }
    @Override
    public void activate(){
        Main.p.isRadarActivated = true;
    }
}
class DecoyPU extends Powerup{
    public static double decoyDuration = 600;
    public DecoyPU(){
        super();
        img = Main.powerup_decoy_img;
    }
    @Override
    public void activate(){
        Main.p.decoy = Main.p.new Decoy();
    }
}
class Slowmo extends Powerup{
    public static double slowmoDuration = 2000;
    public Slowmo(){
        super();
        img = Main.powerup_slowmo_img;
    }
    @Override
    public void activate(){
        Main.p.isSlowmoActivated = true;
    }
}
class Life extends  Powerup{
    public Life(){
        super();
        img = Main.powerup_life_img;
    }
    @Override
    public void activate(){
        Main.lives++;
    }
}
