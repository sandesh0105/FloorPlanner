package FloorPlanner;

 import com.formdev.flatlaf.FlatDarkLaf;
 import com.formdev.flatlaf.FlatLaf;
 import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class newFrame extends JFrame {
    private RoomType selectedRoomType;
    private Canvas canvas;
    public FurnitureType selectedFurnitureType;

    JButton bedroombtn;
    JButton bathroombtn;
    JButton kitchenbtn;
    JButton livingRoombtn;

    JButton rotClockButton;
    JButton rotAntiClockButton;

    public newFrame() {

        this.setSize(800, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add
        this.setTitle("2D FLOOR PLANNER");
        this.setLayout(new BorderLayout());
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); //Opens in full Screen

        canvas = new Canvas();

        JPanel sidePanel = createSidePanel();
        this.add(sidePanel, BorderLayout.WEST);

//@add
        // Add Menu Bar
        this.setJMenuBar(createMenuBar());

        // Add Canvas
        this.add(canvas, BorderLayout.CENTER);
        this.setVisible(true);

        // Connect Canvas Selection
        canvas.setSelectedRoomType(() -> selectedRoomType);
        canvas.setSelectedFurnitureType(() -> selectedFurnitureType);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");

        JMenuItem newFileItem = new JMenuItem("New File");
        newFileItem.addActionListener(e -> canvas.clearDrawing());

        JMenuItem saveFileItem = new JMenuItem("Save");
        saveFileItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showSaveDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                canvas.saveDrawing(file.getAbsolutePath());
            }
        });

        JMenuItem loadFileItem = new JMenuItem("Load");
        loadFileItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                canvas.loadDrawing(file.getAbsolutePath());
            }
        });

        // Add Items to File Menu
        fileMenu.add(newFileItem);
        fileMenu.add(saveFileItem);
        fileMenu.add(loadFileItem);

        // Edit Menu
        JMenu editMenu = new JMenu("Edit");

        JMenuItem undoItem = new JMenuItem("Undo");
        undoItem.addActionListener(e -> canvas.undo());

        JMenuItem redoItem = new JMenuItem("Redo");
        redoItem.addActionListener(e -> canvas.redo());

        // Add Items to Edit Menu
        editMenu.add(undoItem);
        editMenu.add(redoItem);

        // Add Menus to Menu Bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        return menuBar;
    }

    public void alignSelectedRooms(String direction) {
        if (canvas.primaryRoom != null && canvas.secondaryRoom != null) {
            canvas.alignRooms(canvas.primaryRoom, canvas.secondaryRoom, direction);
            canvas.alignmentMode = false; // Exit alignment mode after alignment
            canvas.repaint();
        } else {
            System.out.println("Please select two rooms first before choosing direction.");
        }
    }

    public JPanel createSidePanel() {
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(200, getHeight()));

        JLabel label = new JLabel("Select an item to add : ");
        sidePanel.add(label);

        JButton roomButton = new JButton("Room");
        roomButton.setFocusable(false);
        JButton furnitureButton = new JButton("Furniture");

        sidePanel.add(roomButton);


        JPanel roomOptionsPanel = new JPanel();
        roomOptionsPanel.setLayout(new BoxLayout(roomOptionsPanel, BoxLayout.Y_AXIS));
        roomOptionsPanel.setVisible(false);

        bedroombtn = new JButton("BEDROOM");
        bedroombtn.setBackground(new Color(255,0,0,95));
        bedroombtn.setFocusable(false);
        bedroombtn.addActionListener(e -> {
            selectedRoomType = RoomType.BEDROOM;
            canvas.enableRoomPlacement = true;
            canvas.enableFurniturePlacement = false;
        });
        bathroombtn = new JButton("BATHROOM");
        bathroombtn.setBackground(new Color(0,255,255,95));
        bathroombtn.setFocusable(false);
        bathroombtn.addActionListener(e -> {
            selectedRoomType = RoomType.BATHROOM;
            canvas.enableRoomPlacement = true;
            canvas.enableFurniturePlacement = false;
        });
        kitchenbtn = new JButton("KITCHEN");
        kitchenbtn.setBackground(new Color(255,165,0,95));
        kitchenbtn.setFocusable(false);
        kitchenbtn.addActionListener(e -> {
            selectedRoomType = RoomType.KITCHEN;
            canvas.enableRoomPlacement = true;
            canvas.enableFurniturePlacement = false;
        });
        livingRoombtn = new JButton("LIVINGROOM");
        livingRoombtn.setBackground(new Color(0,255,0,95));
        livingRoombtn.setFocusable(false);
        livingRoombtn.addActionListener(e -> {
            selectedRoomType = RoomType.LIVINGROOM;
            canvas.enableRoomPlacement = true;
            canvas.enableFurniturePlacement = false;
        });

        JPanel furnitureOptionsPanel = new JPanel();
        furnitureOptionsPanel.setLayout(new BoxLayout(furnitureOptionsPanel, BoxLayout.Y_AXIS));
        furnitureOptionsPanel.setVisible(false);




        JButton basinButton = new JButton("BASIN");
        //basinButton.setIcon(new ImageIcon("OOPS_PROJ_FINAL\\Furniture Images\\Basin.png"));
        basinButton.addActionListener(e -> {
            selectedFurnitureType = FurnitureType.BASIN;
            canvas.enableFurniturePlacement = true;
            canvas.enableRoomPlacement = false;
        });

        JButton ctableButton = new JButton("CENTRE TABLE");
        //ctableButton.setIcon(new ImageIcon("OOPS_PROJ_FINAL\\Furniture Images\\Center Table.png"));
        ctableButton.addActionListener(e -> {
            selectedFurnitureType = FurnitureType.CTABLE;
            canvas.enableFurniturePlacement = true;
            canvas.enableRoomPlacement = false;
        });

        JButton commodeButton = new JButton("WASHROOM");
        //commodeButton.setIcon(new ImageIcon("OOPS_PROJ_FINAL\\Furniture Images\\Commode.png"));
        commodeButton.addActionListener(e -> {
            selectedFurnitureType = FurnitureType.COMMODE;
            canvas.enableFurniturePlacement = true;
            canvas.enableRoomPlacement = false;
        });

        JButton comptableButton = new JButton("COMPUTER TABLE");
        //comptableButton.setIcon(new ImageIcon("OOPS_PROJ_FINAL\\Furniture Images\\Computer Table.png"));
        comptableButton.addActionListener(e -> {
            selectedFurnitureType = FurnitureType.COMPTABLE;
            canvas.enableFurniturePlacement = true;
            canvas.enableRoomPlacement = false;
        });

        JButton cupboardButton = new JButton("CUPBOARD");
        //cupboardButton.setIcon(new ImageIcon("OOPS_PROJ_FINAL\\Furniture Images\\Cupboard.png"));
        cupboardButton.addActionListener(e -> {
            selectedFurnitureType = FurnitureType.CUPBOARD;
            canvas.enableFurniturePlacement = true;
            canvas.enableRoomPlacement = false;
        });

        JButton dtableButton = new JButton("DINING Table");
        //dtableButton.setIcon(new ImageIcon("OOPS_PROJ_FINAL\\Furniture Images\\Dining Table.png"));
        dtableButton.addActionListener(e -> {
            selectedFurnitureType = FurnitureType.DINTABLE;
            canvas.enableFurniturePlacement = true;
            canvas.enableRoomPlacement = false;
        });

        JButton dbedButton = new JButton("DOUBLE BED");
        //dbedButton.setIcon(new ImageIcon("OOPS_PROJ_FINAL\\Furniture Images\\Double Bed.png"));
        dbedButton.addActionListener(e -> {
            selectedFurnitureType = FurnitureType.DBED;
            canvas.enableFurniturePlacement = true;
            canvas.enableRoomPlacement = false;
        });

        JButton drawerButton = new JButton("DRAWER");
        //drawerButton.setIcon(new ImageIcon("OOPS_PROJ_FINAL\\Furniture Images\\Drawer.png"));
        drawerButton.addActionListener(e -> {
            selectedFurnitureType = FurnitureType.DRAWER;
            canvas.enableFurniturePlacement = true;
            canvas.enableRoomPlacement = false;
        });

        JButton lsofaButton = new JButton("SOFA 1");
        //lsofaButton.setIcon(new ImageIcon("OOPS_PROJ_FINAL\\Furniture Images\\L Sofa.png"));
        lsofaButton.addActionListener(e -> {
            selectedFurnitureType = FurnitureType.LSOFA;
            canvas.enableFurniturePlacement = true;
            canvas.enableRoomPlacement = false;
        });

        JButton sbedButton = new JButton("SINGLE BED");
        //sbedButton.setIcon(new ImageIcon("OOPS_PROJ_FINAL\\Furniture Images\\Single Bed.png"));
        sbedButton.addActionListener(e -> {
            selectedFurnitureType = FurnitureType.SBED;
            canvas.enableFurniturePlacement = true;
            canvas.enableRoomPlacement = false;
        });

        JButton seatingButton = new JButton("SINGLE SEATING");
        //seatingButton.setIcon(new ImageIcon("OOPS_PROJ_FINAL\\Furniture Images\\Single Seating.png"));
        seatingButton.addActionListener(e -> {
            selectedFurnitureType = FurnitureType.SEATING;
            canvas.enableFurniturePlacement = true;
            canvas.enableRoomPlacement = false;
        });

        JButton sofaButton = new JButton("SOFA 2");
        //sofaButton.setIcon(new ImageIcon("OOPS_PROJ_FINAL\\Furniture Images\\Sofa.png"));
        sofaButton.addActionListener(e -> {
            selectedFurnitureType = FurnitureType.SOFA;
            canvas.enableFurniturePlacement = true;
            canvas.enableRoomPlacement = false;
        });




        furnitureOptionsPanel.add(basinButton);
        furnitureOptionsPanel.add(ctableButton);
        furnitureOptionsPanel.add(commodeButton);
        furnitureOptionsPanel.add(comptableButton);
        furnitureOptionsPanel.add(cupboardButton);
        furnitureOptionsPanel.add(dtableButton);
        furnitureOptionsPanel.add(dbedButton);
        furnitureOptionsPanel.add(drawerButton);
        furnitureOptionsPanel.add(lsofaButton);
        furnitureOptionsPanel.add(sbedButton);
        furnitureOptionsPanel.add(seatingButton);
        furnitureOptionsPanel.add(sofaButton);
        JButton rotateButton = new JButton("Rotate");
        rotateButton.addActionListener(e -> canvas.enableRotationMode());
        furnitureOptionsPanel.add(rotateButton);




        furnitureButton.addActionListener(e -> {
            selectedRoomType = null;
            furnitureOptionsPanel.setVisible(!furnitureOptionsPanel.isVisible());
        });


        roomOptionsPanel.add(bedroombtn);
        roomOptionsPanel.add(bathroombtn);
        roomOptionsPanel.add(kitchenbtn);
        roomOptionsPanel.add(livingRoombtn);
        sidePanel.add(roomOptionsPanel);
        sidePanel.add(furnitureButton);
        sidePanel.add(furnitureOptionsPanel);

        roomButton.addActionListener(e -> {
            roomOptionsPanel.setVisible(!roomOptionsPanel.isVisible());
            selectedFurnitureType=null;
        });


        JLabel instructionsLabel = new JLabel("<html>Click <b>Relative Adding</b>, select your preferred room button, select room from grid, and then direction.</html>");
        instructionsLabel.setPreferredSize(new Dimension(200, 0)); // Ensure the label respects the panel width
        instructionsLabel.setVerticalAlignment(SwingConstants.TOP);
        instructionsLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Padding around text
        sidePanel.add(instructionsLabel);

        JButton relativeAddingButton = new JButton("Relative Adding");
        relativeAddingButton.addActionListener(e -> {
            canvas.setRelativeAddingMode(true);
        });
        sidePanel.add(relativeAddingButton);

        JButton buttonNorth = new JButton("NORTH");
        buttonNorth.addActionListener(e -> canvas.addRoomRelativeToReference("North"));
        JButton buttonSouth = new JButton("SOUTH");
        buttonSouth.addActionListener(e -> canvas.addRoomRelativeToReference("South"));
        JButton buttonEast = new JButton("EAST");
        buttonEast.addActionListener(e -> canvas.addRoomRelativeToReference("East"));
        JButton buttonWest = new JButton("WEST");
        buttonWest.addActionListener(e -> canvas.addRoomRelativeToReference("West"));
        sidePanel.add(buttonNorth);
        sidePanel.add(buttonSouth);
        sidePanel.add(buttonEast);
        sidePanel.add(buttonWest);


        JLabel instructionsLabel1 = new JLabel("<html>Click <b>Align</b>, select both rooms, and then select direction.</html>");
        instructionsLabel1.setPreferredSize(new Dimension(200, 0)); // Ensure the label respects the panel width
        instructionsLabel1.setVerticalAlignment(SwingConstants.TOP);
        instructionsLabel1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Padding around text
        sidePanel.add(instructionsLabel1);


        JButton alignNorth = new JButton("North");
        JButton alignSouth = new JButton("South");
        JButton alignEast = new JButton("East");
        JButton alignWest = new JButton("West");
        JButton alignButton = new JButton("Align");

        sidePanel.add(alignButton);
        sidePanel.add(alignNorth);
        sidePanel.add(alignSouth);
        sidePanel.add(alignEast);
        sidePanel.add(alignWest);

        alignNorth.addActionListener(e -> alignSelectedRooms("North"));
        alignSouth.addActionListener(e -> alignSelectedRooms("South"));
        alignEast.addActionListener(e -> alignSelectedRooms("East"));
        alignWest.addActionListener(e -> alignSelectedRooms("West"));
        alignButton.addActionListener(e -> canvas.enableAlignmentMode());



