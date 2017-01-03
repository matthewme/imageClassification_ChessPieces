/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imageclassification;
import java.awt.image.BufferedImage;
import java.io.File;
/**
 *
 * @author Matthew Martinez
 * Instructor: Dr. Quweider
 * Course: CSCI 4301
 */
public class ImageClassification {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        File[] files = new File("D:\\OneDrive\\Documents\\NetBeansProjects\\ImageClassification\\chessPieces").listFiles();
      
       int totalPics = countFilesOnly(files,0);//Get the amount of files in the DB 
        
       ChessPiece[] cPieces = new ChessPiece[totalPics];//Create class to hold the pictures
       
       int[][] centroids =  new int[4][6];
       
        //Read the chest pieces
        showFiles(files,0,cPieces, 0);
        
        Morphology m = new Morphology();
        
        //Get Features
        for(int i = 0; i < cPieces.length; i++)
        {
            cPieces[i].grayByteData = m.dilation(cPieces[i].grayByteData, m,1);//Dilate
            cPieces[i].grayByteData = m.dilation(cPieces[i].grayByteData, m,1);//Dilate
            cPieces[i].grayByteDataEroded = m.erosion(cPieces[i].grayByteData, m,1);//Erode
            cPieces[i].grayByteDataEroded = m.erosion(cPieces[i].grayByteData, m,1);//Erode
            getArea(cPieces[i]);
            getPerimeter(cPieces[i]);
            getCompactness(cPieces[i]);       
            getLength(cPieces[i]);
        }
       
        //Calculate centroids
        int start = 0;
        for(int i  = 0; i < 6; i++)
        {
            
            int avg = 0;
            int totalArea = 0;
            int totalPerimeter = 0;
            int totalCompactness = 0;        
            int totalLength = 0;
            
            for(int j = start; j < start+10; j++)
            {
                totalArea += cPieces[j].area;
                totalPerimeter += cPieces[j].perimeter;
                totalCompactness += cPieces[j].compactness;             
                totalLength += cPieces[j].length;
            }
            
            avg = totalArea/10;
            centroids[0][i] = avg;
            avg = 0;
            avg = totalPerimeter/10;
            centroids[1][i] = avg;
            avg=0;
            avg = totalCompactness/10;
            centroids[2][i] = avg;           
            avg=0;
            avg = totalLength/10;
            centroids[3][i] = avg;           
            start+= 10;
        }
        
        //Read a new image
        ChessPiece newPiece = new ChessPiece();
       
        BufferedImage testImage = ImageIo.readImage("testKing.png");
        newPiece.bGrayImage = ImageIo.toGray(testImage);//Convert to Gray
        newPiece.grayByteData = ImageIo.getGrayByteImageArray2DFromBufferedImage(newPiece.bGrayImage);//Extract gray byte data
        ImageIo.thresholdReverse(newPiece.grayByteData,128);//Threshold
        
        //Extract Features
        newPiece.grayByteData = m.dilation(newPiece.grayByteData, m,1);//Dilate
        newPiece.grayByteDataEroded = m.erosion(newPiece.grayByteData, m,1);//Erode
        getArea(newPiece);
        getPerimeter(newPiece);
        getCompactness(newPiece);
        getLength(newPiece);
        
        int[][] newCentroid = {{newPiece.area},
                                {newPiece.perimeter},
                                {newPiece.compactness},
                                {newPiece.length}};
        
        classify(centroids, newCentroid,newPiece);
        
