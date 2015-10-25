package vidivox.ui;

import javax.swing.JSlider;

/**
 * A JSlider which will jump to the position clicked when it is clicked
 */
public class EnhancedJSlider extends JSlider {
    //override default slider behaviour so that the slider jumps to clicked position
    public EnhancedJSlider() {
//        setUI(new MetalSliderUI(){
//            @Override
//            protected void scrollDueToClickInTrack(int dir) {
//                int value=slider.getValue();
//                if(slider.getOrientation()==HORIZONTAL) {
//                    value = this.valueForXPosition(slider.getMousePosition().x);
//                }else if(slider.getOrientation()==VERTICAL){
//                    value = this.valueForXPosition(slider.getMousePosition().y);
//                }
//                setValue(value);
//            }
//        });
    }

    public EnhancedJSlider(int orientation) {
        super(orientation);
    }
}