//        JButton alignNorth = new JButton("North");
//        JButton alignSouth = new JButton("South");
//        JButton alignEast = new JButton("East");
//        JButton alignWest = new JButton("West");
//        JButton alignButton = new JButton("Align");
//
//        sidePanel.add(alignNorth);
//        sidePanel.add(alignSouth);
//        sidePanel.add(alignEast);
//        sidePanel.add(alignWest);
//        sidePanel.add(alignButton);
//
//        alignNorth.addActionListener(e -> alignSelectedRooms("North"));
//        alignSouth.addActionListener(e -> alignSelectedRooms("South"));
//        alignEast.addActionListener(e -> alignSelectedRooms("East"));
//        alignWest.addActionListener(e -> alignSelectedRooms("West"));
//        alignButton.addActionListener(e -> canvas.enableAlignmentMode());
//
//        JButton buttonNorth = new JButton("NORTH");
//        buttonNorth.addActionListener(e -> canvas.addRoomRelativeToReference("North"));
//        JButton buttonSouth = new JButton("SOUTH");
//        buttonSouth.addActionListener(e -> canvas.addRoomRelativeToReference("South"));
//        JButton buttonEast = new JButton("EAST");
//        buttonEast.addActionListener(e -> canvas.addRoomRelativeToReference("East"));
//        JButton buttonWest = new JButton("WEST");
//        buttonWest.addActionListener(e -> canvas.addRoomRelativeToReference("West"));
//        sidePanel.add(buttonNorth);
//        sidePanel.add(buttonSouth);
//        sidePanel.add(buttonEast);
//        sidePanel.add(buttonWest);
//
//
//        JButton relativeAddingButton = new JButton("Relative Adding");
//        relativeAddingButton.addActionListener(e -> {
//            canvas.setRelativeAddingMode(true);
//        });
//        sidePanel.add(relativeAddingButton);




