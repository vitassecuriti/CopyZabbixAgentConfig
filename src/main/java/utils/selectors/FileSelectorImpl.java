package utils.selectors;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileType;

/**
 * Created by VSKryukov on 28.01.2016.
 */
public class FileSelectorImpl implements FileSelector {
    @Override
    public boolean includeFile(FileSelectInfo fileSelectInfo) throws Exception {
        FileObject file = fileSelectInfo.getFile();
        //System.out.println(fileSelectInfo.getFile().getName().getBaseName());
        return !FileType.FOLDER.equals(file.getType()) && file.getName().getBaseName().contains("zabbix_agentd.conf");
    }

    @Override
    public boolean traverseDescendents(FileSelectInfo fileSelectInfo) throws Exception {
        return fileSelectInfo.getDepth() < 1;
    }
}

