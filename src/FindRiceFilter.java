import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by xuchen on 16/3/4.
 */
public class FindRiceFilter extends AbstractBufferedImageOp {

    private int sumRice;

    public int getSumRice() {
        return this.sumRice;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        int width = src.getWidth();
        int height = src.getHeight();

        if ( dest == null )
            dest = createCompatibleDestImage( src, null );

        int[] inPixels = new int[width*height];
        int[] outPixels = new int[width*height];
        getRGB(src, 0, 0, width, height, inPixels );

        FastConnectedComponentLabelAlg fccAlg = new FastConnectedComponentLabelAlg();
        fccAlg.setBgColor(0);
        int[] outData = fccAlg.doLabel(inPixels, width, height);
        // labels statistic
        HashMap<Integer, Integer> labelMap = new HashMap<Integer, Integer>();
        for(int d=0; d<outData.length; d++) {
            if(outData[d] != 0) {
                if(labelMap.containsKey(outData[d])) {
                    Integer count = labelMap.get(outData[d]);
                    count+=1;
                    labelMap.put(outData[d], count);
                } else {
                    labelMap.put(outData[d], 1);
                }
            }
        }

        // try to find the max connected component
        Integer[] keys = labelMap.keySet().toArray(new Integer[0]);
        Arrays.sort(keys);
        int threshold = 10;
        ArrayList<Integer> listKeys = new ArrayList<Integer>();
        for(Integer key : keys) {
            if(labelMap.get(key) <=threshold){
                listKeys.add(key);
            }
            System.out.println( "Number of " + key + " = " + labelMap.get(key));
        }
        sumRice = keys.length - listKeys.size();

        // calculate means of pixel
        int index = 0;
        for(int row=0; row<height; row++) {
            int ta = 0, tr = 0, tg = 0, tb = 0;
            for(int col=0; col<width; col++) {
                index = row * width + col;
                ta = (inPixels[index] >> 24) & 0xff;
                tr = (inPixels[index] >> 16) & 0xff;
                tg = (inPixels[index] >> 8) & 0xff;
                tb = inPixels[index] & 0xff;
                if(outData[index] != 0 && validRice(outData[index], listKeys)) {
                    tr = tg = tb = 255;
                } else {
                    tr = tg = tb = 0;
                }
                outPixels[index] = (ta << 24) | (tr << 16) | (tg << 8) | tb;
            }
        }
        setRGB( dest, 0, 0, width, height, outPixels );
        return dest;
    }

    private boolean validRice(int i, ArrayList<Integer> listKeys) {
        for(Integer key : listKeys) {
            if(key == i) {
                return false;
            }
        }
        return true;
    }

}
