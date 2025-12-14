package threads;

import functions.Functions;
import functions.LogFunction;

public class SimpleGenerator implements Runnable {
    private final Task task;
    
    public SimpleGenerator(Task task) {
        this.task = task;
    }
    
    @Override
    public void run() {
        System.out.println("SimpleGenerator started");
        
        try {
            for (int i = 0; i < task.getTasksCount(); i++) {
                // Проверяем прерывание
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("SimpleGenerator: Interrupted");
                    break;
                }
                
                // Генерация случайных параметров
                double base = 1.0 + Math.random() * 9.0; // от 1 до 10
                double leftBorder = Math.random() * 100.0; // от 0 до 100
                double rightBorder = 100.0 + Math.random() * 100.0; // от 100 до 200
                double step = Math.random(); // от 0 до 1
                
                // Создание функции - используем фабричный метод
                LogFunction function = Functions.createLogFunction(base);
                
                // Установка задания с использованием синхронизированного метода setTask
                synchronized (task) {
                    task.setTask(function, leftBorder, rightBorder, step);
                    
                    System.out.printf("Source %.2f %.2f %.2f%n", 
                        leftBorder, rightBorder, step);
                }
                
                // Небольшая пауза для имитации работы
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
        
        System.out.println("SimpleGenerator finished. Generated: " + task.getGeneratedTasks());
    }
}