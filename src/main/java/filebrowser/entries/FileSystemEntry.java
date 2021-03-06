package filebrowser.entries;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;

import filebrowser.FileBrowserException;
import filebrowser.Localization;

public class FileSystemEntry implements Entry {
    
    private final File file;
    
    public FileSystemEntry(File file) {

        /*
         * Getting the absolute path, otherwise getParentFile may return null
         * for "." on Linux. Bug in the File implementation?
         */
        this.file = new File(file.getAbsolutePath());
    }
    
    public List<Entry> listEntries() throws FileBrowserException {
        File[] children = file.listFiles();
        List<Entry> entries = new ArrayList<Entry>();

        if (children != null) {
            for (File child : children) {
                Entry childEntry = EntryFactory.create(child.getAbsolutePath(), this);

                entries.add(childEntry);
            }
        }
        return entries;
    }
    
    public Entry getParentEntry() {
        File parentFile = file.getParentFile();
        
        return (null != parentFile) ? new FileSystemEntry(parentFile) : null;
    }
    
    public Entry navigationTarget() {
        return this;
    }
    
    public String getName() {
        return file.getName();
    }
    
    public Date getLastModified() {
        return new Date(file.lastModified());
    }
    
    public boolean isDirectory() {
        return file.isDirectory();
    }
    
    public String getFullPath() {
        return file.getAbsolutePath();
    }

    public boolean isNavigationPossible() {
        return isDirectory();
    }
    
    public byte[] readContent() throws FileBrowserException {
        InputStream inputStream = null;
        
        try {
            inputStream = new FileInputStream(file);
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new FileBrowserException(Localization.ERROR_CANNOT_READ_ENTRY_CONTENT, e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public InputStream getInputStream() throws FileBrowserException  {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new FileBrowserException(Localization.ERROR_CANNOT_READ_ENTRY_CONTENT, e);
        }
    }
}