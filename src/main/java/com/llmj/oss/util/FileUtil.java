package com.llmj.oss.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSString;
import com.dd.plist.PropertyListParser;

import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import net.dongliu.apk.parser.bean.UseFeature;

public class FileUtil {

	public static void main(String[] args) {
		File file = new File("F://upload//ffylmj_formal_20190215_01.ipa");
		Map<String, Object> map = FileUtil.readIPA(file);
		System.out.println(map);
		
	}

	/**
	 * 读取IPA信息
	 * 
	 * @param ipaURL
	 * @return
	 */
	public static Map<String, Object> readIPA(File file) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		InputStream is = null;
		InputStream is2 = null;
		ZipInputStream zipIns = null;
		ZipInputStream zipIns2 = null;
		InputStream infoIs = null;
		try {
			is = new FileInputStream(file);
			is2 = new FileInputStream(file);
			zipIns = new ZipInputStream(is);
			zipIns2 = new ZipInputStream(is2);
			ZipEntry ze;
			ZipEntry ze2;
			NSDictionary rootDict = null;
			String icon = null;
			while ((ze = zipIns.getNextEntry()) != null) {
				if (!ze.isDirectory()) {
					String name = ze.getName();
					if (null != name && name.toLowerCase().contains(".app/info.plist")) {
						ByteArrayOutputStream _copy = new ByteArrayOutputStream();
						int chunk = 0;
						byte[] data = new byte[1024];
						while (-1 != (chunk = zipIns.read(data))) {
							_copy.write(data, 0, chunk);
						}
						infoIs = new ByteArrayInputStream(_copy.toByteArray());
						rootDict = (NSDictionary) PropertyListParser.parse(infoIs);

						// 我们可以根据info.plist结构获取任意我们需要的东西
						// 比如下面我获取图标名称，图标的目录结构请下面图片
						// 获取图标名称
						/*NSDictionary iconDict = (NSDictionary) rootDict.get("CFBundleIcons");

						while (null != iconDict) {
							if (iconDict.containsKey("CFBundlePrimaryIcon")) {
								NSDictionary CFBundlePrimaryIcon = (NSDictionary) iconDict.get("CFBundlePrimaryIcon");
								if (CFBundlePrimaryIcon.containsKey("CFBundleIconFiles")) {
									NSArray CFBundleIconFiles = (NSArray) CFBundlePrimaryIcon.get("CFBundleIconFiles");
									icon = CFBundleIconFiles.getArray()[0].toString();
									if (icon.contains(".png")) {
										icon = icon.replace(".png", "");
									}
									System.out.println("获取icon名称:" + icon);
									break;
								}
							}
						}*/
						break;
					}
				}
			}

			// 根据图标名称下载图标文件到指定位置
			/*while ((ze2 = zipIns2.getNextEntry()) != null) {
				if (!ze2.isDirectory()) {
					String name = ze2.getName();
					System.out.println(name);
					if (name.contains(icon.trim())) {
						System.out.println(11111);
						FileOutputStream fos = new FileOutputStream(new File("F:\\upload\\icon.png"));
						int chunk = 0;
						byte[] data = new byte[1024];
						while (-1 != (chunk = zipIns2.read(data))) {
							fos.write(data, 0, chunk);
						}
						fos.close();
						break;
					}
				}
			}*/

			////////////////////////////////////////////////////////////////
			// 如果想要查看有哪些key ，可以把下面注释放开
			// for (String keyName : rootDict.allKeys()) {
			// System.out.println(keyName + ":" +
			// rootDict.get(keyName).toString());
			// }

			// 应用包名
			NSString parameters = (NSString) rootDict.get("CFBundleIdentifier");
			map.put("package", parameters.toString());
			// 应用版本名
			parameters = (NSString) rootDict.objectForKey("CFBundleShortVersionString");
			map.put("versionName", parameters.toString());
			// 应用版本号
			parameters = (NSString) rootDict.get("CFBundleVersion");
			map.put("versionCode", parameters.toString());
			//应用名称
			parameters = (NSString) rootDict.get("CFBundleDisplayName");
			map.put("name", parameters.toString());
			
			/*for (String key : rootDict.keySet()) {
				System.out.println(key);
				System.out.println(rootDict.get(key).toString());
			}*/
		} catch (Exception e) {
			map.put("code", "fail");
			map.put("error", "读取ipa文件失败");
		} finally {
			//关闭数据流
			if (infoIs != null) {
				try {
					infoIs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (is2 != null) {
				try {
					is2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (zipIns != null) {
				try {
					zipIns.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (zipIns2 != null) {
				try {
					zipIns.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return map;
	}

	/**
	 * 读取APK信息
	 * 
	 * @param apkUrl
	 * @return
	 */
	public static Map<String, Object> readAPK(File file) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		try (ApkFile apkFile = new ApkFile(file)) {
			ApkMeta apkMeta = apkFile.getApkMeta();
			resMap.put("versionCode", apkMeta.getVersionCode());
			resMap.put("versionName", apkMeta.getVersionName());
			resMap.put("package", apkMeta.getPackageName());
			resMap.put("name", apkMeta.getName());
			/*for (UseFeature feature : apkMeta.getUsesFeatures()) {
				System.out.println(feature.getName());
			}*/
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resMap;
	}
	
	/**
	 * 删除文件
	 * @param fileName
	 * @return
	 */
	public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }
	
	/**
	 * 创建文件夹
	 * @param path
	 */
	public static void makeDir(String path) {
		File file = new File(path);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
	}
	
	/**
	 * 文件转成字符串
	 */
	public static String fileToString(String filePath, String encoding) {
		InputStreamReader reader = null;
		StringWriter writer = new StringWriter();
		try {
			File file = new File(filePath);
			if (encoding == null || "".equals(encoding.trim())) {
				reader = new InputStreamReader(new FileInputStream(file), encoding);
			} else {
				reader = new InputStreamReader(new FileInputStream(file));
			}
			// 将输入流写入输出流
			char[] buffer = new char[1024];
			int n = 0;
			while (-1 != (n = reader.read(buffer))) {
				writer.write(buffer, 0, n);
			}
			return writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	/** 
     * 将字符串写入指定文件(当指定的父路径中文件夹不存在时，会最大限度去创建，以保证保存成功！) 
     * 
     * @param res 原字符串 
     * @param filePath 文件路径 
     * @return 成功标记 
	 * @throws Exception 
     */ 
	public static boolean stringToFile(String res, String filePath) throws Exception {
		boolean flag = true;
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;
		try {
			File distFile = new File(filePath);
			if (!distFile.getParentFile().exists())
				distFile.getParentFile().mkdirs();
			bufferedReader = new BufferedReader(new StringReader(res));
			bufferedWriter = new BufferedWriter(new FileWriter(distFile));
			char buf[] = new char[1024]; // 字符缓冲区
			int len;
			while ((len = bufferedReader.read(buf)) != -1) {
				bufferedWriter.write(buf, 0, len);
			}
			bufferedWriter.flush();
		} catch (IOException e) {
			throw e;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bufferedWriter != null) {
				try {
					bufferedWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
	
	/** 
     * @Title: LoadPopertiesFile 
     * @Description: 加载.properties文件,并获取其中的内容(key-value) 
     * @param filePath 
     *            : 文件路径 
     */  
    public static Map<String,String> LoadPopertiesFile(String filePath) throws Exception {  
    	
    	Map<String,String> result = new HashMap<>();
    	
        if (null == filePath || "".equals(filePath.trim())) {  
            System.out.println("The file path is null,return");  
            return result;  
        }  
  
        filePath = filePath.trim();  
  
        // 获取资源文件  
        /*InputStream is = FileUtil.class.getClassLoader()  
                .getResourceAsStream(filePath); */
        InputStream is = null; 
  
        // 属性列表  
        Properties prop = new Properties();  
  
        try {  
        	is = new FileInputStream(filePath);
            // 从输入流中读取属性列表  
            prop.load(is);  
        } catch (Exception e) {  
            System.out.println("load file fail " + e);  
            throw e; 
        } finally {
        	if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				} 
        }
  
        // 返回Properties中包含的key-value的Set视图  
        Set<Entry<Object, Object>> set = prop.entrySet();  
        // 返回在此Set中的元素上进行迭代的迭代器  
        Iterator<Map.Entry<Object, Object>> it = set.iterator();  
        String key = null, value = null;  
        // 循环取出key-value  
        while (it.hasNext()) {  
            Entry<Object, Object> entry = it.next();  
            key = String.valueOf(entry.getKey());  
            value = String.valueOf(entry.getValue()); 
            result.put(key, value);
        }  
        return result;
    }  
    
    /**
     * 读入TXT文件
     */
	public static Set<String> readTxtFile(String filePath) throws Exception {
		
		Set<String> set = new HashSet<>();
		FileReader reader = null;
		BufferedReader br = null;
		try {
			reader = new FileReader(filePath);
			br = new BufferedReader(reader);
			String line = null;
			// 网友推荐更加简洁的写法
			while ((line = br.readLine()) != null) {
				// 一次读入一行数据
				if (line != null) 
					set.add(line.trim());
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (br != null) {
				br.close();
			}
		}
		return set;
	}
	
	/**
	 * 文件是否存在
	 * @param path
	 * @return
	 */
	public static boolean fileExist(String path) {
		File file = new File(path);
		if (file.exists() && file.isFile()) {
			return true;
		}
		return false;
	}
}
