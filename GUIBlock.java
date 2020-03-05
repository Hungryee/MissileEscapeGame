import java.awt.*;

public class GUIBlock {
    public static int IDs = -1;
    int id;
    int x;
    int y;
    int width;
    int height;
    Color bgColor;
    Font textFont;
    boolean isSelected;
    String label;
    Color labelColor = Color.WHITE;

    public GUIBlock(int x, int y, int width, int height, Color bgColor, Font textFont, String label) {
        IDs++;
        id = IDs;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bgColor = bgColor;
        this.textFont = textFont;
        this.label = label;
    }
    public GUIBlock(int x, int y, int width, int height, Color bgColor, Font textFont, String label, Color labelColor) {
        this(x,y,width,height,bgColor,textFont,label);
        this.labelColor = labelColor;
    }
    public void display(Graphics2D g2d){
        g2d.setColor(Color.black);
        g2d.drawRect(x-width/2,y-height/2, width, height);
        isSelected = id==Main.difficultySelected;
        g2d.setColor(bgColor);
        g2d.fillRect(x-width/2,y-height/2, width, height);
        if (isSelected){
            g2d.setColor(new Color(255,255,255,100));
            g2d.fillRect(x-width/2,y-height/2, width, height);
        }
        g2d.setFont(textFont);
        g2d.setColor(labelColor);
        g2d.drawString(label, (int) (x-textFont.getStringBounds(label, g2d.getFontRenderContext()).getWidth()/2), (int) (y+textFont.getStringBounds(label, g2d.getFontRenderContext()).getHeight()/3));
    }
}
