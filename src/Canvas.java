
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

import java.util.List;

import java.util.Stack;

class Canvas extends JPanel {
    private List<Furniture> furnitureList; // Add furniture list
    private Furniture selectedFurniture; // Track selected furniture
    private FurnitureType nextFurnitureType = null;

    private boolean windowAddingMode = false;

    public void enableWindowAddingMode() {
        windowAddingMode = true;
        repaint();
    }

    private List<Room> rooms;
    private Stack<Command> undoStack;
    private Stack<Command> redoStack;
    private boolean relativeAddingMode = false;
    private Room referenceRoom = null;

    private Room selectedRoom = null;
    private Point initialClick;
    private boolean resizing;
    private int offsetX, offsetY;
    private RoomTypeProvider selectedRoomTypeProvider;
    private FurnitureTypeProvider selectedFurnitureTypeProvider;
    private static final int GRID_SIZE = 10;
    public boolean alignmentMode = false;
    public boolean mouseOnBorder = false;
    public Room primaryRoom = null, secondaryRoom = null;
    public String selectedDirection = null;
    public boolean enableRoomPlacement = false;
    public boolean enableFurniturePlacement = false;
    public boolean rotation = false;
    public int angle = 0;
    public boolean rotationEnabled = false;

    // Method to enable rotation mode
    public void enableRotationMode() {
        rotationEnabled = true;
    }

    private int gridXMin = 10;
    private int gridYMin = 10;
    private int gridXMax = 1070;
    private int gridYMax = 710;


    private final int MIN_ROOM_DIM=100;
    private final int MIN_COORDS = 10;

