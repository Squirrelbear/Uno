import java.awt.*;

public class Card extends Rectangle {
    public static final int CARD_WIDTH = 60;
    public static final int CARD_HEIGHT = 90;

    private static final String[] cardFaceValues = {"0","1","2","3","4","5","6","7","8","9",
                                    "Draw Two", "Skip", "Reverse", "Draw Four", "Wild"};

    private final String cardLabel;
    private final String cornerLabel;
    private final int colourID;
    private final int faceValueID;
    private Color drawColour;
    private final int cardID;

    public Card(int faceValueID, int colourID, int cardID) {
        super(new Position(0,0), CARD_WIDTH, CARD_HEIGHT);
        this.faceValueID = faceValueID;
        this.cardLabel = cardFaceValues[faceValueID];
        this.colourID = colourID;
        this.drawColour = getColourByID(colourID);
        this.cardID = cardID;
        if(faceValueID == 10) {
            this.cornerLabel = "+2";
        } else if(faceValueID == 13) {
            this.cornerLabel = "+4";
        } else if(faceValueID == 14) {
            this.cornerLabel = "";
        } else {
            this.cornerLabel = cardLabel;
        }
    }

    public void paint(Graphics g) {
        // Draw card background with white border and card colour
        g.setColor(Color.WHITE);
        g.fillRect(position.x, position.y, width, height);
        g.setColor(drawColour);
        g.fillRect(position.x+2, position.y+2, width-4, height-4);

        if(colourID != 4) {
            // Draw a white oval for any non-wild in the middle.
            g.setColor(Color.WHITE);
            g.fillOval(position.x + 4, position.y + height / 2 - ((width - 8) / 4),
                    width - 8, (width - 8) / 2);
        } else {
            // Red, blue, green, yellow segments for any wild card in the middle.
            for(int i = 0; i < 4; i++) {
                g.setColor(getColourByID(i));
                g.fillArc(position.x + 4, position.y + height / 2 - ((width - 8) / 4)-5,
                        width - 8, (width - 8) / 2+10, 270+90*i, 90);
            }
        }

        int fontHeight = (cardLabel.length() > 4) ? 10 : 20;
        g.setFont(new Font("Arial", Font.BOLD, fontHeight));
        int strWidth = g.getFontMetrics().stringWidth(cardLabel);
        // Draw shadow (black) text for central label
        if(colourID == 4 || cardLabel.length() <= 4) {
            g.setColor(Color.BLACK);
            g.drawString(cardLabel, position.x+width/2-strWidth/2-1,
                    position.y+height/2+fontHeight/2+1);
        }
        // Colour to make primary text visible based on whether a shadow was added.
        if(colourID == 4) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(cardLabel.length()<=4 ? drawColour : Color.BLACK);
        }
        // Draw central label
        g.drawString(cardLabel, position.x+width/2-strWidth/2,
                position.y+height/2+fontHeight/2);

        // Draw labels in each of the corners
        fontHeight = (cornerLabel.length() > 2) ? 10 : 20;
        g.setFont(new Font("Arial", Font.BOLD, fontHeight));
        strWidth = g.getFontMetrics().stringWidth(cornerLabel);
        g.setColor(Color.WHITE);
        g.drawString(cornerLabel, position.x+5, position.y+5+fontHeight);
        g.drawString(cornerLabel, position.x+width-strWidth-5, position.y+height-5);
    }

    public static void paintCardBack(Graphics g, Rectangle bounds) {
        g.setColor(Color.WHITE);
        g.fillRect(bounds.position.x, bounds.position.y, bounds.width, bounds.height);
        g.setColor(Color.BLACK);
        g.fillRect(bounds.position.x+2, bounds.position.y+2, bounds.width-4, bounds.height-4);
        g.setColor(new Color(147, 44, 44));
        g.fillOval(bounds.position.x+4, bounds.position.y+bounds.height/2-((bounds.width-8)/4),
                bounds.width-8, (bounds.width-8)/2);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        int strWidth = g.getFontMetrics().stringWidth("UNO");
        g.drawString("UNO", bounds.position.x+bounds.width/2-strWidth/2-2,
                bounds.position.y+bounds.height/2-((bounds.width-8)/4)+2+20);
        g.setColor(new Color(226, 173, 67));
        g.drawString("UNO", bounds.position.x+bounds.width/2-strWidth/2,
                bounds.position.y+bounds.height/2-((bounds.width-8)/4)+20);
    }

    public void setColour(int colourID) {
        drawColour = getColourByID(colourID);
    }

    public int getColourID() {
        return colourID;
    }

    public int getFaceValueID() {
        return faceValueID;
    }

    public int getCardID() {
        return cardID;
    }

    public static Color getColourByID(int colourID) {
        return switch (colourID) {
            case 0 -> new Color(191, 48, 48);
            case 1 -> new Color(36, 94, 160);
            case 2 -> new Color(115, 187, 54);
            case 3 -> new Color(238, 188, 65);
            default -> Color.BLACK;
        };
    }

    public int getScoreValue() {
        if(faceValueID < 10) return faceValueID;
        else if(faceValueID == 13 || faceValueID == 14) return 50;
        else return 20;
    }
}
