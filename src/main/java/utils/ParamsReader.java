package utils;

import objects.Constants;
import objects.HostParams;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by VSKryukov on 30.01.2016.
 */
public class ParamsReader {

    public ArrayList<HostParams> ReadParamsFromFile() throws Exception {
        ArrayList<HostParams> hostParamses = new ArrayList<>();

        File file = new File(Constants.fileHostParams);

        if (!file.exists() || file.isDirectory()) {
            throw new Exception(String.format("File \"'%s'\" does not exisss!", file.getName()));
        }

        Scanner sc = new Scanner(file);
        int countLine = 0;
        //Ignor header from file
        sc.nextLine();
        while (sc.hasNext()) {
            String[] line = sc.nextLine().split(";");
            countLine++;
            if (line.length != 7) {
                throw new Exception(String.format("Wrong number params in line '%s'!", countLine));
            }
            HostParams params = new HostParams(line);

            params.setSourceDir((Paths.get(Constants.fileSourceConfig).getParent()).toString().replace("\\", "/"));
            params.print();
            hostParamses.add(params);
        }

        return hostParamses;
    }
}
