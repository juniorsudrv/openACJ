package myNeuronPC;

import WorkBits.NumberToBits;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JProgressBar;

public class AuxIA implements Serializable {

    ArrayList<coordCutIMG> cdC = new ArrayList();

    int sizeRecX = 16;
    int sizeRecY = 16;
    int qtdQdRec = 4;
    int bdiv = 2;
 
    int xP = 80;
    int yP = 80;

    int ini = 0;

    int sizeTam = 2, size = 2;

    public int nresult = -1;

    ArrayList<String> itens = new ArrayList<>();
    ArrayList<ArrayList<OpenACJ>> nr = new ArrayList<>();

    public void zera() {
        nr = new ArrayList<>();
    }

    public void setValTrainningByte(int index, BufferedImage img, byte result) {

        updateCoord(img);

        int size = -1;
        if (index >= nr.size()) {

            if (size == -1) {

                size = OpenACJ.lenghtImagem(img) * 8 * 4;

            }

            ArrayList<OpenACJ> nnr = new ArrayList<OpenACJ>();

            for (int cont = 0; cont < qtdQdRec; cont++) {

                nnr.add(new OpenACJ(size));
            }

            nr.add(nnr);
        }

        for (int cont = 0; cont < cdC.size(); cont++) {
            nr.get(index).get(cont).addValTrainningNewBytesToBits(convertScaled(img.getSubimage(cdC.get(cont).xI, cdC.get(cont).yI,
                    cdC.get(cont).largX, cdC.get(cont).altX)), result);
        }

    }

    public void limpaDadosTreino() {

        for (ArrayList<OpenACJ> nR : nr) {
            for (OpenACJ n : nR) {

                n.cleanValuesTrainning();

            }
        }
    }

    public void treinar(JProgressBar progresso, int sizeTam, int size, JButton btreino) {

        this.sizeTam = sizeTam;
        this.size = size;
        int valueInc = 100 / nr.size();
        progresso.setValue(0);
        ini = 0;

        for (ArrayList<OpenACJ> nR : nr) {
            for (OpenACJ n : nR) {
                System.gc();
                n.TrainingContinuesValidExitsACJ(sizeTam, size);
            }
            progresso.setValue((ini += valueInc));
        }
        System.gc();
        progresso.setValue(100);
        btreino.setEnabled(true);

    }

    public void treinarNew(JProgressBar progresso, int sizeTam, int size, JButton btreino) {

        this.sizeTam = sizeTam;
        this.size = size;
        int valueInc = 100 / nr.size();
        progresso.setValue(0);
        ini = 0;

        for (ArrayList<OpenACJ> nR : nr) {
            for (OpenACJ n : nR) {
                System.gc();
                n.TrainingNewOpenACJ(sizeTam, size);
            }
            progresso.setValue((ini += valueInc));
        }

        progresso.setValue(100);
        btreino.setEnabled(true);

    }

    public String getResult(JComboBox combo, BufferedImage img) {
        updateCoord(img);
        String result = null;
        int conCertResult = -1;
        for (int cont = 0; cont < nr.size(); cont++) {

            ArrayList<OpenACJ> nR = nr.get(cont);
            int contCerto = 0;
            for (int cNr = 0; cNr < nR.size(); cNr++) {

                OpenACJ n = nR.get(cNr);

                ArrayList<NumberToBits> ar = new ArrayList<>();
                ar.add(nr.get(0).get(0).getValTrainningBytesToBits(img.getSubimage(cdC.get(cNr).xI, cdC.get(cNr).yI,
                        cdC.get(cNr).largX, cdC.get(cNr).altX)));

                if (n.resultNeuronOutArray(ar).get(0) >= 0) {
                    contCerto++;
                }
            }

            if (conCertResult < contCerto) {
                conCertResult = contCerto;
                result = combo.getItemAt(cont) + "";

            }

        }

        return result;
    }

    public int[] getResultCont(JComboBox combo, BufferedImage img) {

        updateCoord(img);
        int conCertResult = -1;
        int contresult = 0;
        for (int cont = 0; cont < nr.size(); cont++) {

            ArrayList<OpenACJ> nR = nr.get(cont);
            int contCerto = 0;
            for (int cNr = 0; cNr < nR.size(); cNr++) {

                OpenACJ n = nR.get(cNr);

                ArrayList<NumberToBits> ar = new ArrayList<>();
                ar.add(nr.get(0).get(0).getValTrainningBytesToBits(convertScaled(img.getSubimage(cdC.get(cNr).xI, cdC.get(cNr).yI,
                        cdC.get(cNr).largX, cdC.get(cNr).altX))));
                System.gc();
                if (n.resultNeuronOutArray(ar).get(0) >= 0) {
                    contCerto++;
                }
            }

            if (conCertResult < contCerto) {
                conCertResult = contCerto;
                contresult = cont;

            }

        }
        return new int[]{contresult, conCertResult};
    }

    public int getResultCont(int indexFind, BufferedImage img) {
        updateCoord(img);
        int acertos = 0;
        ArrayList<OpenACJ> nR = nr.get(indexFind);

        for (int cNr = 0; cNr < nR.size(); cNr++) {

            OpenACJ n = nR.get(cNr);

            ArrayList<NumberToBits> ar = new ArrayList<>();
            ar.add(nr.get(0).get(0).getValTrainningBytesToBits(convertScaled(img.getSubimage(cdC.get(cNr).xI, cdC.get(cNr).yI,
                    cdC.get(cNr).largX, cdC.get(cNr).altX))));

            if (n.resultNeuronOutArray(ar).get(0) >= 0) {
                acertos++;
            }
        }
        return acertos;
    }

    public BufferedImage convertScaled(BufferedImage img) {
        BufferedImage bi = new BufferedImage(
                sizeRecX, sizeRecY,
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = bi.createGraphics();
        graphics2D.drawImage(img.getScaledInstance(sizeRecX, sizeRecY, Image.SCALE_DEFAULT), 0, 0, null);
        graphics2D.dispose();
        return bi;
    }

    public void updateCoord(BufferedImage img) {

        cdC.clear();

        int xqMin = img.getWidth() / (qtdQdRec / bdiv);
        int yqMin = img.getHeight() / (qtdQdRec / bdiv);

        int xIIC = 0;
        int yIIC = 0;

        xqMin = xqMin - 1;

        for (int cont = 0; cont < qtdQdRec; cont++) {

            cdC.add(coordenadasCorte(xIIC, yIIC, xqMin, yqMin));

            xIIC += xqMin;

            if (xIIC + xqMin > img.getWidth()) {
                yIIC += yqMin;
                xIIC = 0;

                if (yIIC + yqMin > img.getHeight()) {

                    yIIC = yIIC - ((yIIC + yqMin) - img.getHeight());

                }
            }

        }
    }

    public coordCutIMG coordenadasCorte(int xI, int yI, int largX, int altX) {
        return new coordCutIMG(xI, yI, largX, altX);

    }

    public class coordCutIMG implements Serializable {

        int xI, yI;
        int largX, altX;

        public coordCutIMG(int xI, int yI, int largX, int altX) {
            this.xI = xI;
            this.yI = yI;
            this.largX = largX;
            this.altX = altX;
        }

    }

}