    public Canvas() {
        furnitureList = new java.util.ArrayList<>();
        rooms = new java.util.ArrayList<>();
        undoStack = new Stack<>();
        redoStack = new Stack<>();

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                if (relativeAddingMode) {
                    int snappedX = (e.getX() / GRID_SIZE) * GRID_SIZE;
                    int snappedY = (e.getY() / GRID_SIZE) * GRID_SIZE;

                    // Select the room that was clicked as the reference room
                    referenceRoom = findRoomAt(snappedX, snappedY);
                    if (referenceRoom != null) {
                        System.out.println("Reference room selected for relative adding.");
                    }
                }
                else if (alignmentMode) {
                    Room clickedRoom = findRoomAt(e.getX(), e.getY());
                    if (clickedRoom != null) {
                        if (primaryRoom == null) {
                            primaryRoom = clickedRoom;
                            System.out.println("Primary room selected for alignment.");
                        } else if (secondaryRoom == null && clickedRoom != primaryRoom) {
                            secondaryRoom = clickedRoom;
                            System.out.println("Secondary room selected for alignment. Now choose a direction.");
                        }
                    }
                }
                else {
                    // gets mouse location relative to grid
                    System.out.println("Working");
                    int snappedX = (e.getX() / GRID_SIZE) * GRID_SIZE;
                    int snappedY = (e.getY() / GRID_SIZE) * GRID_SIZE;

                    selectedRoom = findRoomAt(snappedX, snappedY);
                    //&& selectedRoom.x != 10
                    if (selectedRoom != null) {
                        // Start dragging or resizing an existing room
                        offsetX =  (selectedRoom.x <= MIN_COORDS) ? 0 : snappedX - selectedRoom.x;
                        offsetY = (selectedRoom.y <= MIN_COORDS) ? 0 : snappedY - selectedRoom.y;
                        resizing = Math.abs(offsetX - selectedRoom.width) < 15 || Math.abs(offsetY - selectedRoom.height) < 15;
                        int MAX_X = selectedRoom.x + selectedRoom.width - (2 * Room.BORDER_WIDTH);
                        int MAX_Y = selectedRoom.y + selectedRoom.height - (2 * Room.BORDER_WIDTH);
                        if (selectedRoom.contains(e.getX(), e.getY()) && snappedX <= MAX_X  && snappedY <= MAX_Y) {
                            initialClick = e.getPoint();
                        }
                        else{
                            initialClick = null;
                        }
                    } else if (selectedRoomTypeProvider.getSelectedRoomType() != null) {
                        // Add new room at clicked location
                        selectedRoom = new Room(snappedX, snappedY, selectedRoomTypeProvider.getSelectedRoomType().getColor());
                        resizing = false;
                        System.out.println("Pressed");
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (windowAddingMode) {
                    int x = (e.getX() / GRID_SIZE) * GRID_SIZE;
                    int y = (e.getY() / GRID_SIZE) * GRID_SIZE;

                    for (Room room : rooms) {
                        // Check if the clicked position is on a single wall of the room
                        if (isSingleWallClicked(room, x, y)) {
                            // Add the window to the room
                            addWindowToWall(room, x, y);

                            // Disable window-adding mode
                            windowAddingMode = false;
                            repaint();
                            return;
                        }
                    }

                    JOptionPane.showMessageDialog(Canvas.this, "No valid wall clicked for window placement!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                else if (rotationEnabled) {
                    // Rotate furniture
                    int mouseX = e.getX();
                    int mouseY = e.getY();
                    for (Furniture furniture : furnitureList) {
                        if (furniture.contains(mouseX, mouseY)) {
                            furniture.rotationAngle = (furniture.rotationAngle + 90) % 360; // Rotate by 90 degrees
                            rotationEnabled = false; // Reset rotation mode
                            repaint();
                            break;
                        }
                    }
                } else if (selectedFurnitureTypeProvider.getSelectedFurnitureType() != null && enableFurniturePlacement && !enableRoomPlacement) {
                    // Place furniture
                    int x = (e.getX() / GRID_SIZE) * GRID_SIZE;
                    int y = (e.getY() / GRID_SIZE) * GRID_SIZE;

                    String imagePath = selectedFurnitureTypeProvider.getSelectedFurnitureType().getImagePath();
                    Furniture newFurniture = new Furniture(x, y, 50, 50, imagePath);

                    // Check if the furniture is inside any room
                    boolean placedInRoom = false;
                    for (Room room : rooms) {
                        if (room.contains(x, y)) {
                            // Add furniture to the room
                            room.addFurniture(newFurniture);
                            furnitureList.add(newFurniture); // Add to global list for rendering
                            placedInRoom = true;
                            break;
                        }
                    }

                    if (!placedInRoom) {
                        // Show error if furniture is not inside any room
                        JOptionPane.showMessageDialog(Canvas.this, "Furniture must be placed inside a room!", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    enableFurniturePlacement = false; // Disable placement mode
                    repaint(); // Redraw the canvas
                }

                if (selectedRoom != null) {
                    enableRoomPlacement = true;
                    enableFurniturePlacement = false;
                }

                addDoorOnBorderClick(e); // Handle door placement logic
            }


            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedRoom != null && !resizing) {
                    // Check for overlap before adding new room
                    boolean overlaps = false;
                    for (Room room : rooms) {
                        if (selectedRoom.overlapsWith(room)) {
                            overlaps = true;
                            break;
                        }
                    }
                    if (!overlaps && !rooms.contains(selectedRoom)) {
                        RoomCommand command = new RoomCommand(Canvas.this, selectedRoom);
                        executeCommand(command);
                    } else if (overlaps) {
                        System.out.println("Overlap detected! Room not added.");
                    }
                }
                initialClick = null;
                selectedRoom = null;
                repaint();
                System.out.println("Repainted");
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            private boolean canShowDialog = true; // Declare cooldown flag as a field
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedRoom != null) {
                    int mouseX = (e.getX() / GRID_SIZE) * GRID_SIZE;
                    int mouseY = (e.getY() / GRID_SIZE) * GRID_SIZE;

                    int previousWidth = selectedRoom.width;
                    int previousHeight = selectedRoom.height;
                    int previousX = selectedRoom.x;
                    int previousY = selectedRoom.y;

                    if (resizing) {
                        // Handle resizing
                        if (mouseY - selectedRoom.y < MIN_ROOM_DIM || mouseX - selectedRoom.x < MIN_ROOM_DIM) {
                            int newHeight = (mouseY - selectedRoom.y < MIN_ROOM_DIM) ? MIN_ROOM_DIM : Math.abs(mouseY - selectedRoom.y);
                            int newWidth = (mouseX - selectedRoom.x < MIN_ROOM_DIM) ? MIN_ROOM_DIM : Math.abs(mouseX - selectedRoom.x);
                            selectedRoom.height = newHeight;
                            selectedRoom.width = newWidth;
                        } else {
                            selectedRoom.width = Math.abs(mouseX - selectedRoom.x);
                            selectedRoom.height = Math.abs(mouseY - selectedRoom.y);
                        }
                    } else if (initialClick != null) {
                        // Handle dragging
                        int deltaX = e.getX() - initialClick.x;
                        int deltaY = e.getY() - initialClick.y;

                        // Calculate new position for the room
                        int newX = selectedRoom.x + deltaX;
                        int newY = selectedRoom.y + deltaY;

                        // Constrain within bounds
                        newX = Math.max(gridXMin, Math.min(newX, gridXMax - selectedRoom.width));
                        newY = Math.max(gridYMin, Math.min(newY, gridYMax - selectedRoom.height));

                        // Calculate movement offsets
                        int offsetX = newX - selectedRoom.x;
                        int offsetY = newY - selectedRoom.y;

                        // Move the room
                        selectedRoom.x = newX;
                        selectedRoom.y = newY;

                        // Move furniture inside the room
                        for (Furniture furniture : selectedRoom.furnitureList) {
                            furniture.x += offsetX;
                            furniture.y += offsetY;
                        }

                        // Update initial click to avoid sudden jumps
                        initialClick = e.getPoint();

                        // Repaint the panel to reflect changes
                        repaint();
                    }

                    boolean overlaps = false;
                    for (Room room : rooms) {
                        if (room != selectedRoom && selectedRoom.overlapsWith(room)) {
                            overlaps = true;
                            break;
                        }
                    }

                    // Revert changes if overlap is detected
                    if (overlaps) {
                        if (canShowDialog) {
                            showOverlapDialog(); // Display overlap dialog
                            startCooldownTimer(); // Initiate cooldown
                        }

                        selectedRoom.width = previousWidth;
                        selectedRoom.height = previousHeight;
                        selectedRoom.x = previousX;
                        selectedRoom.y = previousY;

                        // Revert furniture positions
                        for (Furniture furniture : selectedRoom.furnitureList) {
                            furniture.x -= (selectedRoom.x - previousX);
                            furniture.y -= (selectedRoom.y - previousY);
                        }
                    }

                    repaint();
                }
            }

            private void showOverlapDialog() {
                JOptionPane.showMessageDialog(null, "Overlap detected! Reverting changes.");
            }

            private void startCooldownTimer() {
                canShowDialog = false; // Disable showing the dialog
                Timer cooldownTimer = new Timer(5000, e -> canShowDialog = true);
                cooldownTimer.setRepeats(false); // Timer runs only once
                cooldownTimer.start();
            }
        });
    }


    private Room findRoomAt(int x, int y) {
        for (Room room : rooms) {
            if (room.contains(x, y)) {
                return room;
            }
        }
        return null;
    }

    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        if (selectedRoom != null) {
            selectedRoom.draw(g);
        }
        for (Room room : rooms) {
            room.draw(g);
        }


