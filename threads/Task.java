package threads;

import functions.Function;
import functions.LogFunction;

public class Task {
    private Function function;
    private double leftBorder;
    private double rightBorder;
    private double discretizationStep;
    private int tasksCount;
    private int generatedTasks = 0;
    private int solvedTasks = 0;
    
    // Флаги для синхронизации
    private boolean hasTask = false;
    private boolean generatorFinished = false;
    
    public Task() {
        this.tasksCount = 100; // значение по умолчанию
    }
    
    public Task(int tasksCount) {
        this.tasksCount = tasksCount;
    }
    
    // Геттеры
    public synchronized Function getFunction() {
        return function;
    }
    
    public synchronized double getLeftBorder() {
        return leftBorder;
    }
    
    public synchronized double getRightBorder() {
        return rightBorder;
    }
    
    public synchronized double getDiscretizationStep() {
        return discretizationStep;
    }
    
    public synchronized int getTasksCount() {
        return tasksCount;
    }
    
    public synchronized int getGeneratedTasks() {
        return generatedTasks;
    }
    
    public synchronized int getSolvedTasks() {
        return solvedTasks;
    }
    
    public synchronized boolean isHasTask() {
        return hasTask;
    }
    
    public synchronized boolean isAllTasksCompleted() {
        return solvedTasks >= tasksCount;
    }
    
    public synchronized boolean isGeneratorFinished() {
        return generatorFinished;
    }
    
    public synchronized void setGeneratorFinished() {
        this.generatorFinished = true;
        notifyAll();
    }
    
    /**
     * Установка задания с увеличением счетчика сгенерированных
     */
    public synchronized void setTask(Function function, double leftBorder, 
                                   double rightBorder, double discretizationStep) {
        this.function = function;
        this.leftBorder = leftBorder;
        this.rightBorder = rightBorder;
        this.discretizationStep = discretizationStep;
        this.hasTask = true;
        this.generatedTasks++;
        notifyAll();
    }
    
    /**
     * Ожидание задания
     */
    public synchronized TaskData waitForTask() throws InterruptedException {
        // Ждем пока появится задание или генератор завершит работу
        while (!hasTask && !generatorFinished && solvedTasks < tasksCount) {
            wait(50); // timeout 50 ms
        }
        
        if (hasTask) {
            return getTaskData();
        }
        return null;
    }
    
    /**
     * Отметка задания как решенного
     */
    public synchronized void markAsSolved() {
        this.hasTask = false;
        this.solvedTasks++;
        notifyAll();
    }
    
    /**
     * Получение данных задания
     */
    public synchronized TaskData getTaskData() {
        if (!hasTask || function == null) {
            return null;
        }
        return new TaskData(
            (LogFunction) function,
            leftBorder,
            rightBorder,
            discretizationStep
        );
    }
    
    /**
     * Вспомогательный класс для передачи данных задания
     */
    public static class TaskData {
        public final LogFunction function;
        public final double leftBorder;
        public final double rightBorder;
        public final double discretizationStep;
        
        public TaskData(LogFunction function, double leftBorder, 
                       double rightBorder, double discretizationStep) {
            this.function = function;
            this.leftBorder = leftBorder;
            this.rightBorder = rightBorder;
            this.discretizationStep = discretizationStep;
        }
    }
}