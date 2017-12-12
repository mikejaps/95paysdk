package com.gandalf.daemon.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

import de.mindpipe.android.logging.log4j.LogConfigurator;


public class XL_log {
    public static boolean isSDCanRead = true;// sd卡可读，由于其他地方调用，使用static

    public static boolean DEBUG = true;// 是否打日志
    public static final String LOG_PATH_NAME = "YouBao-quite";
    private static String LOG_DIR = null;
    private static String LOG_FILE_PATH = null;
    private static final String DEFAULT_LOG_NAME = "xl_log";
    private static String LOG_NAME_SYSMBOL = null;

    private static LogConfigurator smLogConfigurator = null;

    public static String getLogPath() {
        return LOG_FILE_PATH;
    }


    public synchronized static void init(Context context, String logName) {
        if (smLogConfigurator != null) {
            String dir = Util.getSDCardDir(context) + LOG_PATH_NAME;
            String filepath = null;
            if (TextUtils.isEmpty(logName)) {
                filepath = dir + File.separator + DEFAULT_LOG_NAME;
            } else {
                filepath = dir + File.separator + logName;
                LOG_FILE_PATH = filepath;
                LOG_NAME_SYSMBOL = logName;
                LOG_DIR = dir;
            }
            Util.ensureDir(Util.getSDCardDir(context) + LOG_PATH_NAME);

            File f = new File(filepath);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            smLogConfigurator.setFileName(filepath);
            // Set log level of a specific logger
            // 等级可分为OFF、FATAL、ERROR、WARN、INFO、DEBUG、ALL
            // 设置为OFF即关闭,开发调试时一般设置成DEBUG
            String packageName = context.getPackageName();
            if (DEBUG) {
                smLogConfigurator.setLevel(packageName, Level.ALL);
            } else {
                smLogConfigurator.setLevel(packageName, Level.OFF);
            }

            try {
                if (f.canWrite()) {
                    isSDCanRead = true;
                    smLogConfigurator.setUseLogCatAppender(false);
                    // smLogConfigurator.setUseFileAppender(false);
                    smLogConfigurator.configure();
                } else {
                    isSDCanRead = false;
                }
            } catch (Exception e) {
                isSDCanRead = false;
            }
        } else {
            if (isSdcardExist()) {
                smLogConfigurator = new LogConfigurator();
                String dir = Util.getSDCardDir(context) + LOG_PATH_NAME;
                // 配置生成的日志文件路径,log4j会自动备份文件，最多5个文件
                String filepath = null;
                if (TextUtils.isEmpty(logName)) {
                    filepath = dir + File.separator + DEFAULT_LOG_NAME;
                } else {
                    filepath = dir + File.separator + logName;
                    LOG_FILE_PATH = filepath;
                    LOG_NAME_SYSMBOL = logName;
                    LOG_DIR = dir;
                }
                Util.ensureDir(Util.getSDCardDir(context) + LOG_PATH_NAME);
                File f = new File(filepath);
                if (!f.exists()) {
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                smLogConfigurator.setFileName(filepath);
                // Set log level of a specific logger
                // 等级可分为OFF、FATAL、ERROR、WARN、INFO、DEBUG、ALL
                // 设置为OFF即关闭,开发调试时一般设置成DEBUG
                String packageName = context.getPackageName();
                if (DEBUG) {
                    smLogConfigurator.setLevel(packageName, Level.ALL);
                } else {
                    smLogConfigurator.setLevel(packageName, Level.OFF);
                }

                try {
                    if (f.canWrite()) {
                        isSDCanRead = true;
                        smLogConfigurator.configure();
                    } else {
                        isSDCanRead = false;
                    }
                } catch (Exception e) {
                    isSDCanRead = false;
                }
            }
        }
    }

    public static String getLogContent(Context context) {
        StringBuffer sb = new StringBuffer();
        if (LOG_NAME_SYSMBOL != null && LOG_DIR != null) {
            File dir = new File(LOG_DIR);
            try {
                if (dir.exists() && dir.isDirectory()) {
                    String[] files = dir.list();
                    for (int i = 0; i < files.length; i++) {
                        String fileName = dir.getAbsolutePath() + File.separator + files[i];
                        if (fileName != null && fileName.contains(LOG_NAME_SYSMBOL)) {
                            File log = new File(fileName);
                            if (log.exists() && log.isFile()) {
                                StringBuilder content = FileUtil.readFile(log.getAbsolutePath());
                                sb.append(content);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return sb.toString();
    }

    public static void clearLogFile(Context context) {
        // if (smLogConfigurator != null) {
        // smLogConfigurator = null;
        // }
        if (LOG_NAME_SYSMBOL != null && LOG_DIR != null) {
            File dir = new File(LOG_DIR);
            try {
                if (dir.exists() && dir.isDirectory()) {
                    String[] files = dir.list();
                    for (int i = 0; i < files.length; i++) {
                        String fileName = dir.getAbsolutePath() + File.separator + files[i];
                        if (fileName != null && fileName.contains(LOG_NAME_SYSMBOL)) {
                            File log = new File(fileName);
                            if (log.exists() && log.isFile()) {
                                log.delete();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        // if (LOG_FILE_PATH != null) {
        // File f = new File(LOG_FILE_PATH);
        // if (!f.exists()) {
        // try {
        // f.createNewFile();
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // }
        // }
        // smLogConfigurator.setFileName(LOG_FILE_PATH);
        if (LOG_NAME_SYSMBOL != null) {
            init(context, LOG_NAME_SYSMBOL);
        }
        // smLogConfigurator.configure();
    }

    private Logger log = null;

    public XL_log(Class<?> obj) {
        if (isSdcardExist()) {
            log = Logger.getLogger(obj);
        }
    }

    // 输出Level.DEBUG级别日志,一般开发调试信息用
    public void debug(Object message) {
        if (null != log && log.isDebugEnabled()) {
            log.debug(getFunctionName() + " msg= " + message);
        }
    }

    // 输出Level.INFO级别日志
    public void info(Object message) {
        if (null != log && log.isInfoEnabled()) {
            log.info("currentThread id=" + Thread.currentThread().getId() + " msg= " + message);
        }
    }

    // 输出Level.WARN级别日志
    public void warn(Object message) {
        if (null != log && log.isEnabledFor(Level.WARN)) {
            log.warn("currentThread id=" + Thread.currentThread().getId() + " msg= " + message);
        }
    }

    // 输出Level.ERROR级别日志,一般catch住异常后使用,使用e.printStackTrace()打印出错误信息;
    public void error(Object message) {
        if (null != log && log.isEnabledFor(Level.ERROR)) {
            log.error(getFunctionName() + " msg= " + message);
        }
    }

    // 输出Level.FATAL级别日志
    public void fatal(Object message) {
        if (null != log && log.isEnabledFor(Level.FATAL)) {
            log.fatal("currentThread id=" + Thread.currentThread().getId() + " msg= " + message);
        }
    }

    private static boolean isSdcardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    // 获取线程ID log所在方法名 log所在行数
    private String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                // 本地方法native jni
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                // 线程
                continue;
            }
            if (st.getClassName().equals(this.getClass().getName())) {
                // 构造方法
                continue;
            }
            // return "[ " + Thread.currentThread().getName() + ": "
            // + st.getFileName() + ":" + st.getLineNumber() + " "
            // + st.getMethodName() + " ]";
            return "currentThread id=" + Thread.currentThread().getId() + " methodName = " + st.getMethodName()
                    + ":line=" + st.getLineNumber();
        }
        return null;
    }
}