//        for (Furniture furniture : furnitureList) {
//            g.drawImage(furniture.getImage(), furniture.getX(), furniture.getY(), furniture.getWidth(), furniture.getHeight(), this);
//        }
        for (Furniture furniture : furnitureList) {
            furniture.draw(g); // Each furniture handles its own rotation and drawing
        }
        for (Room room : rooms) {
            for (Door door : room.getDoors()) {
                door.draw(g);
            }
        }
    }
    public void rotateSelectedFurniture() {
        if (selectedFurniture != null) {
            selectedFurniture.rotationAngle = (selectedFurniture.rotationAngle + 90) % 360;
            repaint();
        }
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        int width = getWidth();
        int height = getHeight();
        for (int i = 0; i < width; i += GRID_SIZE) {
            g.drawLine(i, 0, i, height);
        }
        for (int j = 0; j < height; j += GRID_SIZE) {
            g.drawLine(0, j, width, j);
        }
    }

    public void saveDrawing(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath)))
        {
            oos.writeObject(rooms);
            oos.writeObject(furnitureList);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadDrawing(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))){
            rooms = (List<Room>) ois.readObject();
            furnitureList = (List<Furniture>) ois.readObject();
            for (Furniture furniture : furnitureList) {
                furniture.loadImage(); // Load the image after deserialization
            }
            repaint();
        }
        catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }



    public void clearDrawing() {
        rooms.clear(); // Clear the list of rooms
        furnitureList.clear(); // Clear the list of furniture
        primaryRoom = null;
        secondaryRoom = null;
        alignmentMode = false;
        relativeAddingMode = false;
        referenceRoom = null;
        selectedRoom = null;

        undoStack.clear();
        redoStack.clear();

        repaint(); // Repaint the canvas
    }




    public void addRoom(Room room){
        if(enableRoomPlacement == true && enableFurniturePlacement == false){
            rooms.add(room);
            repaint();
            enableRoomPlacement = false;
        }
    }

    public void removeRoom(Room room){
        rooms.remove(room);
        repaint();
    }


    public void undo(){
        if(!undoStack.isEmpty()){
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    public void redo(){
        if(!redoStack.isEmpty()){
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }

    public void setSelectedRoomType(RoomTypeProvider provider) {
        this.selectedRoomTypeProvider = provider;
    }
    public void setSelectedFurnitureType(FurnitureTypeProvider provider) {
        this.selectedFurnitureTypeProvider = provider;
    }

    public void enableAlignmentMode() {
        alignmentMode = true;
        primaryRoom = null;
        secondaryRoom = null;
    }
    public void alignRooms(Room primary, Room secondary, String direction) {
        int originalX = primary.x, originalY = primary.y;

        switch (direction) {
            case "North":
                primary.y = secondary.y - primary.height - GRID_SIZE;
                primary.x = secondary.x;
                break;
            case "South":
                primary.y = secondary.y + secondary.height + GRID_SIZE;
                primary.x = secondary.x;
                break;
            case "East":
                primary.x = secondary.x + secondary.width + GRID_SIZE;
                primary.y = secondary.y;
                break;
            case "West":
                primary.x = secondary.x - primary.width - GRID_SIZE;
                primary.y = secondary.y;
                break;
        }

        // Check for overlap
        for (Room room : rooms) {
            if (room != primary && primary.overlapsWith(room)) {
                JOptionPane.showMessageDialog(null, "Overlap detected! Alignment reverted.");
                primary.x = originalX;
                primary.y = originalY;
                repaint();
                return;
            }
        }

        // Check bounds
        if (primary.x < gridXMin || primary.y < gridYMin ||
                primary.x + primary.width > gridXMax || primary.y + primary.height > gridYMax) {
            JOptionPane.showMessageDialog(null, "Out of bounds! Alignment reverted.");
            primary.x = originalX;
            primary.y = originalY;
            repaint();
            return;
        }

        alignmentMode = false;
        primaryRoom = null;
        secondaryRoom = null;
        repaint();
    }

    public void setRelativeAddingMode(boolean enabled) {
        relativeAddingMode = enabled;
        referenceRoom = null; // Reset the reference room each time mode is enabled
        repaint();
    }

    private void resetRelativeAddingMode() {
        relativeAddingMode = false;
        referenceRoom = null;
    }


    public void addRoomRelativeToReference(String direction) {
        if (referenceRoom == null || !relativeAddingMode) return;

        int newRoomX = referenceRoom.x;
        int newRoomY = referenceRoom.y;

        switch (direction) {
            case "North":
                newRoomY = referenceRoom.y - Room.DEFAULT_HEIGHT - GRID_SIZE;
                break;
            case "South":
                newRoomY = referenceRoom.y + referenceRoom.height + GRID_SIZE;
                break;
            case "East":
                newRoomX = referenceRoom.x + referenceRoom.width + GRID_SIZE;
                break;
            case "West":
                newRoomX = referenceRoom.x - Room.DEFAULT_WIDTH - GRID_SIZE;
                break;
        }

        // Create a new room
        Room newRoom = new Room(newRoomX, newRoomY, selectedRoomTypeProvider.getSelectedRoomType().getColor());

        // Check for overlaps with existing rooms
        for (Room room : rooms) {
            if (newRoom.overlapsWith(room)) {
                JOptionPane.showMessageDialog(null, "Overlap detected! Room not added.");
                resetRelativeAddingMode();
                return;
            }
        }

        // Check bounds
        if (newRoom.x < gridXMin || newRoom.y < gridYMin ||
                newRoom.x + newRoom.width > gridXMax || newRoom.y + newRoom.height > gridYMax) {
            JOptionPane.showMessageDialog(null, "Out of bounds! Room not added.");
            resetRelativeAddingMode();
            return;
        }

        // Add room if no issues
        RoomCommand command = new RoomCommand(this, newRoom);
        executeCommand(command);

        resetRelativeAddingMode();
        repaint();
    }

    private boolean isSingleWallClicked(Room room, int clickX, int clickY) {
        int snapRange = 5; // Snap range for detecting border clicks

        // Top wall
        if (clickY >= room.y && clickY <= room.y + snapRange &&
                clickX >= room.x && clickX <= room.x + room.width) {
            return true;
        }
        // Bottom wall
        if (clickY >= room.y + room.height - snapRange && clickY <= room.y + room.height &&
                clickX >= room.x && clickX <= room.x + room.width) {
            return true;
        }
        // Left wall
        if (clickX >= room.x && clickX <= room.x + snapRange &&
                clickY >= room.y && clickY <= room.y + room.height) {
            return true;
        }
        // Right wall
        if (clickX >= room.x + room.width - snapRange && clickX <= room.x + room.width &&
                clickY >= room.y && clickY <= room.y + room.height) {
            return true;
        }

        return false;
    }

    private void addWindowToWall(Room room, int clickX, int clickY) {
        final int WINDOW_LENGTH = 40;
        final int BORDER_WIDTH = 10;

        int windowX = clickX, windowY = clickY, windowWidth = BORDER_WIDTH, windowHeight = WINDOW_LENGTH;

        // Top wall
        if (clickY >= room.y && clickY <= room.y + BORDER_WIDTH) {
            windowX = clickX - (WINDOW_LENGTH / 2);
            windowY = room.y;
            windowWidth = WINDOW_LENGTH;
            windowHeight = BORDER_WIDTH;
        }
        // Bottom wall
        else if (clickY >= room.y + room.height - BORDER_WIDTH && clickY <= room.y + room.height) {
            windowX = clickX - (WINDOW_LENGTH / 2);
            windowY = room.y + room.height - BORDER_WIDTH;
            windowWidth = WINDOW_LENGTH;
            windowHeight = BORDER_WIDTH;
        }
        // Left wall
        else if (clickX >= room.x && clickX <= room.x + BORDER_WIDTH) {
            windowX = room.x;
            windowY = clickY - (WINDOW_LENGTH / 2);
            windowWidth = BORDER_WIDTH;
            windowHeight = WINDOW_LENGTH;
        }
        // Right wall
        else if (clickX >= room.x + room.width - BORDER_WIDTH && clickX <= room.x + room.width) {
            windowX = room.x + room.width - BORDER_WIDTH;
            windowY = clickY - (WINDOW_LENGTH / 2);
            windowWidth = BORDER_WIDTH;
            windowHeight = WINDOW_LENGTH;
        }

        // Create the window and add it to the room
        Door window = new Door(windowX, windowY, windowWidth, windowHeight); // Reuse Door for Window
        room.addDoor(window); // Use the same structure to store windows
    }


    private void addDoorOnBorderClick(MouseEvent e) {
        int x = (e.getX() / GRID_SIZE) * GRID_SIZE;
        int y = (e.getY() / GRID_SIZE) * GRID_SIZE;

        for (Room room1 : rooms) {
            for (Room room2 : rooms) {
                if (room1 != room2 && room1.isAdjacentTo(room2)) {
                    // Check if the mouse click is on the shared border
                    if (room1.isBorderClickedWith(room2, x, y)) {
                        // Add door
                        room1.addDoorWith(room2, x, y);
                        if((room1.x == room2.x + room2.width || room1.x + room2.width == room2.x)){
                            room1.addDoorWith(room2, x-10, y);
                        }
                        else{
                            room1.addDoorWith(room2, x, y-10);
                        }
                        System.out.println("Door added between rooms at: " );
                        repaint();
                        return;
                    }
                }
            }
        }
        System.out.println("No shared border clicked for door placement.");

    }


}



interface RoomTypeProvider {
    RoomType getSelectedRoomType();
}
interface FurnitureTypeProvider {
    FurnitureType getSelectedFurnitureType();
}

