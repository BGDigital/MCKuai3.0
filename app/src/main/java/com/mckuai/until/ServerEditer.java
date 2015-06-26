package com.mckuai.until;

import android.util.Log;

import com.mckuai.bean.ServerInfo;

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
    private static  final  String TAG = "ServerEditer";

    private boolean saveAllFlag = false;//是否需要删除文件后再重写所有数据
    private String fileName = "/storage/sdcard0/games/com.mojang/minecraftpe/external_servers.txt";

    public ArrayList<ServerInfo> getServers() {
        return servers;
    }

    public void setServers(ArrayList<ServerInfo> servers) {
        this.servers = servers;
    }

    private ArrayList<ServerInfo> servers = new ArrayList<>();

    public ServerEditer(){
        loadServerFromDisk();
    }

    private void addServer(ServerInfo server){

    }

    private void deleteServer(ServerInfo server){

    }

    public void save(){
        File file = new File(fileName);
        //删除原有文件
        if (file.exists()){
            try {
                file.delete();
            }
            catch (Exception e){
                Log.e(TAG,"delete file false,"+e.getLocalizedMessage());
                return;
            }
        }
        //创建新文件
        try {
            file.createNewFile();
            file.setWritable(true);
        }
        catch (Exception e){
            Log.e(TAG,"create file false,"+e.getLocalizedMessage());
            return;
        }

        //写入数据
        try {
            FileWriter fileWriter = new FileWriter(file);
            String data = getServersString();
            if (null != fileWriter && null != data){
                fileWriter.write(getServersString());
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"save file false, "+e.getLocalizedMessage());
        }

    }

    private  void loadServerFromDisk(){
        File file = new File(fileName);
        if (!file.exists()){
            return;
        }
        file = null;
        //文件存在,开始一行一行的读取
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String data = null;
            do {
                data = reader.readLine().toString();
                if (null != data){
                    parseData(data);
                }
                else break;
            }
            while (null != data);
        }
        catch (Exception e){
            Log.e(TAG,"read file false:"+e.getLocalizedMessage());
        }
    }



    private void parseData(String data){
        if (null != data && 3 == getSplitCount(data)){
            String[] array = data.split(":");

            ServerInfo info = new ServerInfo();
            info.setPosition(array[0]);
            info.setName(array[1]);
            info.setAddress(array[2]);
            info.setPort(array[3]);
            servers.add(info);
        }
    }

    private int getSplitCount(String data){
        int count = 0;
        for (int i = 0;i < data.length();i++){
            if (data.charAt(i) == ':'){
                count++;
            }
        }
        return  count;
    }

    private String getServersString(){
        if (!servers.isEmpty()){
            String data = "";
            for (ServerInfo server:servers){
                data  += (server.getPosition()+":");
                data  += (server.getName()+":");
                data  += (server.getAddress()+":");
                data  += (server.getPort() + "\r\n");
            }
            return  data;
        }
        else {
            return  null;
        }
    }

}
