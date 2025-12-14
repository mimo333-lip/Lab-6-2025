package functions;

public interface TabulatedFunction extends Function, java.io.Serializable, Cloneable {
    int getPointCount();
    double getPointX(int index) throws FunctionPointIndexOutOfBoundsException;
    double getPointY(int index) throws FunctionPointIndexOutOfBoundsException;
    void setPointX(int index, double x) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException;
    void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException;
    FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException;
    void setPoint(int index, FunctionPoint point) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException;
    void addPoint(FunctionPoint point) throws InappropriateFunctionPointException;
    void deletePoint(int index) throws FunctionPointIndexOutOfBoundsException, IllegalStateException;
    
    // clone() (задание 4)
    Object clone() throws CloneNotSupportedException;
}