package utils;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import objects.Constants;
import objects.HostParams;
import org.apache.commons.vfs2.*;
import java.io.*;
import java.io.FileFilter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by VSKryukov on 27.01.2016.
 */
public class Utils {

    public ArrayList<HostParams> hostParamses = new ArrayList<>();
    public ArrayList<String> ipList = new ArrayList<>();

    public void GenerateFile(String fileTamlatePath, String host, String ip) throws Exception {
        File fileTamlate = new File(fileTamlatePath);
        ArrayList<String> generateFile = new ArrayList<>();
        Scanner sc = new Scanner(fileTamlate);
        String line;
        while (sc.hasNext()) {

            line = sc.nextLine();

            if (line.equals("ListenIP=")) {
                line += ip;
            }

            if (line.equals("Hostname=")) {
                line += host;
            }

            generateFile.add(line);
        }


        writeToFile(Constants.dirForConfByHostName, host + "_zabbix_agentd.conf", generateFile);


    }

    public void writeToFile(String path, String fileName, ArrayList<String> listReport) {
        if (listReport.size() != 0) {
            String fileReport = path + "/" + fileName;

            try (FileWriter writer = new FileWriter(fileReport, false)) {
                for (String record : listReport)
                    writer.write(record + "\n");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void deleteAllFileFromResultFolder() {
        deleteAllFiles(Constants.dirForConfByHostName);
    }

    private void deleteAllFiles(String path) {
        for (File myFile : new File(path).listFiles())
            if (myFile.isFile()) myFile.delete();
    }


    public void prepeareConfigFiles() throws Exception {
        if (!Paths.get(Constants.dirForConfByHostName).toFile().exists()) {
            Files.createDirectory(Paths.get(Constants.dirForConfByHostName));
        }

        if (!(Paths.get(Constants.dirForIPReport).toFile().exists())) {
            Files.createDirectory(Paths.get(Constants.dirForIPReport));
        }

        hostParamses = new ArrayList<>(new ParamsReader().ReadParamsFromFile());
        deleteAllFileFromResultFolder();
        for (HostParams hostParam : hostParamses) {
            String ipByHost = UtilNet.getIPbyHostName(hostParam.getHost());
            String hostName = hostParam.getHost();
            GenerateFile(Constants.fileTamplate, hostName, ipByHost);
            ipList.add(hostName + " : " + ipByHost);

        }
        System.out.println("All files for copy is READY!");
        writeToFile(Constants.dirForIPReport, "IPReportByHostName.txt", ipList);
    }

    public void copyAllByHost() throws Exception {
        FileSystemManager vfsManager = VFS.getManager();
        for (HostParams hostParam : hostParamses) {
            prepeareConfigFileForCopy(hostParam.getHost());
            CopyFile copyFile = new CopyFile(hostParam);
            copyFile.setFsManager(vfsManager);
            copyFile.copyFile();

            System.out.println("File of a configuration of the zabbix agent was successfully copied");

            restartZabbixAgent(hostParam.getHost(), hostParam.getUser(), hostParam.getPass(), hostParam.getPort());

            System.out.println("Zabbix agent was successfully restarted!");
        }
    }


    public void prepeareConfigFileForCopy(String hostName) throws Exception {

        File file[] = new File(Constants.dirForConfByHostName).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().contains(hostName);
            }
        });

        if (file.length == 0) {
            System.out.println("Files for copy to host:" + hostName + " not found!");
        } else if (file.length > 1) {
            System.out.println("It is found more than one file with the name of a host:" + hostName + "!");
        } else {
            if (!Paths.get(Constants.fileSourceConfig).toFile().exists()) {
                Files.createDirectory(Paths.get(Constants.fileSourceConfig).getParent());
            }
            try {
                Files.copy(file[0].toPath(), Paths.get(Constants.fileSourceConfig), StandardCopyOption.REPLACE_EXISTING);
            } catch (NoSuchFileException e) {
                e.printStackTrace();
            }
        }
    }

    private void restartZabbixAgent(String host, String user, String pass, int port) throws JSchException, IOException {
        SshExecComand sshExecComand = new SshExecComand();

        Session session = sshExecComand.connect(host, user, pass, port);
        sshExecComand.execCommand(session, "cd /etc/init.d && ./zabbix-agent stop");
        sshExecComand.execCommand(session, "cd /etc/init.d && ./zabbix-agent start");

        sshExecComand.closeConnection(session, sshExecComand.getChannel());
    }


    public static class UtilNet {

        public static String getIPbyHostName(String hostName) {
            String ipAddr = "";
            try {
                InetAddress inetAddr = InetAddress.getByName(hostName);
                byte[] addr = inetAddr.getAddress();
                // Convert to dot representation


                for (int i = 0; i < addr.length; i++) {
                    if (i > 0) {
                        ipAddr += ".";
                    }
                    ipAddr += addr[i] & 0xFF;
                }
                System.out.println("IP Address: " + ipAddr);
            } catch (UnknownHostException e) {
                System.out.println("Host not found: " + e.getMessage());
            }
            return ipAddr;
        }
    }


}
