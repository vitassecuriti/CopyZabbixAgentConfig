import utils.Utils;

/**
 * Created by VSKryukov on 27.01.2016.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Utils utilFile = new Utils();
        utilFile.prepeareConfigFiles();
        utilFile.copyAllByHost();
    }


}
