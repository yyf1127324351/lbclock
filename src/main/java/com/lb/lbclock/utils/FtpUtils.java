package com.lb.lbclock.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;

public class FtpUtils {
    private static Logger log = LoggerFactory.getLogger(FtpUtils.class);
    private static final String ftpPortalRootDir = "portal";

    public static String localCharset = Charset.defaultCharset().toString();
    // FTP协议里面，规定文件名编码为iso-8859-1
    public static String serverCharset = "iso-8859-1";

    @Value("${fileUrl}")
    private static String fileUrl;
    @Value("${ftpIp}")
    private static String ftpIp;
    @Value("${ftpPort}")
    private static String ftpPort;
    @Value("${ftpUserName}")
    private static String ftpUserName;
    @Value("${ftpPassword}")
    private static String ftpPassword;

    @Value("${configEnv}")
    private static String configEnv;

    public static String ftpUploadFile(String filename, InputStream inputStream) {
        // 去除名字中的空客串
        filename = StringUtils.trimAllWhitespace(filename);
        FTPClient ftpClient = null;
        boolean bl = true;
        String fileMakeDir = getFileMakeDir(filename);
        String dailyFileDir = getDailyFileDir();
        String newFileName = getNewName(filename);
        String fileDirPath = ftpPortalRootDir + "/" + fileMakeDir + "/" + dailyFileDir;
        try {
            ftpClient = getFtpClient();
            if (null != ftpClient) {
                ftpMakeDirectory(ftpClient, fileDirPath);
                // 需要转码
                String ftpFileName = new String(newFileName.getBytes(localCharset), serverCharset);
                bl = ftpClient.storeFile(ftpFileName, inputStream);
            }
        } catch (Exception e) {
            throw new RuntimeException("FTP客户端出错！", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
            try {
                if (ftpClient != null) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
        }
        if (!bl) {
            throw new RuntimeException("保存文件至FTP服务器失败！");
        }
        if (configEnv.equals("online")) {
            // 是否线上环境
            return "http://" + fileUrl + "/" + fileDirPath + "/" + newFileName;
        } else {
            return "http://" + ftpIp + ":" + ftpPort + "/" + fileDirPath + "/" + newFileName;
        }
    }

    private static void ftpMakeDirectory(FTPClient ftpClient, String filePath) throws IOException {
        if (null != filePath) {
            String[] paths = filePath.trim().split("/");
            for (String path : paths) {
                if (!ftpClient.changeWorkingDirectory(path)) {
                    ftpClient.makeDirectory(path);
                    ftpClient.changeWorkingDirectory(path);
                }
            }
        }
    }

    /**
     * 获取文件在ftp上面的路径
     */
    public static String getFileFtpPath(String path) {
        if (configEnv.equals("online")) {
            // 是否线上环境
            return path.replace("http://" + fileUrl, "");
        } else {
            return path.replace("http://" + ftpIp + ":" + ftpPort, "");
        }
    }

    public static String ftpUploadFile(String filename, String base64) {
        String url = null;
        File tempImg = null;
        try {
            base64 = base64.replaceFirst("data:image/jpeg;base64,", "");
            byte[] byteimg = new BASE64Decoder().decodeBuffer(base64.trim());
            for (int i = 0; i < byteimg.length; ++i) {
                if (byteimg[i] < 0) {// 调整异常数据
                    byteimg[i] += 256;
                }
            }
            url = ftpUploadFile(filename, new ByteArrayInputStream(byteimg));
        } catch (Exception e) {
            log.error("ftpUploadFile", e);
            throw new RuntimeException("base64转图片报错！", e);
        } finally {
            if (tempImg != null) {
                tempImg.delete();
            }
        }
        return url;
    }

    public static InputStream toInputStream(String base64) {
        try {
            base64 = base64.replaceFirst("data:image/jpeg;base64,", "");
            byte[] byteimg = new BASE64Decoder().decodeBuffer(base64.trim());
            for (int i = 0; i < byteimg.length; ++i) {
                if (byteimg[i] < 0) {// 调整异常数据
                    byteimg[i] += 256;
                }
            }
            return new ByteArrayInputStream(byteimg);
        } catch (Exception e) {
            log.error("toInputStream", e);
            throw new RuntimeException("base64转化输入流error！", e);
        }
    }

    // 每天要创建的文件夹名
    private static String getDailyFileDir() {
        String dateDir = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        return dateDir;
    }

    // 进入创建每天的文件夹 images或files
    // windows与Linux文件分隔符？ 只能用 "/"，不能File.separator
    private static String getFileMakeDir(String filename) {
        String root = isImageFile(filename) ? "images" : "files";
        return root;
    }

    // 新文件名上加上时间戳
    private static String getNewName(String filename) {
        int index = filename.lastIndexOf('.');
        String time = DateFormatUtils.format(new Date(), "HHmmssSSS");
        // 文件名后缀
        if (index >= 0) {
            String extension = filename.substring(index + 1).toLowerCase();
            String prefix = filename.substring(0, index);
            return prefix + "_" + time + "." + extension;
        } else {
            return filename + "_" + time;
        }
    }

    private static boolean isImageFile(String filename) {
        String[] imgSuffix = new String[]{".jpg", ".jpeg", ".gif", ".png", ".bmp"};
        for (String suf : imgSuffix) {
            if (filename.toLowerCase().endsWith(suf)) {
                return true;
            }
        }
        return false;
    }

    public static FTPClient getFtpClient() throws SocketException, IOException {
        FTPClient ftp = new FTPClient();
        ftp.setDefaultPort(21);
        ftp.connect(ftpIp);
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            log.error("FTP server refused connection");
            ftp.disconnect();
            return null;
        }
        if (!ftp.login(ftpUserName, ftpPassword)) {
            log.error("FTP server refused login ");
            ftp.logout();
            ftp.disconnect();
            return null;
        }
        String charset = localCharset;
        // 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
        if (FTPReply.isPositiveCompletion(ftp.sendCommand("OPTS UTF8", "ON"))) {
            charset = "utf-8";
        }

        ftp.setControlEncoding(charset);
        ftp.setCharset(Charset.forName("UTF-8"));
        ftp.setBufferSize(1024);
        // 设置文件类型（二进制）
        ftp.setCharset(Charset.forName("UTF-8"));
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();
        return ftp;
    }

    /**
     * 删除文件
     *
     * @param hostname FTP服务器地址
     * @param port     FTP服务器端口号
     * @param username FTP登录帐号
     * @param password FTP登录密码
     * @param pathname FTP服务器保存目录
     * @param filename 要删除的文件名称
     * @return
     */
    public static boolean deleteFile(String hostname, int port, String username, String password, String pathname,
                                     String filename) {
        boolean flag = false;
        FTPClient ftpClient = new FTPClient();
        try {
            // 连接FTP服务器
            ftpClient.connect(hostname, port);
            // 登录FTP服务器
            ftpClient.login(username, password);
            // 验证FTP服务器是否登录成功
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                return flag;
            }
            // 切换FTP目录
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.dele(filename);
            ftpClient.logout();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                } catch (IOException e) {

                }
            }
        }
        return flag;
    }

    @SuppressWarnings("static-access")
    public static byte[] downloadFile(String path, String fileName) {
        byte[] bytes = null;

        try {
            FTPClient ftp = getFtpClient();
            ftp.changeWorkingDirectory(getFileFtpPath(path));
            // 列出该目录下所有文件
            FTPFile[] fs = ftp.listFiles(fileName);
            // 遍历所有文件，找到指定的文件
            for (FTPFile file : fs) {
                if (file.getName().equals(fileName)) {
                    InputStream in = null;
                    // 下载文件
                    ftp.setBufferSize(1024);
                    String charset = localCharset;
                    // 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
                    if (FTPReply.isPositiveCompletion(ftp.sendCommand("OPTS UTF8", "ON"))) {
                        charset = "utf-8";
                    }
                    ftp.setControlEncoding(charset);
                    ftp.setFileType(ftp.BINARY_FILE_TYPE);
                    String remoteAbsoluteFile = file.getName();
                    remoteAbsoluteFile = new String(remoteAbsoluteFile.getBytes("UTF-8"), "ISO-8859-1");
                    in = ftp.retrieveFileStream(remoteAbsoluteFile);
                    bytes = input2byte(in);
                    in.close();
                }
            }

            ftp.logout(); // 退出连接
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * byte[] 转 file
     *
     * @param bytes
     * @param path
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static void byteToFile(byte[] bytes, String path) {
        OutputStream os = null;
        try {
            // 根据绝对路径初始化文件
            File localFile = new File(path);
            if (!localFile.exists()) {
                localFile.createNewFile();
            }
            // 输出流
            os = new FileOutputStream(localFile);
            os.write(bytes);
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != os) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 文件转成 byte[] <一句话功能简述> <功能详细描述>
     *
     * @param inStream
     * @return
     * @throws IOException
     * @see [类、类#方法、类#成员]
     */
    public static byte[] input2byte(InputStream inStream) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();

        swapStream.close();

        return in2b;
    }

    /**
     * @param path----全路径
     * @param localFile----目标文件
     * @return
     */
    public static boolean downFileByHttp(String path, File localFile) {
        OutputStream tmpOut = null;
        InputStream tmpIn = null;
        boolean result = false;
        try {
            tmpOut = new FileOutputStream(localFile);
            // ftp服务器文件URL地址
            URL url = new URL(path);
            // 建立HTTP连接
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            tmpIn = httpConn.getInputStream();

            // 读取流
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = tmpIn.read(buffer, 0, 1024)) > 0) {
                tmpOut.write(buffer, 0, len);
            }
            result = true;
        } catch (Exception e) {

        } finally {
            try {
                // 关闭流
                tmpOut.close();
                tmpIn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 从FTP服务器下载文件
     *
     * @param ftpFile
     * @param localFile
     */
    public static void downloadFtpFile(String ftpFile,
                                       File localFile) {
        FTPClient ftpClient = null;
        OutputStream os = null;

        String ftpPath = ftpFile.substring(ftpFile.indexOf(fileUrl) + fileUrl.length(), ftpFile.lastIndexOf("/"));
        String ftpFileName = ftpFile.substring(ftpFile.lastIndexOf("/") + 1);
        try {
            ftpClient = getFtpClient();
            ftpClient.setControlEncoding("UTF-8"); // 中文支持
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(ftpPath);
            os = new FileOutputStream(localFile);
            ftpClient.retrieveFile(ftpFileName, os);
        } catch (FileNotFoundException e) {
            System.out.println("没有找到" + ftpPath + "文件");
            e.printStackTrace();
        } catch (SocketException e) {
            System.out.println("连接FTP失败.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("文件读取错误。");
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(os);
            try {
                if (ftpClient != null) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
        }
    }


    public static String ftpUploadFileUrl(String filename, InputStream inputStream) {
        // 去除名字中的空客串
        filename = StringUtils.trimAllWhitespace(filename);
        FTPClient ftpClient = null;
        boolean bl = true;
        String fileMakeDir = getFileMakeDir(filename);
        String dailyFileDir = getDailyFileDir();
        String newFileName = getNewName(filename);
        String fileDirPath = ftpPortalRootDir + "/" + fileMakeDir + "/" + dailyFileDir;
        try {
            ftpClient = getFtpClient();
            if (null != ftpClient) {
                ftpMakeDirectory(ftpClient, fileDirPath);
                // 需要转码
                String ftpFileName = new String(newFileName.getBytes(localCharset), serverCharset);
                bl = ftpClient.storeFile(ftpFileName, inputStream);
            }
        } catch (Exception e) {
            throw new RuntimeException("FTP客户端出错！", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
            try {
                if (ftpClient != null) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
        }
        if (!bl) {
            throw new RuntimeException("保存文件至FTP服务器失败！");
        }
        return "http://hr.digi800.com" + "/" + fileDirPath + "/" + newFileName;
    }
}
