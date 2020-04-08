package com.liuyang.file_change.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class FileEntity implements Serializable {

    private List<FileEntity> files;
    private List<FileEntity> childDirs;
    private String fileName;
    private boolean delete;
    private String size;
    private String date;

    public FileEntity(String dirName){
        this.fileName = dirName;
        this.files = new ArrayList<>();
        this.childDirs = new ArrayList<>();
        this.delete = false;
    }
}
