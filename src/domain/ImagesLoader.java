package src.domain;
import java.util.*;
import java.io.*;
import java.net.URL;
import java.awt.image.BufferedImage;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.GraphicsEnvironment;


public class ImagesLoader
{
  private static final String IMAGE_DIR  = "/Images/";

  private HashMap<String, ArrayList<BufferedImage>> imagesMap;
  private HashMap<String, ArrayList<String>> gNamesMap;

  private GraphicsConfiguration gc;

  public ImagesLoader(String fnm)
  {
      initLoader();
      loadImagesFile(fnm);
  }

  public ImagesLoader()
  {  initLoader();  }

  private void initLoader()
  {
      imagesMap = new HashMap<>();
      gNamesMap = new HashMap<>();

      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice gd = ge.getDefaultScreenDevice();
      gc = gd.getDefaultConfiguration();
  }


  private void loadImagesFile(String fnm)
  {
    String imsFNm = IMAGE_DIR + fnm;
    System.out.println("Reading file: "+ imsFNm);
    InputStream in = this.getClass().getResourceAsStream(imsFNm);
    if (in == null){
        System.out.println("Could not find file: " + imsFNm);
        return;
    }
    try (BufferedReader br = new BufferedReader(new InputStreamReader(in))){
        String line;
        char ch;
        while ((line = br.readLine()) != null){
            line = line.trim();
            if (line.length() ==0){ //blank line
                continue;
            }

            if (line.startsWith("//")){ // comment
                continue;
            }
            ch = Character.toLowerCase(line.charAt(0));
            if (ch == 'o'){ // a single image
                getFileNameImage(line);
            }
            else if (ch == 'n'){ // a numbered sequence of images
                getNumberedImages(line);
            }
            else if (ch == 's'){
                getStripImage(line);
            }
            else if (ch == 'g'){ // a group of images
                getGroupImages(line);
            }
            else{
                System.out.println("Do not recognize line: " + line);
            }
        }
    }catch(IOException e){
     System.out.println("Error reading file: " + imsFNm);
     System.exit(1);
    }
  }

  public void getFileNameImage(String line)
  // format is o <fnm>
  {
      StringTokenizer tokens = new StringTokenizer(line);
      if (tokens.countTokens() != 2){
          System.out.println("Wrong no. of arguments for " + line);
      }
      else{
          tokens.nextToken(); //skip command label
          System.out.print("o Line: ");
          loadSingleImage(tokens.nextToken());
      }
  }

  public boolean loadSingleImage(String fnm)
  {
      String name = getPrefix(fnm);
      if (imagesMap.containsKey(name)){
          System.out.println("Error: " + name + " already used");
          return false;
      }

      BufferedImage bi = loadImage(fnm);
      if (bi != null){
          ArrayList<BufferedImage> imsList = new ArrayList<>();
          imsList.add(bi);
          imagesMap.put(name, imsList);
          System.out.println(" Stored " + name + "/"+ fnm);
          return true;
      }
      else{
          return false;
      }
  }


  private void getNumberedImages(String line)
  // format is n <fnm*.ext> <number>
  {
      StringTokenizer tokens = new StringTokenizer(line);
      if (tokens.countTokens() != 3){
          System.out.println("Wrong no. of arguments for " + line);
          return;
      }
      tokens.nextToken(); // skip command label
      System.out.print("n Line: ");
      String fnm = tokens.nextToken();
      int number = parseNumber(tokens.nextToken(), line);
      if (number > 0){
          loadNumImages(fnm, number);
      }
  }

  public int loadNumImages(String fnm, int number)
  {
      int starPosn = fnm.lastIndexOf('*');
      if (starPosn == -1){
          System.out.println("No '*' in filename: " + fnm);
          return 0;
      }
      String prefix = fnm.substring(0, starPosn);
      String postfix = fnm.substring(starPosn+1);
      if (imagesMap.containsKey(prefix)){
          System.out.println("Error: " + prefix + " already used");
          return 0;
      }

      ArrayList<BufferedImage> imsList = new ArrayList<>();
      int loadCount = 0;
      for (int i = 0; i < number; i++){
          BufferedImage bi = loadImage(prefix + i + postfix);
          if (bi != null){
              imsList.add(bi);
              loadCount++;
          }
      }

      if (loadCount == 0){
          System.out.println(" No images loaded for " + prefix);
      }
      else{
          imagesMap.put(prefix, imsList);
          System.out.println(" Stored " + loadCount + " images under " + prefix);
      }
      return loadCount;
  }


  private void getStripImage(String line)
  // format is s <fnm> <number>
  {
      StringTokenizer tokens = new StringTokenizer(line);
      if (tokens.countTokens() != 3){
          System.out.println("Wrong no. of arguments for " + line);
          return;
      }
      tokens.nextToken(); // skip command label
      System.out.print("s Line: ");
      String fnm = tokens.nextToken();
      int number = parseNumber(tokens.nextToken(), line);
      if (number > 0){
          loadStripImages(fnm, number);
      }
  }

  public int loadStripImages(String fnm, int number)
  {
      String name = getPrefix(fnm);
      if (imagesMap.containsKey(name)){
          System.out.println("Error: " + name + " already used");
          return 0;
      }

      BufferedImage[] strip = loadStripImageArray(fnm, number);
      if (strip == null){
          return 0;
      }

      ArrayList<BufferedImage> imsList = new ArrayList<>(Arrays.asList(strip));
      imagesMap.put(name, imsList);
      System.out.println(" Stored " + strip.length + " images under " + name);
      return strip.length;
  }

