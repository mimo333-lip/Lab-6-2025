package functions;

import java.io.*;

public class LinkedListTabulatedFunction implements TabulatedFunction, Externalizable {
    private static class FunctionNode implements Serializable {
        FunctionPoint point;
        FunctionNode prev;
        FunctionNode next;
        
        FunctionNode(FunctionPoint point) {
            this.point = point;
        }
        
        FunctionNode() {
            this.point = null;
        }
    }
    
    private FunctionNode head;
    private int size;
    private FunctionNode lastAccessedNode;
    private int lastAccessedIndex;
    
    private static final long serialVersionUID = 1L;
    
    public LinkedListTabulatedFunction() {
        initHead();
    }
    
    public LinkedListTabulatedFunction(FunctionPoint[] points) throws IllegalArgumentException {
        if (points.length < 2) {
            throw new IllegalArgumentException("At least 2 points required");
        }
        
        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() <= points[i-1].getX()) {
                throw new IllegalArgumentException("Points must be sorted by X in ascending order");
            }
        }
        
        initHead();
        this.size = 0;
        
        for (FunctionPoint point : points) {
            FunctionNode newNode = addNodeToTail();
            newNode.point = (FunctionPoint) point.clone();
        }
        
        lastAccessedNode = (size > 0) ? head.next : head;
        lastAccessedIndex = 0;
    }
    
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointCount) throws IllegalArgumentException {
        if (leftX >= rightX || pointCount < 2) {
            throw new IllegalArgumentException("Invalid parameters: leftX must be less than rightX and pointCount >= 2");
        }
        
        initHead();
        this.size = 0;
        
        double step = (rightX - leftX) / (pointCount - 1);
        for (int i = 0; i < pointCount; i++) {
            double x = leftX + i * step;
            double y = Math.sin(x);
            FunctionNode newNode = addNodeToTail();
            newNode.point = new FunctionPoint(x, y);
        }
        
        lastAccessedNode = (size > 0) ? head.next : head;
        lastAccessedIndex = 0;
    }
    
    public LinkedListTabulatedFunction(double[] xValues, double[] yValues) throws IllegalArgumentException {
        if (xValues.length < 2 || xValues.length != yValues.length) {
            throw new IllegalArgumentException("Arrays must have same length and at least 2 elements");
        }
        
        for (int i = 1; i < xValues.length; i++) {
            if (xValues[i] <= xValues[i - 1]) {
                throw new IllegalArgumentException("X values must be strictly increasing");
            }
        }
        
        initHead();
        this.size = 0;
        
        for (int i = 0; i < xValues.length; i++) {
            FunctionNode newNode = addNodeToTail();
            newNode.point = new FunctionPoint(xValues[i], yValues[i]);
        }
        
        lastAccessedNode = (size > 0) ? head.next : head;
        lastAccessedIndex = 0;
    }
    
    private void initHead() {
        head = new FunctionNode();
        head.next = head;
        head.prev = head;
        size = 0;
        lastAccessedNode = head;
        lastAccessedIndex = -1;
    }
    
    private FunctionNode getNodeByIndex(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }
        
        if (lastAccessedNode != head && lastAccessedIndex != -1) {
            int diff = index - lastAccessedIndex;
            if (Math.abs(diff) < Math.min(index + 1, size - index)) {
                FunctionNode current = lastAccessedNode;
                if (diff > 0) {
                    for (int i = 0; i < diff; i++) {
                        current = current.next;
                    }
                } else if (diff < 0) {
                    for (int i = 0; i < -diff; i++) {
                        current = current.prev;
                    }
                }
                lastAccessedNode = current;
                lastAccessedIndex = index;
                return current;
            }
        }
        
        FunctionNode current;
        if (index < size / 2) {
            current = head.next;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = head.prev;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
        }
        
        lastAccessedNode = current;
        lastAccessedIndex = index;
        return current;
    }
    
    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode();
        FunctionNode tail = head.prev;
        
        newNode.prev = tail;
        newNode.next = head;
        tail.next = newNode;
        head.prev = newNode;
        
        size++;
        return newNode;
    }
    
    private FunctionNode addNodeByIndex(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index > size) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }
        
        if (index == size) {
            return addNodeToTail();
        }
        
        FunctionNode nextNode = getNodeByIndex(index);
        FunctionNode prevNode = nextNode.prev;
        
        FunctionNode newNode = new FunctionNode();
        newNode.prev = prevNode;
        newNode.next = nextNode;
        prevNode.next = newNode;
        nextNode.prev = newNode;
        
        size++;
        
        if (index <= lastAccessedIndex) {
            lastAccessedIndex++;
        }
        
        return newNode;
    }
    
    private FunctionNode deleteNodeByIndex(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }
        
        FunctionNode nodeToDelete = getNodeByIndex(index);
        FunctionNode prevNode = nodeToDelete.prev;
        FunctionNode nextNode = nodeToDelete.next;
        
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
        
        size--;
        
        if (nodeToDelete == lastAccessedNode) {
            lastAccessedNode = (nextNode != head) ? nextNode : head.next;
        } else if (index < lastAccessedIndex) {
            lastAccessedIndex--;
        }
        
        return nodeToDelete;
    }
    
    @Override
    public double getLeftDomainBorder() {
        return (size > 0) ? head.next.point.getX() : Double.NaN;
    }
    
    @Override
    public double getRightDomainBorder() {
        return (size > 0) ? head.prev.point.getX() : Double.NaN;
    }
    
    @Override
    public double getFunctionValue(double x) {
        if (size == 0 || x < head.next.point.getX() || x > head.prev.point.getX()) {
            return Double.NaN;
        }
        
        FunctionNode current = head.next;
        while (current != head) {
            if (FunctionPoint.equals(current.point.getX(), x)) {
                return current.point.getY();
            }
            
            if (current.next != head && x >= current.point.getX() && x <= current.next.point.getX()) {
                double x1 = current.point.getX();
                double y1 = current.point.getY();
                double x2 = current.next.point.getX();
                double y2 = current.next.point.getY();
                
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
            
            current = current.next;
        }
        
        return Double.NaN;
    }
    
    @Override
    public int getPointCount() {
        return size;
    }
    
    @Override
    public double getPointX(int index) throws FunctionPointIndexOutOfBoundsException {
        return getNodeByIndex(index).point.getX();
    }
    
    @Override
    public double getPointY(int index) throws FunctionPointIndexOutOfBoundsException {
        return getNodeByIndex(index).point.getY();
    }
    
    @Override
    public void setPointX(int index, double x) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);
        
        if ((index > 0 && x <= node.prev.point.getX()) || 
            (index < size - 1 && x >= node.next.point.getX())) {
            throw new InappropriateFunctionPointException(x);
        }
        
        node.point.setX(x);
    }
    
    @Override
    public void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException {
        getNodeByIndex(index).point.setY(y);
    }
    
    @Override
    public FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException {
        return (FunctionPoint) getNodeByIndex(index).point.clone();
    }
    
    @Override
    public void setPoint(int index, FunctionPoint point) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);
        double x = point.getX();
        
        if ((index > 0 && x <= node.prev.point.getX()) || 
            (index < size - 1 && x >= node.next.point.getX())) {
            throw new InappropriateFunctionPointException(x);
        }
        
        node.point = (FunctionPoint) point.clone();
    }
    
    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode current = head.next;
        while (current != head) {
            if (FunctionPoint.equals(current.point.getX(), point.getX())) {
                throw new InappropriateFunctionPointException(point.getX());
            }
            current = current.next;
        }
        
        current = head.next;
        int index = 0;
        while (current != head && current.point.getX() < point.getX()) {
            current = current.next;
            index++;
        }
        
        FunctionNode newNode = addNodeByIndex(index);
        newNode.point = (FunctionPoint) point.clone();
    }
    
    @Override
    public void deletePoint(int index) throws FunctionPointIndexOutOfBoundsException, IllegalStateException {
        if (size < 3) {
            throw new IllegalStateException("Cannot delete point: minimum 3 points required");
        }
        
        deleteNodeByIndex(index);
    }
    
    // toString() - задание 3
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        FunctionNode current = head.next;
        boolean first = true;
        while (current != head) {
            if (!first) sb.append(", ");
            sb.append(current.point);
            current = current.next;
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
    
    // equals() - задание 3
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        
        if (!(obj instanceof TabulatedFunction)) return false;
        
        TabulatedFunction other = (TabulatedFunction) obj;
        
        if (this.size != other.getPointCount()) {
            return false;
        }
        
        if (obj instanceof LinkedListTabulatedFunction) {
            LinkedListTabulatedFunction listOther = (LinkedListTabulatedFunction) obj;
            FunctionNode current1 = this.head.next;
            FunctionNode current2 = listOther.head.next;
            
            while (current1 != this.head && current2 != listOther.head) {
                if (!current1.point.equals(current2.point)) {
                    return false;
                }
                current1 = current1.next;
                current2 = current2.next;
            }
            return current1 == this.head && current2 == listOther.head;
        } else {
            for (int i = 0; i < size; i++) {
                try {
                    if (!this.getPoint(i).equals(other.getPoint(i))) {
                        return false;
                    }
                } catch (FunctionPointIndexOutOfBoundsException e) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    // hashCode() - задание 3
    @Override
    public int hashCode() {
        int result = size;
        FunctionNode current = head.next;
        while (current != head) {
            result ^= current.point.hashCode();
            current = current.next;
        }
        return result;
    }
    
    // clone() - задание 3 и 4 (оптимизированное глубокое клонирование)
    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            LinkedListTabulatedFunction clone = (LinkedListTabulatedFunction) super.clone();
            clone.initHead();
            
            FunctionNode current = this.head.next;
            while (current != this.head) {
                FunctionNode newNode = clone.addNodeToTail();
                newNode.point = (FunctionPoint) current.point.clone();
                current = current.next;
            }
            
            clone.lastAccessedNode = (clone.size > 0) ? clone.head.next : clone.head;
            clone.lastAccessedIndex = 0;
            
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Clone not supported", e);
        }
    }
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(size);
        FunctionNode current = head.next;
        while (current != head) {
            out.writeDouble(current.point.getX());
            out.writeDouble(current.point.getY());
            current = current.next;
        }
    }
    
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        initHead();
        int newSize = in.readInt();
        for (int i = 0; i < newSize; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            FunctionNode newNode = addNodeToTail();
            newNode.point = new FunctionPoint(x, y);
        }
        lastAccessedNode = (size > 0) ? head.next : head;
        lastAccessedIndex = 0;
    }
}