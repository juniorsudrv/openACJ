package myNeuronPC;

import WorkBits.NumberToBits;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
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

    ArrayList<String> valuesPossible = new ArrayList();

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

    public void setValTrainningByteAll(int index, BufferedImage img, byte result) {

        for (int vcont = 0; vcont <= 360; vcont += 10) {

            setValTrainningByte(index, rotateImageByDegrees(img,
                    vcont), result);
        }
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

    public void trainning(JProgressBar progresso, int sizeTam, int size) {

        this.sizeTam = sizeTam;
        this.size = size;
        int valueInc = 100 / nr.size();
        if (progresso != null) {
            progresso.setValue(0);
        }
        ini = 0;

        for (ArrayList<OpenACJ> nR : nr) {
            for (OpenACJ n : nR) {
                System.gc();
                n.TrainingContinuesValidExitsACJ(sizeTam, size);
            }
            if (progresso != null) {
                progresso.setValue((ini += valueInc));
            }
        }
        System.gc();
        if (progresso != null) {
            progresso.setValue(100);
        }
    }

    public void trainningStarter(JProgressBar progresso, int sizeTam, int size) {

        this.sizeTam = sizeTam;
        this.size = size;
        int valueInc = 100 / nr.size();
        if (progresso != null) {
            progresso.setValue(0);
        }
        ini = 0;

        for (ArrayList<OpenACJ> nR : nr) {
            for (OpenACJ n : nR) {
                System.gc();
                n.TrainingNewOpenACJ(sizeTam, size);
            }
            if (progresso != null) {
                progresso.setValue((ini += valueInc));
            }
        }
        if (progresso != null) {
            progresso.setValue(100);
        }
    }

    public String getResult(BufferedImage img) {
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
                result = valuesPossible.get(cont);

            }

        }

        return result;
    }

    public int[] getResultCont(BufferedImage img) {

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

    public BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {

        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return rotated;
    }

}
