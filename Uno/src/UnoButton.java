import java.awt.*;

public class UnoButton extends WndInterface {
    public static final int WIDTH = 80;
    public static final int HEIGHT = 60;
    private boolean isHovered;

    public UnoButton(Position position) {
        super(new Rectangle(position, WIDTH, HEIGHT));
        isHovered = false;
        setEnabled(true);
    }

    @Override
    public void update(int deltaTime) {

    }

    @Override
    public void paint(Graphics g) {
        if(!isEnabled()) return;

        g.setColor(new Color(147, 44, 44));
        int expandAmount = isHovered ? 20 : 0;
        g.fillOval(bounds.position.x-expandAmount/2, bounds.position.y-expandAmount/2,
                bounds.width+expandAmount,bounds.height+expandAmount);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        int strWidth = g.getFontMetrics().stringWidth("UNO");
        g.drawString("UNO", bounds.position.x+bounds.width/2-strWidth/2-2, bounds.position.y+bounds.height/2+2+10);
        g.setColor(new Color(226, 173, 67));
        g.drawString("UNO", bounds.position.x+bounds.width/2-strWidth/2, bounds.position.y+bounds.height/2+10);
    }

    @Override
    public void handleMouseMove(Position mousePosition) {
        isHovered = bounds.isPositionInside(mousePosition);
    }
}
