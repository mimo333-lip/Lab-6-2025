import functions.*;
import threads.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Laboratory Work #6 ===");
        System.out.println("Multithreaded Integration\n");
        
        try {
            // Задание 1: Тестирование интегрирования
            testIntegration();
            
            // Задание 2: Последовательная версия
            nonThread();
            
            // Задание 3: Простая многопоточная версия
            simpleThreads();
            
            // Задание 4: Продвинутая версия с семафором
            complicatedThreads();
            
        } catch (Exception e) {
            System.out.println("Error in main: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Задание 1: Тестирование интегрирования
     */
    private static void testIntegration() {
        System.out.println("1. Testing integration:");
        
        // Тестирование интеграла экспоненты
        Functions.ExpFunction expFunction = new Functions.ExpFunction();
        
        double left = 0.0;
        double right = 1.0;
        double theoretical = Math.exp(1) - Math.exp(0); // e^1 - e^0 = e - 1
        
        System.out.println("  Testing exp(x) on [" + left + ", " + right + "]");
        System.out.println("  Theoretical value: " + theoretical);
        
        // Поиск шага для точности 10^-7
        double[] steps = {0.1, 0.01, 0.001, 0.0001, 0.00001};
        double targetPrecision = 1e-7;
        
        for (double step : steps) {
            double result = Functions.integrate(expFunction, left, right, step);
            double error = Math.abs(result - theoretical);
            
            System.out.printf("    Step = %.5f: result = %.10f, error = %.10f%n", 
                step, result, error);
            
            if (error < targetPrecision) {
                System.out.println("    Required step for 1e-7 precision: " + step);
                break;
            }
        }
        
        // Тестирование обработки ошибок
        System.out.println("\n  Testing error handling:");
        
        // Тест 1: Шаг <= 0
        try {
            Functions.integrate(expFunction, 0, 1, 0);
            System.out.println("    ERROR: Should have thrown exception for step=0!");
        } catch (IllegalArgumentException e) {
            System.out.println("    OK (step=0): " + e.getMessage());
        }
        
        // Тест 2: Левая граница > правой
        try {
            Functions.integrate(expFunction, 10, 1, 0.1);
            System.out.println("    ERROR: Should have thrown exception for left>right!");
        } catch (IllegalArgumentException e) {
            System.out.println("    OK (left>right): " + e.getMessage());
        }
        
        // Тест 3: Область определения логарифма
        LogFunction logFunc = Functions.createLogFunction(2);
        try {
            Functions.integrate(logFunc, -10, 0, 0.1);
            System.out.println("    ERROR: Should have thrown exception for log negative!");
        } catch (IllegalArgumentException e) {
            System.out.println("    OK (log negative): " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Задание 2: Последовательная версия
     */
    private static void nonThread() {
        System.out.println("2. Non-threaded version:");
        
        int tasksCount = 10; // 10 заданий для демонстрации
        
        for (int i = 0; i < tasksCount; i++) {
            // Генерация случайных параметров
            double base = 1.0 + Math.random() * 9.0;
            double leftBorder = Math.random() * 100.0;
            double rightBorder = 100.0 + Math.random() * 100.0;
            double step = Math.random();
            
            // Создание функции
            LogFunction function = Functions.createLogFunction(base);
            
            // Вывод исходных данных
            System.out.printf("  Source %.2f %.2f %.2f%n", 
                leftBorder, rightBorder, step);
            
            // Вычисление интеграла
            try {
                double result = Functions.integrate(function, leftBorder, rightBorder, step);
                
                // Вывод результата
                System.out.printf("  Result %.2f %.2f %.2f %.6f%n", 
                    leftBorder, rightBorder, step, result);
                    
            } catch (IllegalArgumentException e) {
                System.out.printf("  Error: %s%n", e.getMessage());
            }
        }
        
        System.out.println();
    }
    
    /**
     * Задание 3: Простая многопоточная версия
     */
    private static void simpleThreads() {
        System.out.println("3. Simple threaded version:");
        
        Task task = new Task(10); // 10 заданий для демонстрации
        
        // Создание потоков
        Thread generatorThread = new Thread(new SimpleGenerator(task));
        Thread integratorThread = new Thread(new SimpleIntegrator(task));
        
    
        generatorThread.setPriority(Thread.NORM_PRIORITY);
        integratorThread.setPriority(Thread.NORM_PRIORITY);
        
        // Запуск потоков
        generatorThread.start();
        integratorThread.start();
        
        // Ожидание завершения
        try {
            generatorThread.join(5000); // таймаут 5 секунд
            integratorThread.join(5000);
            
            if (generatorThread.isAlive()) {
                System.out.println("  Warning: Generator thread is still alive");
                generatorThread.interrupt();
            }
            
            if (integratorThread.isAlive()) {
                System.out.println("  Warning: Integrator thread is still alive");
                integratorThread.interrupt();
            }
            
        } catch (InterruptedException e) {
            System.out.println("  Main thread interrupted");
        }
        
        System.out.println("  Simple threads finished");
        System.out.println("  Generated: " + task.getGeneratedTasks() + 
                         ", Solved: " + task.getSolvedTasks());
        System.out.println();
    }
    
    /**
     * Задание 4: Продвинутая версия с семафором
     */
    private static void complicatedThreads() {
        System.out.println("4. Complicated threads with semaphore:");
        
        Task task = new Task(15); // 15 заданий
        Semaphore semaphore = new Semaphore();
        
        // Создание потоков
        Generator generator = new Generator(task, semaphore);
        Integrator integrator = new Integrator(task, semaphore);
        
    
        generator.setPriority(Thread.MAX_PRIORITY);
        integrator.setPriority(Thread.MIN_PRIORITY);
        
        // Запуск потоков
        generator.start();
        integrator.start();
        
        // Ожидание 200 мс и прерывание
        try {
            Thread.sleep(200);
            
            System.out.println("  Main: Interrupting threads after 200ms");
            generator.interrupt();
            integrator.interrupt();
            
            // Ожидание завершения
            generator.join(1000);
            integrator.join(1000);
            
            if (generator.isAlive()) {
                System.out.println("  Warning: Generator is still alive");
                generator.interrupt();
            }
            
            if (integrator.isAlive()) {
                System.out.println("  Warning: Integrator is still alive");
                integrator.interrupt();
            }
            
        } catch (InterruptedException e) {
            System.out.println("  Main thread interrupted");
        }
        
        System.out.println("  Complicated threads finished");
        System.out.println("  Generated: " + task.getGeneratedTasks() + 
                         ", Solved: " + task.getSolvedTasks());
        System.out.println();
    }
}