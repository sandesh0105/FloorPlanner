
import java.awt.*;
import java.io.Serializable;

class Door implements Serializable {
    int x, y, width, height;
    private static final long serialVersionUID = 1L;

    public Door(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g) {

        Graphics2D g2d = (Graphics2D)g;
        g2d.setStroke(new BasicStroke(5));
        g2d.setColor(Color.WHITE); // Door color
        g2d.fillRect(x, y, width, height);
        // g2d.drawArc(x-10, y, 40, 75, 0, 90);
    }


}