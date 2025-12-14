package threads;

import functions.Functions;

public class Integrator extends Thread {
    private final Task task;
    private final Semaphore semaphore;
    
    public Integrator(Task task, Semaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }
    
    @Override
    public void run() {
        System.out.println("Integrator (with semaphore) started");
        
        try {
            while (!task.isAllTasksCompleted() && !isInterrupted()) {
                // Получение задания с использованием семафора
                semaphore.startRead();
                try {
                    // Ожидание задания
                    Task.TaskData taskData = task.waitForTask();
                    
                    if (taskData == null) {
                        // Если заданий нет и генератор завершил работу
                        if (task.isGeneratorFinished() && 
                            task.getSolvedTasks() >= task.getGeneratedTasks()) {
                            System.out.println("Integrator: No more tasks, exiting...");
                            break;
                        }
                        continue;
                    }
                    
                    // Вычисление интеграла
                    try {
                        double result = Functions.integrate(
                            taskData.function,
                            taskData.leftBorder,
                            taskData.rightBorder,
                            taskData.discretizationStep
                        );
                        
                        // Вывод результата
                        System.out.printf("Integrator: Result %.2f %.2f %.2f %.6f%n", 
                            taskData.leftBorder, 
                            taskData.rightBorder, 
                            taskData.discretizationStep, 
                            result);
                        
                    } catch (IllegalArgumentException e) {
                        System.out.printf("Integrator: Error for [%.2f, %.2f]: %s%n", 
                            taskData.leftBorder, taskData.rightBorder, e.getMessage());
                    }
                    
                    // Отметка задания как решенного
                    task.markAsSolved();
                    
                } finally {
                    semaphore.endRead();
                }
                
                // Небольшая пауза
                Thread.sleep(1);
            }
        } catch (InterruptedException e) {
            System.out.println("Integrator interrupted");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.out.println("Integrator error: " + e.getMessage());
        }
        
        System.out.println("Integrator finished. Solved tasks: " + task.getSolvedTasks());
    }
}