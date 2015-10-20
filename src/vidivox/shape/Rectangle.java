package vidivox.shape;

import java.awt.*;

/**
 * Created by hamcake on 20/10/15.
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
