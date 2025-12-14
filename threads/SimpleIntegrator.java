package threads;

import functions.Functions;

public class SimpleIntegrator implements Runnable {
    private final Task task;
    
    public SimpleIntegrator(Task task) {
        this.task = task;
    }
    
    @Override
    public void run() {
        System.out.println("SimpleIntegrator started");
        
        while (true) {
            try {
                // Проверяем, все ли задания выполнены
                synchronized (task) {
                    if (task.getSolvedTasks() >= task.getTasksCount()) {
                        System.out.println("SimpleIntegrator: All tasks solved, exiting...");
                        break;
                    }
                }
                
                // Получение задания
                Task.TaskData taskData = null;
                boolean hasTask = false;
                
                synchronized (task) {
                    // Ждем, пока появится задание, но не более 100 мс
                    long startTime = System.currentTimeMillis();
                    while (!task.isHasTask() && 
                           (System.currentTimeMillis() - startTime) < 100 &&
                           task.getSolvedTasks() < task.getTasksCount()) {
                        task.wait(10);
                    }
                    
                    if (task.isHasTask()) {
                        // Получаем данные задания
                        taskData = task.getTaskData();
                        hasTask = true;
                    } else if (task.getSolvedTasks() >= task.getTasksCount()) {
                        // Все задания выполнены
                        break;
                    }
                }
                
                if (!hasTask || taskData == null) {
                    // Нет задания, проверяем может генератор завершил работу
                    synchronized (task) {
                        if (task.getGeneratedTasks() >= task.getTasksCount() &&
                            task.getSolvedTasks() >= task.getGeneratedTasks()) {
                            System.out.println("SimpleIntegrator: No more tasks, exiting...");
                            break;
                        }
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
                    System.out.printf("Result %.2f %.2f %.2f %.6f%n", 
                        taskData.leftBorder, 
                        taskData.rightBorder, 
                        taskData.discretizationStep, 
                        result);
                    
                } catch (IllegalArgumentException e) {
                    System.out.printf("Integration error for [%.2f, %.2f]: %s%n", 
                        taskData.leftBorder, taskData.rightBorder, e.getMessage());
                }
                
                // Отмечаем задание как решенное
                synchronized (task) {
                    task.markAsSolved();
                }
                
                // Небольшая пауза
                Thread.sleep(1);
                
            } catch (InterruptedException e) {
                System.out.println("Integrator interrupted");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.out.println("Integrator error: " + e.getMessage());
                break;
            }
        }
        
        System.out.println("SimpleIntegrator finished. Solved tasks: " + task.getSolvedTasks());
    }
}