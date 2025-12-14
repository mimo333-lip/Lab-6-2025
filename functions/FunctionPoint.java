package functions;

public class FunctionPoint {
    private double x;
    private double y;
    
    public static final double EPS = 1e-9;
    
    public FunctionPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public FunctionPoint(FunctionPoint point) {
        this.x = point.x;
        this.y = point.y;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public void setY(double y) {
        this.y = y;
    }
    
    public static boolean equals(double a, double b) {
        return Math.abs(a - b) < EPS;
    }
    
    @Override
    public String toString() {
        return "(" + x + "; " + y + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    FunctionPoint that = (FunctionPoint) obj;
    //Double.compare
    return Double.compare(this.x, that.x) == 0 && 
           Double.compare(this.y, that.y) == 0;
}
    @Override
    public int hashCode() {
        long xBits = Double.doubleToLongBits(x);
        long yBits = Double.doubleToLongBits(y);
        return (int)(xBits ^ (xBits >>> 32)) ^ (int)(yBits ^ (yBits >>> 32));
    }
    
    @Override
    public Object clone() {
        return new FunctionPoint(this);
    }
}