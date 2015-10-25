package vidivox.shape;

import java.awt.*;

/**
 * Class to represent the ellipse shape
 */
public class Ellipse extends Shape {
    public Ellipse(int x, int y){
        super(x,y,100,100,5,5);
    }

    @Override
    public void draw(Graphics g) {
        g.fillOval((int)x,(int)y,width,height);
    }
}
