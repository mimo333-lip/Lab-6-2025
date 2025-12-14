package threads;

import functions.Functions;
import functions.LogFunction;

public class Generator extends Thread {
    private final Task task;
    private final Semaphore semaphore;
    
    public Generator(Task task, Semaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }
    
    @Override
    public void run() {
        System.out.println("Generator (with semaphore) started");
        
        try {
            for (int i = 0; i < task.getTasksCount() && !isInterrupted(); i++) {
                // Генерация случайных параметров
                double base = 1.0 + Math.random() * 9.0; // от 1 до 10
                double leftBorder = Math.random() * 100.0; // от 0 до 100
                double rightBorder = 100.0 + Math.random() * 100.0; // от 100 до 200
                double step = Math.random(); // от 0 до 1
                
                // Создание функции
                LogFunction function = Functions.createLogFunction(base);
                
                // Установка задания с использованием семафора
                semaphore.startWrite();
                try {
                    task.setTask(function, leftBorder, rightBorder, step);
                    
                    System.out.printf("Generator: Source %.2f %.2f %.2f%n", 
                        leftBorder, rightBorder, step);
                } finally {
                    semaphore.endWrite();
                }
                
                // Небольшая пауза
                Thread.sleep(1);
            }
        } catch (InterruptedException e) {
            System.out.println("Generator interrupted");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.out.println("Generator error: " + e.getMessage());
        } finally {
            // Помечаем, что генератор завершил работу
            synchronized (task) {
                task.setGeneratorFinished();
            }
        }
        
        System.out.println("Generator finished. Generated tasks: " + task.getGeneratedTasks());
    }
}