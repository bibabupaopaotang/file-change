package com.liuyang.file_change.entity;


public class Cache {

    private Cache(){

    }

    private static FileEntity fileCache = new FileEntity("root");

    public static FileEntity getFileCache() {
        return fileCache;
    }

    public static void setFileCache(FileEntity fileCache) {
        Cache.fileCache = fileCache;
    }
}
