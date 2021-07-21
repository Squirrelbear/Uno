/**
 * Uno
 *
 * Position class:
 * Used to represent a single position x,y.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class Position {
    /**
     * Down moving unit vector.
     */
    public static final Position DOWN = new Position(0,1);
    /**
     * Up moving unit vector.
     */
    public static final Position UP = new Position(0,-1);
    /**
     * Left moving unit vector.
     */
    public static final Position LEFT = new Position(-1,0);
    /**
     * Right moving unit vector.
     */
    public static final Position RIGHT = new Position(1,0);
    /**
     * Zero unit vector.
     */
    public static final Position ZERO = new Position(0,0);

    /**
     * X coordinate.
     */
    public int x;
    /**
     * Y coordinate.
     */
    public int y;

    /**
     * Sets the value of Position.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Copy constructor to create a new Position using the values in another.
     *
     * @param positionToCopy Position to copy values from.
     */
    public Position(Position positionToCopy) {
        this.x = positionToCopy.x;
        this.y = positionToCopy.y;
    }

    /**
     * Sets the Position to the specified x and y coordinate.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Updates this position by adding the values from the otherPosition.
     *
     * @param otherPosition Other Position to add to this one.
     */
    public void add(Position otherPosition) {
        this.x += otherPosition.x;
        this.y += otherPosition.y;
    }

    /**
     * Calculate the distance from this position to the other position.
     *
     * @param otherPosition Position to check distance to.
     * @return Distance between this position and the other position.
     */
    public double distanceTo(Position otherPosition) {
        return Math.sqrt(Math.pow(x-otherPosition.x,2)+Math.pow(y-otherPosition.y,2));
    }

    /**
     * Multiplies both components of the position by an amount.
     *
     * @param amount Amount to multiply vector by.
     */
    public void multiply(int amount) {
        x *= amount;
        y *= amount;
    }

    /**
     * Updates this position by subtracting the values from the otherPosition.
     *
     * @param otherPosition Other Position to add to this one.
     */
    public void subtract(Position otherPosition) {
        this.x -= otherPosition.x;
        this.y -= otherPosition.y;
    }

    /**
     * Compares the Position object against another object.
     * Any non-Position object will return false. Otherwise compares x and y for equality.
     *
     * @param o Object to compare this Position against.
     * @return True if the object o is equal to this position for both x and y.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    /**
     * Gets a string version of the Position.
     *
     * @return A string in the form (x, y)
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
