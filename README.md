# Link de tutorial abaixo 
Operacoes Básicas https://youtu.be/NRuFDO2clg4 

Treinar a rede neural utilizando um software https://www.youtube.com/watch?v=QumrLZcZ8Lc 

Contando peças de xadrez e dama https://www.youtube.com/watch?v=j8gtOoU2JCc


Links jars https://github.com/juniorsudrv/openACJ/issues/1


Operações básicas com numeros 

        byte out2 = -2;
        byte outI2 = 2;

        OpenACJ acj = new OpenACJ(4);
        acj.setValForTraining(0, outI2);
        acj.setValForTraining(1, outI2);
        acj.setValForTraining(2, out2);
        acj.setValForTraining(3, out2);

        acj.TrainingNewOpenACJ(2, 2);

        float saida = acj.outNeuronCompletResult(acj.getValueTestBits(
                0));

        System.out.println(""
                + saida);
        saida = acj.outNeuronCompletResult(acj.getValueTestBits(
                1));

        System.out.println(""
                + saida);
        saida = acj.outNeuronCompletResult(acj.getValueTestBits(
                2));

        System.out.println(""
                + saida);
        saida = acj.outNeuronCompletResult(acj.getValueTestBits(
                3));

        System.out.println(""
                + saida);


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



                
Exemplo simples de treino de imagens em diversas pastas 

        //Código exemplo, não pode haver duas imagens iguais (muito parecidas) nas pastas de treino, irá causar um loop infinito
        //Comece com imagens simples, se for uma imagem de bicicleta ela deve ser apeans isso, não deve haver outros objetos juntos
        byte yes = 2, no = -2;

        AuxIA auxia = new AuxIA();

        String[] folderTrainn = {"PastaX", "PastaO", "PastaW"};

        for (String folderOK : folderTrainn) {
            auxia.valuesPossible.add(folderOK);
        }
        for (String folderOK : folderTrainn) {
            File imgs = new File(folderOK);

            for (int count = 0; count < auxia.valuesPossible.size(); count++) {
                for (File img : imgs.listFiles()) {
                    System.out.println(count + " " + img.getName() + " " + auxia.valuesPossible.get(count) + " "
                            + (auxia.valuesPossible.get(count).contentEquals(folderOK) ? yes : no));
                    auxia.setValTrainningByteAll(count, ImageIO.read(img), auxia.valuesPossible.get(count).contentEquals(folderOK) ? yes : no);
                }

            }

        }
        System.out.println("Start Trainning");
        auxia.trainningStarter(null, 2, 2);
        System.out.println("Finish Trainning");
        
        System.out.println("Result "+auxia.getResult(ImageIO.read(new File("PastaValid").listFiles()[0])));
        System.out.println("Result "+auxia.getResult(ImageIO.read(new File("PastaValid").listFiles()[1])));










