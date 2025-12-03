/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package IAAprender;

import java.util.Random;

public class KMeansClustering {
    private int k; // número de clusters
    private int maxIter = 100; // número máximo de iterações
    private Random random = new Random();

    public KMeansClustering(int k) {
        this.k = k;
    }

    public int[] cluster(double[][] data) {
        int n = data.length;       // quantidade de pontos
        int d = data[0].length;    // dimensão (3 no caso: R,G,B)

        // Inicializa centróides aleatoriamente
        double[][] centroids = new double[k][d];
        for (int i = 0; i < k; i++) {
            int idx = random.nextInt(n);
            centroids[i] = data[idx].clone();
        }

        int[] labels = new int[n];
        boolean mudou;

        for (int iter = 0; iter < maxIter; iter++) {
            mudou = false;

            // Atribui cada ponto ao cluster mais próximo
            for (int i = 0; i < n; i++) {
                int melhorCluster = -1;
                double melhorDist = Double.MAX_VALUE;

                for (int j = 0; j < k; j++) {
                    double dist = distancia(data[i], centroids[j]);
                    if (dist < melhorDist) {
                        melhorDist = dist;
                        melhorCluster = j;
                    }
                }

                if (labels[i] != melhorCluster) {
                    labels[i] = melhorCluster;
                    mudou = true;
                }
            }

            // Recalcula centróides
            double[][] novosCentroids = new double[k][d];
            int[] count = new int[k];

            for (int i = 0; i < n; i++) {
                int cluster = labels[i];
                for (int j = 0; j < d; j++) {
                    novosCentroids[cluster][j] += data[i][j];
                }
                count[cluster]++;
            }

            for (int j = 0; j < k; j++) {
                if (count[j] > 0) {
                    for (int m = 0; m < d; m++) {
                        novosCentroids[j][m] /= count[j];
                    }
                    centroids[j] = novosCentroids[j];
                }
            }

            // Se não houve mudança nas labels, converge
            if (!mudou) break;
        }

        return labels;
    }

    private double distancia(double[] a, double[] b) {
        double soma = 0;
        for (int i = 0; i < a.length; i++) {
            soma += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(soma);
    }
}
