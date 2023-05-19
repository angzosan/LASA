package com.company;


import java.util.LinkedList;


public class Station {
    private final LinkedList<Packet> queue;
    private final Simulation simulation;
    private final int maxSize;
    private Types defined;
    private double load;
    private int slots;

    boolean silentMode;


    int packets_for_station;  //counter for the demand matrix
    int totalpackets; //how many packets were given to
    int current_demand;
    int packets_sent; // how many packets it actually sent
    int lost;
    Types type;
    static class Packet {
        int payload;
        int arrivalTime;

        public Packet(int payload, int arrivalTime) {
            this.payload = payload;
            this.arrivalTime = arrivalTime;
        }
    }

    public Station(Simulation simulation, int maxSize) {

        this.silentMode = false;
        this.queue = new LinkedList<>();
        this.simulation = simulation;
        this.maxSize = maxSize;
        this.packets_sent = 0;
        this.lost = 0;
        this.load = 0;
        this.current_demand = 0;
    }

    /**
     * adds a new packet at the beginning of the queue
     * @param payload (currently not used)
     */
    public boolean enqueuePacket(int payload) {
        if (queue.size()==maxSize) {
            lost++;
            return false;
        }
        queue.add(new Packet(payload, simulation.getTime()));
        return true;
    }

    /**
     * removes first element (every new packet is placed in the beginning of the list)
     * @return the transmitted packet
     */
    public Packet dequeuePacket() {
        return queue.pollFirst();
    }


    public void changePacketsSent(){packets_sent++;}

    boolean hasPacket() {
        return !queue.isEmpty();
    }

    public double accuracy(){
        if (packets_for_station==0)
            return 0;
        return packets_sent / (double) totalpackets;
    }

    public void setLoad(double load){this.load = load;}

    public double getLoad() {return load;}

    public void setSlots(int slots){this.slots = slots;}

    public void setType(Types type){this.type = type;}

    public Types getType(){return type;}

    public void setDefined(Types type){this.defined = type;}


    public Types getDefined(){return defined;}


    public int getSlots(){return slots;}
}