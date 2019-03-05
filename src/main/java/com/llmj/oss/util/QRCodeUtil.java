package com.llmj.oss.util;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeUtil {
	
	public static void main(String[] args) {
		String save = "F:\\";
		String img = save + "麻将.png";
		try {
			//QRCodeUtil.encode("www.baidu.com", img,save,true,"123");
			
			byte[] ary = QRCodeUtil.encode("www.baidu.com");
			System.out.println(Arrays.toString(ary));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static final String CHARSET = "utf-8";
	private static final String FORMAT_NAME = "PNG";
	// 二维码尺寸
	private static final int QRCODE_SIZE = 300;
	// LOGO宽度
	private static final int WIDTH = 60;
	// LOGO高度
	private static final int HEIGHT = 60;
	
	
	/*
	 * 生成二维码
	 */
	private static BufferedImage createImage(String content, String imgPath, boolean needCompress) throws Exception {
		Hashtable hints = new Hashtable();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
		hints.put(EncodeHintType.MARGIN, 1);
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE,
				hints);
		int width = bitMatrix.getWidth();
		int height = bitMatrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
			}
		}
		if (imgPath == null || "".equals(imgPath)) {
			return image;
		}
		// 插入图片
		QRCodeUtil.insertImage(image, imgPath, needCompress);
		return image;
	}
	
	/*
	 * 生成的二维码中插入图片
	 */
	private static void insertImage(BufferedImage source, String imgPath, boolean needCompress) throws Exception {
        File file = new File(imgPath);
        //System.out.println(file+"****************************");
        if (!file.exists()) {
            System.err.println("" + imgPath + "   该文件不存在！");
            return;
        }
        Image src = ImageIO.read(new File(imgPath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (needCompress) { // 压缩LOGO
            if (width > WIDTH) {
                width = WIDTH;
            }
            if (height > HEIGHT) {
                height = HEIGHT;
            }
            Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }
	
	/**
	 * 生成带logo二维码，并保存到磁盘
	 * @param content 内容
	 * @param imgPath 插入图片的地址
	 * @param destPath 保存图片的地址
	 * @param needCompress 是否需要插入图片
	 * @param name 图片命名
	 * @return
	 * @throws Exception
	 */
    public static Boolean encode(String content, String imgPath, String destPath, boolean needCompress,String name) throws Exception {
        BufferedImage image = QRCodeUtil.createImage(content, imgPath, needCompress);
        if(image==null){
            return false;
        }
        FileUtil.makeDir(destPath);
        String file = name + ".png";
        ImageIO.write(image, FORMAT_NAME, new File(destPath + "/" + file));
        return true;
    }
    
    public static Boolean encode(String content, String destPath,String name) throws Exception {
        BufferedImage image = QRCodeUtil.createImage(content, "", false);
        if(image==null){
            return false;
        }
        FileUtil.makeDir(destPath);
        String file = name + ".png";
        ImageIO.write(image, FORMAT_NAME, new File(destPath + "/" + file));
        return true;
    }
    
    public static void encode(String content, String imgPath, OutputStream output, boolean needCompress)
            throws Exception {
        BufferedImage image = QRCodeUtil.createImage(content, imgPath, needCompress);
        ImageIO.write(image, FORMAT_NAME, output);
    }
    
    /**
     * 获得二进制流
     * @param content
     * @param output
     * @throws Exception
     */
    public static byte[] encode(String content) throws Exception {
    	ByteArrayOutputStream output = new ByteArrayOutputStream();
    	try {
            QRCodeUtil.encode(content, null, output, false);
            return output.toByteArray();
		} finally {
			output.flush();
		}
    }
	
}
