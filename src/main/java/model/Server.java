package model;

import controller.Scheduler;
import controller.SimulationManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {

    private ArrayList<Task> tasks = new ArrayList<>();
    private int noOfClients = 0;
    private Thread serverThread = new Thread(this);

    private AtomicInteger waitingPeriod = new AtomicInteger();
    private SimulationManager simulationManager;

    public Server(BlockingQueue<Task> tasks) throws InterruptedException {
        this.noOfClients = 0;
        waitingPeriod.set(0);
    }

    public Server() {
    }

    public int getNoOfClients() {
        return noOfClients;
    }
    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }

    public void addTask(Task newTask) throws InterruptedException {
        this.tasks.add(newTask);
        this.noOfClients++;
        waitingPeriod.addAndGet(newTask.getServiceTime());
        if (this.noOfClients == 1)
            this.serverThread.start();

    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    @Override
    public void run() {
        Task currentTask;
        while (tasks.size() != 0) {
            currentTask = tasks.get(0);
            while (currentTask.getServiceTime() != 0) {
                this.waitingPeriod.set(this.waitingPeriod.get() - 1);
                currentTask.setServiceTime(currentTask.getServiceTime() - 1);
                if (this.waitingPeriod.get() < 0)
                    break;
                if (currentTask.getServiceTime() == 0) {
                    this.noOfClients--;
                    if (this.noOfClients < 0)
                        this.noOfClients = 0;
                    tasks.remove(currentTask);
                    this.serverThread = new Thread(this);
                }
                try {
                    Thread.sleep(1000l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}