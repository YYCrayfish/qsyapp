package com.manyu.videoshare.util.universally;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import com.manyu.videoshare.util.UriToPathUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Author：xushiyong
 * Date：2020/4/28
 * Descript：文件操作管理
 */
public class FileUtil {

//    //文件夹信息
//    private static List<String> dirNames= null;
//    //文件信息
//    private static List<String> fileNames = null;
//
//    //文件大小单位 ，我们可以通过添加SizeStr 来添加单位
//    private static String [] sizeStr = {"B","KB","MB","GB","PB"};
//    private static int count =0;
//    private static long fileSize = 0;

//    public static String fileSavePath = Environment.getExternalStorageDirectory()
//            + File.separator + Environment.DIRECTORY_DCIM
//            + File.separator + "Camera" + File.separator+"filelist.txt";

//    public static String CHECK_PATH = Environment.getExternalStorageDirectory()
//            + File.separator + Environment.DIRECTORY_DCIM
//            + File.separator + "Camera" + File.separator;
    //Environment.getExternalStorageDirectory().getPath()+File.separator+"filelist.txt";

//    /**
//     * 视频路径列表字符串写入本地txt
//     * @return 写入结果
//     */
//    public static String writePathList2File(String ...data) {
//        String strContent = "";
//
//        for (int i = 0 ; i < data.length ; i ++){
//            String temp = data[i];
//            if(temp.contains(CHECK_PATH)){
//                LOG.showE("###这个地址包含了："+temp+" "+CHECK_PATH);
//            }
//            else{
//                LOG.showE("！！！这个地址并不包含："+temp+" "+CHECK_PATH);
//                String movePath = CHECK_PATH + File.separator + "copy_" + UriToPathUtil.getFileNameByPath(temp);
//                // 如果不是和filelist在一个目录就复制一个副本到指定目录下
//                fileCopy(temp,movePath);
//                temp = movePath;
//            }
//            strContent += "file "+temp+"\n";
//        }
//
//        LOG.showE("写入完毕：预览fileList.txt->"+strContent);
//
//        try {
//            File file = new File(fileSavePath);
//            if(!file.exists())
//            file.createNewFile();
//
////            String content = "";
////            InputStream instream = new FileInputStream(file);
////            if (instream != null) {
////                InputStreamReader inputreader = new InputStreamReader(instream);
////                BufferedReader buffreader = new BufferedReader(inputreader);
////                String line;
////                //分行读取
////                while ((line = buffreader.readLine()) != null) {
////                    content += line;
////                }
////                instream.close();
////            }
////
////            LOG.showE("打印内容："+content);
//
//            // 以覆盖形式写入到文件中
//            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
//
//            raf.setLength(0);
//
//            // 这样会直接追加
//            //raf.seek(file.length());
//            raf.write(strContent.getBytes());
//            raf.close();
//
//            return fileSavePath;
//        } catch (Exception e) {
//            Log.e("TestFile", "Error on write File:" + e);
//        }
//
//        return null;
//    }


    /**
     * 复制文件到指定目录路径下
     * @param oldFilePath 当前旧文件
     * @param newFilePath 要复制到的路径
     * @return 是否成功
     */
    public static boolean fileCopy(String oldFilePath,String newFilePath) {
        try{
            //如果原文件不存在
            if(fileExists(oldFilePath) == false){
                return false;
            }

            File oldFile = new File(oldFilePath);
            //获得原文件流
            FileInputStream inputStream = new FileInputStream(oldFile);
            byte[] data = new byte[1024];

            File newFile = new File(newFilePath);
            //输出流
            FileOutputStream outputStream =new FileOutputStream(newFile);
            //开始处理流
            while (inputStream.read(data) != -1) {
                outputStream.write(data);
            }
            inputStream.close();
            outputStream.close();
            return true;
        }catch (Exception e){
            LOG.showE("报错了："+e.toString());
        }
        return false;
    }

    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /** 从给定路径加载图片*/
    public static Bitmap loadBitmap(String imgpath) {
        return BitmapFactory.decodeFile(imgpath);
    }

