/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package redeneuralimagens;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import myNeuronPC.AuxIA;

/**
 *
 * @author junio
 */
public class RedeNeuralImagens {

    public static void main(String[] args) throws IOException {

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
                    auxia.setValTrainningByte(count, ImageIO.read(img), auxia.valuesPossible.get(count).contentEquals(folderOK) ? yes : no);
                }

            }

        }
        System.out.println("Start Trainning");
        auxia.trainningStarter(null, 2, 2);
        System.out.println("Finish Trainning");
        
        System.out.println("Result "+auxia.getResult(ImageIO.read(new File("PastaValid").listFiles()[0])));
        System.out.println("Result "+auxia.getResult(ImageIO.read(new File("PastaValid").listFiles()[1])));

    }

}
