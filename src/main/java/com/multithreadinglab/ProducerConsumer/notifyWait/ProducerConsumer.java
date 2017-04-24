package com.multithreadinglab.ProducerConsumer.notifyWait;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 *
 * @author MITRA
 */
public class ProducerConsumer {

    private static int[] buffer;
    private static int count;
    private static Object lock = new Object();

    static class Producer {

        void produce() {
            synchronized (lock) {
                while (isFull(buffer)) {
                    try {
                        lock.wait();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                buffer[count++] = 1;
                lock.notify();
            }
        }
    }

    static class Consumer {

        void consume() {
            synchronized (lock) {
                while (isEmpty(buffer)) {
                    try {
                        lock.wait();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                buffer[--count] = 0;
                lock.notify();
            }
        }
    }

    static boolean isEmpty(int[] buffer) {
        return count == 0;
    }

    static boolean isFull(int[] buffer) {
        return count == buffer.length;
    }

    public static void main(String[] args) throws InterruptedException {
        buffer = new int[10];
        count = 0;

        int noOfElementsToProduce = 50;
        int noOfElementsToConsume = 50;

        Producer producer = new Producer();
        Consumer consumer = new Consumer();

        Runnable producerTask = () -> {
            IntStream.range(0, noOfElementsToProduce).forEachOrdered(x -> producer.produce());
            System.out.println("Completed Producing");
        };
        
        
        Runnable consumerTask = () -> {
            IntStream.range(0, noOfElementsToConsume).forEachOrdered(x -> consumer.consume());
            System.out.println("Completed Consuming");
        };

        Thread producerThread = new Thread(producerTask, "Producer Thread");
        Thread consumerThread = new Thread(consumerTask, "Consumer Thread");

        producerThread.start();
        consumerThread.start();

        producerThread.join();
        consumerThread.join();

        System.out.println("Value of Count: " + count);

    }

}
