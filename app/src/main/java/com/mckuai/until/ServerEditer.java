package com.mckuai.until;

import android.util.Log;

import com.mckuai.bean.GameServerInfo;
import com.mckuai.imc.MCkuai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by kyly on 2015/6/26.
 * 管理游戏中的服务器
 * 从配置文件中读取,解析,添加,删除和保存服务器信息
 */
public class ServerEditer {
    private static final String TAG = "ServerEditer";

    private boolean saveAllFlag = false;//是否需要删除文件后再重写所有数据
    private String fileName;
    //    private String fileName = "/storage/sdcard0/games/com.mojang/minecraftpe/external_servers.txt";
    private ArrayList<GameServerInfo> servers;

    public ServerEditer() {
        fileName = MCkuai.getInstance().getGameProfileDir() + "minecraftpe/external_servers.txt";
        loadServerFromDisk();
    }

    public ArrayList<GameServerInfo> getServers() {

        return servers;
    }

    /**
     * 添加服务器
     * 此函数仅将服务器添加到内存中，如果要保存，需调用save()函数
     *
     * @param server 要添加的服务器
     */
    public void addServer(GameServerInfo server) {
        if (null != server) {
            int maxposition = 1;
            for (GameServerInfo info : servers) {
                maxposition = maxposition > info.getPosition() ? maxposition : info.getPosition();
                if (info.getViewName().equalsIgnoreCase(server.getViewName()) && info.getServerPort() == server.getServerPort() && info.getResIp().equalsIgnoreCase(server.getResIp())) {
                    //已存在此服务器，不添加
                    return;
                }
            }
            server.setPosition(maxposition + 1);
            saveAllFlag = true;
            servers.add(server);
        }
    }

    private void deleteServer(GameServerInfo server) {

    }

    public void save() {
        if (!saveAllFlag) {
            return;
        }
        File file = new File(fileName);
        //删除原有文件
        if (file.exists()) {
            try {
                file.delete();
            } catch (Exception e) {
                Log.e(TAG, "delete file false," + e.getLocalizedMessage());
                return;
            }
        }
        //创建新文件
        try {
            file.createNewFile();
            file.setWritable(true);
        } catch (Exception e) {
            Log.e(TAG, "create file false," + e.getLocalizedMessage());
            return;
        }

        //写入数据
        try {
            FileWriter fileWriter = new FileWriter(file);
            String data = getServersString();
            if (null != fileWriter && null != data) {
                fileWriter.write(getServersString());
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "save file false, " + e.getLocalizedMessage());
        }

    }

    private void loadServerFromDisk() {
        if (null == servers){
            servers = new ArrayList<>();
        }
        else {
            servers.clear();
        }

        File file = new File(fileName);
        if (!file.exists()) {
            return;
        }

        //文件存在,开始一行一行的读取
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String data = null;
            GameServerInfo info;
            data = reader.readLine().toString();

            while (null != data){
                if (null != data) {
                    info = parseData(data);
                    if (null != info) {
                        if (null == servers) {
                            servers = new ArrayList<>();
                        }
                        servers.add(info);
                    }
                }
                try{
                    data = reader.readLine().toString();
                }
                catch (Exception e){
                    data = null;
                    break;
                }

            }

            reader.close();
            inputStreamReader.close();
        } catch (Exception e) {
            Log.e(TAG, "read file false:" + e.getLocalizedMessage());
            try {
                if (null != inputStreamReader) {
                    inputStreamReader.close();
                }
                if (null != reader) {
                    reader.close();
                }
            } catch (Exception e1) {

            }
        }
    }


    private GameServerInfo parseData(String data) {
        if (null != data && 3 == getSplitCount(data)) {
            String[] array = data.split(":");
            GameServerInfo info = new GameServerInfo();
            info.setPosition(Integer.parseInt(array[0]));
            info.setViewName(array[1]);
            info.setResIp(array[2]);
            info.setServerPort(Integer.parseInt(array[3]));
            return info;
        }
        return null;
    }

    private int getSplitCount(String data) {
        int count = 0;
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) == ':') {
                count++;
            }
        }
        return count;
    }

    private String getServersString() {
        if (!servers.isEmpty()) {
            String data = "";
            for (GameServerInfo server : servers) {
                data += (server.getPosition() + ":");
                data += (server.getViewName() + ":");
                data += (server.getResIp() + ":");
                data += (server.getServerPort() + "\r\n");
            }
            return data;
        } else {
            return null;
        }
    }

}
