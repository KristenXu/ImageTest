import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by xuchen on 16/3/4.
 */
public class FastConnectedComponentLabelAlg {
    private int bgColor;
    private int[] labels;
    private int[] outData;
    private int dw;
    private int dh;
    public FastConnectedComponentLabelAlg() {
        bgColor = 255; // black color
    }
//    public static int[] imageData = new int[]{
//            255, 0,   0,   255,   0,   255, 255, 0,   255, 255, 255,
//            255, 0,   0,   255,   0,   255, 255, 0,   0,   255, 0,
//            255, 0,   0,   0,     255, 255, 255, 255, 255, 0,   0,
//            255, 255, 0,   255,   255, 255, 0,   255, 0,   0,   255,
//            255, 255, 0,   0,     0,   0,   255, 0,   0,   0,   0
//    };
//    FastConnectedComponentLabelAlg ccl = new FastConnectedComponentLabelAlg();
//    int[] outData = ccl.doLabel(imageData, 11, 5);

    public int[] doLabel(int[] inPixels, int width, int height) {
        dw = width;
        dh = height;
        int nextlabel = 1;
        int result = 0;
        labels = new int[dw * dh/2];
        outData = new int[dw * dh];
        for(int i=0; i<labels.length; i++) {
            labels[i] = i;
        }

        // we need to define these two variable arrays.
        int[] fourNeighborhoodPixels = new int[8];
        int[] fourNeighborhoodLabels = new int[8];
        int[] knownLabels = new int[4];

        int srcrgb = 0, index = 0;
        boolean existedLabel = false;
        for(int row = 0; row < height; row ++) {
            for(int col = 0; col < width; col++) {
                index = row * width + col;
                srcrgb = inPixels[index] & 0x000000ff;
                if(srcrgb == bgColor) {
                    result = 0; // which means no labeled for this pixel.
                } else {
                    // we just find the eight neighborhood pixels.
                    fourNeighborhoodPixels[0] = getPixel(inPixels, row-1, col); // upper cell
                    fourNeighborhoodPixels[1] = getPixel(inPixels, row, col-1); // left cell
                    fourNeighborhoodPixels[2] = getPixel(inPixels, row+1, col); // bottom cell
                    fourNeighborhoodPixels[3] = getPixel(inPixels, row, col+1); // right cell

                    // four corners pixels
                    fourNeighborhoodPixels[4] = getPixel(inPixels, row-1, col-1); // upper left corner
                    fourNeighborhoodPixels[5] = getPixel(inPixels, row-1, col+1); // upper right corner
                    fourNeighborhoodPixels[6] = getPixel(inPixels, row+1, col-1); // left bottom corner
                    fourNeighborhoodPixels[7] = getPixel(inPixels, row+1, col+1); // right bottom corner

                    // get current possible existed labels
                    fourNeighborhoodLabels[0] = getLabel(outData, row-1, col); // upper cell
                    fourNeighborhoodLabels[1] = getLabel(outData, row, col-1); // left cell
                    fourNeighborhoodLabels[2] = getLabel(outData, row+1, col); // bottom cell
                    fourNeighborhoodLabels[3] = getLabel(outData, row, col+1); // right cell

                    // four corners labels value
                    fourNeighborhoodLabels[4] = getLabel(outData, row-1, col-1); // upper left corner
                    fourNeighborhoodLabels[5] = getLabel(outData, row-1, col+1); // upper right corner
                    fourNeighborhoodLabels[6] = getLabel(outData, row+1, col-1); // left bottom corner
                    fourNeighborhoodLabels[7] = getLabel(outData, row+1, col+1); // right bottom corner

                    knownLabels[0] = fourNeighborhoodLabels[0];
                    knownLabels[1] = fourNeighborhoodLabels[1];
                    knownLabels[2] = fourNeighborhoodLabels[4];
                    knownLabels[3] = fourNeighborhoodLabels[5];

                    existedLabel = false;
                    for(int k=0; k<fourNeighborhoodLabels.length; k++) {
                        if(fourNeighborhoodLabels[k] != 0) {
                            existedLabel = true;
                            break;
                        }
                    }

                    if(!existedLabel) {
                        result = nextlabel;
                        nextlabel++;
                    } else {
                        int found = -1, count = 0;
                        for(int i=0; i<fourNeighborhoodPixels.length; i++) {
                            if(fourNeighborhoodPixels[i] != bgColor) {
                                found = i;
                                count++;
                            }
                        }

                        if(count == 1) {
                            result = (fourNeighborhoodLabels[found] == 0) ? nextlabel : fourNeighborhoodLabels[found];
                        } else {
                            result = (fourNeighborhoodLabels[found] == 0) ? nextlabel : fourNeighborhoodLabels[found];
                            for(int j=0; j<knownLabels.length; j++) {
                                if(knownLabels[j] != 0 && knownLabels[j] != result &&
                                        knownLabels[j] < result) {
                                    result = knownLabels[j];
                                }
                            }

                            boolean needMerge = false;
                            for(int mm = 0; mm < knownLabels.length; mm++ ) {
                                if(knownLabels[0] != knownLabels[mm] && knownLabels[mm] != 0) {
                                    needMerge = true;
                                }
                            }

                            // merge the labels now....
                            if(needMerge) {
                                int minLabel = knownLabels[0];
                                for(int m=0; m<knownLabels.length; m++) {
                                    if(minLabel > knownLabels[m] && knownLabels[m] != 0) {
                                        minLabel = knownLabels[m];
                                    }
                                }

                                // find the final label number...
                                result = (minLabel == 0) ? result : minLabel;

                                // re-assign the label number now...
                                if(knownLabels[0] != 0) {
                                    setData(outData, row-1, col, result);
                                }
                                if(knownLabels[1] != 0) {
                                    setData(outData, row, col-1, result);
                                }
                                if(knownLabels[2] != 0) {
                                    setData(outData, row-1, col-1, result);
                                }
                                if(knownLabels[3] != 0) {
                                    setData(outData, row-1, col+1, result);
                                }

                            }
                        }
                    }
                }
                outData[index] = result; // assign to label
            }
        }

        // post merge each labels now
        for(int row = 0; row < height; row ++) {
            for(int col = 0; col < width; col++) {
                index = row * width + col;
                mergeLabels(index);
            }
        }

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
        int maxKey = 1;
        int max = 0;
        for(Integer key : keys) {
            if(max < labelMap.get(key)){
                max = labelMap.get(key);
                maxKey = key;
            }
            System.out.println( "Number of " + key + " = " + labelMap.get(key));
        }
        System.out.println("maxkey = " + maxKey);
        System.out.println("max connected component number = " + max);
        return outData;
    }