//        rotAntiClockButton = new JButton("Rotate Anticlockwise");
//        rotAntiClockButton.addActionListener(e ->{
//            canvas.angle -= 90;
//            canvas.rotation = true;
//        });
//        sidePanel.add(rotAntiClockButton);

//        rotClockButton = new JButton("Rotate Clockwise");
//        rotClockButton.addActionListener(e ->{
//            canvas.angle += 90;
//            canvas.rotation = true;
//        });
//        sidePanel.add(rotClockButton);
        JButton windowButton = new JButton("Add Window");
        windowButton.addActionListener(e -> canvas.enableWindowAddingMode());
        sidePanel.add(windowButton);


        return sidePanel;
    }
    public static void main(String [] args){

         FlatLaf.registerCustomDefaultsSource("C:\\Users\\Darshan Jain\\IdeaProjects\\src\\src\\oop\\Project\\code\\CustomFlatDarkLaf.properties"); // Adjust this path
         FlatLightLaf.setup();
//         FlatDarkLaf.setup();
//         FlatLightLaf.setup();
         try{
             UIManager.setLookAndFeel(new FlatLightLaf());
         }
         catch (Exception ex){
             ex.printStackTrace();
         }
        new newFrame();
    }

}
enum FurnitureType {

    BASIN("Basin.png"),
    CTABLE("Center Table.png"),
    COMMODE("Commode.png"),
    COMPTABLE("Computer Table.png"),
    CUPBOARD("Cupboard.png"),
    DINTABLE("Dining Table.png"),
    DBED("Double Bed.png"),
    DRAWER("Drawer.png"),
    LSOFA("L Sofa.png"),
    SBED("Single Bed.png"),
    SEATING("Single Seating.png"),
    SOFA("Sofa.png");


    private final String imagePath;

    // Constructor to initialize the image path for each furniture type
    FurnitureType(String imagePath) {
        this.imagePath = imagePath;
    }

    // Getter for the image path
    public String getImagePath() {
        return imagePath;
    }

    // Load the image associated with this FurnitureType
    public Image loadImage() {
        try {
            // Directly use 'this' to refer to the current instance of the enum
            return new ImageIcon(this.getImagePath()).getImage();
        } catch (Exception e) {
            System.err.println("Error loading image: " + this.imagePath);
            return null; // Return null if image loading fails
        }
    }
}

