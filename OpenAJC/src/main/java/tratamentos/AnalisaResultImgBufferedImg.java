/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tratamentos;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 *
 * @author junio
 */
public class AnalisaResultImgBufferedImg implements Serializable, Cloneable {

    public Color cor;
    public BufferedImage imgRec = null;
    public int iIndexResult;

    public int acertosResult = -1;
    public String result;
    public int xR, yR, lR, aR;
    public int area;

    public String nomeRec = null;

    public JLabel jl = new JLabel();
    public JLabel jlr = new JLabel();
    public javax.swing.JProgressBar treino_progress = new javax.swing.JProgressBar();

    public AnalisaResultImgBufferedImg(String nomeRec, int iIndexResult, BufferedImage imgRec, int xR, int yR, int lR, int aR, Color cor) {
        this.xR = xR;
        this.yR = yR;
        this.lR = lR;
        this.aR = aR;
        this.imgRec = imgRec;
        this.cor = cor;
        this.nomeRec = nomeRec;
        this.iIndexResult = iIndexResult;

    }

    public AnalisaResultImgBufferedImg(BufferedImage imgRec, int xR, int yR, int lR, int aR, Color cor) {
        this.xR = xR;
        this.yR = yR;
        this.lR = lR;
        this.aR = aR;
        this.imgRec = imgRec;
        this.cor = cor;

    }

    public int area() {
        return aR * lR;
    }

    @Override
    public AnalisaResultImgBufferedImg clone() {
        try {
            return (AnalisaResultImgBufferedImg) super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(AnalisaResultImgBufferedImg.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String toString() {
        return "analisaResultImgBufferedImg{" + "cor=" + cor + ", imgRec=" + imgRec + ", iIndexResult=" + iIndexResult + ", acertosResult=" + acertosResult + ", result=" + result + ", xR=" + xR + ", yR=" + yR + ", lR=" + lR + ", aR=" + aR + ", area=" + area + '}';
    }

}
