/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.multithreadinglab.ProducerConsumer.reentrantLock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 *
 * @author MITRA
 */
/*
*Using this we can safely interrupt threads
*/
public class ProducerConsumer {

    private static int[] buffer;
    private static int count;

    private static final Lock lock = new ReentrantLock();
    private static final Condition notFull = lock.newCondition();
    private static final Condition notEmpty = lock.newCondition();

    static class Producer {

        public void produce() {
            try {
                lock.lock();
                while (isFull(buffer)) {
                    notFull.await();
                }

                buffer[count++] = 1;
                notEmpty.signal();
            } catch (InterruptedException ex) {
                Logger.getLogger(ProducerConsumer.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                lock.unlock();
            }
        }

    }

    static class Consumer {

        public void consume() {

            try {
                lock.lock();
                while (isEmpty(buffer)) {
                    notEmpty.await();
                }
                buffer[--count] = 0;
                notFull.signal();
            } catch (InterruptedException ex) {
                Logger.getLogger(ProducerConsumer.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                lock.unlock();
            }
        }

    }

    private static boolean isFull(int[] buffer) {
        return count ==  buffer.length;
    }

    private static boolean isEmpty(int[] buffer) {
        return count == 0;
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
