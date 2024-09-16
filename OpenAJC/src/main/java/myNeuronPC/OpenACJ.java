package myNeuronPC;

import WorkBits.NumberToBits;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class OpenACJ
        implements Serializable {

    public int limitFileira = 105;

    public int countTrainning = 0;

    ArrayList<NeuronPC[]> neuronios = new ArrayList<>();

    public ArrayList<NumberToBits> inTrainningBits = new ArrayList<>();

    class valueExpected implements Serializable {

        ArrayList<Float> valueExpect = new ArrayList<>();
    }

    ArrayList<valueExpected> valuesExpected = new ArrayList<>();

    private String ultimaRodada = "";

    private int nentradas = 0;

    public int nBitsG;

    public void cleanValuesTrainning() {
        inTrainningBits.clear();
    }

    public void setValForTraining(int valor, byte... valoreEsperado) {

        this.inTrainningBits.add(new NumberToBits(this.nBitsG, 2, valor, valoreEsperado));

        for (int cont = 0; cont < inTrainningBits.size() - 1; cont++) {

            if (checkVals(inTrainningBits.get(cont),
                    inTrainningBits.get(inTrainningBits.size() - 1))) {
                inTrainningBits.remove(inTrainningBits.size() - 1);

            }

        }

    }

    public void printaValores() {
        for (int cont = 0; cont < inTrainningBits.size(); cont++) {
            System.out.println("\n" + inTrainningBits.get(cont).vetbits.length + "\n");
            for (int d = inTrainningBits.get(cont).vetbits.length - 1; d >= 0; d--) {
                System.out.print("" + (int) inTrainningBits.get(cont).vetbits[d] + "");
            }
        }
    }

    public NumberToBits getValorTeste(int valor) {
        return (new NumberToBits(this.nBitsG, 2, valor, (byte) 0));

    }

    public NumberToBits getValorTesteCEsperado(int valor, byte... valorEsperado) {
        return (new NumberToBits(this.nBitsG, 2, valor, valorEsperado));

    }

    public static int lenghtImagem(BufferedImage img) {
        return (((java.awt.image.DataBufferInt) img.getRaster().getDataBuffer()).getData()).length;

    }

    public static int lenghtImagem(File valor) {
        try {
            return (((DataBufferByte) (ImageIO.read(valor)).getRaster().getDataBuffer()).getData()).length;
        } catch (IOException ex) {
            Logger.getLogger(OpenACJ.class.getName()).log(Level.SEVERE, (String) null, ex);

            return -1;
        }
    }

    public void addValTrainningNew(BufferedImage img, byte... valoreEsperado) {

        int[] dados = (((java.awt.image.DataBufferInt) img.getRaster().getDataBuffer()).getData());

        this.inTrainningBits.add(new NumberToBits(dados.length, 1, (int[]) dados.clone(), valoreEsperado));

        for (int cont = 0; cont < inTrainningBits.size() - 1; cont++) {

            if (checkVals(inTrainningBits.get(cont),
                    inTrainningBits.get(inTrainningBits.size() - 1))) {

                inTrainningBits.remove(inTrainningBits.size() - 1);

            }

        }
    }

    public void addValTrainningNew(File valor, byte... valoreEsperado) {
        try {

            byte[] dados = ((DataBufferByte) (ImageIO.read(valor)).getRaster().getDataBuffer()).getData();

            this.inTrainningBits.add(new NumberToBits(dados.length, 1, (byte[]) dados.clone(), valoreEsperado));

            for (int cont = 0; cont < inTrainningBits.size() - 1; cont++) {
                if (checkVals(inTrainningBits.get(cont),
                        inTrainningBits.get(inTrainningBits.size() - 1))) {
                    inTrainningBits.remove(inTrainningBits.size() - 1);

                }
            }
        } catch (IOException ex) {
            Logger.getLogger(OpenACJ.class.getName()).log(Level.SEVERE, (String) null, ex);
        }
    }

    public void addValTrainningNewBytesToBits(BufferedImage valor, byte... valoreEsperado) {

        int[] dados = ((DataBufferInt) ((valor)).getRaster().getDataBuffer()).getData();

        NumberToBits n = new NumberToBits(8 * 4, dados.length, 1, dados.clone(), valoreEsperado);

        n.setFileName("");
        this.inTrainningBits.add(n);

        for (int cont = 0; cont < inTrainningBits.size() - 1; cont++) {

            if (checkVals(inTrainningBits.get(cont),
                    inTrainningBits.get(inTrainningBits.size() - 1))) {
                inTrainningBits.remove(inTrainningBits.size() - 1);
            }

        }

    }

    public void addValTrainningNewBytesToBits(File valor, byte... valoreEsperado) {
        try {

            int[] dados = ((DataBufferInt) (ImageIO.read(valor)).getRaster().getDataBuffer()).getData();
            NumberToBits n = new NumberToBits(8 * 4, dados.length, 1, dados.clone(), valoreEsperado);

            n.setFileName(valor.getName());
            this.inTrainningBits.add(n);

            for (int cont = 0; cont < inTrainningBits.size() - 1; cont++) {

                if (checkVals(inTrainningBits.get(cont),
                        inTrainningBits.get(inTrainningBits.size() - 1))) {
                    inTrainningBits.remove(inTrainningBits.size() - 1);
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(OpenACJ.class.getName()).log(Level.SEVERE, (String) null, ex);
        }
    }

    public boolean checkVals(NumberToBits n1, NumberToBits n2) {

        byte[] vet1 = n1.vetbits;
        byte[] vet2 = n2.vetbits;

        boolean iguais = false;
        for (int cont = 0; cont < n1.valueExpected.length && cont < n2.valueExpected.length; cont++) {

            if (n1.valueExpected[cont] == n2.valueExpected[cont]) {

                iguais = true;
                return false;
            }
        }

        for (int cont = 0; cont < vet1.length; cont++) {

            if (vet1[cont] != vet2[cont]) {

                return false;

            }
        }

        return true;

    }

    public NumberToBits getValTrainningBytesToBits(BufferedImage valor) {
        int[] dados;

        dados = ((DataBufferInt) ((valor)).getRaster().getDataBuffer()).getData();
        return new NumberToBits(8 * 4, dados.length, 1, dados.clone(), (byte) 0);

    }

    public NumberToBits getValTrainningBytesToBits(File valor) {
        try {
            byte[] dados = ((DataBufferByte) (ImageIO.read(valor)).getRaster().getDataBuffer()).getData();

            return (new NumberToBits(dados.length, 1, (byte[]) dados.clone(), (byte) 0));

        } catch (IOException ex) {
            Logger.getLogger(OpenACJ.class.getName()).log(Level.SEVERE, (String) null, ex);
        }
        return null;
    }

    public NumberToBits getValTrainning(BufferedImage img) {

        int[] dados = (((java.awt.image.DataBufferInt) img.getRaster().getDataBuffer()).getData());

        return (new NumberToBits(dados.length, 1, (int[]) dados.clone(), (byte) 0));
    }

    public OpenACJ(int nSizeBits) {

        this.nBitsG = nSizeBits;

    }

    public void TrainingContinuesValidExitsACJ(int tamFileira, int nNeuronios) {

        treinoPSFileira(tamFileira, nNeuronios, inTrainningBits, inTrainningBits, neuronios.size() == tamFileira);
    }

    public void TrainingNewOpenACJ(int tamFileira, int nNeuronios) {

        treinoPSFileira(tamFileira, nNeuronios, inTrainningBits, inTrainningBits, false);
    }

    public void TrainingContinuesACJ(int tamFileira, int nNeuronios) {

        treinoPSFileira(tamFileira, nNeuronios, inTrainningBits, inTrainningBits, true);
    }

    public boolean treinoPSFileira(int tamFileira, int NS, ArrayList<NumberToBits> nbits, ArrayList<NumberToBits> nSaida, boolean continua) {

        if (!continua) {

            neuronios = new ArrayList<>();

            int nNeuro;
            int countIndex = 1;
            for (int nTamFileira = 0; nTamFileira < tamFileira; nTamFileira++) {

                NeuronPC[] camada = new NeuronPC[NS];

                for (nNeuro = 0; nNeuro < NS; nNeuro++) {

                    camada[nNeuro] = new NeuronPC(countIndex, nNeuro % 2 == 0);

                    countIndex++;
                }

                neuronios.add(camada);

            }

        }
        boolean erro = false;

        int cont = 0;

        NumberToBits t = null;

        int nFileira = 1;
        int contTreinoFileira = 0;
        do {
            int nBitsCount = 0;
            erro = false;

            for (int l = 0; l < nbits.size(); l++) {
                NumberToBits valoresEntrada = nbits.get(l);

                nBitsCount++;

                if (t == null) {
                    t = valoresEntrada;
                }

                boolean erroLocal = false;
                do {
                    erroLocal = false;

                    for (int index = 0; index < valoresEntrada.valueExpected.length; index++) {

                        double r = saidaCompletaFileiras(valoresEntrada);

                        if (valoresEntrada.valueExpected[index] < 0 && r >= 0 || valoresEntrada.valueExpected[index] >= 0 && r < 0) {

                            treinaNeuroniosF1Fileiras(valoresEntrada, nFileira);

                            erro = true;
                            erroLocal = true;

                            contTreinoFileira++;

                            if (contTreinoFileira > limitFileira) {
                                contTreinoFileira = 0;
                                nFileira++;
                                if (nFileira > neuronios.size()) {
                                    nFileira = 1;
                                }

                            }
                        }

                    }
                    cont++;
                } while (false);
            }
        } while (erro);

        countTrainning = cont;

        return true;
    }

    public float saidaCompletaFileiras(NumberToBits valoresEntrada) {

        Integer sM = null;
        int s1 = 0;
        for (int ncount = 0; ncount < neuronios.size(); ncount++) {
            s1 = 0;
            NumberToBits n = sM == null ? valoresEntrada : getValorTesteCEsperado(sM.intValue(), valoresEntrada.valueExpected);
            for (int nNeuro = 0; nNeuro < neuronios.get(ncount).length; nNeuro++) {
                s1 += neuronios.get(ncount)[nNeuro]
                        .dentritos(n.vetbits.length, n.vetbits.clone())
                        .saida();

            }
            sM = s1;

        }
        return sM;
    }

    public double treinaNeuroniosF1Fileiras(NumberToBits valoresEntrada, int camada) {

        Integer sM = null;
        int s1 = 0;
        for (int ncount = 0; ncount < neuronios.size(); ncount++) {
            s1 = 0;
            NumberToBits n = sM == null ? valoresEntrada : getValorTesteCEsperado(sM.intValue(), valoresEntrada.valueExpected);
            for (int nNeuro = 0; nNeuro < neuronios.get(ncount).length; nNeuro++) {
                s1 += neuronios.get(ncount)[nNeuro]
                        .dentritos(n.vetbits.length, n.vetbits.clone())
                        .saida();

            }

            if (ncount + 1 == camada) {

                for (int nNeuro = 0; nNeuro < neuronios.get(ncount).length; nNeuro++) {

                    neuronios.get(ncount)[nNeuro].corrigePesos(n.vetbits.clone(),
                            n.getValueExpected());
                }
            }

            sM = s1;

        }

        return sM;

    }

    public ArrayList<Float> saidaPSFileira(ArrayList<NumberToBits> nbits) {
        ArrayList<Float> result = new ArrayList<>();
        for (int l = 0; l < nbits.size(); l++) {
            NumberToBits nb = nbits.get(l);
            result.add(saidaCompletaFileiras(nb));
        }

        return result;
    }

    public String stringDeDouble(double d[]) {
        String t = "";

        for (int cont = 0; cont < d.length; cont++) {

            t += d[cont] + "-";
        }
        return t;
    }

    static byte PAR = 2, IMPAR = -2;

    public static void main(String[] args) {

        byte b = 2;
        float t = 0.3f;
        System.out.println("" + (b * t));

//        BitSet b = BitSet.valueOf(new byte[]{-18}).get(0, 8);
//        System.out.println("10001010 " + Integer.toBinaryString(-18));
//
//        for (int cont = 8 - 1; cont >= 0; cont--) {
//
//            System.out.print("" + (b.get(cont) ? 1 : 0));
//        }
//        System.out.println("\n---");
//        for (int cont = b.length() - 1; cont >= 0; cont--) {
//
//            System.out.print("" + (b.get(cont) ? 1 : 0));
//        }
//        System.out.println("\n---");
//
        //    nr.setValForTraining(4, PAR);
//        nr.setValForTraining(5, IMPAR);
//        nr.setValForTraining(6, PAR);
//        nr.setValForTraining(7, IMPAR);
//        nr.setValForTraining(8, PAR);
//        nr.setValForTraining(4, IMPAR);
//        nr.setValForTraining(5, PAR);
//        nr.setValForTraining(6, PAR);
//        nr.setValForTraining(7, IMPAR);
        //  nr.setValForTraining(4, PAR);
        // nr.printaValores();
        long tempoInicial = System.currentTimeMillis();

        OpenACJ nr = new OpenACJ(16);
//
//        nr.setValForTraining(0, PAR);
//        nr.setValForTraining(1, IMPAR);
//        nr.setValForTraining(2, PAR);
//        nr.setValForTraining(3, IMPAR);
// 
//        nr.limitFileira = 105;
//
//        for (int cont = 105; cont > 4; cont--) {
//            nr.limitFileira = cont;
//            nr.TrainingNewOpenACJ(6, 6);
//            System.out.println("VezesTreino  " + nr.countTrainning + " Limite " + cont + " ");
//        }
//        //nr.printaValoresPesos();
//
//        if (true) {
//            return;
//        }
        for (int cont = 105; cont > 4; cont--) {
            nr = new OpenACJ(16);

            nr.setValForTraining(0, PAR);
            nr.setValForTraining(1, IMPAR);
            nr.setValForTraining(2, PAR);
            nr.setValForTraining(3, IMPAR);
            nr.setValForTraining(999, IMPAR);
            nr.setValForTraining(1000, PAR);
            nr.limitFileira = cont;
            nr.TrainingNewOpenACJ(3, 3);
            //2 2 O método foi executado em 5021
            int aderrado = 0;
            int c = 2;
            do {
//AdErrado 38 Errado 89 -421.6863671271541
                float s = nr.saidaCompletaFileiras(nr.getValorTeste(
                        c));
                if (c % 2 != 0 && s < 0
                        || c % 2 == 0 && s >= 0) {

//                System.out.println("Certo " + c + " " + nr.saidaCompletaFileiras(nr.getValorTeste(
//                        c)));
                } else {
                    aderrado++;
                    //   System.out.println(cont + " AdErrado " + aderrado + " Errado " + c + " " + s);

                    nr.setValForTraining(c, (c % 2 == 0) ? PAR : IMPAR);
                    nr.TrainingContinuesACJ(4, 8);

                }

                c++;

                //System.out.println("" + c);
                if (c > 10000) {
                    break;
                }

            } while (true);

            System.out.println("Terminou " + aderrado + " " + cont);
        }
// 100 -> 2968

        System.out.println("O método foi executado em " + (System.currentTimeMillis() - tempoInicial));

        // System.out.println(nr.saidaCompletaFileiras(nr.inTrainningBits.get(1)));
//        File fileN = new File("E:\\MeusProjetos com IA\\iaAprendizado\\IAaprendizadoCamera\\IMGcopo");
//        File fileS = new File("E:\\MeusProjetos com IA\\iaAprendizado\\IAaprendizadoCamera\\IMGnaocopo");
//        File[] nfile = fileN.listFiles();
//        File[] sfile = fileS.listFiles();
//
//        System.out.println("" + OpenACJ.lenghtImagem(sfile[0]));
//        System.out.println("" + OpenACJ.lenghtImagem(sfile[0]) * 8);
//
//        OpenACJ nr = new OpenACJ(OpenACJ.lenghtImagem(sfile[0]) * 8);
//        OpenACJ nr2 = new OpenACJ(OpenACJ.lenghtImagem(sfile[0]) * 8);
//
//        ArrayList<NumeroBits> aN = new ArrayList<>();
//
//        ArrayList<NumeroBits> aN2 = new ArrayList<>();
//
//        for (int cont = 0; cont < nfile.length; cont++) {
//            System.out.println(" " + nfile[cont].getPath());
//            nr.addValTrainningNewBytesToBits(nfile[cont], PAR);
//            nr2.addValTrainningNewBytesToBits(nfile[cont], IMPAR);
//            aN.add(nr.getValTrainningBytesToBits(nfile[cont]));
//            aN2.add(nr.getValTrainningBytesToBits(nfile[cont]));
//        }
//
//        for (int cont = 0; cont < sfile.length; cont++) {
//            System.out.println(" " + sfile[cont].getPath());
//            nr2.addValTrainningNewBytesToBits(sfile[cont], PAR);
//            nr.addValTrainningNewBytesToBits(sfile[cont], IMPAR);
//            aN.add(nr.getValTrainningBytesToBits(sfile[cont]));
//            aN2.add(nr.getValTrainningBytesToBits(sfile[cont]));
//        }
//
//         System.out.println(""+nr.getValTrainningBytesToBits(sfile[0]).paraStringCast()
//          .contentEquals(nr.getValTrainningBytesToBits(sfile[0]).paraStringCast()));
//        long startTime = System.nanoTime();
//
//          nr.printaValores();
//        nr.TrainingNewOpenACJ(1);
//         nr.printaValoresPesos();
//        nr2.TrainingNewOpenACJ(1);
//
//        long endTime = System.nanoTime();
//        long duration = (endTime - startTime) / 1000000;
//        System.out.println("" + duration);
//
//        System.out.println(nr.saidaPSFileira(aN).toString() + "-------");
//        System.out.println(nr2.saidaPSFileira(aN2).toString() + "-------");
//
//        System.out.println(nr.saidaCompletaFileiras(nr.inTrainningBits.get(0)));
//
//        System.out.println(nr2.saidaCompletaFileiras(nr.inTrainningBits.get(0)));
    }
}


/* Location:              /home/junior/Downloads/RedeACJLib_0.3.jar!/meupneuronio/NeuronioAJC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
