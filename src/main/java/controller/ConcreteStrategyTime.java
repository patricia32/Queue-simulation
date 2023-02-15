package controller;

import model.Server;
import model.Task;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcreteStrategyTime implements Strategy{
    @Override
    public void addTask(List<Server> servers, Task T) throws InterruptedException {
        int minTime = servers.get(0).getWaitingPeriod().intValue();

        for(Server server: servers){
            if(minTime > server.getWaitingPeriod().intValue())
                minTime = server.getWaitingPeriod().intValue();
        }
        for(Server server: servers){
            if(minTime == server.getWaitingPeriod().intValue()) {
                server.addTask(T);
                break;
            }
        }
    }
}
