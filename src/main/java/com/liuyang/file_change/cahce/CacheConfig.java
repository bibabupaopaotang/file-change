package com.liuyang.file_change.cahce;

import com.liuyang.file_change.entity.Cache;
import com.liuyang.file_change.entity.FileEntity;
import com.liuyang.file_change.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

@Component
public class CacheConfig {

    @Value("${filePath}")
    private String fileRepDir;

    @PostConstruct
    public void cacheFiles(){
        File root = new File(fileRepDir);
        if(!root.exists())
            root.mkdirs();
        synchronized (Cache.class){
            FileEntity newRoot = FileUtil.getFiles(root, null,fileRepDir);
            Cache.setFileCache(newRoot);
        }
    }

}
