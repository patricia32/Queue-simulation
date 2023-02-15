package controller;

import controller.Strategy;
import model.Server;
import model.Task;
import view.SimulationFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class SimulationManager implements Runnable{

    public SimulationFrame view;
    public int timeLimit = 100;
    public int maxProcessingTime = 10;
    public int minProcessingTime = 2;
    public int minArrivalTime = 2;
    public int maxArrivalTime = 30;
    public int numberOfServers = 3;
    public int numberOfClients = 10;
    private int id = 1;
    public SelectionPolicy selectionPolicy;

    private ArrayList<Task> generatedTasks = new ArrayList<Task>();
    BlockingQueue<Task> queueComplete = new BlockingQueue<Task>() {
        @Override
        public boolean add(Task task) {
            return false;
        }

        @Override
        public boolean offer(Task task) {
            return false;
        }

        @Override
        public void put(Task task) throws InterruptedException {

        }

        @Override
        public boolean offer(Task task, long timeout, TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public Task take() throws InterruptedException {
            return null;
        }

        @Override
        public Task poll(long timeout, TimeUnit unit) throws InterruptedException {
            return null;
        }

        @Override
        public int remainingCapacity() {
            return 0;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public int drainTo(Collection<? super Task> c) {
            return 0;
        }

        @Override
        public int drainTo(Collection<? super Task> c, int maxElements) {
            return 0;
        }

        @Override
        public Task remove() {
            return null;
        }

        @Override
        public Task poll() {
            return null;
        }

        @Override
        public Task element() {
            return null;
        }

        @Override
        public Task peek() {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Iterator<Task> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends Task> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }
    };
    private Thread thread = new Thread(this);


    private Server server = new Server();
    public SimulationManager(SimulationFrame view){
        this.view = view;
    }

    private void generateRandomTasks(){
        for(int i=0; i<this.numberOfClients; i++) {
            Task newTask = new Task(id, 0, 0);
            id++;
            Random random = new Random();
            int serviceTime = 0, arrivalTime = 0;
            while (serviceTime < this.minProcessingTime)
                serviceTime = random.nextInt(this.maxProcessingTime);
            newTask.setServiceTime(serviceTime);
            while (arrivalTime < this.minArrivalTime)
                arrivalTime = random.nextInt(this.maxArrivalTime);
            newTask.setArrivalTime(arrivalTime);
            this.generatedTasks.add(newTask);
        }
        Collections.sort(this.generatedTasks);
        queueComplete.addAll(generatedTasks);
        thread.start();
    }

    public void clientsPrint(ArrayList<Task> generatedTasks){
        String toWrite = "Clienti: ";
        for (Task task : generatedTasks) {
            toWrite = toWrite.concat(String.valueOf(task.getID()));
            toWrite = toWrite.concat(" ");
            toWrite = toWrite.concat(String.valueOf(task.getArrivalTime()));
            toWrite = toWrite.concat(" ");
            toWrite = toWrite.concat(String.valueOf(task.getServiceTime()));
            toWrite = toWrite.concat(", ");
        }
        view.setQueues(toWrite);
    }

    public void queuesPrint(List<Server> serversList, Scheduler scheduler){
        String serversString = "";
        int list = 0;
        serversList = scheduler.getServers();
        for (Server server : serversList) {
            serversString = serversString.concat("List number ");
            serversString = serversString.concat(String.valueOf(list));
            serversString = serversString.concat(": number of clients: ");
            serversString = serversString.concat(String.valueOf(server.getNoOfClients()));
            serversString = serversString.concat(", waiting time: ");
            serversString = serversString.concat(String.valueOf(server.getWaitingPeriod()));
            serversString = serversString.concat("\n");
            list++;
        }
        view.setQueuesList(serversString);
    }

    public void timePrint(int currentTime){
        String string = "Timp parcurs: ";
        string = string.concat(String.valueOf(currentTime));
        string = string.concat(" sec.");
        view.setTime(string);
    }

    @Override
    public void run() {
        String write = "";
        int currentTime = 0;
        Scheduler scheduler = null;
        try {
            scheduler = new Scheduler(numberOfServers, numberOfServers);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        scheduler.getThreads();

        Task currentTask;

        clientsPrint(generatedTasks);
        List<Server> serversList = scheduler.getServers();
        queuesPrint(serversList, scheduler);

        while (currentTime < timeLimit && generatedTasks != null) {
            Iterator<Task> iteratorTask = generatedTasks.iterator();
          //  System.out.println("SD" + currentTime);
            while (iteratorTask.hasNext()) {
                currentTask = iteratorTask.next();
             //   System.out.println("Clientul care se verifica " + currentTask.getArrivalTime());
                if (currentTask.getArrivalTime() == currentTime - 1) {
                    generatedTasks.remove(currentTask);
                    clientsPrint(generatedTasks);
                    iteratorTask = generatedTasks.iterator();
                    try {
                        scheduler.dispatchTask(currentTask, selectionPolicy);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    queuesPrint(serversList, scheduler);

                }
            }
            queuesPrint(serversList, scheduler);
                try {
                    Thread.sleep(1000l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timePrint(currentTime);

            int line = 1;
            write = write.concat("\nTime "+ currentTime + "\n");
            write = write.concat("Waiting clients: ");
            for(Task task: generatedTasks) {
                write = write.concat(task.getID() + " " + task.getArrivalTime() + " " + task.getServiceTime() + ", ");
            }
            currentTime++;
            for(Server serverc: serversList){
                if(serverc.getNoOfClients() != 0) {
                    write =write.concat("\nCoada " + line + " ");
                    line++;
                    for (Task task : serverc.getTasks())
                        write = write.concat(task.getID() + " " + task.getArrivalTime() + " " + task.getServiceTime() + ", ");
                }
            }
        }

        FileOutputStream g= null;
        try {
            g = new FileOutputStream("log3.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintStream gchar=new PrintStream(g);
        gchar.println(write);
    }

    public class startListener implements  ActionListener{
        public void actionPerformed(ActionEvent e) {
            int timeLimit = Integer.parseInt(view.getSimulationTime());
            int maxProcessingTime =  Integer.parseInt(view.getMaxService());
            int minProcessingTime =  Integer.parseInt(view.getMinService());
            int minArrivalTime =  Integer.parseInt(view.getMinArrival());
            int maxArrivalTime =  Integer.parseInt(view.getMaxArrival());
            int numberOfClients =  Integer.parseInt(view.getNumberOfClients());
            int numberOfServers =  Integer.parseInt(view.getNumberOfQueues());
            String strategyMode = view.getStrategy();
           // System.out.println(strategyMode);
            try {
                assign( numberOfClients,  numberOfServers,  maxArrivalTime,  minArrivalTime,  maxProcessingTime,  minProcessingTime,  timeLimit, strategyMode);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void assign(int numberOfClients, int numberOfServers, int maxArrivalTime, int minArrivalTime, int maxProcessingTime, int minProcessingTime, int timeLimit, String strategyMode) throws InterruptedException {
        this.timeLimit = timeLimit;
        this.numberOfClients = numberOfClients;
        this.numberOfServers = numberOfServers;
        this.maxArrivalTime = maxArrivalTime;
        this.minArrivalTime = minArrivalTime;
        this.maxProcessingTime = maxProcessingTime;
        this.minProcessingTime = minProcessingTime;
        if(strategyMode.equals("SHORTEST_TIME"))
            this.selectionPolicy = SelectionPolicy.SHORTEST_TIME;
        else
            selectionPolicy = SelectionPolicy.SHORTEST_QUEUE;

        generateRandomTasks();
        Scheduler scheduler = new Scheduler(this.numberOfServers, this.numberOfServers);
        scheduler.changeStrategy(selectionPolicy);
    }

    public SimulationManager startRun(SimulationManager simulationManager){
        view.addStartListener(new startListener());
        return simulationManager;
    }

}
