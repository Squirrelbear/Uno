import java.awt.*;

public class UnoButton extends Rectangle {
    public static final int WIDTH = 80;
    public static final int HEIGHT = 60;
    private boolean isHovered;
    private boolean isEnabled;

    public UnoButton(Position position) {
        super(position, WIDTH, HEIGHT);
        isHovered = false;
        isEnabled = true;
    }

    public void paint(Graphics g) {
        if(!isEnabled) return;

        g.setColor(new Color(147, 44, 44));
        int expandAmount = isHovered ? 20 : 0;
        g.fillOval(position.x-expandAmount/2, position.y-expandAmount/2,width+expandAmount,height+expandAmount);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        int strWidth = g.getFontMetrics().stringWidth("UNO");
        g.drawString("UNO", position.x+width/2-strWidth/2-2, position.y+height/2+2+10);
        g.setColor(new Color(226, 173, 67));
        g.drawString("UNO", position.x+width/2-strWidth/2, position.y+height/2+10);
    }

    public void updateHover(Position mousePosition) {
        isHovered = isPositionInside(mousePosition);
    }
}