    private void mergeLabels(int index) {
        int row = index / dw;
        int col = index % dw;
        // get current possible existed labels
        int min = getLabel(outData, row, col);
        if(min == 0) return;
        if(min > getLabel(outData, row-1, col) && getLabel(outData, row-1, col) != 0) {
            min = getLabel(outData, row-1, col);
        }

        if(min > getLabel(outData, row, col-1) && getLabel(outData, row, col-1) != 0) {
            min = getLabel(outData, row, col-1);
        }

        if(min > getLabel(outData, row+1, col) && getLabel(outData, row+1, col) != 0) {
            min = getLabel(outData, row+1, col);
        }

        if(min > getLabel(outData, row, col+1) && getLabel(outData, row, col+1) != 0) {
            min = getLabel(outData, row, col+1);
        }

        if(min > getLabel(outData, row-1, col-1) && getLabel(outData, row-1, col-1) != 0) {
            min = getLabel(outData, row-1, col-1);
        }

        if(min > getLabel(outData, row-1, col+1) && getLabel(outData, row-1, col+1) != 0) {
            min = getLabel(outData, row-1, col+1);
        }

        if(min > getLabel(outData, row+1, col-1) && getLabel(outData, row+1, col-1) != 0) {
            min = getLabel(outData, row+1, col-1);
        }

        if(min > getLabel(outData, row+1, col+1) && getLabel(outData, row+1, col+1) != 0) {
            min = getLabel(outData, row+1, col+1);
        }

        if(getLabel(outData, row, col) == min)
            return;
        outData[index] = min;

        // eight neighborhood pixels
        if((row -1) >= 0) {

            mergeLabels((row-1)*dw + col);
        }

        if((col-1) >= 0) {
            mergeLabels(row*dw+col-1);
        }

        if((row+1) < dh) {
            mergeLabels((row + 1)*dw+col);
        }

        if((col+1) < dw) {
            mergeLabels((row)*dw+col+1);
        }

        if((row-1)>= 0 && (col-1) >=0) {
            mergeLabels((row-1)*dw+col-1);
        }

        if((row-1)>= 0 && (col+1) < dw) {
            mergeLabels((row-1)*dw+col+1);
        }

        if((row+1) < dh && (col-1) >=0) {
            mergeLabels((row+1)*dw+col-1);
        }

        if((row+1) < dh && (col+1) < dw) {
            mergeLabels((row+1)*dw+col+1);
        }
    }

    private void setData(int[] data, int row, int col, int value) {
        if(row < 0 || row >= dh) {
            return;
        }

        if(col < 0 || col >= dw) {
            return;
        }

        int index = row * dw + col;
        data[index] = value;
    }

    private int getLabel(int[] data, int row, int col) {
        // handle the edge pixels
        if(row < 0 || row >= dh) {
            return 0;
        }

        if(col < 0 || col >= dw) {
            return 0;
        }

        int index = row * dw + col;
        return (data[index] & 0x000000ff);
    }

    private int getPixel(int[] data, int row, int col) {
        // handle the edge pixels
        if(row < 0 || row >= dh) {
            return bgColor;
        }

        if(col < 0 || col >= dw) {
            return bgColor;
        }

        int index = row * dw + col;
        return (data[index] & 0x000000ff);
    }

    public void setBgColor(int value){
        bgColor=value;
    }

    /**
     * binary image data:
     *
     * 255, 0,   0,   255,   0,   255, 255, 0,   255, 255, 255,
     * 255, 0,   0,   255,   0,   255, 255, 0,   0,   255, 0,
     * 255, 0,   0,   0,     255, 255, 255, 255, 255, 0,   0,
     * 255, 255, 0,   255,   255, 255, 0,   255, 0,   0,   255
     * 255, 255, 0,   0,     0,   0,   255, 0,   0,   0,   0
     *
     * height = 5, width = 11
     * @param args
     */


//    public static void main(String[] args) {
//        FastConnectedComponentLabelAlg ccl = new FastConnectedComponentLabelAlg();
//        int[] outData = ccl.doLabel(imageData, 11, 5);
//        for(int i=0; i<5; i++) {
//            System.out.println("--------------------");
//            for(int j = 0; j<11; j++) {
//                int index = i * 11 + j;
//                if(j != 0) {
//                    System.out.print(",");
//                }
//                System.out.print(outData[index]);
//            }
//            System.out.println();
//        }
//    }

}