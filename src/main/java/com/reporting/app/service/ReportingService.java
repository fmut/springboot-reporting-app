package com.reporting.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reporting.app.configuration.FTPServerConfig;
import com.reporting.app.entity.TestEntity;
import com.reporting.app.model.Result;
import com.reporting.app.repository.TestRepository;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.util.zip.GZIPOutputStream;


@Component
public class ReportingService {

    @Autowired
    FTPServerConfig ftpServerConfig;
    @Autowired
    TestRepository repository;


    public Result<String> createRapor() throws IOException {
        Result<String> result = new Result<String>();
        try {
            Instant start = Instant.now();
            // Create file
            String fileName = createGZFile();
            // Write file
            writeToGZFile(fileName);
            // Compress file
            compressToFile(fileName);
            Instant end = Instant.now();
            Duration timeElapsed = Duration.between(start, end);

            System.out.println("Creating folder time:" + timeElapsed);
            start = Instant.now();
            fileName = fileName + ".gz";

            // Upload file
            uploadFilesToSFTPServer(fileName);

            end = Instant.now();
            timeElapsed = Duration.between(start, end);
            System.out.println("File uploading :" + timeElapsed);

            result.setCode("100");
            result.setMessage("Success.");
        } catch (Exception ex) {
            result.setData(String.valueOf(ex));
            result.setMessage(ex.getMessage());
        }
        return result;
    }

    private void compressToFile(String fileName) throws IOException {
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(new FileOutputStream(fileName + ".gz"),true);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fileName));
        byte[] buffer = new byte[1024];
        int len;
        while ((len = bufferedInputStream.read(buffer)) > 0) {
            gzipOutputStream.write(buffer, 0, len);
        }
        gzipOutputStream.close();
        bufferedInputStream.close();
        File file = new File(fileName);
        file.delete();
    }

    private String createGZFile() throws IOException {
        String pathName = "tmp/";
        String fileName = "reportfile";
        LocalDateTime dateTime = LocalDateTime.now();
        fileName = pathName + fileName + dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".abn";
        File file = new File(fileName);
        file.createNewFile();
        return fileName;
    }

    private String convertToSpesificFormat(TestEntity testEntity) {
       return toQS(testEntity);
    }

    private void writeToGZFile(String fileName) throws IOException {
        Iterator allTestInfo = repository.findAll().iterator();
        List<String> infoList = new ArrayList<>();

        while(allTestInfo.hasNext()){
            TestEntity item = (TestEntity)allTestInfo.next();
            infoList.add(convertToSpesificFormat(item));
        }
        Iterator<String> ite = infoList.iterator();
        while(ite.hasNext()){
            FileUtils.writeStringToFile(new File(fileName), ite.next(),"UTF-8", true);
        }
        infoList.clear();
        infoList = null;
    }

    public static String toQS(Object object) {

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map =
                objectMapper.convertValue(
                        object, Map.class);
        StringBuilder qs = new StringBuilder();
        map.keySet().remove("id");
        for (String key : map.keySet()) {
            //If you want manipulate value, Can use here
            if (map.get(key)==null){
                qs.append("|;|");
            }else {
                qs.append(map.get(key));
                qs.append("|;|");
            }
        }

        if (qs.length() != 0) {
            qs.delete(qs.length() - 3, qs.length());
        }
        //indent at the end of a line
        String result = qs.toString().replaceAll("[\\n\\t ]", "");;
        result += "\r\n";
        return result;
    }

    private void uploadFilesToSFTPServer(String fileName)throws Exception {
        FTPClient client = new FTPClient();
        // Read the file from resources folder.
        try {
            //Spesific folder location
            String remoteFolderLocation = "/backup/input";
            File file = new File(fileName);
            fileName = fileName.replaceAll("tmp/","");

            remoteFolderLocation = remoteFolderLocation +"/"+ fileName;

            client.connect(ftpServerConfig.getSftpUrl(), 21);
            client.login(ftpServerConfig.getSftpUser(), ftpServerConfig.getSftpPassword());
            client.setFileType(FTP.BINARY_FILE_TYPE);
            // Store file to server
            client.storeFile(remoteFolderLocation, new FileInputStream(file));
            client.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String uploadFilesToFTPControl(String fileName)throws Exception {
        FTPClient client = new FTPClient();
        // Read the file from resources folder.
        try {
            //Spesific folder location
            String remoteFolderLocation = "/backup/input";
            File file = new File(fileName);
            fileName = fileName.replaceAll("tmp/","");

            remoteFolderLocation = remoteFolderLocation +"/"+ fileName;

            client.connect(ftpServerConfig.getSftpUrl(), 21);
            client.login(ftpServerConfig.getSftpUser(), ftpServerConfig.getSftpPassword());
            client.setFileType(FTP.BINARY_FILE_TYPE);
            // Store file to server
            client.storeFile(remoteFolderLocation, new FileInputStream(file));
            client.logout();
            return "SUCCESS FTP.";
        } catch (IOException e) {
           return e.getMessage();
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Result ftpControl() throws IOException {
        Result result = new Result();
        try {

            String fileName = createTestGZFile();
            String ftpMessage = "FTP Client Sonuc:" + uploadFilesToFTPControl(fileName);
            result.setData(ftpMessage);

        } catch (Exception ex) {
            result.setData(String.valueOf(ex));
            result.setMessage(ex.getMessage());
        }
        return result;
    }

    private String createTestGZFile() throws IOException {
        String fileName = "test";
        LocalDateTime dateTime = LocalDateTime.now();
        fileName = fileName + dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "_01.abn";
        File file = new File(fileName);
        file.createNewFile();
        return fileName;
    }
}
