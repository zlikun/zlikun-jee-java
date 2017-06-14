package com.zlikun.jee;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * 使用ImageIO计算图片的灰度值
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/6/14 8:48
 */
public class ImageIOTest {

    /**
     * 获取一张图片的RGB值，并将其转成灰度图
     */
    @Test
    public void test() throws IOException {

//        File file = new File("src/test/resources/image/01.jpg") ;     // 正常
//        File file = new File("src/test/resources/image/02.jpg") ;     // 正常
        File file = new File("src/test/resources/image/03.jpg") ;   // 过暗

        BufferedImage bi = ImageIO.read(file) ;

        // 获取图片属性
        int width = bi.getWidth() ;
        int height = bi.getHeight() ;
        System.out.println(String.format("图片属性：width = %d ,height = %d" ,width ,height));

        // 计算等分的 1 << 4 (2的4次幂)点上像素RGB值
        int power = 4 ; // 纵横各采集$power个点，意味着要$power + 1等分
        int [] grays = new int [1 << power] ;
        int index = 0 ;
        for (int w = 1 ,chunk = power + 1 ,wLength = width / chunk ; w < chunk ; w ++) {

            for (int h = 1 ,hLength = height / chunk ; h < chunk ; h ++) {

                int x = w * wLength ,y = h * hLength ;
                int rgb = bi.getRGB(x, y);

                // #getRGB() 获取的点的RGB值是ARGB。实际应用中使用的是RGB，所以需要将ARGB转化成RGB，#getRGB(i, j) & 0xFFFFFF
                int r = (rgb & 0xff0000) >> 16;
                int g = (rgb & 0xff00) >> 8;
                int b = (rgb & 0xff);

//                // 输出采集坐标及RGB值
//                System.out.println(String.format("%d,%d -> [%d,%d,%d]" ,x ,y ,r ,g ,b));

                // 计算灰度值
                int gray = this.getGray(r ,g ,b) ;

                grays[index ++] = gray ;

            }

        }

        // 输出采集到的灰度值
        // [155, 143, 148, 159, 46, 111, 112, 128, 143, 130, 181, 209, 157, 173, 177, 168]
        System.out.println(Arrays.toString(grays));

        // XXX 采集到的灰度值样本，其中有一半小于指定值，即认为图片过暗
        int count = 0 ,threshold = 50 ;
        for (int gray : grays) {
            if (gray < threshold) count ++ ;
        }
        System.out.println((count > grays.length / 2) ? "图片过暗" : "图片正常");

        // 将图片转换为灰度图片
        generateGrayImage(bi ,"JPEG" ,"target/gray-" + file.getName());

    }

    /**
     * 生成灰度图片
     * @param image
     * @param type
     * @param target
     * @throws IOException
     */
    private void generateGrayImage(Image image ,String type ,String target) throws IOException {
        BufferedImage bi2 = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        bi2.getGraphics().drawImage(image, 0, 0, null);
        for (int x = 0; x < bi2.getWidth(); x++) {
            for (int y = 0; y < bi2.getHeight(); y++) {
                Color pixel = new Color(bi2.getRGB(x, y));
                int gray = getGray(pixel) ; // RGB值相等时，为灰度值
                bi2.setRGB(x, y, new Color(gray, gray, gray).getRGB());
            }
        }
        ImageIO.write(bi2, type, new File(target));
    }

    /**
     * 计算像素的灰度值
     * @param pixel
     * @return
     */
    private int getGray(Color pixel) {
        return getGray(pixel.getRed()  ,pixel.getGreen() ,pixel.getBlue()) ;
    }

    /**
     * 计算RGB的灰度值
     * @param r
     * @param g
     * @param b
     * @return
     */
    private int getGray(int r ,int g ,int b) {
        return (int)(r * 0.3 + g * 0.59 + b * 0.11) ;
    }

}
