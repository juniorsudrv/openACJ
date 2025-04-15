# Link de tutorial abaixo 
Operacoes Básicas https://youtu.be/NRuFDO2clg4 

Treinar a rede neural utilizando um software https://www.youtube.com/watch?v=QumrLZcZ8Lc 

Contando peças de xadrez e dama https://www.youtube.com/watch?v=j8gtOoU2JCc


Links jars https://github.com/juniorsudrv/openACJ/issues/1

Exemplo treinando algumas imagens que estão dentro de pastas

        //Código exemplo, não pode haver duas imagens iguais (muito parecidas) nas pastas de treino, irá causar um loop infinito
        //Comece com imagens simples, se for uma imagem de bicicleta ela deve ser apeans isso, não deve haver outros objetos juntos
 
        AuxIA ia = new AuxIA();

        //Pasta contendo imagens para treino neste caso letra X
        File imgsX[] = new File("E:\\Imgs0").listFiles();
        //Pasta contendo imagens para treino neste caso letra O
        File imgs0[] = new File("E:\\Imgs1").listFiles();

        //Pasta contendo imagens para treino neste caso letra W
        File imgsW[] = new File("E:\\Imgs2").listFiles();

        //Pasta com letra X de teste
        File imgstest[] = new File("E:\\Imgstest").listFiles();

        for (int cont = 0; cont < imgsX.length; cont++) {
            System.out.println(" " + imgsX[cont].getPath());
            //O campo index deve ser associado ao objeto que sera treinado, 
            //o valor e incrementado quando se treina um novo objeto, quando se treina um objeto que já exista utiliza se o valor original 
            //O ultimo campo deve ser sim apenas pra imagem associada ao index atual o resto deve ser não
            ia.setValTrainningByteAll(0, ImageIO.read(imgsX[cont]), SIM);
            ia.setValTrainningByteAll(1, ImageIO.read(imgsX[cont]), NAO);
            ia.setValTrainningByteAll(2, ImageIO.read(imgsX[cont]), NAO);
        }

        for (int cont = 0; cont < imgs0.length; cont++) {
            System.out.println(" " + imgs0[cont].getPath());
            ia.setValTrainningByteAll(0, ImageIO.read(imgs0[cont]), NAO);
            ia.setValTrainningByteAll(1, ImageIO.read(imgs0[cont]), SIM);
            ia.setValTrainningByteAll(2, ImageIO.read(imgs0[cont]), NAO);
        }
        
        
           for (int cont = 0; cont < imgsW.length; cont++) {
            System.out.println(" " + imgsW[cont].getPath());
            ia.setValTrainningByteAll(0, ImageIO.read(imgsW[cont]), NAO);
            ia.setValTrainningByteAll(1, ImageIO.read(imgsW[cont]), NAO);
            ia.setValTrainningByteAll(2, ImageIO.read(imgsW[cont]), SIM);
        }

        ia.trainning(null, 2, 2);

        int result[] = ia.getResultCont(ImageIO.read(imgs0[0]));

        System.out.println("Result " + (result[0] == 0 ? "X" : result[0] == 1 ? "0" : "W"));

