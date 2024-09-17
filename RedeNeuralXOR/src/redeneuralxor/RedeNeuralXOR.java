/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package redeneuralxor;

import myNeuronPC.OpenACJ;

/**
 *
 * @author junio
 */
public class RedeNeuralXOR {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        byte out2 = -2;
        byte outI2 = 2;

        OpenACJ acj = new OpenACJ(2);
        acj.setValForTraining(0, outI2);
        acj.setValForTraining(1, outI2);
        acj.setValForTraining(2, outI2);
        acj.setValForTraining(3, outI2);
        
        acj.TrainingNewOpenACJ(2, 2);
        
        float saida= acj.outNeuronCompletResult(acj.getValueTestBits(
                        0));
        
        System.out.println(""+
              saida );
        saida= acj.outNeuronCompletResult(acj.getValueTestBits(
                        1));
        
        System.out.println(""+
              saida );
        saida= acj.outNeuronCompletResult(acj.getValueTestBits(
                        2));
        
        System.out.println(""+
              saida );
        saida= acj.outNeuronCompletResult(acj.getValueTestBits(
                        3));
        
        System.out.println(""+
              saida );
        

    }

}