  public BufferedImage[] loadStripImageArray(String fnm, int number)
  {
    if (number <= 0){
        System.out.println("number <= 0; returning null");
        return null;
    }

    BufferedImage stripIm;
    if ((stripIm = loadImage(fnm)) == null){
        System.out.println("Returning null");
        return null;
    }

    int imWidth = (int) stripIm.getWidth() /number;
    int height = stripIm.getHeight();
    int transparency = stripIm.getColorModel().getTransparency();

    BufferedImage[] strip = new BufferedImage[number];
    Graphics2D stripGC;

    //each BufferedImage from the strip file is stored in strip[]
    for (int i = 0; i < number; i++){
        strip[i] = gc.createCompatibleImage(imWidth,height,transparency);

        //create a graphics context
        stripGC =strip[i].createGraphics();

        //copy image
        stripGC.drawImage(stripIm, 0,0,imWidth,height, i*imWidth,0, (i*imWidth)+ imWidth,height, null);
        stripGC.dispose();
    }

    return strip;
  }//end of loadStripImageArray()


  private void getGroupImages(String line)
 
  {
      StringTokenizer tokens = new StringTokenizer(line);
      if (tokens.countTokens() < 3){
          System.out.println("Wrong no. of arguments for " + line);
          return;
      }
      tokens.nextToken(); // skip command label
      System.out.print("g Line: ");
      String name = tokens.nextToken();

      ArrayList<String> fnms = new ArrayList<>();
      while (tokens.hasMoreTokens()){
          fnms.add(tokens.nextToken());
      }
      loadGroupImages(name, fnms);
  }

  public int loadGroupImages(String name, ArrayList<String> fnms)

  {
      if (imagesMap.containsKey(name)){
          System.out.println("Error: " + name + " already used");
          return 0;
      }

      ArrayList<BufferedImage> imsList = new ArrayList<>();
      ArrayList<String> nms = new ArrayList<>();
      for (String fnm : fnms){
          BufferedImage bi = loadImage(fnm);
          if (bi != null){
              imsList.add(bi);
              nms.add(getPrefix(fnm));
          }
      }

      if (imsList.isEmpty()){
          System.out.println(" No images loaded for " + name);
      }
      else{
          imagesMap.put(name, imsList);
          gNamesMap.put(name, nms);
          System.out.println(" Stored " + imsList.size() + " images under " + name);
      }
      return imsList.size();
  }




  public BufferedImage getImage(String name)
  {
      ArrayList<BufferedImage> imsList = imagesMap.get(name);
      if (imsList == null){
          System.out.println("No image(s) stored under " + name);
          return null;
      }
      return imsList.get(0);
  }

  public BufferedImage getImage(String name, int posn)
  {
      ArrayList<BufferedImage> imsList = imagesMap.get(name);
      if (imsList == null){
          System.out.println("No image(s) stored under " + name);
          return null;
      }
      int size = imsList.size();
      int pos = ((posn % size) + size) % size;
      return imsList.get(pos);
  }

  public BufferedImage getImage(String name, String fnmPrefix)
 
  {
      ArrayList<String> nms = gNamesMap.get(name);
      if (nms == null){
          System.out.println("No group stored under " + name);
          return null;
      }
      int posn = nms.indexOf(fnmPrefix);
      if (posn == -1){
          System.out.println("No " + fnmPrefix + " image in group " + name);
          return null;
      }
      return getImage(name, posn);
  }

  public ArrayList<BufferedImage> getImages(String name)
  {
      ArrayList<BufferedImage> imsList = imagesMap.get(name);
      if (imsList == null){
          System.out.println("No image(s) stored under " + name);
      }
      return imsList;
  }

  public boolean isLoaded(String name)
  {  return imagesMap.containsKey(name);  }

  public int numImages(String name)
  {
      ArrayList<BufferedImage> imsList = imagesMap.get(name);
      return (imsList == null) ? 0 : imsList.size();
  }


  public  BufferedImage loadImage(String fnm)
  {
      URL url = getClass().getResource(IMAGE_DIR+fnm);
      if (url == null){
          System.out.println("Could not find image: " + IMAGE_DIR + fnm);
          return null;
      }
      try{
          BufferedImage im = ImageIO.read(url);
          int transparency = im.getColorModel().getTransparency();
          BufferedImage copy = gc.createCompatibleImage(im.getWidth(), im.getHeight(), transparency);

          //Create a graphics context
          Graphics2D g2d = copy.createGraphics();
          //copy image
          g2d.drawImage(im,0,0,null);
          g2d.dispose();
          return copy;

      }catch(IOException e){
          System.out.println("load Image error for " + IMAGE_DIR + fnm + ":\n" + e );
          return null;
      }
  }

  private String getPrefix(String fnm)
  {
      int posn = fnm.lastIndexOf('.');
      if (posn == -1){
          System.out.println("No prefix found for filename: " + fnm);
          return fnm;
      }
      return fnm.substring(0, posn);
  }

  private int parseNumber(String token, String line)
  {
      try{
          int number = Integer.parseInt(token);
          if (number <= 0){
              System.out.println("Number must be > 0 in " + line);
              return -1;
          }
          return number;
      }catch(NumberFormatException e){
          System.out.println("Number is incorrect for " + line);
          return -1;
      }
  }
}
