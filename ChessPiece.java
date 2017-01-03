/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imageclassification;


import javafx.scene.image.Image;
import java.awt.image.BufferedImage;

/**
 *
 * @author Paypa
 */
public class ChessPiece {
    
    int id;
    Image img;
    String name;
    BufferedImage bImg;
    BufferedImage bGrayImage;
    byte[][] grayByteData;
    byte[][] grayByteDataEroded;
    byte[][] perimeterByteData;
    //byte[][] grayByteDataDilated;
    double[] imageData;//The image data into a 1-D array
    
    //Features
    int area;
    int perimeter;
    int compactness;
    int length;
    
    
}
