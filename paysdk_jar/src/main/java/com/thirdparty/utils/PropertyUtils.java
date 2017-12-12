package com.thirdparty.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * @创建者: Administrator
 * @创建时间: 2016/9/6 16:44
 * @类描述:
 * @版本号:
 * @修改者: Administrator
 * @修改时间: 2016/9/6 16:44
 * @修改描述:
 */
public class PropertyUtils {
    private static PropertyUtils instance;
    private static Properties prop = null;
    private static OutputStream os = null;
    private static String path;

    private PropertyUtils() {
        prop = new Properties();
    }

    public static PropertyUtils init(String filePath, String file) {
        if (instance == null) {
            synchronized (PropertyUtils.class) {
                try {
                    instance = new PropertyUtils();
                    createFile(filePath, file);
                    path = filePath + file;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                createFile(filePath, file);
                path = filePath + file;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    /***
     * 创建文件
     *
     * @param filePath
     * @return
     */
    private static boolean createFile(String filePath, String files) {
        try {
            File dir = new File(filePath);
            if (!dir.exists())
                dir.mkdirs();
            File file = new File(dir, files);
            if (!file.exists())
                file.createNewFile();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     * 根据主键key读取主键的值value
     *
     * @param key 键名
     * @return
     */
    public String readValue(String key) {
        try {
            prop.load(new FileInputStream(path));
            return prop.getProperty(key);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /***
     * 更新（或插入）一对properties信息(主键及其键值)
     * 如果该主键已经存在，更新该主键的值；
     * 如果该主键不存在，则插件一对键值
     *
     * @param keyname  键名
     * @param keyvalue 键值
     */
    public void writeProperties(String keyname, String keyvalue) {
        try {
            os = new FileOutputStream(path);
            // 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。
            // 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果
            prop.setProperty(keyname, keyvalue);
            // 以适合使用 load 方法加载到 Properties 表中的格式，
            // 将此 Properties 表中的属性列表（键和元素对）写入输出流
            prop.store(os, "Update '" + keyname + "' value");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
