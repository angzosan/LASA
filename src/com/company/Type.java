package com.company;

import java.util.ArrayList;
import java.util.Random;


public class Type {

    private final int y;
    private final int stationCount;
    private final int[][] Demand;
    private final Random randomEngine;


    public Type(int[][] demand,int y,int stationCount){
        this.stationCount = stationCount;
        this.y = y;
        this.Demand = demand;
        this.randomEngine = new Random();
    }

    double[][] findDistance(double[][] centroid){
        double[][] distance = new double[stationCount][3];   // <- k
        for (int i=0; i<3; i++) {     //find the distance between every random station and all the other stations
            for (int j = 0; j < stationCount; j++) {
                double sum = 0.0;
                for (int o = 1; o < y; o++) {
                    double xi = Demand[j][o];
                    double yi = centroid[i][o];
                    if (xi == 0 && yi == 0){
                        sum = 0.0;
                    }
                    else{
                        sum += Math.pow((yi - xi), 2);
                    }
                }
                if (sum == 0) {
                    distance[j][i] = 0.0;
                }
                else {
                    distance[j][i] = Math.sqrt(sum);
                }
            }
        }
        return distance;
    }

    double[][] findNewCentroids(int[][] clusters){ //centroid [k][y]
        double[][] centroid = new double[3][y];
        for (int i=0; i<3; i++){
            for (int o=0; o<y; o++){
                int sum = 0;
                int j =0;
                int count = 0;
                while (j<stationCount && clusters[j][i]!=-1){
                    count++;
                    sum += Demand[clusters[j][i]][o];
                    j++;
                }
                centroid[i][o] = sum / (double)count;
            }
        }
        return centroid;
    }



