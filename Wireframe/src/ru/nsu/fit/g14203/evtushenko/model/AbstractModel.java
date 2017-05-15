package ru.nsu.fit.g14203.evtushenko.model;

import ru.nsu.fit.g14203.evtushenko.Observable;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class AbstractModel extends Observable implements Runnable {
    private BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<>(1);

    public void runInBackground(Runnable runnable){
        taskQueue.clear();
        taskQueue.offer(runnable);
    }

    @Override
    public void run() {
        while (true) {
            try {
                taskQueue.take().run();
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
