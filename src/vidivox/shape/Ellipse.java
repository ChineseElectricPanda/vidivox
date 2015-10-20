package vidivox.shape;

import java.awt.*;

/**
 * Created by hamcake on 20/10/15.
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
