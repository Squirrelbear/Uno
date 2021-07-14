import java.awt.*;

public class WildColourSelectorOverlay extends WndInterface {

    private int hoveredRegion, hoverX, hoverY;
    private boolean isEnabled;

    public WildColourSelectorOverlay(Position position, int width, int height) {
        super(new Rectangle(position, width, height));
    }

    @Override
    public void update(int deltaTime) {

    }

    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(bounds.position.x-20, bounds.position.y-40, bounds.width+40, bounds.height+60);
        // Red, blue, green, yellow segments for any wild card in the middle.
        for(int i = 0; i < 4; i++) {
            g.setColor(Card.getColourByID(i));
            if(i == hoveredRegion) {
                int offsetX = (hoverX == 0) ? -1 : 1;
                int offsetY = (hoverY == 0) ? -1 : 1;
                g.fillArc(bounds.position.x+offsetX*10, bounds.position.y+offsetY*10,
                        bounds.width, bounds.height, 270 + 90 * i, 90);
            } else {
                g.fillArc(bounds.position.x, bounds.position.y, bounds.width, bounds.height,
                        270 + 90 * i, 90);
            }
        }
        g.setColor(Color.WHITE);
        g.drawRect(bounds.position.x-20, bounds.position.y-40, bounds.width+40, bounds.height+60);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String message = "Choose Colour";
        int strWidth = g.getFontMetrics().stringWidth(message);
        g.drawString(message, bounds.position.x+bounds.width/2-strWidth/2, bounds.position.y-5);
    }

    public void updateHover(Position mousePosition) {
        hoveredRegion = -1;
        if(bounds.isPositionInside(mousePosition)) {
            hoverX = (mousePosition.x - bounds.position.x) / (bounds.width/2);
            hoverY = (mousePosition.y - bounds.position.y) / (bounds.height/2);
            if(hoverX == 0 && hoverY == 0) hoveredRegion = 2;
            else if(hoverX == 1 && hoverY == 0) hoveredRegion = 1;
            else if(hoverX == 1 && hoverY == 1) hoveredRegion = 0;
            else if(hoverX == 0 && hoverY == 1) hoveredRegion = 3;
        }
    }

    public int getColourFromClick(Position mousePosition) {
        updateHover(mousePosition);
        return hoveredRegion;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
