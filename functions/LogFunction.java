package functions;

/**
 * Логарифмическая функция с произвольным основанием
 */
public class LogFunction implements Function {
    private final double base;
    
    public LogFunction(double base) {
        if (base <= 0 || Math.abs(base - 1.0) < 1e-10) {
            throw new IllegalArgumentException(
                String.format("Logarithm base must be positive and not equal to 1 (got %.2f)", base));
        }
        this.base = base;
    }
    
    @Override
    public double getLeftDomainBorder() {
        return 0; // Логарифм определен для x > 0
    }
    
    @Override
    public double getRightDomainBorder() {
        return Double.POSITIVE_INFINITY;
    }
    
    @Override
    public double getFunctionValue(double x) {
        if (x <= 0) {
            throw new IllegalArgumentException(
                String.format("Logarithm is defined only for positive arguments (got %.2f)", x));
        }
        return Math.log(x) / Math.log(base);
    }
    
    public double getBase() {
        return base;
    }
    
    @Override
    public String toString() {
        return String.format("log_%.2f(x)", base);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LogFunction that = (LogFunction) obj;
        return Math.abs(this.base - that.base) < 1e-10;
    }
    
    @Override
    public int hashCode() {
        return Double.hashCode(base);
    }
}