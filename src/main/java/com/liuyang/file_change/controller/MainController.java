package com.liuyang.file_change.controller;

import com.alibaba.fastjson.JSONArray;
import com.liuyang.file_change.cahce.CacheConfig;
import com.liuyang.file_change.entity.Cache;
import com.liuyang.file_change.entity.FileEntity;
import com.liuyang.file_change.entity.Menu;
import com.liuyang.file_change.util.JsonUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@ResponseBody
@Controller
public class MainController {

    @Resource
    private CacheConfig cacheConfig;

    @Value("${filePath}")
    private String fileRepDir;

    @Value("${menuPath}")
    private String menuPath;

    private static final String ENCODE = "UTF-8";
    public static final Logger logger = Logger.getLogger(MainController.class);

    @PostMapping("/upload")
    public boolean upload(@RequestParam(value = "file", required = false) MultipartFile[] files,
                          @RequestParam(value = "category", required = false) String category) {
        if (files == null || files.length == 0) {
            return true;
        }

        String folderName = fileRepDir + (StringUtils.isEmpty(category) ? "" : File.separator +
                category.replace("/", File.separator));
        File folder = new File(folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            File uploadfile = new File(folderName + File.separator + fileName);
            try {
                file.transferTo(uploadfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cacheConfig.cacheFiles();
        return true;
    }

    @PostMapping("/newFolder")
    public boolean newFolder(String folderPath, String folderName) {

        if (StringUtils.isEmpty(folderName)) {

            return false;
        }
        folderPath = StringUtils.isEmpty(folderPath) ? "" : File.separator + folderPath;
        folderPath = fileRepDir + folderPath.replace("/", File.separator) + File.separator + folderName;
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
            cacheConfig.cacheFiles();
        }
        return true;
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response, String fileName) throws UnsupportedEncodingException {

        try {
            fileName = URLDecoder.decode(fileName, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String path = fileRepDir + File.separator + fileName;
        File file = new File(path);
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode((getExactiFileName(fileName)), ENCODE));
        response.setHeader("Access-Control-Allow-Origin", "*");
        try {
            OutputStream os = response.getOutputStream();
            FileInputStream in = new FileInputStream(file);
            FileChannel channel = in.getChannel();
            int bufferSize = 1024 * 1024;
            ByteBuffer bb = ByteBuffer.allocate(6 * bufferSize);
            byte[] bytes = new byte[bufferSize];
            int read;
            int get;
            while ((read = channel.read(bb)) != -1) {

                if (read == 0) continue;
                ;
                bb.position(0);
                bb.limit(read);
                while (bb.hasRemaining()) {
                    get = Math.min(bb.remaining(), bufferSize);
                    bb.get(bytes, 0, get);
                    os.write(bytes);
                }
                bb.clear();
            }
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getExactiFileName(String fileName) {
        if (fileName.indexOf(File.separator) < 0)
            return fileName;
        return fileName.substring(fileName.lastIndexOf(File.separator) + 1);
    }

    @PostMapping("/getFileList")
    public FileEntity getFileList(String fileName, String currentPath) throws UnsupportedEncodingException {
        fileName = URLDecoder.decode(fileName, ENCODE);
        currentPath = URLDecoder.decode(currentPath, ENCODE);
        FileEntity fileEntity = null;
        if (StringUtils.isEmpty(fileName)) {
            fileEntity = StringUtils.isEmpty(currentPath) ? Cache.getFileCache() : getTheDir(Cache.getFileCache(), currentPath);
            if (fileEntity == null)
                fileEntity = Cache.getFileCache();
        } else {
            fileEntity = new FileEntity("root");
            getTheFile(Cache.getFileCache(), fileEntity, fileName);
        }
    return fileEntity;
    }

    private FileEntity getTheFile(FileEntity file, FileEntity parent, String fileName) {
        List<FileEntity> files = file.getFiles();
        if (!CollectionUtils.isEmpty(files)) {
            for (FileEntity singleFile : files) {
                String reallyFileName = singleFile.getFileName();
                reallyFileName = reallyFileName.substring(reallyFileName.indexOf("\\") + 1);
                if (reallyFileName.toLowerCase().indexOf(fileName.toLowerCase()) != -1) {
                    parent.getFiles().add(singleFile);
                }
            }
        }

        List<FileEntity> dirs = file.getChildDirs();
        if (!CollectionUtils.isEmpty(dirs)) {
            for (FileEntity dir : dirs) {
                getTheFile(dir, parent, fileName);
            }
        }
        return parent;
    }

    private FileEntity getTheDir(FileEntity root, String dirName) {
        for (FileEntity child : root.getChildDirs()) {
            if (child.getFileName().equalsIgnoreCase(dirName.replace("/", File.separator))) {
                return child;
            } else {
                FileEntity one = getTheDir(child, dirName);
                if (one != null) {
                    return one;
                }
            }
        }
        return null;
    }

    @PostMapping("/getMenuList")
    public List<Menu> getMenuList() {
        String filePath = StringUtils.isEmpty(menuPath) ? "" : fileRepDir + File.separator + menuPath;
        String menuString = null;
        try {
            menuString = JsonUtil.readJsonStringFromFile(filePath, "Menu.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(menuString)) {
            return new ArrayList<>();
        }
        return JSONArray.parseArray(menuString, Menu.class);
    }

    @PostMapping("/deleteFile")
    public boolean deleteFile(String fileName) throws UnsupportedEncodingException {
        fileName = URLDecoder.decode(fileName, ENCODE);
        File file = new File(fileRepDir + File.separator + fileName);
        if (!file.exists()) {
            return true;
        }
        if (file.delete()) {
            cacheConfig.cacheFiles();
        }
        return true;
    }
}
