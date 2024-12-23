/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tratamentos;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author junio
 */
public class TrataImagens implements Serializable {

    public ArrayList<AnalisaResultImgBufferedImg> anR = new ArrayList<>();

    public boolean calibrado = false;
    int inc = 1;
    int lWidth = 0;
    int lHeight = 0;

    public int nivelBrilho = 300;

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

                    //   System.out.println("Entrou no loop "+xL+" "+yI);
                    boolean ignora = false;

                    while (true) {

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

    public boolean isFundo(int rgb) {
        Color cp = new Color(rgb);
        int dr = cp.getRed();
        int dg = cp.getGreen();
        int db = cp.getBlue();

        return Math.sqrt(0.299 * dr * dr + 0.587 * dg * dg + 0.114 * db * db) > nivelBrilho;
    }
}
