package com.mckuai;

import com.mckuai.until.FileUtil;

import java.io.File;
import java.io.Serializable;

/**
 * Created by kyly on 2015/7/9.
 */
public class WorldItem implements Serializable{
    private static final String EMPTY_NAME = "";
    public static final String levelNameFileName = "levelname.txt";
    private static final long serialVersionUID = 1999765797018607246L;
    public final File folder;                                       //文件夹
    public boolean hasLevelFileName = false;
    public final File levelDat;                                  //levelDat文件
    public String showName = null;                       //文件夹名称
    private Long size;                                           //大小
    public Long lastPlayTime;

    public WorldItem(File folderFile)
    {
        this.folder = folderFile;
        this.levelDat = new File(this.folder, "level.dat");
        this.showName = this.folder.getName();
        if ((levelDat.isFile()) && (levelDat.exists()))
        {
            this.hasLevelFileName = true;
        }
        this.size = FileUtil.getFolderSize(folderFile);
    }

    public WorldItem(File folderFile, Long size)
    {
        this.folder = folderFile;
        this.levelDat = new File(this.folder, "level.dat");
        this.size = size;
        this.showName = this.folder.getName();
        if ((levelDat.isFile()) && (levelDat.exists()))
        {
            this.hasLevelFileName = true;
        }
    }

    public WorldItem(File folderFile, Long size, String name)
    {
        this.folder = folderFile;
        this.levelDat = new File(this.folder, "level.dat");
        this.size = size;
        this.showName = name;
        if (levelDat.isFile() && levelDat.exists()){
            hasLevelFileName =true;
        }
    }

    private String getFolderName()
    {
        if (null == folder){
            return  null;
        }

        String name = folder.getName();

        return name;
    }

    public boolean equals(WorldItem item)
    {
        if (null == item)
        {
            return  false;
        }

        if (null == getName() && item.getName() == null){
            return  true;
        }

        if (getName().equals(item.getName()) ){
            return true;
        }

        return  false;

        /*
        paramObject = (WorldItem)paramObject;
        if (paramObject == null) {}
        do
        {
            return false;
            if ((getName() == null) && (((WorldItem)paramObject).getName() == null)) {
                return true;
            }
        } while ((getName() == null) || (((WorldItem)paramObject).getName() == null) || (!getName().equals(((WorldItem)paramObject).getName())));
        return true;*/
    }

    public File getFolder()
    {
        return this.folder;
    }

    public File getLevelDat()
    {
        return this.levelDat;
    }

    public String getMapKey()
    {
        String str2 = getFolderName();
        String str1 = str2;
        if (str2 != null) {
            str1 = str2 + "#" + this.folder.lastModified();
        }
        return str1;
    }

    public String getName()
    {
        if (this.folder != null) {
            return getFolderName();
        }
        return null;
    }

    public String getShowName()
    {
        return this.showName;
    }

    public String getSize()
    {
        return this.size+"";
    }

    public String getTrueName()
    {
        if (this.folder == null) {
            return null;
        }
        return this.folder.getName();
    }

    public boolean isHasLevelFileName()
    {
        return this.hasLevelFileName;
    }

    public void setHasLevelFileName(boolean hasLevelFileName)
    {
        this.hasLevelFileName = hasLevelFileName;
    }

    public void setSize(long size)
    {
        this.size = size;
    }

    public String toString()
    {
        return getFolderName();
    }
}
