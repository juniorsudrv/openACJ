package myNeuronPC;

import Biblis.MemoryUtils;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NeuronPC
        implements Serializable, Cloneable {

    public int countIndex = 0;
    public byte invertD, invertM;
    public int indice = 0;
    public int maxtentativas = 2000;
    public int ncorrections = 0;
    public byte[] inTrainning = null;
    public float weightNeuronStart = 0.17f;
    public float learning = 0.0025f;
    public float bias = -0.3f;
    public float[] weights = null;
    public float S = 0.0f;
    public byte[] udentritos;
    double MaxM = 0.0f;

    float invertReturnN,
            invertReturnP;

    public NeuronPC(int indice, boolean iP) {

        invertD = (byte) -3;
        invertM = (byte) 3;

        learning = learning * indice;
        weightNeuronStart = weightNeuronStart * indice;

        bias = 0.07f;

        this.MaxM = MemoryUtils.maxMemory();
  
        if (indice != 0) {
             this.maxtentativas += indice * 2;
        }
    }

    protected void startWeight(int lt) {
        this.weights = new float[lt];

        for (int cont = 0; cont < lt; cont++) {
            if (MemoryUtils.usedMemory() > this.MaxM - 200.0D) {
                System.out
                        .println(MemoryUtils.usedMemory() + "  " + MemoryUtils.maxMemory());
                System.gc();
            }

            this.weights[cont] = weightNeuronStart;//* ((cont+1)*0.07f);
        }
        
              this.indice = this.weights.length-1;
    }

    protected NeuronPC dentritos(byte... entradas) {
        this.inTrainning = entradas;
        this.udentritos = entradas;

        if (this.weights == null) {
            startWeight(entradas.length);
        }

        if (this.ncorrections > this.maxtentativas) {
            this.inTrainning[this.indice] = invertEntrada(this.inTrainning[this.indice]);
        }
        float acum = 0.0f;
        try {
            for (int cont = 0; cont < entradas.length; cont++) {
                acum += this.weights[cont] * entradas[cont];

            }
        } catch (Exception ex) {
            Logger.getLogger(NeuronPC.class.getName()).log(Level.SEVERE, (String) null, ex);

            System.out.println(" Entrada p  " + this.weights.length + " " + entradas.length);
            System.exit(0);
        }
        acum += (this.bias);
        this.S = acum;
        return this;
    }

    protected NeuronPC dentritos(int nBitsG, byte... entradas) {
        this.inTrainning = entradas;
        this.udentritos = entradas;

        if (this.weights == null) {
            startWeight(nBitsG);
        }

        if (this.ncorrections > this.maxtentativas) {
            this.inTrainning[this.indice] = invertEntrada(this.inTrainning[this.indice]);
        }
        float acum = 0.0f;
        try {
            for (int cont = 0; cont < nBitsG; cont++) {
                acum += this.weights[cont] * entradas[cont];

            }
        } catch (Exception ex) {
            Logger.getLogger(NeuronPC.class.getName()).log(Level.SEVERE, (String) null, ex);

            System.out.println(" Error start " + this.weights.length + " " + entradas.length);
            System.exit(0);
        }
        acum += (this.bias);
        this.S = acum;
        return this;
    }

    public float out() {
        //  System.out.println("SaidaAJ " + S);
        return S;
    }

    public int outValidateS1() {
        //  System.out.println("SaidaAJ " + S);
        return S >= 0 ? 1 : 0;
    }

    protected boolean outTrainning(double valoresperado) {
        if (this.ncorrections > this.maxtentativas) {
            valoresperado *= this.inTrainning[0];
        }

        return (out() == valoresperado);
    }

    protected double outTrainningNumber(double valoresperado) {

        // System.out.println(out()+ " VVVV "+expectedValue);        
        return out();
    }

    protected void toCorrectWeights(byte[] valuesTrainning, byte[] outExpected) {

        for (int contS = 0; contS < outExpected.length; contS++) {
            byte v = outExpected[contS];
            for (int cont = 0; cont < this.weights.length; cont++) {
                float expectedValue = v;
                expectedValue = expectedValue * valuesTrainning[cont];
                float out = expectedValue > 0 ? 1 : 0;

                if (cont + 1 == weights.length) {
                    this.weights[cont] = this.weights[cont] + this.learning * (expectedValue - out) * this.bias;
                } else {

                    this.weights[cont] = this.weights[cont] + this.learning * (expectedValue - out) * valuesTrainning[cont];
                }
            }
        }

        doCorrections();
    }

    protected void doCorrections() {
        this.ncorrections++;
    }

    protected boolean ativadoAJ() {
        return (this.ncorrections > this.maxtentativas);
    }

    protected byte invertEntrada(byte entrada) {
        if (entrada == 1.0D) {
            return invertD;
        }
        return invertM;
    }

    protected void showWeights() {
        for (int cont = 0; cont < this.weights.length; cont++) {
            System.out.print("P" + cont + " " + ((Float) this.weights[cont]));
        }
        System.out.println();
    }
}


/* Location:              /home/junior/Downloads/RedeACJLib_0.3.jar!/meupneuronio/NeuronioAJ.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
