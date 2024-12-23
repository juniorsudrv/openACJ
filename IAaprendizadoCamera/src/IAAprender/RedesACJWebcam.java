/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IAAprender;

import Biblis.MemoryUtils;
import OpIO.IO;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import myNeuronPC.AuxIA;
import myNeuronPC.OpenACJ;
import teste.DecodeAndPlayVideo;
import tratamentos.AnalisaResultImgBufferedImg;

/**
 *
 * @author junior
 */
public class RedesACJWebcam extends javax.swing.JFrame {
    public ArrayList<AnalisaResultImgBufferedImg> anR = new ArrayList<>();
    public ArrayList<AnalisaResultImgBufferedImg> rTreino = new ArrayList<>();

    public TrataImagensCamera trataImagens = new TrataImagensCamera();
    DecodeAndPlayVideo dDecodeAndPlayVideo = new DecodeAndPlayVideo();

    int limitcor = 255;

    Image fundoL = null;
    public BufferedImage fundo = null;

    static byte SIM = 2;
    static byte NAO = -2;
    AuxIA ia = new AuxIA();
    int sizeReal = -1;

    int xI = 0, yI = 0;

    BufferedImage imagem = null;
    Webcam webcam = null;

    String result = "";
    int nresult = 1000;
    boolean coletaid0 = false, coletaid1 = false;
    boolean reconhece = false;
    boolean reconheceSimples = false;
    boolean reconheceMulti = false;
    boolean treinaTempoReal = false;
    boolean calibrando = false;
    File temp = null;
    RecorteAtual rec = null;

    public boolean usarCamera = true;
    public boolean paraCamera = false;

    public RedesACJWebcam() {
        rec = new RecorteAtual();
        rec.setVisible(true);
        initComponents();
       
        temp = new File("temp.png");
//        try {
//            temp.createNewFile();
//        } catch (IOException ex) {
//            Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
//        }
        System.out.println("" + temp.getAbsolutePath());
        new Thread(new Runnable() {
            @Override
            public void run() {

                Webcam fw = null;
                for (Webcam w : Webcam.getWebcams()) {
                    fw = w;

                }

                webcam = fw;
                webcam.setViewSize(WebcamResolution.VGA.getSize());

                treino_progress.setValue(10);
                webcam.open();

                if (ia == null) {
                    ia = new AuxIA();
                }

                largura.setText(ia.xP + "");
                altura.setText(ia.yP + "");

                tamFileira.setText(ia.sizeTam + "");
                neuronios.setText(ia.size + "");

                treino_progress.setValue(20);
                atualizaColetas();
                treino_progress.setValue(60);
                bt_reconhecer.setBackground(Color.YELLOW);

                preencheRedes();
                treino_progress.setValue(100);

                treino_progress.setVisible(false);
                painel.setVisible(true);

                try {
                    rTreino = (ArrayList<AnalisaResultImgBufferedImg>) IO.ler("rTreino");
                } catch (Exception ex) {
                   
                    Logger.getLogger(SetaRegiaoTreino.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        ).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    memoria.setText((int) MemoryUtils.usedMemory() + "  " + MemoryUtils.maxMemory());

                    try {
                        Thread.sleep(8);
                    } catch (InterruptedException ex) {

                    }
                }
            }
        }).start();

        comb_redes.removeAllItems();

        // valor.setText(limitcor + "");
    }

    public static BufferedImage image2BlackWhite(BufferedImage image1) {

        int w = image1.getWidth();
        int h = image1.getHeight();
        byte[] comp = {0, -1};
        IndexColorModel cm = new IndexColorModel(2, 2, comp, comp, comp);
        BufferedImage image2 = new BufferedImage(w, h,
                BufferedImage.TYPE_BYTE_INDEXED, cm);
        Graphics2D g = image2.createGraphics();
        g.drawRenderedImage(image1, null);
        g.dispose();

        return image2;
    }

