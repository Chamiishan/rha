package roadcondition.cynsore.cyient.com.cynsore.graph;

/**
 * Created by ij39559 on 8/13/2018.
 */

public class GradientColor {

    private int startColor;
    private int endColor;

    public GradientColor(int startColor, int endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
    }

    public int getStartColor() {
        return startColor;
    }

    public void setStartColor(int startColor) {
        this.startColor = startColor;
    }

    public int getEndColor() {
        return endColor;
    }

    public void setEndColor(int endColor) {
        this.endColor = endColor;
    }
}