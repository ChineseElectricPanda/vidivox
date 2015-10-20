package vidivox.shape;

import java.awt.*;

/**
 * Created by hamcake on 20/10/15.
 */
public abstract class Shape {
    protected int width,height;
    protected float x,y;
    protected float xSpeed,ySpeed;

    public Shape(int x,int y,int width,int height, float xSpeed,float ySpeed){
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
        this.xSpeed=xSpeed;
        this.ySpeed=ySpeed;
    }

    public void move(int boundaryX, int boundaryY){
        if((x>=boundaryX-width && xSpeed>0)||(x<=0 && xSpeed<0)){
            xSpeed*=-1;
        }
        if((y>=boundaryY-height && ySpeed>0)||(y<=0 && ySpeed<0)){
            ySpeed*=-1;
        }
        x+=xSpeed;
        y+=ySpeed;
    }
    public abstract void draw(Graphics g);
}
