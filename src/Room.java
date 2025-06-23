package  FloorPlanner;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Door> doors;
    public List<Furniture> furnitureList;
    public int x;
    public int y;
    public int width;
    public int height;
    public int centerX,centerY;
    static final int BORDER_WIDTH = 10;
    public static final int DEFAULT_WIDTH = 200;
    public static final int DEFAULT_HEIGHT = 200;
    Color color;

    public Room(int x, int y, Color color) {
        this.x = x > 1510 ? 1510 : ( x < 10 ? 10 : x);
        this.y = y > 760 ? 760 : (y < 10 ? 10 :y);
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.color = color;
        this.furnitureList = new ArrayList<>();
        this.doors = new ArrayList<>();
    }
    public void addDoor(Door door) {
        doors.add(door);
    }

    public List<Door> getDoors() {
        return doors;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw the room interior
        g.setColor(color);
        g.fillRect(x,y,width,height);

        // Draw the border using thick lines
        g2d.setColor(Color.BLACK); // Border color
        g2d.setStroke(new BasicStroke(BORDER_WIDTH)); // Set border thickness

        // Draw top border
        g2d.drawLine(x, y, x + width, y);
        // Draw left border
        g2d.drawLine(x, y, x, y + height);
        // Draw bottom border
        g2d.drawLine(x, y + height, x + width, y + height);
        // Draw right border
        g2d.drawLine(x + width, y, x + width, y + height);
    }
    public boolean overlapsWith(Room other) {
        return x < other.x + other.width && x + width > other.x &&
                y < other.y + other.height && y + height > other.y;
    }

    public boolean contains(int x, int y) {
        return x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height;
    }



    //gpt added




    public boolean isAdjacentTo(Room other) {
        // Check if the two rooms share a border
        boolean horizontalTouch = (this.x == other.x + other.width || this.x + this.width == other.x);
        boolean verticalTouch = (this.y == other.y + other.height || this.y + this.height == other.y);
        boolean overlapHorizontally = (this.y < other.y + other.height && this.y + this.height > other.y);
        boolean overlapVertically = (this.x < other.x + other.width && this.x + this.width > other.x);
        return (horizontalTouch && overlapHorizontally) || (verticalTouch && overlapVertically);
    }
    public boolean isBorderClickedWith(Room other, int clickX, int clickY) {
        if (!isAdjacentTo(other)) return false;
        int snapRange = 5;

        // Check for a border click with snap area
        if (this.x == other.x + other.width || this.x + this.width == other.x) { // Vertical border
            return (Math.abs(clickX - this.x) <= snapRange || Math.abs(clickX - (this.x + this.width)) <= snapRange) &&
                    (clickY >= Math.max(this.y, other.y) && clickY <= Math.min(this.y + this.height, other.y + other.height));
        } else if (this.y == other.y + other.height || this.y + this.height == other.y) { // Horizontal border
            return (Math.abs(clickY - this.y) <= snapRange || Math.abs(clickY - (this.y + this.height)) <= snapRange) &&
                    (clickX >= Math.max(this.x, other.x) && clickX <= Math.min(this.x + this.width, other.x + other.width));
        }
        return false;
    }


    public Door addDoorWith(Room other, int clickX, int clickY) {
        // Door dimensions
        final int DOOR_LENGTH = 30;
        final int BORDER_WIDTH = 10;

        int doorX = clickX, doorY = clickY, doorWidth = BORDER_WIDTH, doorHeight = DOOR_LENGTH;

        if (this.x == other.x + other.width || this.x + this.width == other.x) { // Vertical border
            doorWidth = BORDER_WIDTH;
            doorHeight = DOOR_LENGTH;
            doorY = clickY - (DOOR_LENGTH / 2); // Center the door
        } else if (this.y == other.y + other.height || this.y + this.height == other.y) { // Horizontal border
            doorWidth = DOOR_LENGTH;
            doorHeight = BORDER_WIDTH;
            doorX = clickX - (DOOR_LENGTH / 2); // Center the door
        }

        Door door = new Door(doorX, doorY, doorWidth, doorHeight);

        // Add the door to both rooms
        this.addDoor(door);
        other.addDoor(door);

        return door;
    }
    public void addDoorToBorder(Room room, int clickX, int clickY) {
        // Door dimensions
        final int DOOR_LENGTH = 20;
        final int BORDER_WIDTH = 10;

        int doorX = clickX, doorY = clickY, doorWidth = BORDER_WIDTH, doorHeight = DOOR_LENGTH;

        if (clickY >= y && clickY <= y + BORDER_WIDTH) { // Top border
            doorX = clickX - (BORDER_WIDTH / 2);
            doorY = y - (DOOR_LENGTH / 2);
        } else if (clickY >= y + height - BORDER_WIDTH && clickY <= y + height) { // Bottom border
            doorX = clickX - (BORDER_WIDTH / 2);
            doorY = y + height - (DOOR_LENGTH / 2);
        } else if (clickX >= x && clickX <= x + BORDER_WIDTH) { // Left border
            doorX = x - (DOOR_LENGTH / 2);
            doorY = clickY - (BORDER_WIDTH / 2);
            doorWidth = DOOR_LENGTH;
            doorHeight = BORDER_WIDTH;
        } else if (clickX >= x + width - BORDER_WIDTH && clickX <= x + width) { // Right border
            doorX = x + width - (DOOR_LENGTH / 2);
            doorY = clickY - (BORDER_WIDTH / 2);
            doorWidth = DOOR_LENGTH;
            doorHeight = BORDER_WIDTH;
        }

        // Create the door and add it to the room
        Door door = new Door(doorX, doorY, doorWidth, doorHeight);
        room.addDoor(door);
    }
    public void addFurniture(Furniture furniture) {
        this.furnitureList.add(furniture);
    }


}
enum RoomType{

    BEDROOM(new Color(255,0,0,90)),
    BATHROOM(new Color(0,255,255,90)),
    KITCHEN(new Color(255,165,0,90)),
    LIVINGROOM(new Color(0,255,0,90));
//    STUDYROOM(Color.PINK);

    private final Color color;

    RoomType(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }


}

