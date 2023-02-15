package model;


public class Task implements Comparable<Task>{
    private int ID;
    private int arrivalTime;
    private int serviceTime;
    private int enterTime;

    public Task(int ID, int arrivalTime, int serviceTime) {
        this.ID = ID;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }
    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }
    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }


    @Override
    public int compareTo(Task task) {
        if(task.getArrivalTime() < this.getArrivalTime())
          return 1;
        if(task.getArrivalTime() == this.getArrivalTime())
            return 0;
        return -1;
    }

}
