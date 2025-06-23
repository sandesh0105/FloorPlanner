
import java.awt.*;
import java.io.Serializable;
import javax.swing.ImageIcon;

class Furniture implements Serializable {
    private static final long serialVersionUID = 1L;
    public int x, y, width, height;
    // private String imagePath;
    private String imagePath; // Store image path as a string
    private transient Image image;
    //  private transient Image image; //changed in Nayyar
    // public Image image;
    public int rotationAngle; // 0, 90, 180, 270 degrees

    public Furniture(int x, int y, int width, int height,String imagePath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.imagePath = imagePath;
        this.rotationAngle = 0;
        loadImage();
    }

    void loadImage() {
        if (imagePath != null) {
            try {
                image = new ImageIcon(imagePath).getImage();
            } catch (Exception e) {
                System.err.println("Error loading image: " + imagePath);
            }
        }
    }
    public void draw(Graphics g) {
        if (image != null) {
            Graphics2D g2d = (Graphics2D) g;

            // Calculate the center of the furniture
            double centerX = x + width / 2.0;
            double centerY = y + height / 2.0;

            // Apply rotation around the center
            g2d.rotate(Math.toRadians(rotationAngle), centerX, centerY);

            // Draw the image
            g2d.drawImage(image, x, y, width, height, null);

            // Reset rotation transformation
            g2d.rotate(-Math.toRadians(rotationAngle), centerX, centerY);
        }
    }



//    public void draw(Graphics g) {
//        Graphics2D g2d = (Graphics2D) g;
//        g2d.rotate(Math.toRadians(rotationAngle), x + width / 2.0, y + height / 2.0);
//        g2d.drawImage(image, x, y, width, height, null);
//        g2d.rotate(-Math.toRadians(rotationAngle), x + width / 2.0, y + height / 2.0); // Reset rotation
//    }

    public String getImagePath() {
        return imagePath;
    }

    public boolean contains(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Getter for furniture width
    public int getWidth() {
        return width;
    }

    // Getter for furniture height
    public int getHeight() {
        return height;
    }

    // Getter for the image
    public Image getImage() {
        return image;
    }
}
