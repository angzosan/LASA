package com.company;

import java.util.ArrayList;
import java.util.Random;

public class Simulation {
    private final Random randomEngine;
    private final ArrayList<Station> stations;


    ArrayList<Integer> Cluster0, Cluster1, Cluster2;
    int time, accumulatedDelay, sentPackages,availableTime, packages_created,stationCount, queueSize, count;
    long startTime, finishTime;
    int[][] D;     // demand matrix
    int [] cl;   // clusters' matrix
    int[] ClusteringTimes;
    double dataRate, genRate;
    Station current_station;
    Type a;



    /**
     * every simulation has a number of stations
     * a prefixed queue size
     * and the packetGenRate as the load factor.
     * Every time we give a new load factor, we create the new simulation.
     */

    public Simulation(int stationCount, int queueSize,double dataRate,int iterations) {
        this.Cluster0 = new ArrayList<>();
        this.Cluster1 = new ArrayList<>();
        this.Cluster2 = new ArrayList<>();
        this.stations = new ArrayList<>();
        this.randomEngine = new Random();
        this.current_station =new Station(this, queueSize);

        this.availableTime = iterations;
        this.stationCount = stationCount;
        this.queueSize = queueSize;
        this.dataRate = dataRate;
        this.genRate = dataRate;


        this.time = 0;
        this.accumulatedDelay = 0;
        this.sentPackages = 0;
        this.packages_created=0;

        D = new int[availableTime/stationCount][availableTime/stationCount];
        cl = new int[stationCount];
        ClusteringTimes = new int[stationCount];
        int start = 100;
        int i=0;
        while(start<iterations && i<stationCount){
            ClusteringTimes[i] = start;
            start *=2;
            i++;
        }

    }

    /*
     * Each stations' traffic.
     */

    private void setLoads(){
       /* for (int i=0;i<stationCount;i++){
            int ran = randomEngine.nextInt(3);
            if (ran == 0){
                stations.get(i).setLoad(genRate/5);     //CLIENT
                stations.get(i).setDefined(Types.client);
            }else if (ran == 1){
                stations.get(i).setLoad(genRate);        //NODE
                stations.get(i).setDefined(Types.node);
            }else{
                stations.get(i).setLoad(2*genRate);     //SERVER
                stations.get(i).setDefined(Types.server);
            }
        }*/
        stations.get(0).setLoad(genRate);        //NODE
        stations.get(0).setDefined(Types.node);
        stations.get(1).setLoad(genRate/5);     //CLIENT
        stations.get(1).setDefined(Types.client);
        stations.get(2).setLoad(2*genRate);     //SERVER
        stations.get(2).setDefined(Types.server);
        stations.get(3).setLoad(genRate);
        stations.get(3).setDefined(Types.node);
        stations.get(4).setLoad(genRate);
        stations.get(4).setDefined(Types.node);
        stations.get(5).setLoad(genRate/5);
        stations.get(5).setDefined(Types.client);
        stations.get(6).setLoad(2*genRate);
        stations.get(6).setDefined(Types.server);
        stations.get(7).setLoad(genRate/5);
        stations.get(7).setDefined(Types.client);
        stations.get(8).setLoad(genRate);
        stations.get(8).setDefined(Types.node);
        stations.get(9).setLoad(genRate/5);
        stations.get(9).setDefined(Types.client);
    }

    private void check(){
        if (count<stationCount && time>=ClusteringTimes[count]){
            clustering();
            count++;
        }
    }

    public void run() {
        initSimulation();
        setLoads();
        clustering();
        count =0;

        for (var i = time; i < availableTime; i++) {

            check();
            if (time>=availableTime)
                break;

            for (Integer integer : Cluster0) {
                current_station = stations.get(integer);
                if (time==availableTime)
                    break;
                advanceSimulation();
            }

            check();

            if (time==availableTime)
                break;

            for (Integer integer : Cluster1) {
                current_station = stations.get(integer);
                if (time==availableTime)
                    break;
                advanceSimulation();
            }

            check();

            if (time==availableTime)
                break;

            for (Integer integer : Cluster2) {
                current_station = stations.get(integer);
                if (time==availableTime)
                    break;
                advanceSimulation();
            }

            if (time==availableTime)
                break;
        }
    }

    private void clustering(){

        if (!Cluster0.isEmpty())
            Cluster0.clear();
        if (!Cluster1.isEmpty())
            Cluster1.clear();
        if (!Cluster2.isEmpty())
            Cluster2.clear();

        makeDemand();

        //printDemand();

        a = new Type(D, stationCount ,stationCount);
        cl = a.kMeans(stationCount,3);
        /*for (int i=0; i<10; i++){
            System.out.println("Station "+i+" Cluster "+cl[i]);
        }*/
        for (int i=0;i<stationCount;i++){
            if (cl[i]==cl[cl.length-2]) {
                Cluster0.add(i);
                stations.get(i).setType(Types.server);
            }
            else if (cl[i]==cl[cl.length-1]) {
                Cluster1.add(i);
                stations.get(i).setType(Types.node);
            }
            else {
                Cluster2.add(i);
                stations.get(i).setType(Types.client);
            }
        }

        setSlots(Cluster0);
        setSlots(Cluster1);
        setSlots(Cluster2);
    }

