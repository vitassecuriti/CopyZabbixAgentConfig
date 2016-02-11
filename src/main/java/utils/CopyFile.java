package utils;

import objects.HostParams;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import utils.selectors.FileSelectorImpl;

import java.io.File;


/**
 * Created by VSKryukov on 27.01.2016.
 */
public class CopyFile {

    private String host;
    private int port;
    private String user;
    private String password;
    private String sourceDir;
    private String targetDir;
    private String protocol;
    private int timeout;
    private long size;
    private long start_time;
    private long end_time;

    private FileSelectorImpl fileSelector = new FileSelectorImpl();


    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;

    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public void setSize(long size) {

        this.size = size;
    }

    public long getSize() {
        return size;
    }

    private FileSystemManager fsManager;

    public void setFsManager(FileSystemManager fsManager) {
        this.fsManager = fsManager;

    }

    public CopyFile(HostParams params) {
        this.host = params.getHost();
        this.user = params.getUser();
        this.password = params.getPass();
        this.port = params.getPort();
        this.sourceDir = params.getSourceDir();
        this.targetDir = params.getTargetDir();
        this.protocol = params.getProtocol().toLowerCase();
        this.timeout = params.getTimeOut();
        this.size = 0;
    }

    //ftp://[ username[: password]@] hostname[: port][ relative-path]
    //sftp://[ username[: password]@] hostname[: port][ relative-path]
    public String getURL() {
        return protocol + "://" + user + ":" + password + "@" + host + ":" + port + (targetDir == null ? "" : targetDir);
    }


    //COPY FILES---------------------------------------------
    private FileSystemOptions getFileSystemOptions() throws FileSystemException {
        FileSystemOptions fsOptions = new FileSystemOptions();

        switch (protocol) {
            case "sftp": {
                SftpFileSystemConfigBuilder builder = SftpFileSystemConfigBuilder.getInstance();
                builder.setUserDirIsRoot(fsOptions, false);
                builder.setStrictHostKeyChecking(fsOptions, "no");
                builder.setTimeout(fsOptions, timeout);
                // builder.setCompression(fsOptions, "zlib,none");

            }
            break;
            case "ftp": {
                FtpFileSystemConfigBuilder builder = FtpFileSystemConfigBuilder.getInstance();
                builder.setUserDirIsRoot(fsOptions, false);
                builder.setPassiveMode(fsOptions, true);
                builder.setDataTimeout(fsOptions, timeout);
            }
            break;
        }

        return fsOptions;
    }

    private FileObject getTargetDir() throws Exception {
        FileObject targetDir = null;

        try {
            // System.out.println(getURL());
            targetDir = fsManager.resolveFile(getURL(), getFileSystemOptions());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return targetDir;
    }

    private FileObject getSourceDir() throws Exception {
        FileObject sourceDir = null;

        try {
            sourceDir = fsManager.resolveFile(this.sourceDir);
        } catch (Exception e) {
            System.out.println(e);
        }

        return sourceDir;
    }

    public void copyFile() throws Exception {
        try {
            FileObject sourceDir = getSourceDir();
            // System.out.println(sourceDir.getURL());

            FileObject targetDir = getTargetDir();
            //  System.out.println(targetDir.getURL());


            FileObject[] foundFiles = sourceDir.findFiles(fileSelector);
            if (foundFiles.length == 0) {
                throw new Exception("File not found!");
            } else if (foundFiles.length > 1) {
                throw new Exception("It is found files more than 1!");
            } else {
                //    System.out.println("File Size - " + foundFiles[0].getContent().getSize());
                setSize(foundFiles[0].getContent().getSize());

            }


            setStart_time(System.currentTimeMillis());
            targetDir.copyFrom(sourceDir, fileSelector);
            setEnd_time(System.currentTimeMillis());
        } catch (Exception e) {
            // System.out.println(e);
            e.printStackTrace();
        }
    }


    public void cleanFolder() throws Exception {

        FileObject targetDir = getTargetDir();


        targetDir.delete(fileSelector);

        File file = new File(targetDir.getURL().toString());

        if (file.exists()) {
            System.out.println("The copied file isn't removed");
        } else {
            System.out.println("The copied file is successfully removed");
        }

    }


}
