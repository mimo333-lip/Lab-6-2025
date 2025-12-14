package threads;

/**
 * Семафор с поддержкой чтения и записи
 */
public class Semaphore {
    private int readers = 0;
    private int writers = 0;
    private int writeRequests = 0;
    
    /**
     * Начать чтение
     */
    public synchronized void startRead() throws InterruptedException {
        while (writers > 0 || writeRequests > 0) {
            wait();
        }
        readers++;
    }
    
    /**
     * Завершить чтение
     */
    public synchronized void endRead() {
        readers--;
        notifyAll();
    }
    
    /**
     * Начать запись
     */
    public synchronized void startWrite() throws InterruptedException {
        writeRequests++;
        while (readers > 0 || writers > 0) {
            wait();
        }
        writeRequests--;
        writers++;
    }
    
    /**
     * Завершить запись
     */
    public synchronized void endWrite() {
        writers--;
        notifyAll();
    }
}