          //----------------------------------------------------------      
//        for(int i = 0; i < 60; i++)
//        {
//            System.out.println(cPieces[i].perimeter);
//        }
//        
//        System.out.println(newPiece.area);
//        System.out.println(newPiece.perimeter);
//        System.out.println(newPiece.compactness);
//        System.out.println(newPiece);            
//        int num = 50;
//        cPieces[num].grayByteData = m.dilation(cPieces[num].grayByteData, m,5);
//        cPieces[num].grayByteDataEroded = m.erosion(cPieces[num].grayByteData, m,10);
//        getArea(cPieces[0]);
//        System.out.println("The AREA: " + cPieces[0].area);
//        getPerimeter(cPieces[0]);
//        System.out.println("The Perimeter: " + cPieces[0].perimeter);
//        getCompactness(cPieces[0]);
//        System.out.println("The compactness: " + cPieces[0].compactness);
//       
//        BufferedImage outImage = ImageIo.setGrayByteImageArray2DToBufferedImage(cPieces[0].grayByteData);
//        ImageIo.writeImage(outImage, "png", "test.png"); 
//        
//        BufferedImage outImage1 = ImageIo.setGrayByteImageArray2DToBufferedImage(cPieces[0].grayByteDataEroded);
//        ImageIo.writeImage(outImage1, "png", "test1.png");      
//        BufferedImage perimeter = ImageIo.setGrayByteImageArray2DToBufferedImage(cPieces[50].perimeterByteData);
//        ImageIo.writeImage(perimeter, "png", "perimeter.png");
    }
    
    public static void classify(int[][] cent, int[][] newCent, ChessPiece p )
    {
        int[] distances = new int[cent[0].length];//Store the Distances 
        int[][] cMinusF = new int[newCent.length][newCent[0].length];
        int[][] cMinusFTp = new int[newCent[0].length][newCent.length];
        for(int i = 0; i < 6; i++)
        {
            for(int j = 0; j < 4; j++)
            {
                //Subtract
                cMinusF[j][0] = cent[j][i] - newCent[j][0];   
            }
            //Transpose
            cMinusFTp[0][0] = cMinusF[0][0];  
            cMinusFTp[0][1] = cMinusF[1][0];
            cMinusFTp[0][2] = cMinusF[2][0];
            cMinusFTp[0][3] = cMinusF[3][0];
            
            //Multiply
            distances[i] = (int)Math.sqrt((cMinusFTp[0][0] * cMinusF[0][0]) + (cMinusFTp[0][1] * cMinusF[1][0]) + (cMinusFTp[0][2] * cMinusF[2][0]) + (cMinusFTp[0][3] * cMinusF[3][0]));
        }
        
//        for(int i = 0;i < distances.length;i++)
//            System.out.println(distances[i]);
        
        int min = distances[0];
        int index = 0;
        for(int i = 0; i < distances.length; i++)
        {
            if(distances[i] < min)
            {
                min = distances[i];
                index = i;
            }
        }
        System.out.println("Input Image has values: " +
                           "\nArea: " + p.area + ", Perimeter: " + p.perimeter + ", Compactness: " + p.compactness + ", Length: " + p.length);
        
        System.out.println("STORED CENTOID VALUES ARE: "+
                   "\nFeatures:              C-1:Bishop   C-1:King C-3:Knight C-4:Pawn C-5:Queen C-6:Rook"+
                   "\nFeature-01 Area        :" + cent[0][0] +"       "+ cent[0][1] +"      "+ cent[0][2] +"      "+ cent[0][3] +"      "+ cent[0][4] +"      "+cent[0][5] +
                   "\nFeature-02 Perimeter   :" + cent[1][0] +"        "+ cent[1][1] +"       "+ cent[1][2] +"       "+ cent[1][3] +"       "+ cent[1][4] +"       "+cent[1][5] +
                   "\nFeature-03 Compactness :" + cent[2][0] +"           "+ cent[2][1] +"         "+ cent[2][2] +"           "+ cent[2][3] +"         "+ cent[2][4] +"         "+cent[2][5] +
                   "\nFeature-04 Length      :" + cent[3][0] +"         "+ cent[3][1] +"        "+ cent[3][2] +"        "+ cent[3][3] +"        "+ cent[3][4] +"      "+cent[3][5]);
        
        System.out.println("CALCULATED DISTANCE METRICS (Inputs to each stored centroid): "+
                          "\nDistance from Bishop: " + distances[0] +
                           "\nDistance from King: " + distances[1] +
                           "\nDistance from Knight: " + distances[2] +
                           "\nDistance from Pawn: " + distances[3] +
                           "\nDistance from Queen: " + distances[4] +
                           "\nDistance from Rook: " + distances[5]);
            
        if(index == 0)
            System.out.println("Item Identified as Bishop");
        else if(index == 1)
            System.out.println("Item Identified as King");
        else if(index == 2)
            System.out.println("Item Identified as Knight");
        else if(index == 3)
            System.out.println("Item Identified as Pawn");
        else if(index == 4)
            System.out.println("Item Identified as Queen");
        else if(index == 5)
            System.out.println("Item Identified as Rook");        
    }
    
    public static void getLength(ChessPiece c)
    {
        /*Start from top-left and bottom-right and spiral pixel opposite ends.
          If both equal 255 then it is the perimeter(original image subtracted from eroded image).
          Calulate the length and check if its longer then last length, store if it is.
          Lengths/2 so not to calulate twice(Stop at the center).
        */
        double length = 0;
        double temp = 0;   
        int row = c.perimeterByteData.length;
        for(int i = 0; i < c.perimeterByteData.length/2; i ++)
        {
            int col = c.perimeterByteData[0].length;
            for(int j = 0; j < c.perimeterByteData[0].length/2; j++)
            {
                if(((c.perimeterByteData[i][j] & 0xff) == 255) && ((c.perimeterByteData[row][col] & 0xff) == 255))
                {
                    temp = Math.sqrt(((row-i)*(row-i)) + ((col-j)*(col-j))); 
                }
                col -=1;
                if(temp > length)
                    length = temp;   
               
               // System.out.println(row + " - " + i + " + " + col + " - " + j + " = " + temp);
            }
            row-=1;
        }      
        c.length = (int)length;
    }
    
    public static void getCompactness(ChessPiece c)
    {
        //C=P^2/(4piA)
        c.compactness = (int)((c.perimeter*c.perimeter)/(4*Math.PI*c.area));
    }
    
    public static void getArea(ChessPiece c)
    {    
        for(int i = 0; i < c.grayByteData.length; i++)
        {
            for(int j = 0 ; j < c.grayByteData[0].length; j++)
            {
                if ((c.grayByteData[i][j] & 0xFF) == 255) {
                    c.area += 1;
                }
            }
        }
    }
    
    public static void getPerimeter(ChessPiece c)
    {
        byte[][] temp = new byte[c.grayByteData.length][c.grayByteData[0].length];
        //Subtract the original image from the eroded image
        for(int i = 0; i < c.grayByteData.length; i++)
        {
            for(int j = 0 ; j < c.grayByteData[0].length; j++)
            {
               temp[i][j]= (byte)((c.grayByteData[i][j] & 0xFF) - (c.grayByteDataEroded[i][j] & 0xFF));
            }
        }
        c.perimeterByteData = temp;
        
        //Calculate perimeter
        for (byte[] perimeterByteData : c.perimeterByteData) {
            for (int j = 0; j < c.perimeterByteData[0].length; j++) {
                if ((perimeterByteData[j] & 0xFF) == 255) {
                    c.perimeter += 1;
                }
            }
        } 
    }
        
    public static void showFiles(File[] files,int count, ChessPiece[] iArray, int num) 
    {
        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println("Directory: " + file.getName());
                showFiles(file.listFiles(),num,iArray,num);
                num += file.listFiles().length;
            } else {
                System.out.println("File: " + file.getName());
                System.out.println("The Count is: " + count);
                    iArray[count] = new ChessPiece();//Create object, store in array
                    iArray[count].bImg = ImageIo.readImage(file.getPath());//Store bufferedImage
                    iArray[count].bGrayImage = ImageIo.toGray(iArray[count].bImg);//Convert to gray
                    iArray[count].grayByteData = ImageIo.getGrayByteImageArray2DFromBufferedImage(iArray[count].bGrayImage);//extract gray byte data
                    ImageIo.thresholdReverse(iArray[count].grayByteData,128);//Threshold
                    iArray[count].name = file.getName();
                    //[count].img = SwingFXUtils.toFXImage(iArray[count].bImg, null);//Convert from buffered to Image
                    //iArray[count].imageData = ImageIo.getDoubleImageArray1DFromBufferedImage(iArray[count].bImg);//1D Row Image 
                    iArray[count].id = count;
                    count++;
            }
        }
    }
    
    //Returns the total of all files in a directory and its subdirectories
    public static int countFilesOnly(File[] files, int num) 
    {
        for (File file : files) {
            if (file.isDirectory()) {
                //System.out.println("Directory: " + file.getName());
                countFilesOnly(file.listFiles(),num);
                num += file.listFiles().length;
            }
        }
        return num;
    }

        
        
}
