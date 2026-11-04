package src.presentation;
import java.util.*;
import java.io.*; 


public class ImageLoader
{
  private HashMap imagesMap, gNamesMap;
  private static final String IMAGE_DIR  = "/images/";
  
  private void LoadImagesFile(String fnm){
    String imsFNm = IMAGE_DIR + fnm;
    System.out.println("Reading file: "+ imsFNm);
    try{
        InputStream in = this.getClass().getResourceAsStream(imsFNm);
        BufferedReader bIn = new BufferedReader(
                 new InputStreamReader(in));
        String line;
        char ch;
        while ((line = bIn.readLine()) != null){
            if (line.length() ==0){ //blank line
                continue;
            }
            
            if (line.startsWith("//")){ // comment
                continue;
            }
            ch = Character.toLowerCase(line.charAt(0));
            
        }
    }catch(IOException e){
        
    }
      
  }
}