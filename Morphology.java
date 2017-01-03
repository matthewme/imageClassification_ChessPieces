/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imageclassification;

/**
 *
 * @author Paypa
 */
public class Morphology {
    
    public byte[][] dilate(byte[][] input)
    {
        byte[][] output = new byte[input.length][input[0].length];
        int h = input.length;
        int w = input[0].length;

        int x1,x2,x3,x4,x5,x6,x7,x8,x9;

        for(int i = 1; i < h-1; i++)
        {
            for(int j = 1; j < w-1; j++)
            {
                x1 = (input[i-1][j-1] & 0xff);
                x2 = (input[i-1][j] & 0xff);
                x3 = (input[i-1][j+1] & 0xff);
                x4 = (input[i][j-1] & 0xff);
                x5 = (input[i][j] & 0xff);
                x6 = (input[i][j+1] & 0xff);
                x7 = (input[i+1][j-1] & 0xff);
                x8 = (input[i+1][j] & 0xff);
                x9 = (input[i+1][j+1] & 0xff);
                
                if(x1==0 &&
                    x2==0 &&
                    x3==0 &&
                    x4==0 &&
                    x5==0 &&
                    x6==0 &&
                    x7==0 &&
                    x8==0 &&
                    x9==0)
                  output[i][j] = (byte)0;  
                else
                  output[i][j] = (byte)255;             
            }
        }
        return output;
    }

    public byte[][] erode(byte[][] input)
    {
        byte[][] output = new byte[input.length][input[0].length];
        int h = input.length;
        int w = input[0].length;

        int x1,x2,x3,x4,x5,x6,x7,x8,x9;

        for(int i = 1; i < h-1; i++)
        {
            for(int j = 1; j < w-1; j++)
            {
                x1 = (input[i-1][j-1] & 0xff);
                x2 = (input[i-1][j] & 0xff);
                x3 = (input[i-1][j+1] & 0xff);
                x4 = (input[i][j-1] & 0xff);
                x5 = (input[i][j] & 0xff);
                x6 = (input[i][j+1] & 0xff);
                x7 = (input[i+1][j-1] & 0xff);
                x8 = (input[i+1][j] & 0xff);
                x9 = (input[i+1][j+1] & 0xff);
                
                if(x1==255 &&
                    x2==255 &&
                    x3==255 &&
                    x4==255 &&
                    x5==255 &&
                    x6==255 &&
                    x7==255 &&
                    x8==255 &&
                    x9==255)
                  output[i][j] = (byte)255;  
                else
                  output[i][j] = (byte)0;             
            }
        }
        return output;
    }    
   
    public byte[][] dilation(byte[][] c,Morphology m, int num)
    {
        byte[][] d = m.dilate(c);
        if(num == 0)
        {
        }
        else
        {
            dilation(d, m,num-1);
        }
 
        return d;
    }
    
    public byte[][] erosion(byte[][] c,Morphology m, int num)
    {
        byte[][] e = m.erode(c);
        if(num == 0)
        { 
        }
        else
        {
            dilation(e, m,num-1);
        }
        return e;
    }
    
    public int globalThreshold(byte[][] byteData, int T)
    {       
        int mu1 = 0;//Less Than T
        int mu2 = 0;//Greater than T
        int mu1Count = 0;//Total to get average
        int mu2Count = 0;
      
        int[] histogram = new int[256]; //H[i]
        
        for (int i = 0; i < byteData.length; i++) {
            for (int j = 0; j < byteData[0].length; j++) {
                histogram[(byteData[i][j] & 0xff)]++;
            }
        }
        
        for(int i = 0; i < histogram.length; i++)
        {      
            //System.out.println("Gray level " + i + " is " + histogram[i]); 
            if(i < T)
            {
                mu1 += i * histogram[i];
                mu1Count+= histogram[i] ;
            }
            else
            {
                mu2 += i * histogram[i];
                mu2Count+= histogram[i];
            }
        }
        
        //New Threshold. T = .5(mu1 + mu1)
        int newT = ((mu1/mu1Count) + (mu2/mu2Count))/2;
        
        return newT;
    }
}
