/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tratamentos;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 *
 * @author junio
 */
public class BuffImage extends BufferedImage implements Serializable{
    
    public BuffImage(int width, int height, int imageType) {
        super(width, height, imageType);
    }
    
}
