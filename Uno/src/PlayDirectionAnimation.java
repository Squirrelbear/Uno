import java.awt.*;

public class PlayDirectionAnimation {

    private Position centre;
    private int radiusFromCentre;
    private int indicatorWidth;
    private Position movingObject1, movingObject2;
    private double currentAngle;
    private boolean isIncreasing;

    public PlayDirectionAnimation(Position centre, int radiusFromCentre, int indicatorWidth) {
        this.centre = centre;
        this.radiusFromCentre = radiusFromCentre;
        this.indicatorWidth = indicatorWidth;
        currentAngle = 0;
        movingObject1 = new Position(centre.x, centre.y + radiusFromCentre);
        movingObject2 = new Position(centre.x, centre.y - radiusFromCentre);
        isIncreasing = true;
    }

    public void update(int deltaTime) {
        if(isIncreasing) currentAngle += deltaTime / 1000.0;
        else currentAngle -= deltaTime / 1000.0;
        if(currentAngle > Math.PI * 2) currentAngle = 0;

        movingObject1.setPosition(centre.x + (int)(radiusFromCentre * Math.cos(currentAngle)), centre.y + (int)(radiusFromCentre * Math.sin(currentAngle)));
        movingObject2.setPosition(centre.x - (int)(radiusFromCentre * Math.cos(currentAngle)), centre.y - (int)(radiusFromCentre * Math.sin(currentAngle)));
    }

    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillOval(movingObject1.x, movingObject1.y, indicatorWidth, indicatorWidth);
        g.fillOval(movingObject2.x, movingObject2.y, indicatorWidth, indicatorWidth);
    }

    public void setIsIncreasing(boolean isIncreasing) {
        this.isIncreasing = isIncreasing;
    }

}