    /**
     * 从给定的路径加载图片，并指定是否自动旋转方向
     * @param imagePath  图片路径
     * @param adjustOritation
     * @return
     */
    public static Bitmap loadBitmap(String imagePath, boolean adjustOritation) {
        if (!adjustOritation) {
            return loadBitmap(imagePath);
        } else {
            Bitmap bm = loadBitmap(imagePath);
            int digree = 0;
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(imagePath);
            } catch (IOException e) {
                e.printStackTrace();
                exif = null;
            }
            if (exif != null) {
                // 读取图片中相机方向信息
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                // 计算旋转角度
                switch (ori) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        digree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        digree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        digree = 270;
                        break;
                    default:
                        digree = 0;
                        break;
                }
            }
            if (digree != 0) {
                // 旋转图片
                Matrix m = new Matrix();
                m.postRotate(digree);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
            }
            return bm;
        }
    }

    /**
     * 删除单个文件
     * @param   filePath    被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    private static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除文件夹以及目录下的文件
     * @param   filePath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    private static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }

    /**
     *  根据路径删除指定的目录或文件，无论存在与否
     *@param filePath  要删除的目录或文件
     *@return 删除成功返回 true，否则返回 false。
     */
    public static boolean deleteFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else {
                // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }

    /**
     * 文件分割方法
     * @param targetFile 分割的文件
     * @param cutSize 分割文件的大小
     * @return int 文件切割的个数
     */
    public static int getSplitFile(File targetFile ,long cutSize ) {

        //计算切割文件大小
        int count = targetFile.length() % cutSize == 0 ? (int) (targetFile.length() / cutSize) :
                (int) (targetFile.length() / cutSize + 1);

        RandomAccessFile raf = null;
        try {
            //获取目标文件 预分配文件所占的空间 在磁盘中创建一个指定大小的文件  r 是只读
            raf = new RandomAccessFile(targetFile, "r");
            long length = raf.length();//文件的总长度
            long maxSize = length / count;//文件切片后的长度
            long offSet = 0L;//初始化偏移量

            for (int i = 0; i < count - 1; i++) { //最后一片单独处理
                long begin = offSet;
                long end = (i + 1) * maxSize;

                offSet = getWrite(targetFile.getAbsolutePath(), i, begin, end);
            }
            if (length - offSet > 0) {
                getWrite(targetFile.getAbsolutePath(), count-1, offSet, length);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            LOG.showE("发生异常："+e.toString());
        } finally {
            try {
                if(raf != null)
                    raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    /**
     * 获取文件后缀名 例如：.mp4 /.jpg /.apk
     * @param file 指定文件
     * @return String 文件后缀名
     */
    public static String suffixName (File file){
        String fileName=file.getName();
        String fileTyle=fileName.substring(fileName.lastIndexOf("."),fileName.length());
        return fileTyle;
    }

    /**
     * 指定文件每一份的边界，写入不同文件中
     * @param file 源文件地址
     * @param index 源文件的顺序标识
     * @param begin 开始指针的位置
     * @param end 结束指针的位置
     * @return long
     */
    public static long getWrite(String file,int index,long begin,long end ){

        long endPointer = 0L;

        String a=file.split(suffixName(new File(file)))[0];
        try {
            //申明文件切割后的文件磁盘
            RandomAccessFile in = new RandomAccessFile(new File(file), "r");
            //定义一个可读，可写的文件并且后缀名为.tmp的二进制文件
            //读取切片文件
            File mFile = new File(a + "_" + index + ".tmp");
            if(!mFile.exists())
                mFile.createNewFile();
            //如果存在
            if (mFile.exists()) {
                RandomAccessFile out = new RandomAccessFile(mFile, "rw");
                //申明具体每一文件的字节数组
                byte[] b = new byte[1024];
                int n = 0;
                //从指定位置读取文件字节流
                in.seek(begin);
                //判断文件流读取的边界
                while ((n = in.read(b)) != -1 && in.getFilePointer() <= end) {
                    //从指定每一份文件的范围，写入不同的文件
                    out.write(b, 0, n);
                }

                //定义当前读取文件的指针
                endPointer = in.getFilePointer();
                //关闭输入流
                in.close();
                //关闭输出流
                out.close();
            }else {
                //不存在

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return endPointer - 1024;
    }

    /**
     * 文件合并
     * @param fileName 指定合并文件
     * @param targetFile 分割前的文件
     * @param cutSize 分割文件的大小
     */
    public static void merge(String fileName,File targetFile ,long cutSize) {

        int tempCount = targetFile.length() % cutSize == 0 ? (int) (targetFile.length() / cutSize) :
                (int) (targetFile.length() / cutSize + 1);
        //文件名
        String a=targetFile.getAbsolutePath().split(suffixName(targetFile))[0];

        RandomAccessFile raf = null;
        try {
            //申明随机读取文件RandomAccessFile
            raf = new RandomAccessFile(new File(fileName), "rw");
            //开始合并文件，对应切片的二进制文件
            for (int i = 0; i < tempCount; i++) {
                //读取切片文件
                File mFile = new File(a + "_" + i + ".tmp");
                RandomAccessFile reader = new RandomAccessFile(mFile, "r");
                byte[] b = new byte[1024];
                int n = 0;
                //先读后写
                while ((n = reader.read(b)) != -1) {//读
                    raf.write(b, 0, n);//写
                }

                //80c99805f2f0002b42f3c00ad18f26ae
                // 删除文件
                mFile.delete();
            }
            raf.write(new Random().nextInt(10));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
