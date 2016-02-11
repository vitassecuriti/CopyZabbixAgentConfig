package objects;

import java.io.File;

/**
 * Created by VSKryukov on 30.01.2016.
 */
public class Constants {

    public static final String EXECUTION_DIR = new File("").getAbsolutePath();
    public static final String dirForConfByHostName = EXECUTION_DIR + "/RedyConfFile/";
    public static final String fileTamplate = EXECUTION_DIR + "/confTampl/zabbix_agentd.conf";
    public static final String fileHostParams = EXECUTION_DIR + "/HostList.txt";
    public static final String fileSourceConfig = EXECUTION_DIR + "/sourceConfig/zabbix_agentd.conf";
    public static final String dirForIPReport = EXECUTION_DIR + "/IPReport";
}
