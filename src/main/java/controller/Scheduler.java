package controller;

import model.Server;
import model.Task;
import controller.ConcreteStrategyQueue;
import java.util.*;

public class Scheduler {

    ArrayList<Server> servers = new ArrayList<>() ;
    ConcreteStrategyQueue concreteStrategyQueue = new ConcreteStrategyQueue();
    ConcreteStrategyTime concreteStrategyTime = new ConcreteStrategyTime();
    private int maxNoServers;
    private int maxTasksPerServer;
    private Strategy strategy = new Strategy() {
        @Override
        public void addTask(List<Server> servers, Task T) {

        }
    };
    private Thread[] threads = new Thread[300];

    public Scheduler(int maxNoServers, int maxTasksPerServer) throws InterruptedException {
        for(int i=0; i<maxNoServers; i++){
           Server serv = new Server(null);
           servers.add(serv);
        }
    }
    public void changeStrategy(SelectionPolicy policy){
        if(policy == SelectionPolicy.SHORTEST_QUEUE)
            strategy = new ConcreteStrategyQueue();
        if(policy == SelectionPolicy.SHORTEST_TIME)
            strategy = new ConcreteStrategyTime();
    }

    public void dispatchTask(Task task, SelectionPolicy strategyMode) throws InterruptedException {
        if(strategyMode.equals("SHORTEST_QUEUE"))
             concreteStrategyQueue.addTask(servers, task);
        else
            concreteStrategyTime.addTask(servers,task);
    }

    public List<Server> getServers(){
        return servers;
    }
    public Thread[] getThreads(){
        return threads;
    }
}
