import java.awt.*;

public class Cloud {
    public double x;
    public double y;
    public double accX;
    public static double windDir = (Math.random()*2-1);
    public static int width = 120;
    public static int height = 75;
    private Image img;
    public Cloud(){
        x = Math.random()*Main.WIDTH;
        y = Math.random()*Main.HEIGHT;
        accX = Math.random()/2;
        img = Main.cloud_img;
        double scalingFactor = (1+Math.random()/2-0.25);img = img.getScaledInstance((int) (width*scalingFactor), (int) (height*scalingFactor), Image.SCALE_DEFAULT);
    }

    public static int countCurrentClouds(){
        int result = 0;
        for (int i = 0; i < Main.clouds.size(); i++) {
            Cloud c = Main.clouds.get(i);
            if (c.x>0&&c.x<Main.WIDTH&&c.y>0&&c.y<Main.HEIGHT){
                result++;
            }
        }
        return result;
    }

    public static void generateAdditionalClouds(){
        for (int i = 1; i <= 5 - countCurrentClouds(); i++) {
            Main.clouds.add(new Cloud());
        }
    }
    public boolean isVisible(){
        return (x>0&&x<Main.WIDTH&&y>0&&y<Main.HEIGHT);
    }
    public void show(Graphics2D g2d){
        if (isVisible()){
            g2d.drawImage(img,(int) (x-width/2), (int) (y-height/2), null);
        }
    }
    public void move(){
        x+=windDir*accX;
        if (x<=-2*Main.WIDTH){
            Main.clouds.remove(this);
        }
        if (x>=2*Main.WIDTH){
            Main.clouds.remove(this);
        }
        if (y<0||y>Main.HEIGHT){
            Main.clouds.remove(this);
        }
    }
}