    int[] kMeans(int stationCount, int k) {

        double[][] centroidForCheck = new double[k][y];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < y; j++) {
                centroidForCheck[i][j] = -1.0;
            }
        }

        int[][] clusters = new int[stationCount+1][k];   //we're creating the clusters (as many as the types of stations we want, in this case 3)

        /*
         1. CHOOSING THREE RANDOM POINTS AND MAKE THEM CENTROIDS
         2d array- 1st column :station id, 2nd column:packets_Sent(x coordinate), 3rd column:packets_for_station(y coordinate)
         */
        double[][] centroid = new double[k][y];

        /// START : https://stackoverflow.com/a/49245601

        ArrayList<Integer> arrayList = new ArrayList<>();

        while (arrayList.size() < k) {
            int a = randomEngine.nextInt(stationCount);
            if (!arrayList.contains(a))
                arrayList.add(a);
        }

        //// :END

        for (int i=0; i<k; i++){
            centroid[i][0] = arrayList.get(i);
        }

        for (int i=0; i<k; i++){
            int station = (int)centroid[i][0];
            for (int j=0; j<y; j++){
                centroid[i][j] = Demand[station][j];
            }
        }


        int count = 0;
        boolean go = true;
        while (go && count<y*y){
            go = false;
            count++;

            /*
            2. CALCULATE THE DISTANCE BETWEEN THE CENTROIDS AND ALL THE OTHER POINTS
             */
            double[][] distance = findDistance(centroid);
            /*
             * 3. ADDING THE STATIONS TO CLUSTERS
             */

            for (int i = 0; i < k; i++) {
                for (int j = 0; j < stationCount; j++) {
                    clusters[j][i] = -1;
                }
            }
            for (int i = 0; i < stationCount; i++) {  // for each station, add it to the cluster with the min distance
                double min = 1000000000;
                int minCluster = -1;
                for (int j = 0; j < k; j++) {   // we find the min distance
                    if (distance[i][j] < min ) {
                        min = distance[i][j];
                        minCluster = j;
                    }
                }
                // we place it in the first empty spot
                int j=0;
                while (j<stationCount && clusters[j][minCluster] != -1) {
                    j++;
                }
                clusters[j][minCluster] = i;
            }

            /*
            4. FIND NEW CENTROIDS / AVERAGE
             */
            centroid = findNewCentroids(clusters);

            /*
             5. CHECK
             */


            if (centroidForCheck[0][0]==-1.0){ //if it's true, we just finished the first "run"
                go = true;
                for (int i=0; i<k; i++){
                    if (y >= 0) System.arraycopy(centroid[i], 0, centroidForCheck[i], 0, y );
                }
            }else{
                for (int i=0; i<k; i++){
                    for(int j=0; j<y; j++){
                        if (centroidForCheck[i][j]!=centroid[i][j]) {
                            go = true;
                            for (int o=0; o<k; o++){
                                if (y  >= 0) System.arraycopy(centroid[i], 0, centroidForCheck[i], 0, y );
                            }
                        }
                    }
                }
            }
        }

        int[] CL = new int[stationCount];
        for (int i=0; i<stationCount; i++){
            for(int j=0; j<k; j++){
                for (int o=0; o<stationCount; o++){
                    if ( i == clusters[o][j] ){
                        CL[i] = j;
                        break;
                    }
                }
            }
        }

        /*System.out.println("_______________CL_________________");
        for (int i=0; i<stationCount;i++)
            System.out.println("Station "+i+" Cluster "+CL[i]);*/


        double[][] Means = new double[k][y];

        for (int i=0; i<y; i++){
            double sum0 = 0.0;
            int count0 = 0;
            double sum1 = 0.0;
            int count1 = 0;
            double sum2 = 0.0;
            int count2 = 0;
            for (int j=0; j<stationCount; j++){
                if (CL[j] == 0) {
                    sum0 += Demand[j][i];
                    count0++;
                }
                else if (CL[j] == 1) {
                    sum1 += Demand[j][i];
                    count1++;
                }
                else {
                    sum2 += Demand[j][i];
                    count2++;
                }

            }

            Means[0][i] = sum0 / (double) count0;
            Means[1][i] = sum1 / (double) count1;
            Means[2][i] = sum2 / (double) count2;
        }
        int[] turn = new int[k];
        turn[0] = 0;
        turn[1] = 1;
        turn[2] = 2;

      /*  System.out.println("____________MEANS_BEFORE_________________");
        for (int i=0;i<3;i++){
            for (int j=0;j<stationCount;j++){
                String num = String.format("%.3f", Means[i][j]);
                System.out.print(num +" , ");
            }
            System.out.println();
        }*/

        double[] sum = new double[k];
        sum[0] = 0;
        for (int i = 0; i < stationCount; i++) {
            sum[0] += Means[0][i];
        }
        sum[1] = 0;
        for (int i = 0; i < stationCount; i++) {
            sum[1] += Means[1][i];
        }
        sum[2] = 0;
        for (int i = 0; i < stationCount; i++) {
            sum[2] += Means[2][i];
        }

        for (int i=0; i<k-1; i++){
            for (int j=i; j<k; j++){
                if(sum[i]<sum[j]){
                    for(int o=0; o<y; o++){
                        double temp;
                        temp = Means[i][o];
                        Means[i][o] = Means[j][o];
                        Means[j][o] = temp;
                    }
                    int temp1 = turn[i];
                    turn[i] = turn[j];
                    turn[j] = temp1;
                    double temp;
                    temp = sum[i];
                    sum[i] = sum[j];
                    sum[j] = temp;
                }
            }
        }
        /*System.out.println("____________MEANS_AFTER_________________");
        for (int i=0;i<3;i++){
            for (int j=0;j<stationCount;j++){
                String num = String.format("%.3f", Means[i][j]);
                System.out.print(num +" , ");
            }
            System.out.println();
        }*/
        int[] finalCl = new int[k+stationCount];
        for (int i=0; i<k+stationCount; i++ ){
            if (i>=stationCount)
                finalCl[i] = turn[i-stationCount];
            else
                finalCl[i] = CL[i];

        }
        /*
         * In the fist stationCount places are the clusters that the stations belong to]
         * In the last three, the hierarchy of the clusters ( first -> last // highest ->lowest)
         * */

        return finalCl;
    }

}
