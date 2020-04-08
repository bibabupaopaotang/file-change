package com.liuyang.file_change.util;


import com.liuyang.file_change.entity.FileEntity;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FileUtil {

    private FileUtil(){};

    public static final String FULL_DATTIME_FORMAT = "yyy/MM/dd HH:mm:ss";

    private static String getName(File file, String fileRepDir){
        return file.getAbsolutePath().substring(fileRepDir.length() + 1);
    }

    public static FileEntity getFiles(File file, FileEntity parent, String fileRepDir){

        if(!file.exists()){
            return  parent;
        }

        if(file.isFile() && parent != null){
            String name = getName(file, fileRepDir);
            String size = getFileSize(file.length());
            String date = getFileDate(file.lastModified());
            FileEntity singleFile = new FileEntity(name);
            singleFile.setDelete(true);
            singleFile.setSize(size);
            singleFile.setDate(date);
            parent.getFiles().add(singleFile);
            return parent;
        }

        FileEntity dir = new FileEntity(parent == null ? "root" : getName(file, fileRepDir));
        File[] files = file.listFiles();
        if(files != null && files.length > 0){
            for(File f : files){
                getFiles(f, dir , fileRepDir);
            }
        }else {
            dir.setDelete(true);
        }

        if(parent != null){
            dir.setDate(getFileDate(file.lastModified()));
            parent.getChildDirs().add(dir);
        }
        return dir;
    }

    private static String getFileSize(Long size){
        if(size == 0){
            return "0KB";
        }
        if(size < 1024){
            return  size + "B";
        }

        DecimalFormat format = new DecimalFormat("0.00");
        double dSize = size;
        dSize = dSize / 1024;
        if(dSize < 1024){
            return Math.round(dSize) +"KB";
        }

        dSize = dSize / 1024;
        if(dSize < 1024){
            return Math.round(dSize) +"MB";
        }

        dSize = dSize / 1024;
        if(dSize < 1024){
            return Math.round(dSize) +"GB";
        }
        throw new IllegalArgumentException("");
    }

    private static String getFileDate(long modifiedDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(modifiedDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FULL_DATTIME_FORMAT);
        return simpleDateFormat.format(calendar.getTime());
    }
}
