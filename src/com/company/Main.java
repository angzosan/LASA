package com.company;
import java.util.Random;
public class Main {


    public static void main(String[] args) {

        double step = 0.01;
        double [] delay = new double[20];
        double [] throughput = new double[20];
        double [] accuracy = new double[20];
        double [] load = new double[20];
        double [] loss = new double[20];
        long [] tradeoff = new long[20];
        int [] pres = new int[20];
        // starting a new simulation with increased load factor in every loop
        int l=0;


        double mean = 1.0;
        for (double m=0.01; m<=1; m=m+0.05) {

            Simulation simulation = new Simulation(10,10000 ,m,150000);
            simulation.run();
            delay[l] = simulation.delay();
            throughput[l] = simulation.throughput();
            accuracy[l] = simulation.accuracy();
            load[l] = simulation.getLoad();
            loss[l] = simulation.lossRate();


            l++;
        }


        /* DELAY */
        System.out.println("Delay");
        for (double j:delay) {
            String num = String.format("%.3f", j);
            System.out.print(num + " , ");
        }

        System.out.println();

        System.out.println("Throughput");
        /* THROUGHPUT */
        for (double j : throughput){
            String num = String.format("%.3f", j);
            System.out.print(num + " , ");
        }

        System.out.println();

        System.out.println("Load");
        /* LOAD */
        for (double j : load) {
            String num = String.format("%.3f", j);
            System.out.print(num + " , ");
        }

        System.out.println();

        System.out.println("Success");
        /* ACCURACY */
        for (double j : accuracy){
            String num = String.format("%.3f", j/10); // 10 == station count
            System.out.print(num + " , ");
        }

    }



}