/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package teste;

import java.awt.Rectangle;
import java.util.ArrayList;
import tratamentos.AnalisaResultImgBufferedImg;

/**
 *
 * @author junio
 */
public class Testes {
    
    
    public void removeDuplicatas(ArrayList<AnalisaResultImgBufferedImg> anR ){
        
    
        anR.add(new AnalisaResultImgBufferedImg(null, 1, 1, 3, 3, null));
        anR.add(new AnalisaResultImgBufferedImg(null, 3, 3, 3, 3, null));

        anR.add(new AnalisaResultImgBufferedImg(null, 6, 6, 2, 2, null));

        for (int cont = 0; cont < anR.size(); cont++) {

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
                    
                    anR.remove(temp.get(cI));

                }

                anR.get(cont).yR = y;
                anR.get(cont).xR = x;
                anR.get(cont).aR = yH;
                anR.get(cont).lR = xW;

            }

        }

        for (int cI = 0; cI < anR.size(); cI++) {

            System.out.println("" + anR.get(cI).toString());
        }

    }

    public static void main(String[] args) {

       
    }

}
