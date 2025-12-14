package functions;

public class Functions {
    /**
     * Вычисление интеграла функции методом трапеций
     * @param function интегрируемая функция
     * @param leftBorder левая граница интегрирования
     * @param rightBorder правая граница интегрирования
     * @param discretizationStep шаг дискретизации
     * @return значение интеграла
     * @throws IllegalArgumentException если интервал интегрирования выходит за границы области определения
     */
    public static double integrate(Function function, double leftBorder, double rightBorder, double discretizationStep) {
        // Проверка области определения
        if (leftBorder < function.getLeftDomainBorder() || rightBorder > function.getRightDomainBorder()) {
            throw new IllegalArgumentException(
                String.format("Integration interval [%.2f, %.2f] is outside function domain [%.2f, %.2f]", 
                    leftBorder, rightBorder, 
                    function.getLeftDomainBorder(), function.getRightDomainBorder())
            );
        }
        
        if (discretizationStep <= 0) {
            throw new IllegalArgumentException(
                String.format("Discretization step must be positive (got %.6f)", discretizationStep));
        }
        
        if (leftBorder >= rightBorder) {
            throw new IllegalArgumentException(
                String.format("Left border (%.2f) must be less than right border (%.2f)", 
                    leftBorder, rightBorder));
        }
        
        double integral = 0.0;
        double currentX = leftBorder;
        
        while (currentX < rightBorder) {
            double nextX = Math.min(currentX + discretizationStep, rightBorder);
            
            try {
                double fCurrent = function.getFunctionValue(currentX);
                double fNext = function.getFunctionValue(nextX);
                
                double trapezoidArea = (fCurrent + fNext) * (nextX - currentX) / 2.0;
                integral += trapezoidArea;
                
            } catch (IllegalArgumentException e) {
                // Если функция не определена в какой-то точке
                throw new IllegalArgumentException(
                    String.format("Function is not defined in interval [%.2f, %.2f]: %s", 
                        currentX, nextX, e.getMessage()));
            }
            
            currentX = nextX;
        }
        
        return integral;
    }
    
    /**
     * Экспоненциальная функция e^x
     */
    public static class ExpFunction implements Function {
        @Override
        public double getLeftDomainBorder() {
            return Double.NEGATIVE_INFINITY;
        }
        
        @Override
        public double getRightDomainBorder() {
            return Double.POSITIVE_INFINITY;
        }
        
        @Override
        public double getFunctionValue(double x) {
            return Math.exp(x);
        }
        
        @Override
        public String toString() {
            return "e^x";
        }
        
        @Override
        public boolean equals(Object obj) {
            return obj instanceof ExpFunction;
        }
        
        @Override
        public int hashCode() {
            return getClass().hashCode();
        }
    }
    
    /**
     * Фабричный метод для создания логарифмической функции
     * (теперь использует отдельный класс LogFunction)
     */
    public static LogFunction createLogFunction(double base) {
        return new LogFunction(base);
    }
}