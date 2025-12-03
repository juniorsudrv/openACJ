/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package IAAprender;

import OpIO.IO;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import tratamentos.AnalisaResultImgBufferedImg;

/**
 *
 * @author junio
 */
public class TrataImagensCamera implements Serializable {

    public TrataImagensCamera() {

        try {
            pixels = (ArrayList<int[][]>) IO.ler("XY");
        } catch (Exception ex) {
            pixels = new ArrayList<>();
            Logger.getLogger(TrataImagensCamera.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public int tamMin = 5;
    public int mliar = 45;

    public int diffFundo = Color.BLACK.getRGB();

    public ArrayList<int[][]> pixels = new ArrayList();

    public ArrayList<AnalisaResultImgBufferedImg> anR = new ArrayList<>();

    public boolean calibrado = false;
    int inc = 1;
    int incvalid = 2;
    int lWidth = 0;
    int lHeight = 0;

    public int nivelBrilho = 300;

    public int xA = 0;
    public int yA = 0;

    public int limitBusca = 250;

    ArrayList<Integer> lx = new ArrayList<>();
    ArrayList<Integer> ly = new ArrayList<>();

    int maxTentavias = 9000;
    int tentativas = 0;

    int cr = 0;

    public synchronized BufferedImage pegaObjetos_Novo(BufferedImage image, BufferedImage baseImage) {
        System.out.println("Pega Objetos");
        anR.clear();

        BufferedImage output = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_INT_RGB);
        int pPixel = 0;

        lWidth = image.getWidth();
        lHeight = image.getHeight();

        for (int y = 0; y < image.getHeight() - 5; y++) {

            for (int x = 0; x < image.getWidth() - 5; x++) {

                // -16777216
                if (!isFundo(x, y, image.getRGB(x, y)) && !isFundoArea(x, y, image, 5)) {

                    boolean existe = false;
                    for (int ca = 0; ca < anR.size(); ca++) {

                        if (x >= anR.get(ca).xR && y >= anR.get(ca).yR
                                && x <= anR.get(ca).xR + anR.get(ca).lR && y <= anR.get(ca).yR + anR.get(ca).aR) {

                            existe = true;
                            break;
                        }

                    }

                    if (existe || anR.size() > 5) {
                        continue;
                    }

                    lx.clear();;
                    ly.clear();
                    cr = 0;

                    tentativas = 0;

                    validaPreenchimento(image, x, y);

                    int xI = Integer.MAX_VALUE;
                    int yI = Integer.MAX_VALUE;
                    int lI = 0;
                    int aI = 0;

                    for (int lxy = 0; lxy < lx.size(); lxy++) {

                        if (xI > lx.get(lxy)) {

                            xI = lx.get(lxy);
                        }

                        if (yI > ly.get(lxy)) {

                            yI = ly.get(lxy);
                        }

                        if (lI < lx.get(lxy)) {

                            lI = lx.get(lxy);
                        }

                        if (aI < ly.get(lxy)) {

                            aI = ly.get(lxy);
                        }

                    }

                    lI = lI - xI;
                    aI = aI - yI;
                    if (lI <= tamMin || aI <= tamMin) {

                        System.out.println("Invalido");
                        continue;
                    }

                    xI -= 2;
                    yI -= 2;

                    lI += 5;
                    aI += 5;

                    BufferedImage bimg = baseImage.getSubimage(xI, yI, lI, aI);

                    anR.add(new AnalisaResultImgBufferedImg(bimg, xI, yI, lI, aI, Color.white));

                }
            }

        }

        //Avalia interceção
        removeDuplicatas(anR, baseImage);

        return output;
    }

    public void removeDuplicatas(ArrayList<AnalisaResultImgBufferedImg> anR, BufferedImage baseImage) { 
        

        for (int cont = 0; cont < anR.size(); cont++) {

            AnalisaResultImgBufferedImg aN = anR.get(cont);
            ArrayList<AnalisaResultImgBufferedImg> temp = new ArrayList();

            for (int cI = 0; cI < anR.size(); cI++) {

                if (cont != cI) {

                    Rectangle rect1 = new Rectangle(anR.get(cont).xR, anR.get(cont).yR, anR.get(cont).lR, anR.get(cont).aR);
                    Rectangle rect2 = new Rectangle(anR.get(cI).xR, anR.get(cI).yR, anR.get(cI).lR, anR.get(cI).aR);

                    if (rect1.intersects(rect2)) {

                        temp.add(anR.get(cI));

                    }

                }
            }

            if (temp.size() > 0) {

                int x = anR.get(cont).xR;
                int y = anR.get(cont).yR;

                int xW = 0;
                int yH = 0;

                for (int cI = 0; cI < temp.size(); cI++) {

                    if (x > temp.get(cI).xR) {
                        x = temp.get(cI).xR;

                    }

                    if (y > temp.get(cI).yR) {
                        y = temp.get(cI).yR;

                    }

                    if (xW < temp.get(cI).lR) {
                        xW = temp.get(cI).lR;

                    }

                    if (yH < temp.get(cI).aR) {
                        yH = temp.get(cI).aR;

                    }

                }

                for (int cI = 0; cI < temp.size(); cI++) {

                    anR.remove(temp.get(cI));

                }

                anR.remove(aN);

                BufferedImage bimg = baseImage.getSubimage(x, y, xW, yH);

                anR.add(new AnalisaResultImgBufferedImg(bimg,
                         x, y, xW, yH, gerarCorAleatoriamente()));

            }

        }
 

//        for (int cI = 0; cI < anR.size(); cI++) {
//
//            System.out.println("" + anR.get(cI).toString());
//        }
    }

    public void validaPreenchimento(BufferedImage image, int x, int y) {

        try {

            if (tentativas >= maxTentavias) {
                return;
            }

            tentativas++;

            if (x > 1 && y > 1 && x < lWidth - 2 && y < lHeight - 2 && image.getRGB(x, y) == diffFundo && !existXY(x, y)) {

                lx.add(x);
                ly.add(y);

                if (!existXY(x + incvalid, y)) {
                    validaPreenchimento(image, x + incvalid, y);

                }
                if (!existXY(x - incvalid, y)) {
                    validaPreenchimento(image, x - incvalid, y);
                }
                if (!existXY(x, y + incvalid)) {
                    validaPreenchimento(image, x, y + incvalid);
                }
                if (!existXY(x, y - incvalid)) {
                    validaPreenchimento(image, x, y - incvalid);
                }

            }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public void coloriRegiao(int limiar, int x, int y, int max, int may, BufferedImage image) {

        for (int contx = x; contx < x + limiar && contx < max; contx++) {

            for (int conty = y; conty < y + limiar && conty < may; conty++) {

                image.setRGB(contx, conty, diffFundo);

            }

        }

    }

    public synchronized boolean existXY(int x, int y) {

        for (int cont = 0; cont < lx.size(); cont++) {

            if (lx.get(cont) != null && lx.get(cont).intValue() == x && ly.get(cont).intValue() == y) {
                return true;
            }

        }
        return false;
    }

    public boolean validaRegiao(int x, int y, int X, int Y) {

        int dist = 3;
        boolean vx = false, vy = false;

        for (int cont = 0; cont < dist; cont++) {

            if (x == X + cont || x == cont - X) {

                vx = true;

            }

            if (y == Y + cont || y == cont - Y) {

                vy = true;

            }

        }

        return vx && vy;

    }

    public int[] validaPonto(BufferedImage image, int x, int y) {

        if (validaPosicao(x + 1, y) && !isFundo(x + 1, y, image.getRGB(x + 1, y))) {

            return validaBranco(image, x, y, 0);
        }

        if (validaPosicao(x + 1, y + 1) && !isFundo(x + 1, y + 1, image.getRGB(x + 1, y + 1))) {

            return validaBranco(image, x, y, 1);
        }

        if (validaPosicao(x, y + 1) && !isFundo(x, y + 1, image.getRGB(x, y + 1))) {

            return validaBranco(image, x, y, 2);
        }

        if (validaPosicao(x - 1, y + 1) && !isFundo(x - 1, y + 1, image.getRGB(x - 1, y + 1))) {

            return validaBranco(image, x, y, 3);
        }

        if (validaPosicao(x - 1, y) && !isFundo(x - 1, y, image.getRGB(x - 1, y))) {

            return validaBranco(image, x, y, 4);
        }

        if (validaPosicao(x - 1, y - 1) && !isFundo(x - 1, y - 1, image.getRGB(x - 1, y - 1))) {

            return validaBranco(image, x, y, 5);
        }

        if (validaPosicao(x, y - 1) && !isFundo(x, y - 1, image.getRGB(x, y - 1))) {

            return validaBranco(image, x, y, 6);
        }

        if (validaPosicao(x + 1, y - 1) && !isFundo(x + 1, y - 1, image.getRGB(x + 1, y - 1))) {

            return validaBranco(image, x, y, 7);
        }

        return null;
    }

    public int[] validaBranco(BufferedImage image, int x, int y, int iStart) {

        for (int cont = 0; cont < 10; cont++) {

            if (!validaPosicao(x + 1, y) && iStart == 0 || iStart == 0 && isFundo(x + 1, y, image.getRGB(x + 1, y))) {

                return new int[]{x + 1, y};
            }

            if (!validaPosicao(x + 1, y + 1) && iStart == 1 || iStart == 1 && isFundo(x + 1, y + 1, image.getRGB(x + 1, y + 1))) {

                return new int[]{x + 1, y + 1};
            }

            if (!validaPosicao(x, y + 1) && iStart == 2 || iStart == 2 && isFundo(x, y + 1, image.getRGB(x, y + 1))) {

                return new int[]{x, y + 1};
            }

            if (!validaPosicao(x - 1, y + 1) && iStart == 3 || iStart == 3 && isFundo(x - 1, y + 1, image.getRGB(x - 1, y + 1))) {

                return new int[]{x - 1, y + 1};
            }

            if (!validaPosicao(x - 1, y) && iStart == 4 || iStart == 4 && isFundo(x - 1, y, image.getRGB(x - 1, y))) {

                return new int[]{x - 1, y};
            }

            if (!validaPosicao(x - 1, y - 1) && iStart == 5 || iStart == 5 && isFundo(x - 1, y - 1, image.getRGB(x - 1, y - 1))) {

                return new int[]{x - 1, y - 1};
            }

            if (!validaPosicao(x, y - 1) && iStart == 6 || iStart == 6 && isFundo(x, y - 1, image.getRGB(x, y - 1))) {

                return new int[]{x, y - 1};
            }

            if (!validaPosicao(x + 1, y - 1) && iStart == 7 || iStart == 7 && isFundo(x + 1, y - 1, image.getRGB(x + 1, y - 1))) {

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

    public BufferedImage checaImagem(BufferedImage image) {
        if (pixels == null || pixels.size() == 0) {
            return image;
        }

        BufferedImage output = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_INT_RGB);

        lWidth = image.getWidth();
        lHeight = image.getHeight();

        for (int y = 0; y < image.getHeight(); y += inc) {

            for (int x = 0; x < image.getWidth(); x += inc) {

                if (!isFundoCheca(x, y, image.getRGB(x, y))) {
                    output.setRGB(x, y, diffFundo);
                } else {
                    output.setRGB(x, y, image.getRGB(x, y));
                }

            }

        }

//        for (int y = 0; y < output.getHeight(); y += inc) {
//
//            for (int x = 0; x < output.getWidth(); x += inc) {
//
//                if (output.getRGB(x, y) == diffFundo) {
//                    rodeiaPixel(x, y, output);
//                }
//
//            }
//
//        }
//        try{
//        output.setRGB(xA, yA, Color.RED.getRGB());
//        output.setRGB(xA + 1, yA + 1, Color.RED.getRGB());
//        output.setRGB(xA + 2, yA + 2, Color.RED.getRGB());
//        output.setRGB(xA + 3, yA + 3, Color.RED.getRGB());
//        output.setRGB(xA + 4, yA + 4, Color.RED.getRGB());
//        output.setRGB(xA + 5, yA + 5, Color.RED.getRGB());
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        for (int y = 1; y < image.getHeight(); y += inc) {
//            for (int x = 1; x < image.getWidth(); x += inc) {
//                if (output.getRGB(x, y) == Color.GREEN.getRGB()) {
//                    coloriRegiao(10, x, y, lWidth, lHeight, output);
//                }
//            }
//
//        }
        return output;

    }

    public void rodeiaPixel(int x, int y, BufferedImage image) {

        for (int cx = 0; cx < inc + 4; cx++) {
            for (int cy = 0; cy < inc + 4; cy++) {

                image.setRGB(x, y, diffFundo);

            }

        }
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

        return rgb != diffFundo;
    }

    public boolean isFundoCheca(int x, int y, int pixel) {
        Color cpf = new Color(pixel);

        for (int[][] xy : pixels) {

            if (x < xy.length && y < xy[0].length) {

                Color cp = new Color(xy[x][y]);

                if (isValidFundo(cpf, cp)) {

                    return true;
                }
            }

        }

        return false;
    }

    public boolean isFundo(int x, int y, int pixel) {

        if (true) {
            return isFundo(pixel);
        }
        Color cpf = new Color(pixel);

        for (int[][] xy : pixels) {

            Color cp = new Color(xy[x][y]);

            if (isValidFundo(cpf, cp)) {

                return true;
            }

        }

        return false;
    }

    public boolean isFundoArea(int x, int y, BufferedImage bImg, int area) {

        if (pixels == null) {
            return false;
        }

        for (int[][] xy : pixels) {
            for (int validy = 0; validy < area; validy++) {
                for (int validx = 0; validx < area; validx++) {

                    if (bImg.getRGB(validx + x, validy + y) != diffFundo) {

                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isValidFundo(Color c1, Color c2) {
        int drf = c1.getRed();
        int dgf = c1.getGreen();
        int dbf = c1.getBlue();

        int dr = c2.getRed();
        int dg = c2.getGreen();
        int db = c2.getBlue();

        drf = drf > dr ? drf - dr : dr - drf;
        dgf = dgf > dg ? dgf - dg : dg - dgf;
        dbf = dbf > db ? dbf - db : db - dbf;

        return drf < mliar && dgf < mliar && dbf < mliar;

    }

    public boolean isFundo(BufferedImage image, int x, int y, int incr) {

        boolean isFundo = false;
        for (int xc = 0; xc < incr; xc++) {

            for (int yc = 0; yc < incr; yc++) {
                Color cp = new Color(image.getRGB(x + xc, y + yc));
                int dr = cp.getRed();
                int dg = cp.getGreen();
                int db = cp.getBlue();

                if (Math.sqrt(0.299 * dr * dr + 0.587 * dg * dg + 0.114 * db * db) >= nivelBrilho) {

                    return true;
                }
            }

        }
        return false;
    }

    public int calibrarBrancoPixels(BufferedImage image) {

        int xy[][] = new int[image.getWidth()][image.getHeight()];

        System.out.println("Entrou ");
        for (int y = 0; y < image.getHeight(); y++) {

            for (int x = 0; x < image.getWidth(); x++) {
                xy[x][y] = image.getRGB(x, y);

            }

        }
        if (pixels == null) {

            pixels = new ArrayList<>();
        }

        pixels.add(xy);
        System.out.println("Saiu ");

        try {
            IO.inserir("XY", pixels);
        } catch (IOException ex) {
            Logger.getLogger(TrataImagensCamera.class.getName()).log(Level.SEVERE, null, ex);
        }

        return pixels.size();

    }

    public int calibrarBrancoPixels(BufferedImage image, int index) {

        int xy[][] = new int[image.getWidth()][image.getHeight()];

        System.out.println("Entrou ");
        for (int y = 0; y < image.getHeight(); y++) {

            for (int x = 0; x < image.getWidth(); x++) {
                xy[x][y] = image.getRGB(x, y);

            }

        }
        if (pixels == null) {

            pixels = new ArrayList<>();
        }

        if (pixels.size() > 1) {
            pixels.remove(0);
        }
        pixels.add(xy);
        System.out.println("Saiu ");

        try {
            IO.inserir("XY", pixels);
        } catch (IOException ex) {
            Logger.getLogger(TrataImagensCamera.class.getName()).log(Level.SEVERE, null, ex);
        }

        return pixels.size();

    }
    
    
    
    public ArrayList<AnalisaResultImgBufferedImg> detectarObjetosOtsu(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();

    // Converte para escala de cinza
    int[] histogram = new int[256];
    int[][] gray = new int[width][height];
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            Color c = new Color(image.getRGB(x, y));
            int g = (int)(0.299*c.getRed() + 0.587*c.getGreen() + 0.114*c.getBlue());
            gray[x][y] = g;
            histogram[g]++;
        }
    }

    // Calcula limiar ótimo com Otsu
    int total = width * height;
    float sum = 0;
    for (int t = 0; t < 256; t++) sum += t * histogram[t];

    float sumB = 0;
    int wB = 0, wF = 0;
    float varMax = 0;
    int limiar = 0;

    for (int t = 0; t < 256; t++) {
        wB += histogram[t];
        if (wB == 0) continue;
        wF = total - wB;
        if (wF == 0) break;

        sumB += (float) (t * histogram[t]);

        float mB = sumB / wB;
        float mF = (sum - sumB) / wF;

        float varBetween = (float)wB * (float)wF * (mB - mF) * (mB - mF);

        if (varBetween > varMax) {
            varMax = varBetween;
            limiar = t;
        }
    }

    // Agora aplica flood fill para separar objetos
    return detectarObjetos(image, limiar);
}
    
    
    
    
    public ArrayList<AnalisaResultImgBufferedImg> detectarObjetos(BufferedImage image, int limiarBrilho) {
    ArrayList<AnalisaResultImgBufferedImg> objetos = new ArrayList<>();

    int width = image.getWidth();
    int height = image.getHeight();

    boolean[][] visitado = new boolean[width][height];

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            if (!visitado[x][y]) {
                Color cor = new Color(image.getRGB(x, y));
                int brilho = (int) Math.sqrt(
                    0.299 * cor.getRed() * cor.getRed() +
                    0.587 * cor.getGreen() * cor.getGreen() +
                    0.114 * cor.getBlue() * cor.getBlue()
                );

                // Se for um pixel "claro" (acima do limiar), consideramos parte de objeto
                if (brilho > limiarBrilho) {
                    // Flood fill para agrupar região conectada
                    Rectangle bbox = floodFillObjeto(image, x, y, visitado, limiarBrilho);

                    if (bbox.width > 5 && bbox.height > 5) { // evita ruído
                        BufferedImage recorte = image.getSubimage(bbox.x, bbox.y, bbox.width, bbox.height);
                        objetos.add(new AnalisaResultImgBufferedImg(recorte, bbox.x, bbox.y, bbox.width, bbox.height, gerarCorAleatoriamente()));
                    }
                }
            }
        }
    }

    return objetos;
}

private Rectangle floodFillObjeto(BufferedImage image, int startX, int startY, boolean[][] visitado, int limiarBrilho) {
    int minX = startX, minY = startY, maxX = startX, maxY = startY;

    ArrayList<Point> fila = new ArrayList<>();
    fila.add(new Point(startX, startY));

    while (!fila.isEmpty()) {
        Point p = fila.remove(0);
        int x = p.x, y = p.y;

        if (x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight()) continue;
        if (visitado[x][y]) continue;

        Color cor = new Color(image.getRGB(x, y));
        int brilho = (int) Math.sqrt(
            0.299 * cor.getRed() * cor.getRed() +
            0.587 * cor.getGreen() * cor.getGreen() +
            0.114 * cor.getBlue() * cor.getBlue()
        );

        if (brilho <= limiarBrilho) continue; // ignora fundo

        visitado[x][y] = true;

        // Atualiza bounding box
        minX = Math.min(minX, x);
        minY = Math.min(minY, y);
        maxX = Math.max(maxX, x);
        maxY = Math.max(maxY, y);

        // Adiciona vizinhos
        fila.add(new Point(x+1, y));
        fila.add(new Point(x-1, y));
        fila.add(new Point(x, y+1));
        fila.add(new Point(x, y-1));
    }

    return new Rectangle(minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
}



public ArrayList<AnalisaResultImgBufferedImg> detectarObjetosKMeans(BufferedImage image, int k) {
    int w = image.getWidth();
    int h = image.getHeight();

    // Converte pixels para vetor RGB
    int[] pixels = new int[w * h];
    image.getRGB(0, 0, w, h, pixels, 0, w);

    double[][] data = new double[w * h][3];
    for (int i = 0; i < pixels.length; i++) {
        Color c = new Color(pixels[i]);
        data[i][0] = c.getRed();
        data[i][1] = c.getGreen();
        data[i][2] = c.getBlue();
    }

    // Executa K-means
    KMeansClustering km = new KMeansClustering(k);
    int[] labels = km.cluster(data);

    // Identifica o cluster mais frequente (fundo)
    int[] freq = new int[k];
    for (int label : labels) freq[label]++;
    int fundoCluster = 0;
    for (int i = 1; i < k; i++) {
        if (freq[i] > freq[fundoCluster]) fundoCluster = i;
    }

    // Mapa de clusters para reconstruir a imagem segmentada
    BufferedImage mask = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    for (int i = 0; i < labels.length; i++) {
        int x = i % w;
        int y = i / w;
        if (labels[i] == fundoCluster) {
            mask.setRGB(x, y, Color.BLACK.getRGB()); // fundo preto
        } else {
            mask.setRGB(x, y, Color.WHITE.getRGB()); // objeto branco
        }
    }

    // Agora faz flood-fill para separar objetos e gerar bounding boxes
    ArrayList<AnalisaResultImgBufferedImg> objetos = new ArrayList<>();
    boolean[][] visitado = new boolean[w][h];

    for (int y = 0; y < h; y++) {
        for (int x = 0; x < w; x++) {
            if (!visitado[x][y] && new Color(mask.getRGB(x, y)).equals(Color.WHITE)) {
                Rectangle bbox = floodFillMask(mask, x, y, visitado);

                if (bbox.width > 5 && bbox.height > 5) {
                    BufferedImage recorte = image.getSubimage(bbox.x, bbox.y, bbox.width, bbox.height);
                    objetos.add(new AnalisaResultImgBufferedImg(recorte, bbox.x, bbox.y, bbox.width, bbox.height, gerarCorAleatoriamente()));
                }
            }
        }
    }

    return objetos;
}

// Flood-fill na máscara binária (objetos brancos)
private Rectangle floodFillMask(BufferedImage mask, int startX, int startY, boolean[][] visitado) {
    int minX = startX, minY = startY, maxX = startX, maxY = startY;

    ArrayDeque<Point> fila = new ArrayDeque<>();
    fila.add(new Point(startX, startY));

    while (!fila.isEmpty()) {
        Point p = fila.removeFirst();
        int x = p.x, y = p.y;

        if (x < 0 || y < 0 || x >= mask.getWidth() || y >= mask.getHeight()) continue;
        if (visitado[x][y]) continue;

        if (mask.getRGB(x, y) != Color.WHITE.getRGB()) continue; // só objeto

        visitado[x][y] = true;

        // Atualiza bounding box
        minX = Math.min(minX, x);
        minY = Math.min(minY, y);
        maxX = Math.max(maxX, x);
        maxY = Math.max(maxY, y);

        // Adiciona vizinhos
        fila.add(new Point(x+1, y));
        fila.add(new Point(x-1, y));
        fila.add(new Point(x, y+1));
        fila.add(new Point(x, y-1));
    }

    return new Rectangle(minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
}

}
