package com.mckuai.until;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by kyly on 2015/6/27.
 */
public class MCGameEditer {

    private static final  String TAG = "MCGameEditer";

    private byte levelData[];
    private int gameMode;
    private int gameTime;
    private int thirdView;
    private int offset_GameMode = 0;
    private int offset_GameTime = 0;
    private int offset_ThirdView = 0;

    private final  String fileName = "/storage/sdcard0/games/com.mojang/minecraftWorlds/My World/level.dat";

    public MCGameEditer(){
        loadData();
    }

    public boolean hasProfile(){
        return  levelData != null;
    }

    public int getGameMode(){
        if (0 == offset_GameMode){
            offset_GameMode = getValueOffset("GameType");
        }
        if (0 < offset_GameMode){
            byte mode[] = {0,0,0,0};
            if (getByteBlock(offset_GameMode, mode)){
                return  toInt(mode);
            }
        }
        return  -1;
    }

    public boolean setGameMode(int mode){
        if (0 == offset_GameMode){
            offset_GameMode = getValueOffset("GameType");
        }

        if (0 < offset_GameMode){
            if (setByteBlock(offset_GameMode,toByteArray(mode,4))){
                if (saveData()){
                    return  true;
                }
            }
        }
        return  false;
    }


    /**
     * 获取指定key的值的起始位置
     * @param key 要查找的key
     * @return 如果找到则返回其值的位置,否则返回-1
     */
    private int getValueOffset(String key){
        if (null != levelData && null != key && 1 < key.length()){
            //查找指定字符串的位置,如果找到则设置index
            for (int i = 0;i < levelData.length - 8;i++){
                byte src[] = key.getBytes();
                byte dst[] = src.clone();
                if (getByteBlock(i, dst)) {
                    if (Arrays.equals(dst,src)){
                        return i + src.length;
                    }
                }
            }

        }
        return  -1;
    }

    /**
     * 从指定位置起读取给定个数的byte
     * @param offset 起始位置
     * @param dst 接收读取到的byte[]
     * @return 如果读取成功则返回true,否则返回false;
     */
    private boolean getByteBlock(int offset,byte dst[]){
        if (null != dst && offset + dst.length < levelData.length){
            for (int i = 0 ;i < dst.length;i++){
                dst[i] = levelData[offset + i];
            }
            return  true;
        }
        return  false;
    }

    private boolean setByteBlock(int offset,byte dst[]){
        if (null != dst && offset + dst.length < levelData.length){
            for (int i = 0;i < dst.length;i++){
                levelData[offset + i] = dst[i];
            }
            return  true;
        }
        return false;
    }

    /**
     * 将byte[]转换为int类型的数据
     * @param bRefArr  要转换的byte[]
     * @return 转换出来的数据
     */
    public static int toInt(byte[] bRefArr) {
        int iOutcome = 0;
        byte bLoop;

        for (int i = 0; i < bRefArr.length; i++) {
            bLoop = bRefArr[i];
            iOutcome += (bLoop & 0xFF) << (8 * i);
        }
        return iOutcome;
    }

    /**
     * 将int转换为byte[]
     * @param iSource 被转换的整形
     * @param iArrayLen 数组长度
     * @return 转换后的byte[]
     */
    public static byte[] toByteArray(int iSource, int iArrayLen) {
        byte[] bLocalArr = new byte[iArrayLen];
        for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
            bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
        }
        return bLocalArr;
    }


    private void loadData(){
        File file = new File(fileName);
        if (!file.exists()){
            return;
        }
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            levelData = new byte[(int)file.length()];
            is.read(levelData);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean saveData(){
    File file = new File(fileName);
        if (!file.exists()){
            return false;
        }
        else {
            //file.delete();
            //file.createNewFile();
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(levelData);
            outputStream.close();
            return  true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"save false,"+e.getLocalizedMessage());
            return  false;
        }
    }
}
