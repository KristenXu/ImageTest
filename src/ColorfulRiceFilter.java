import java.awt.image.BufferedImage;

/**
 * Created by xuchen on 16/3/4.
 */
public class ColorfulRiceFilter extends AbstractBufferedImageOp {

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        int width = src.getWidth();
        int height = src.getHeight();

        if ( dest == null )
            dest = createCompatibleDestImage( src, null );

        int[] inPixels = new int[width*height];
        int[] outPixels = new int[width*height];
        getRGB(src, 0, 0, width, height, inPixels );

        int index = 0, srcrgb;
        for(int row=0; row<height; row++) {
            int ta = 255, tr = 0, tg = 0, tb = 0;
            for(int col=0; col<width; col++) {
                index = row * width + col;
//                ta = (inPixels[index] >> 24) & 0xff;
//                tr = (inPixels[index] >> 16) & 0xff;
//                tg = (inPixels[index] >> 8) & 0xff;
//                tb = inPixels[index] & 0xff;
                srcrgb = inPixels[index] & 0x000000ff;
                if(srcrgb > 0 && row < 140) {
                    tr = 0;
                    tg = 255;
                    tb = 0;
                } else if(srcrgb > 0 && row >= 140 && row <=280) {
                    tr = 0;
                    tg = 0;
                    tb = 255;
                } else if(srcrgb > 0 && row >=280) {
                    tr = 255;
                    tg = 0;
                    tb = 0;
                }
                else {
                    tr = tg = tb = 0;
                }
                outPixels[index] = (ta << 24) | (tr << 16) | (tg << 8) | tb;
            }
        }
        setRGB( dest, 0, 0, width, height, outPixels );
        return dest;
    }
}
