import java.awt.*;

public class Button extends Rectangle {
    private int actionID;
    private boolean isHovered;
    private String text;

    public Button(Position position, int width, int height, String text, int actionID) {
        super(position, width, height);
        this.actionID = actionID;
        isHovered = false;
        this.text = text;
    }

    public void paint(Graphics g) {
        if(isHovered) {
            g.setColor(new Color(63, 78, 123));
            g.fillRect(position.x-3, position.y-3, width+6, height+6);
        } else {
            g.setColor(new Color(123, 133, 163));
            g.fillRect(position.x, position.y, width, height);
        }

        if(isHovered) {
            g.setColor(Color.WHITE);
            g.drawRect(position.x-3, position.y-3, width+6, height+6);
        } else {
            g.setColor(Color.BLACK);
            g.drawRect(position.x, position.y, width, height);
        }
        g.setFont(new Font("Arial", Font.BOLD, 20));
        int strWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, position.x+width/2-strWidth/2, position.y + height/2 + 8);
    }

    public int getActionID() {
        return actionID;
    }

    public void setHovering(boolean isHovering) {
        this.isHovered = isHovering;
    }
}
