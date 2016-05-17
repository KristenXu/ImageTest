import com.sun.xml.internal.fastinfoset.sax.Properties;
import com.sun.xml.internal.fastinfoset.sax.SystemIdResolver;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by xuchen on 16/3/4.
 */
public class MainFrame extends JComponent implements ActionListener {
    /**
     *
     */
    private static final long serialVersionUID = 1518574788794973574L;
    public final static String BROWSE_CMD = "Browse...";
    public final static String NOISE_CMD = "Remove Noise";
    public final static String FUN_CMD = "Colorful Rice";

    private BufferedImage rawImg;
    private BufferedImage chageImg;
    private BufferedImage resultImage;
    private MediaTracker tracker;
    private Dimension mySize;

    // JButtons
    private JButton browseBtn;
    private JButton noiseBtn;
    private JButton colorfulBtn;

    // rice number....
    private int riceNum = -1;

    //black number....
    private int blackNum = 0;

    //pixels number....
    private int pixelsNum = 0;

    //blackPercent
    private double blackPercent;//


    public MainFrame() {
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        browseBtn = new JButton("Browse...");
        noiseBtn = new JButton("Remove Noise");
        colorfulBtn = new JButton("Colorful Image");

        browseBtn.setToolTipText("Please select image file...");
        noiseBtn.setToolTipText("find connected region and draw red rectangle");
        colorfulBtn.setToolTipText("Remove the minor noise region pixels...");

        // buttons
        btnPanel.add(browseBtn);
        btnPanel.add(noiseBtn);
        btnPanel.add(colorfulBtn);

        // setup listener...
        browseBtn.addActionListener(this);
        noiseBtn.addActionListener(this);
        colorfulBtn.addActionListener(this);

        browseBtn.setEnabled(true);
        noiseBtn.setEnabled(true);
        colorfulBtn.setEnabled(true);

//      minX = minY =  10000;
//      maxX = maxY = -1;

        mySize = new Dimension(700, 500);
        JFrame demoUI = new JFrame("Image Detection Demo");
        demoUI.getContentPane().setLayout(new BorderLayout());
        demoUI.getContentPane().add(this, BorderLayout.CENTER);
        demoUI.getContentPane().add(btnPanel, BorderLayout.SOUTH);
        demoUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        demoUI.pack();
        demoUI.setVisible(true);
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if(rawImg != null) {
            Image scaledImage = rawImg.getScaledInstance(200, 300, Image.SCALE_FAST);
            g2.drawImage(scaledImage, 0, 0, 200, 300, null);
        }
        if(resultImage != null) {
            Image scaledImage = resultImage.getScaledInstance(200, 300, Image.SCALE_FAST);
            g2.drawImage(scaledImage, 210, 0, 200, 300, null);
        }

        g2.setPaint(Color.RED);
        g2.setFont(new Font("楷体",Font.PLAIN,15));
        if(blackNum > 0) {
            g2.drawString("Number : " + riceNum, 100, 330);
            g2.drawString("BlackArea : " + blackNum + "; ImageArea : " + pixelsNum, 100, 350);
        } else {
            g2.drawString("Number : Unknown", 100, 330);
            g2.drawString("BlackArea : Unknown", 100, 350);
        }

        if(blackNum>0&&pixelsNum>0){
            blackPercent=(Math.round(blackNum*100.0/pixelsNum));
            g2.drawString("BlackPercent : " + blackPercent + "%" , 100, 370);
        }else{
            g2.drawString("BlackPercent : Unknown", 100, 370);
        }

    }
    public Dimension getPreferredSize() {
        return mySize;
    }

    public Dimension getMinimumSize() {
        return mySize;
    }

    public Dimension getMaximumSize() {
        return mySize;
    }

    public static void main(String[] args) {
        new MainFrame();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(BROWSE_CMD.equals(e.getActionCommand())) {
//            JFileChooser chooser = new JFileChooser();
//            chooser.showOpenDialog(null);
//            File f = chooser.getSelectedFile();
            BufferedImage bImage = null;
            String str = "http://120.27.47.178:8888/show";
            URL url = null;
            try{
                url = new URL(str);
            }catch (MalformedURLException mf){
                mf.printStackTrace();
            }
//            if(f == null) return;
            //获取系统信息
//            java.util.Properties properties = System.getProperties();
//            System.out.println("system : "+ properties);
//            JTextArea jta = new JTextArea(properties.toString());
//            JScrollPane jsp = new JScrollPane(jta){
//                @Override
//                public Dimension getPreferredSize() {
//                    return new Dimension(480, 320);
//                }
//            };
//            JOptionPane.showMessageDialog(this, jsp,
//                    "Error",JOptionPane.ERROR_MESSAGE);
            //获取最近修改文件
//            File path = new File("/Users/xuchen/Desktop/图像源");
//            File[] files = path.listFiles();
//            File lastModifiedFile = files[0];
//            for(int index = 0;index<files.length;index++){
//                if(lastModifiedFile.lastModified() <= files[index].lastModified()){
//                    lastModifiedFile = files[index];
//                }
//            }
//
//            try {
//                bImage = ImageIO.read(lastModifiedFile);
//                System.out.println("lastModified : "+lastModifiedFile.getName());
//
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }

//            BufferedImage bImage = null;
//            URL url=null;
//            try{
//
//                url = new URL("http://7fve54.com1.z0.glb.clouddn.com/Ekf9mnoC/cilkjkf7o006ho0m3a5qzdyii/display");
//            }catch (MalformedURLException e2){
//                e2.printStackTrace();
//            }
            try {
                bImage = ImageIO.read(url);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            tracker = new MediaTracker(this);
            tracker.addImage(bImage, 1);

            // blocked 10 seconds to load the image data
            try {
                if (!tracker.waitForID(1, 10000)) {
                    System.out.println("Load error.");
                    System.exit(1);
                }// end if
            } catch (InterruptedException ine) {
                ine.printStackTrace();
                System.exit(1);
            } // end catch
            rawImg = bImage;
            repaint();
        } else if(NOISE_CMD.equals(e.getActionCommand())) {
            BinaryFilter bfilter = new BinaryFilter();
//            rawImg = bfilter.filter(rawImg, null);
            chageImg = bfilter.filter(rawImg, null);
            blackNum = bfilter.getBlackNum();
            pixelsNum = bfilter.getPixelsNum();
            FindRiceFilter frFilter = new FindRiceFilter();
            resultImage = frFilter.filter(chageImg, null);
            riceNum = frFilter.getSumRice();
//            riceNum =(int) (Math.round(blackNum*100.0/pixelsNum))/10 +1;
            repaint();
        } else if(FUN_CMD.equals(e.getActionCommand())) {
            ColorfulRiceFilter cFilter = new ColorfulRiceFilter();
            resultImage = cFilter.filter(resultImage, null);
            repaint();
        } else {
            // do nothing...
        }

    }
}