    private BufferedImage toBinary(BufferedImage image, int t) {
        int BLACK = Color.BLACK.getRGB();
        int WHITE = Color.WHITE.getRGB();

        BufferedImage output = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        // Percorre a imagem definindo na saída o pixel como branco se o valor
        // na entrada for menor que o threshold, ou como preto se for maior.
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color pixel = new Color(image.getRGB(x, y));
                output.setRGB(x, y, pixel.getRed() < t ? BLACK : WHITE);
            }
        }

        return output;
    }

    private BufferedImage toGrayscale(BufferedImage image) {
        BufferedImage output = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = output.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return output;
    }

    public int indexShow = 0;

    ArrayList<Color> cores = new ArrayList<>();

    class prancha_camera_impl extends JPanel {

        BufferedImage local = null;
        boolean setFont = true;
        private static final double CANNY_THRESHOLD_RATIO = .2; //Suggested range .2 - .4
        private static final int CANNY_STD_DEV = 1;

        public prancha_camera_impl() {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    do {
                        if (indexShow < trataImagens.anR.size()) {
                            // indexShow++;
                        } else {
                            indexShow = 0;
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } while (true);

                }
            }).start();
        }

        int vcont = 0;

        @Override
        public void paintComponent(Graphics g) {

            if (getWidth() > 5 && fundo == null) {

                try {
                    fundoL = ImageIO.read(new File("images.jpeg"));
                    fundo = toBufferedImage(fundoL.getScaledInstance(getWidth(), getHeight(), Image.SCALE_DEFAULT));
                } catch (IOException ex) {
                    Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (usarCamera && webcam != null && (local = webcam.getImage()) != null) {

                //imagem = MeuAJC.image2BlackWhiteTest(local);
                //JCanny.CannyEdges(local, 1, 0.55);//  MeuAJC.image2BlackWhiteTest(local) ;
                if (!usarCamera) {

                    imagem
                            = toBufferedImage(fundo.getScaledInstance(g.getClipBounds().width, g.getClipBounds().height, Image.SCALE_AREA_AVERAGING));

                } else {
                    imagem = !paraCamera
                            ? //toBufferedImage(local.getScaledInstance(g.getClipBounds().width, g.getClipBounds().height, Image.SCALE_AREA_AVERAGING))
                            local
                            : imagem;
                }

                g.drawImage(reconheceMulti || treinaTempoReal ? imagem : trataImagens.checaImagem(imagem), 0, 0, pracha_camera);

            } else {

                imagem = !paraCamera ? toBufferedImage(fundo.getScaledInstance(pracha_camera.getWidth(), pracha_camera.getHeight(), Image.SCALE_AREA_AVERAGING)) : imagem;

                g.drawImage(reconheceMulti || treinaTempoReal ? imagem : trataImagens.checaImagem(imagem), 0, 0, pracha_camera);
            }

            try {
                if (reconhece || reconheceSimples) {
                    g.setFont(new Font(Font.SERIF, Font.BOLD, 18));
                    g.setColor(((nresult == -1) ? Color.red
                            : getCor(nresult)));

                    g.drawString(resultado.getText(), xI, yI - 20);

                } else {
                    g.setColor(Color.DARK_GRAY);
                }

                if (!reconheceMulti) {
                    g.setColor(Color.black);
                    g.drawRect(xI, yI, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));

                }
                for (int cont = 0; cont < anR.size(); cont++) {

                    AnalisaResultImgBufferedImg an = trataImagens.anR.get(cont);

                    g.setColor(an.cor);
                    g.drawString(an.iIndexResult + "_" + an.acertosResult, an.xR, an.yR - 10);
                    g.drawString(an.result + "", an.xR, an.yR - 25);
                    g.drawRect(an.xR, an.yR, an.lR, an.aR);
                    g.drawString(an.result + "", an.xR, an.yR + an.aR + 15);

                }

                if (!reconheceMulti) {

                    for (int cont = 0; cont < rTreino.size(); cont++) {

                        AnalisaResultImgBufferedImg an = rTreino.get(cont);

                        g.setColor(an.cor);
                        g.drawString(an.iIndexResult + "_" + an.acertosResult, an.xR, an.yR - 10);
                        g.drawString(an.nomeRec + "", an.xR, an.yR - 25);
                        g.drawRect(an.xR, an.yR, an.lR, an.aR);
                        g.drawString(an.result + "", an.xR, an.yR + an.aR + 15);

                    }
                }

//               // for (int cont = 0; cont < anR.size(); cont++) 
//                {
//
//                    analisaResultImgBufferedImg an = anR.get(indexShow);
//
//                    g.setColor(an.cor);
//                    g.drawString(an.iIndexResult + "_" + an.acertosResult, an.xR, an.yR - 10);
//                    g.drawRect(an.xR, an.yR, an.lR, an.aR);
//
//                }
//                 
//                           g.drawImage(rotateImageByDegrees(imagem.getSubimage(xI, yI, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText())),
//                                         vcont++), 0, 0, rec)  ;
//                                
//                             
//                             if(vcont>360)vcont=0;
//                for (int cont = 0; cont <= 25; cont += 5) {
//                    g.drawRect(xI + cont, yI + cont, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));
//                    g.drawRect(xI + cont, yI, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));
//                    g.drawRect(xI, yI + cont, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));
//                    g.drawRect(xI - cont, yI - cont, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));
//                    g.drawRect(xI - cont, yI, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));
//                    g.drawRect(xI, yI - cont, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));
//
//                }
//        int xqMin = Integer.parseInt(largura.getText()) / (ia.qtdQdRec / ia.bdiv);
//        int yqMin = Integer.parseInt(altura.getText()) / (ia.qtdQdRec / ia.bdiv);
//
//        int xIIC = 0;
//        int yIIC = 0;
//
//        xqMin = xqMin - 1;
//
//        for (int cont = 0; cont < ia.qtdQdRec; cont++) {
//
//         g.drawRect(xIIC+xI, yI+yIIC, xqMin, yqMin);
//            
//
//            xIIC += xqMin;
//
//            if (xIIC + xqMin  > Integer.parseInt(largura.getText()) ) {
//                yIIC += yqMin;
//                xIIC = 0;
//
//                if (yIIC + yqMin >  Integer.parseInt(altura.getText()) ) {
//
//                    yIIC = yIIC - ((yIIC + yqMin) -  Integer.parseInt(altura.getText()));
//
//                }
//            }
//
//        }
//                for (float cont = 0.05f; cont < 0.40f; cont += 0.05f) {
//
//                    if (0.05f == cont) {
//                        geraCorteMetade(g);
//                    }
//                    geraCortes(cont, g);
//                    geraCortesX0(cont, g);
//                }
            } catch (Exception e) {
            }

            pracha_camera.repaint();

        }

    }

    public BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public void preencheRedes() {
        comb_redes.removeAllItems();

        for (String s : ia.valuesPossible) {

            comb_redes.addItem(s);
        }
    }

    public Color getCor(int index) {

        if (index >= cores.size()) {

            cores.add(trataImagens.gerarCorAleatoriamente());
        }
        return cores.get(index);
    }

    OpenACJ acj = new OpenACJ(40);

    byte saidI = -2;
    boolean treinoa = false;

    boolean cali = false;
    int inc = 1;

    int nivelBrilho = 300;

    public BufferedImage checaImagem(BufferedImage image) {

        if (!treinoa) {
            return image;
        }
        int GREEN = Color.GREEN.getRGB();

        BufferedImage output = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_INT_RGB);

        int lWidth = image.getWidth();
        int lHeight = image.getHeight();

        for (int y = 0; y < image.getHeight(); y += inc) {

            for (int x = 0; x < image.getWidth(); x += inc) {

                int rgb = image.getRGB(x, y);

                if (!isFundo(x, y, rgb)) {
                    output.setRGB(x, y, GREEN);
                } else {
                    output.setRGB(x, y, rgb);
                }

            }

        }

        acj.TrainingNewOpenACJ(2, 2);
        return output;
    }

    public ArrayList<int[][]> pixels = new ArrayList();

    public boolean isFundo(int x, int y, int pixel) {

        Color cp = new Color(pixel);
        int dr = cp.getRed();
        int dg = cp.getGreen();
        int db = cp.getBlue();

        return Math.sqrt(0.299 * dr * dr + 0.587 * dg * dg + 0.114 * db * db) > nivelBrilho;
    }

    public int calibrarBrancoPixels(BufferedImage image) {

        for (int y = 0; y < image.getHeight(); y += inc) {

            for (int x = 0; x < image.getWidth(); x += inc) {

                Color cp = new Color(image.getRGB(x, y));
                int dr = cp.getRed();
                int dg = cp.getGreen();
                int db = cp.getBlue();
                if (nivelBrilho > Math.sqrt(0.299 * dr * dr + 0.587 * dg * dg + 0.114 * db * db)) {
                    nivelBrilho = (int) Math.sqrt(0.299 * dr * dr + 0.587 * dg * dg + 0.114 * db * db);
                }

            }

        }

        return nivelBrilho;

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pracha_camera = new prancha_camera_impl();
        painel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        largura = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        altura = new javax.swing.JTextField();
        memoria = new javax.swing.JTextField();
        bt_reconhecer = new javax.swing.JButton();
        resultado = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        bt_reconhecer1 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        treino_progress = new javax.swing.JProgressBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        progresssos = new javax.swing.JPanel();
        totalObjetos = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton20 = new javax.swing.JButton();
        comb_redes = new javax.swing.JComboBox<>();
        numero_coletaid1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        bt_treinar1 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jSlider1 = new javax.swing.JSlider();
        jButton13 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        valueCalibrado = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        tamFileira = new javax.swing.JTextField();
        neuronios = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pracha_camera.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pracha_camera.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                pracha_cameraMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout pracha_cameraLayout = new javax.swing.GroupLayout(pracha_camera);
        pracha_camera.setLayout(pracha_cameraLayout);
        pracha_cameraLayout.setHorizontalGroup(
            pracha_cameraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1022, Short.MAX_VALUE)
        );
        pracha_cameraLayout.setVerticalGroup(
            pracha_cameraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 536, Short.MAX_VALUE)
        );

        painel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        largura.setText("100");

        jLabel1.setText("Largura");

        jLabel2.setText("Altura");

        altura.setText("100");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel1)
                .addGap(4, 4, 4)
                .addComponent(largura, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(altura, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(largura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(altura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bt_reconhecer.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        bt_reconhecer.setText("RECONHECER OBJETOS");
        bt_reconhecer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_reconhecerActionPerformed(evt);
            }
        });

        jButton5.setText("Reiniciar Neuronios");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton7.setText("Usar Camera");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("Usar Imagem");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        bt_reconhecer1.setText("Reconhecer Simples");
        bt_reconhecer1.setEnabled(false);
        bt_reconhecer1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_reconhecer1ActionPerformed(evt);
            }
        });

        jButton6.setText("Carregar Video");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton10.setText("S");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText("P");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton14.setText("Parar camera");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout painelLayout = new javax.swing.GroupLayout(painel);
        painel.setLayout(painelLayout);
        painelLayout.setHorizontalGroup(
            painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelLayout.createSequentialGroup()
                .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(painelLayout.createSequentialGroup()
                        .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(memoria, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(treino_progress, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(painelLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton5, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, painelLayout.createSequentialGroup()
                                        .addComponent(jButton10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton11)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton14))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, painelLayout.createSequentialGroup()
                                        .addComponent(jButton7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton8))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, painelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(bt_reconhecer1)
                                .addGap(31, 31, 31))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, painelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(bt_reconhecer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resultado, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        painelLayout.setVerticalGroup(
            painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(painelLayout.createSequentialGroup()
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton8)
                            .addComponent(jButton7)))
                    .addComponent(treino_progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(8, 8, 8)
                .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(painelLayout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(bt_reconhecer1))
                    .addGroup(painelLayout.createSequentialGroup()
                        .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton10)
                                .addComponent(jButton11)
                                .addComponent(jButton14)
                                .addComponent(jButton6)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(memoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resultado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bt_reconhecer))
                .addContainerGap())
        );

        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        progresssos.setLayout(new javax.swing.BoxLayout(progresssos, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane1.setViewportView(progresssos);

        totalObjetos.setText(" ");

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jButton20.setText("Criar Area Treino");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        comb_redes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comb_redes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comb_redesActionPerformed(evt);
            }
        });

        numero_coletaid1.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        numero_coletaid1.setText("0");
        numero_coletaid1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButton1.setText("Add RedeN");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton21.setText("Limpar Treino");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        bt_treinar1.setText("Treinar Tempo Real Camera");
        bt_treinar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_treinar1ActionPerformed(evt);
            }
        });

        jButton17.setText("pegarObjeto");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jSlider1.setMaximum(255);
        jSlider1.setValue(45);
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        jButton13.setText("Calibrar Fundo");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton19.setText("Reiniciar Fundo");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton3.setText("Salvar Neuronios");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        valueCalibrado.setText("45");

        jButton9.setText("Carregar Rede Salva");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton2.setText("Del RedeN");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel3.setText("Neuronios");

        tamFileira.setText("2");
        tamFileira.setMinimumSize(new java.awt.Dimension(100, 22));

        neuronios.setText("2");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton21, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(comb_redes, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(0, 95, Short.MAX_VALUE)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tamFileira, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(neuronios, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(numero_coletaid1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(valueCalibrado, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton9))
                            .addComponent(bt_treinar1, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(jButton20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(comb_redes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(tamFileira, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(neuronios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(numero_coletaid1)))
                    .addComponent(jButton21, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(bt_treinar1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton17)
                    .addComponent(jButton13)
                    .addComponent(jButton19)
                    .addComponent(jButton3)
                    .addComponent(jButton9))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(valueCalibrado)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(painel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pracha_camera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalObjetos, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pracha_camera, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(totalObjetos))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(painel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(15, 15, 15))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pracha_cameraMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pracha_cameraMouseReleased

        if (evt.getButton() == MouseEvent.BUTTON3) {

            largura.setText((evt.getX() - xI) + "");
            altura.setText((evt.getY() - yI) + "");

        } else if (evt.getX() + Integer.parseInt(largura.getText()) <= imagem.getWidth()
                && evt.getY() + Integer.parseInt(altura.getText()) <= imagem.getHeight()) {

            xI = evt.getX();
            yI = evt.getY();

        }


    }//GEN-LAST:event_pracha_cameraMouseReleased

    private void bt_reconhecerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_reconhecerActionPerformed

        reconheceMulti = !reconheceMulti;

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (reconheceMulti) {

                    BufferedImage b = trataImagens.checaImagem(imagem);

                    trataImagens.pegaObjetos_Novo(b, imagem);

                    for (int cont = 0; cont < trataImagens.anR.size(); cont++) {

                        BufferedImage bimg = trataImagens.anR.get(cont).imgRec;

                        rec.setImagem_recorte(bimg);

                        int result[] = ia.getResultCont(bimg);

                        System.out.println(" result " + result[1] + " " + result[0] + " " + comb_redes.getSelectedIndex());

                        trataImagens.anR.get(cont).result = comb_redes.getItemAt(result[0]);
                        trataImagens.anR.get(cont).acertosResult = result[1];

                    }
                    
                 

                    try {
                        Thread.sleep(1000);

                    } catch (InterruptedException ex) {
                        Logger.getLogger(RedesACJWebcam.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                    
                      anR = new ArrayList<>( trataImagens.anR);;

                }

            }
        }).start();

        if (reconheceMulti) {

            bt_reconhecer.setText("Reconhecendo!");
            bt_reconhecer.setBackground(Color.GREEN);
        } else {
            bt_reconhecer.setText("Reconhecer!");
            bt_reconhecer.setBackground(Color.YELLOW);
            nresult = -1;

        }


    }//GEN-LAST:event_bt_reconhecerActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        AuxIA ial = new AuxIA();
        ial.valuesPossible = ia.valuesPossible;
        ia = ial;
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        JFileChooser fc = new JFileChooser(".");
        int result = fc.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
//
//            try {
//
//                ImageIO.write(ImageIO.read(fc.getSelectedFile()),
//                        "png", new File("images.jpeg"));
//
//            } catch (IOException ex) {
//                Logger.getLogger(RedesACJWebcam.class
//                        .getName()).log(Level.SEVERE, null, ex);
//            }

        }
        try {
            Image fundoL = ImageIO.read(fc.getSelectedFile());
            fundo = toBufferedImage(fundoL.getScaledInstance(pracha_camera.getWidth(), pracha_camera.getHeight(), Image.SCALE_DEFAULT));

            usarCamera = false;

        } catch (IOException ex) {
            Logger.getLogger(RedesACJWebcam.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:

        usarCamera = true;

    }//GEN-LAST:event_jButton7ActionPerformed

    private void bt_reconhecer1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_reconhecer1ActionPerformed
        reconheceSimples = !reconheceSimples;

        new Thread(new Runnable() {
            @Override
            public void run() {

                do {
                    if (reconheceSimples) {

                        BufferedImage bimg = imagem.getSubimage(xI, yI,
                                Integer.valueOf(largura.getText()), Integer.valueOf(altura.getText()));

                        rec.setImagem_recorte(bimg);

                        int result[] = ia.getResultCont(bimg);

                        System.out.println(" result " + result[1] + " " + result[0] + " " + comb_redes.getSelectedIndex());

                        resultado.setText(comb_redes.getItemAt(result[0]));
                        nresult = result[0];

                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } while (reconheceSimples);

            }
        }).start();

        if (reconheceSimples) {

            bt_reconhecer1.setText("Reconhecendo!");
            bt_reconhecer1.setBackground(Color.GREEN);
        } else {
            bt_reconhecer1.setText("Reconhecer!");
            bt_reconhecer1.setBackground(Color.YELLOW);
            nresult = -1;
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_bt_reconhecer1ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        usarCamera = false;
        new Thread(new Runnable() {
            @Override
            public void run() {

                JFileChooser fileChooser = new JFileChooser();

                fileChooser.showOpenDialog(null);

                try {
                    dDecodeAndPlayVideo.playVideo(RedesACJWebcam.this, fileChooser.getSelectedFile().getAbsolutePath());

// TODO add your handling code here:
                } catch (InterruptedException ex) {
                    Logger.getLogger(RedesACJWebcam.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (IOException ex) {
                    Logger.getLogger(RedesACJWebcam.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

            }
        }).start();

    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        dDecodeAndPlayVideo.setStop(true);        // TODO add your handling code here:
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        dDecodeAndPlayVideo.setStop(false);        // TODO add your handling code here:
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
         // TODO add your handling code here:

        paraCamera = !paraCamera;

        if (paraCamera) {

            jButton14.setText("Voltar camera");
        } else {

            jButton14.setText("Parar camera");
        }

    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        ia.valuesPossible.remove(comb_redes.getSelectedItem());

        preencheRedes();
        //  salvarREDE();

        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        try {
            treino_progress.setValue(20);
            ia = (AuxIA) IO.ler("minharede");
        } catch (Exception e) {

            e.printStackTrace();
        }

        if (ia == null) {
            ia = new AuxIA();
        }

        preencheRedes();
        treino_progress.setValue(100);

        // TODO add your handling code here:
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        salvarREDE();
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed

        trataImagens.pixels.clear();

    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        calibrando = !calibrando;

        new Thread(new Runnable() {
            @Override
            public void run() {

                do {

                    trataImagens.calibrarBrancoPixels(imagem);

                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } while (calibrando);

            }
        }).start();

        if (calibrando) {

            jButton13.setText("Calibrando!");
            jButton13.setBackground(Color.GREEN);
        } else {
            jButton13.setText("Calibrar!");
            jButton13.setBackground(Color.YELLOW);

        }

    }//GEN-LAST:event_jButton13ActionPerformed

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged

        JSlider source = (JSlider) evt.getSource();
        if (!source.getValueIsAdjusting()) {
            //textField.setText(String.valueOf(source.getValue()));
            int power = source.getValue();

            trataImagens.mliar = power;

            valueCalibrado.setText(power + "");
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_jSlider1StateChanged

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed

        coletaid0 = !coletaid0;
        new Thread(new Runnable() {
            @Override
            public void run() {

                do {

                    try {
                        BufferedImage b = trataImagens.checaImagem(imagem);
                        ImageIO.write((b), "png", new File("saida.png"));
                        trataImagens.pegaObjetos_Novo(b, imagem);
                        totalObjetos.setText(trataImagens.anR.size() + "");
                        System.out.println("Terminou ");
                    } catch (IOException ex) {
                        Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } while (coletaid0);

            }
        }).start();

        if (coletaid0) {

            jButton17.setText("Pegando Objetos!");
            jButton17.setBackground(Color.GREEN);
        } else {
            jButton17.setText("PEgar objeto !");
            jButton17.setBackground(Color.YELLOW);
            nresult = -1;

        }
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton17ActionPerformed

    private void bt_treinar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_treinar1ActionPerformed
        // TODO add your handling code here:

        treino_progress.setValue(0);
        treinaTempoReal = !treinaTempoReal;

        if (treinaTempoReal) {
            bt_treinar1.setBackground(Color.GREEN);
            bt_treinar1.setText("Treinando ...");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean primeiro = true;
                    while (treinaTempoReal) {
                        for (int t = 0; t < rTreino.size(); t++) {

                            AnalisaResultImgBufferedImg rt = rTreino.get(t);
                            javax.swing.JProgressBar treino_progress = rt.treino_progress;
                            JLabel jl = rt.jl;
                            JLabel jlr = rt.jlr;
                            if (primeiro) {
                                progresssos.add(jl);
                                progresssos.add(jlr);
                                progresssos.add(treino_progress);
                            }
                            jl.setText("Treinando ...");

                            BufferedImage bimg = imagem.getSubimage(rt.xR, rt.yR,
                                rt.lR, rt.aR);

                            rec.setImagem_recorte(bimg);

                            int result[] = ia.getResultCont(bimg);

                            System.out.println(" result " + result[0] + " " + rt.iIndexResult + " " + rt.nomeRec);

                            if (rt.iIndexResult == result[0] && result[1] >= ia.qtdQdRec ) {

                                // resultado.setText(comb_redes.getItemAt(result[0]));
                                nresult = ia.nresult;

                                jlr.setText(comb_redes.getItemAt(result[0]));

                                treino_progress.setValue(100);
                            } else {

                                jlr.setText("---------------");

                                for (int cont = 0; cont < comb_redes.getItemCount(); cont++) {

                                    bimg = imagem.getSubimage(rt.xR, rt.yR,
                                        rt.lR, rt.aR);

                                    //                                    try {
                                        //                                        ImageIO.write(bimg, "png", new File("image__0.png"));
                                        //                                    } catch (IOException ex) {
                                        //                                        Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
                                        //                                    }
                                    ia.setValTrainningByteAll(cont, bimg, cont == rt.iIndexResult ? SIM : NAO);

                                }

                                ia.trainning(treino_progress, Integer.valueOf(tamFileira.getText()), Integer.valueOf(neuronios.getText()));

                            }

                            try {
                                Thread.sleep(100);

                            } catch (InterruptedException ex) {
                                Logger.getLogger(RedesACJWebcam.class
                                    .getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                        primeiro = false;

                    }

                    for (int t = 0; t < rTreino.size(); t++) {

                        AnalisaResultImgBufferedImg rt = rTreino.get(t);
                        javax.swing.JProgressBar treino_progress = rt.treino_progress;
                        JLabel jl = rt.jl;
                        JLabel jlr = rt.jlr;

                        progresssos.remove(jl);
                        progresssos.remove(jlr);
                        progresssos.remove(treino_progress);

                    }

                    progresssos.repaint();

                    bt_treinar1.setEnabled(true);
                    bt_treinar1.setText("Parou Treinar Tempo Real");
                    bt_treinar1.setBackground(Color.YELLOW);

                }
            }).start();

        }
        if (treinaTempoReal) {

            // bt_treinar1.setEnabled(false);
            bt_treinar1.setBackground(Color.GREEN);
        } else {
            bt_treinar1.setEnabled(true);
            bt_treinar1.setText("Parou Treinar Tempo Real");
            bt_treinar1.setBackground(Color.YELLOW);
            nresult = -1;
        }
    }//GEN-LAST:event_bt_treinar1ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        rTreino.clear();
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        String n1 = JOptionPane.showInputDialog("Digite o nome da rede");

        ia.valuesPossible.add(n1);
        preencheRedes();

        // salvarREDE();
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void comb_redesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comb_redesActionPerformed
        atualizaColetas_Item_Rede();

        // TODO add your handling code here:
    }//GEN-LAST:event_comb_redesActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        BufferedImage bimg = imagem.getSubimage(xI, yI,
            Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));

        new SetaRegiaoTreino(xI, yI, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()),
            comb_redes, rTreino, bimg).setVisible(true);

    }//GEN-LAST:event_jButton20ActionPerformed

    public void atualizaColetas() {

        try {
            numero_coletaid1.setText(new File("IMG" + comb_redes.getSelectedItem()).listFiles().length + "");
        } catch (Exception e) {
            // e.printStackTrace();
            numero_coletaid1.setText("0");
        }
    }

    public void atualizaColetas_Item_Rede() {

        atualizaColetas();
    }

    public void iniciaNr(int nrede) {

    }

    public void salvarREDE() {

        try {
            //ia.limpaDadosTreino();
            ia.xP = Integer.valueOf(largura.getText());
            ia.yP = Integer.valueOf(altura.getText());

            trataImagens.anR = new ArrayList<>();
            IO.inserir("minharede", ia);

        } catch (IOException ex) {
            Logger.getLogger(RedesACJWebcam.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        JOptionPane.showMessageDialog(null, "Salvo");
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
        g2d.drawImage(img, 0, 0, this);
        g2d.dispose();

        return rotated;
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(RedesACJWebcam.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RedesACJWebcam.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RedesACJWebcam.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RedesACJWebcam.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RedesACJWebcam().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField altura;
    private javax.swing.JButton bt_reconhecer;
    private javax.swing.JButton bt_reconhecer1;
    private javax.swing.JButton bt_treinar1;
    private javax.swing.JComboBox<String> comb_redes;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JTextField largura;
    private javax.swing.JTextField memoria;
    private javax.swing.JTextField neuronios;
    private javax.swing.JLabel numero_coletaid1;
    private javax.swing.JPanel painel;
    public javax.swing.JPanel pracha_camera;
    private javax.swing.JPanel progresssos;
    private javax.swing.JTextField resultado;
    private javax.swing.JTextField tamFileira;
    private javax.swing.JLabel totalObjetos;
    private javax.swing.JProgressBar treino_progress;
    private javax.swing.JLabel valueCalibrado;
    // End of variables declaration//GEN-END:variables
}
