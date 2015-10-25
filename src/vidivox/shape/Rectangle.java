package vidivox.shape;

import java.awt.*;

/**
 * Class to represent the rectangle shape
 */
public class Rectangle extends Shape{
    public Rectangle(int x, int y){
        super(x,y,100,100,5,5);
    }

    @Override
    public void draw(Graphics g) {
        g.fillRect((int)x,(int)y,width,height);
    }
}