    private void setSlots(ArrayList<Integer> cluster){
        int sum = 0;
        int StationNum = 0;
        for (Integer i : cluster) {
            sum += stations.get(i).current_demand;
            StationNum++;
        }
        if (StationNum>0) {
            for (Integer i : cluster)
                stations.get(i).setSlots(sum/StationNum );
        }

    }

    private void printDemand(){
        System.out.println("_____________DEMAND_______________");
        for (int i=0;i<stationCount;i++){
            System.out.print("Station "+ (i) +" : ");
            for (int j=0;j<stationCount;j++){
                System.out.print(D[i][j]+" , ");
            }
            System.out.println();
        }
    }

    private void makeDemand(){
        for (int i =0 ; i <stationCount; i++) {
            stations.get(i).packets_for_station = 0;
            stations.get(i).current_demand = 0;
        }
        int l = 0, column = 0, stat = 0;

        int times;
        if (count>0)
            times = ClusteringTimes[count] - ClusteringTimes[count-1];
        else
            times = ClusteringTimes[count];

        for (int i = 0; i <= times; i++){   // TDMA

            if (time>=availableTime)
                return;

            if (stat == stationCount)
                stat = 0;

            if (l==times/stationCount) {   // that means we're at the 10 slot checkpoint
                l = 0;
                for (int j=0; j<stationCount; j++){
                    D[j][column] += stations.get(j).packets_for_station;
                    stations.get(j).packets_for_station = 0;
                }
                column++;
            }
            current_station = stations.get(stat);
            stat++;
            l++;
            advanceSimulation();  //  creates the demand matrix and performs k-means clustering
        }
    }

    /**
     * This method creates/adds the stations in a queue.
     * For every station we need: it's queue size and the information about the
     * simulation it's running for.
     */
    private void initSimulation() {
        for (var i = 0; i < stationCount; i++) {
            this.stations.add(new Station(this, queueSize));
        }
    }

    /**

     * simulates a transmission
     * chooses randomly a station and transmits the first packet found in the queue
     */
    public void advanceSimulation( ) {
        populateStations();
        int k = current_station.getSlots();
        if (k==0)
            k = 1;
        for (int i = 0; i < k; i++) {
            if (current_station.hasPacket()) {
                if (time == availableTime)
                    return;
                time++;
                var packet = current_station.dequeuePacket();
                transmitPacket(packet);
                current_station.changePacketsSent();
                populateStations();
            }else{
                time++;
                return;
            }
        }
    }


    private void populateStations() {
        var randomPayload = randomEngine.nextInt(256);
        int index = randomEngine.nextInt(stationCount);
        if (randomEngine.nextDouble() < stations.get(index).getLoad()) {
            boolean transmit = stations.get(index).enqueuePacket(randomPayload);
            stations.get(index).totalpackets++;
            stations.get(index).packets_for_station++;
            stations.get(index).current_demand++;
            if (transmit) {
                packages_created++;
            }
        }
    }

    private void transmitPacket(Station.Packet p) {
        accumulatedDelay += (getTime() - p.arrivalTime);
        sentPackages += 1;
    }

    public int getTime() {return time;}

    /**
     * Throughput = sentPackages/ time(=slots)
     * Delay = accumulatedDelay / sentPackages
     * Load = packetGenRate
     * Accuracy = Packets_sent / packets_given_to_each_station ( for every station )
     */

    public double delay() {
        return accumulatedDelay / (double) sentPackages;
    }

    public double throughput() {
        return sentPackages/ (double) time ;
    }


    public double accuracy() {
        double sum=0;
        for (Station i : stations)
            sum+=i.accuracy();
        return sum;
    }

    public int precision(){
        int pres = 0;
        for (int i=0;i<stationCount;i++){
            if (stations.get(i).getType()==stations.get(i).getDefined()){
                pres+=10;
            }
        }
        return pres;
    }

    public double lossRate() {
        double sum=0;
        for (Station i : stations)
            sum+=i.lost;
        return sum/(double) packages_created;
    }

    public double getLoad(){
        return packages_created/(double) time;
    }

    public long tradeoff(){
        finishTime = System.nanoTime();

        /*System.out.println("Took: " + ((finishTime - startTime) / 1000000) + "ms");
        System.out.println("Took: " + (finishTime - startTime)/ 1000000000 + " seconds");*/
        return ((finishTime - startTime) / 1000000);
    }
}