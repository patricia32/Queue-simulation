package controller;

import model.Server;
import model.Task;

import java.util.List;

public class ConcreteStrategyQueue  implements Strategy{
    @Override
    public void addTask(List<Server> servers, Task T) throws InterruptedException {
        int clients = 0;
        for (Server currentServer : servers) {
            if (clients > currentServer.getNoOfClients())
                clients = currentServer.getNoOfClients();
        }
        for (Server currentServer : servers) {
            if (clients == currentServer.getNoOfClients()) {
                currentServer.addTask(T);
                break;
            }
        }
    }
}
