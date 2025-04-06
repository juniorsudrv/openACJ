/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package IAAprender;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import tratamentos.AnalisaResultImgBufferedImg;

/**
 *
 * @author junio
 */
public class TrataImagens implements Serializable {

    public int limiar = 100;
    public ArrayList<AnalisaResultImgBufferedImg> anR = new ArrayList<>();

    public boolean calibrado = false;
    int inc = 1;
    int lWidth = 0;
    int lHeight = 0;

    public int nivelBrilho = 200;

    int tentativas = 0;
    public int tamMin = 5;
    public int mliar = 45;

    public BufferedImage pegaObjetos(BufferedImage image) {
        System.out.println("Pega Objetos");
        anR.clear();
        int BLACK = Color.GREEN.getRGB();
        int WHITE = Color.WHITE.getRGB();

        BufferedImage output = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_INT_RGB);
        int pPixel = 0;

        lWidth = image.getWidth();
        lHeight = image.getHeight();

        int cont = 0;

        for (int y = 0; y < image.getHeight(); y++) {

            for (int x = 0; x < image.getWidth(); x++) {

                // -16777216
                if (!isFundo(image.getRGB(x, y))) {

                    boolean existe = false;

                    for (int ca = 0; ca < anR.size(); ca++) {

                        if (x >= anR.get(ca).xR && y >= anR.get(ca).yR
                                && x <= anR.get(ca).xR + anR.get(ca).lR && y <= anR.get(ca).yR + anR.get(ca).aR) {

                            existe = true;
                            break;
                        }

                    }

                    if (existe) {
                        continue;
                    }

                    int cPixel = 0;
                    int xL = x - 1, yL = y;

                    int xI = x;
                    int yI = y;
                    int lI = x;
                    int aI = y;

                    System.out.println("Entrou no loop " + xL + " " + yI);

                    boolean ignora = false;
                    tentativas = 0;

                    while (true) {

                        tentativas++;

//                        if (tentativas > 1000) {
//                            ignora = true;
//                            break;
//                        }
                        int[] xy = validaPonto(image, xL, yL);
                        if (xy == null) {
                            ignora = true;
                            break;
                        }
                        if (xI > xy[0]) {
                            xI = xy[0];
                        }
                        if (yI > xy[1]) {
                            yI = xy[1];

                        }

                        if (lI < xy[0]) {
                            lI = xy[0];
                        }
                        if (aI < xy[1]) {
                            aI = xy[1];

                        }

                        if (x - 1 == xy[0] && y == xy[1] || cPixel > 5 && validaRegiao(x, y, xy[0], xy[1])) {

                            System.out.println("igual " + cont);
                            break;

                        } else {

                            //   System.out.println("Diferente");
                        }

                        xL = xy[0];
                        yL = xy[1];

                    }

                    //   System.out.println("Saiu no loop");
                    cont++;

                    if (ignora) {

                        continue;
                    }
                    lI = lI - xI;
                    aI = aI - yI;

                    if (lI <= tamMin || aI <= tamMin) {

                        System.out.println("Invalido");
                        continue;
                    }

                    BufferedImage bimg = image.getSubimage(xI, yI, lI, aI);

                    anR.add(new AnalisaResultImgBufferedImg(bimg, xI, yI, lI, aI, gerarCorAleatoriamente()));

                }
            }

        }
        return output;
    }

    public BufferedImage checaImagem(BufferedImage image) {

        int GREEN = Color.GREEN.getRGB();

        BufferedImage output = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_INT_RGB);

        int lWidth = image.getWidth();
        int lHeight = image.getHeight();

        for (int y = 2; y < image.getHeight(); y += inc) {

            for (int x = 2; x < image.getWidth(); x += inc) {

                if (isDiff( x, y,  image)) {
                    output.setRGB(x, y, GREEN);

                } else {
                    output.setRGB(x, y, image.getRGB(x, y));
                }

            }

        }

        for (int y = 1; y < image.getHeight(); y += inc) {
            for (int x = 1; x < image.getWidth(); x += inc) {
                if (output.getRGB(x, y) == Color.GREEN.getRGB()) {
                    coloriRegiao(20, x, y, lWidth, lHeight, output);
                }
            }

        }

        return output;
    }

    public void coloriRegiao(int limiar, int x, int y, int max, int may, BufferedImage image) {

        for (int contx = x; contx < x + limiar && contx < max; contx++) {

            for (int conty = y; conty < y + limiar && conty < may; conty++) {

                image.setRGB(contx, conty, Color.BLUE.getRGB());

            }

        }

    }

    public BufferedImage checaImagemDivisao(BufferedImage image) {

        int GREEN = Color.GREEN.getRGB();

        BufferedImage output = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_INT_RGB);

        int lWidth = image.getWidth();
        int lHeight = image.getHeight();

        for (int y = 0; y < image.getHeight(); y += inc) {

            for (int x = 0; x < image.getWidth(); x += inc) {

                if (!isFundo(image.getRGB(x, y))) {
                    output.setRGB(x, y, GREEN);
                } else {
                    output.setRGB(x, y, image.getRGB(x, y));
                }

            }

        }

        return output;
    }

    public boolean validaRegiao(int x, int y, int X, int Y) {

        int dist = 5;
        boolean vx = false, vy = false;

        for (int cont = 0; cont < dist; cont++) {

            if (x == X + cont || x == cont - X) {

                vx = true;

            }

            if (y == y + cont || y == cont - Y) {

                vy = true;

            }

        }

        return vx && vy;

    }

    public int[] validaPonto(BufferedImage image, int x, int y) {

        if (validaPosicao(x + 1, y) && !isFundo(image.getRGB(x + 1, y))) {

            return validaBranco(image, x, y, 0);
        }

        if (validaPosicao(x + 1, y + 1) && !isFundo(image.getRGB(x + 1, y + 1))) {

            return validaBranco(image, x, y, 1);
        }

        if (validaPosicao(x, y + 1) && !isFundo(image.getRGB(x, y + 1))) {

            return validaBranco(image, x, y, 2);
        }

        if (validaPosicao(x - 1, y + 1) && !isFundo(image.getRGB(x - 1, y + 1))) {

            return validaBranco(image, x, y, 3);
        }

        if (validaPosicao(x - 1, y) && !isFundo(image.getRGB(x - 1, y))) {

            return validaBranco(image, x, y, 4);
        }

        if (validaPosicao(x - 1, y - 1) && !isFundo(image.getRGB(x - 1, y - 1))) {

            return validaBranco(image, x, y, 5);
        }

        if (validaPosicao(x, y - 1) && !isFundo(image.getRGB(x, y - 1))) {

            return validaBranco(image, x, y, 6);
        }

        if (validaPosicao(x + 1, y - 1) && !isFundo(image.getRGB(x + 1, y - 1))) {

            return validaBranco(image, x, y, 7);
        }

        return null;
    }

    public int[] validaBranco(BufferedImage image, int x, int y, int iStart) {

        for (int cont = 0; cont < 10; cont++) {

            if (!validaPosicao(x + 1, y) && iStart == 0 || iStart == 0 && isFundo(image.getRGB(x + 1, y))) {

                return new int[]{x + 1, y};
            }

            if (!validaPosicao(x + 1, y + 1) && iStart == 1 || iStart == 1 && isFundo(image.getRGB(x + 1, y + 1))) {

                return new int[]{x + 1, y + 1};
            }

            if (!validaPosicao(x, y + 1) && iStart == 2 || iStart == 2 && isFundo(image.getRGB(x, y + 1))) {

                return new int[]{x, y + 1};
            }

            if (!validaPosicao(x - 1, y + 1) && iStart == 3 || iStart == 3 && isFundo(image.getRGB(x - 1, y + 1))) {

                return new int[]{x - 1, y + 1};
            }

            if (!validaPosicao(x - 1, y) && iStart == 4 || iStart == 4 && isFundo(image.getRGB(x - 1, y))) {

                return new int[]{x - 1, y};
            }

            if (!validaPosicao(x - 1, y - 1) && iStart == 5 || iStart == 5 && isFundo(image.getRGB(x - 1, y - 1))) {

                return new int[]{x - 1, y - 1};
            }

            if (!validaPosicao(x, y - 1) && iStart == 6 || iStart == 6 && isFundo(image.getRGB(x, y - 1))) {

                return new int[]{x, y - 1};
            }

            if (!validaPosicao(x + 1, y - 1) && iStart == 7 || iStart == 7 && isFundo(image.getRGB(x + 1, y - 1))) {

                return new int[]{x + 1, y - 1};
            }

            iStart++;

            if (iStart == 8) {

                iStart = 0;
            }

        }

        return null;

    }

    public boolean validaPosicao(int x, int y) {

        return x > 0 && y > 0 && x < lWidth - 2 && y < lHeight - 2;
    }

    public Color gerarCorAleatoriamente() {
        Random randColor = new Random();
        int r = randColor.nextInt(256);
        int g = randColor.nextInt(256);
        int b = randColor.nextInt(256);
        return new Color(r, g, b);
    }

    public int calibrarBranco(BufferedImage image) {
        calibrado = true;
        for (int y = 0; y < image.getHeight(); y += inc) {

            for (int x = 0; x < image.getWidth(); x += inc) {

                Color cp = new Color(image.getRGB(x, y));
                int dr = cp.getRed();
                int dg = cp.getGreen();
                int db = cp.getBlue();
                if (nivelBrilho > Math.sqrt(0.299 * dr * dr + 0.587 * dg * dg + 0.114 * db * db)) {
                    calibrado = false;
                    nivelBrilho = (int) Math.sqrt(0.299 * dr * dr + 0.587 * dg * dg + 0.114 * db * db);
                }

            }

        }

        return nivelBrilho;
    }

    //Utilizado pra determinar limites do recorte
    public boolean isFundo(int rgb) {
//        Color cp = new Color(rgb);
//        int dr = cp.getRed();
//        int dg = cp.getGreen();
//        int db = cp.getBlue();
//
//        return Math.sqrt(0.299 * dr * dr + 0.587 * dg * dg + 0.114 * db * db) > nivelBrilho;

        return rgb != Color.BLUE.getRGB();
    }

    public boolean isFundoChecaImagem(int rgb) {
        Color cp = new Color(rgb);
        int dr = cp.getRed();
        int dg = cp.getGreen();
        int db = cp.getBlue();

        return Math.sqrt(0.299 * dr * dr + 0.587 * dg * dg + 0.114 * db * db) > nivelBrilho;

    }

    
    
    public boolean isDiff(int x, int y, BufferedImage buf) {

        if (buf.getRGB(x, y) == Color.GREEN.getRGB()) {
            return false;
        }

        int v0 = buf.getRGB(x - 2, y) + buf.getRGB(x, y - 2);
        int v1 = buf.getRGB(x - 1, y) + buf.getRGB(x, y - 1);
        int v2 = buf.getRGB(x, y) + buf.getRGB(x, y);

        int total = v1 > v2 ? v1 - v2 : v2 - v1;

         total = v0 > total ? v0 - total : total - v0;
        
        return total >= limiar;

    }
    
    
    public boolean isDiff(int limiar, int x, int y, int max, int may, BufferedImage buf) {

        if (buf.getRGB(x, y) == Color.GREEN.getRGB()) {
            return false;
        }

        int v1 = 0;
        int v2 = 0;
        for (int contx = 0; contx < limiar && contx + x < max && contx -x >0; contx++) {

            // for (int conty = y; conty < y + limiar && conty < may; conty++) {
            v1 += buf.getRGB(contx + x, y);
            v2 += buf.getRGB(contx - x, y);
            //}

        }

        int total = v1 > v2 ? v1 - v2 : v2 - v1;
 
        return total >= limiar;

    }
}
