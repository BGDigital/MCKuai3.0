package com.mckuai.until;

import android.graphics.BitmapFactory;
import android.util.Log;

import com.mckuai.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;

/**
 * Created by kyly on 2015/7/10.
 */
public class OptionUntil {
    private int gfx_gamma = 0;
    private int ctrl_usetouchjoypad = 0;
    private int old_game_version_beta = 0;
    private int old_game_version_major = 0;
    private int ctrl_invertmouse = 0;
    private String mp_username;
    private float gfx_pixeldensity;
    private int audio_sound=1;
    private int gfx_fancyskies = 1;
    private int old_game_version_minor =10;
    private int ctrl_usetouchscreen = 1;
    private int game_limitworldsize =0;
    private int gfx_animatetextures =1;
    private int gfx_hidegui = 0;
    private float ctrl_sensitivity = 0.5f;
    private int game_thirdperson =0;
    private int  gfx_renderdistance_new;
    private String game_flatworldlayers;
    private int dev_autoloadlevel = 0;
    private int mp_server_visible =1;
    private int gfx_fancygraphics = 1;
    private int dev_showchunkmap = 0;
    private int dev_disablefilesystem =0;
    private int game_difficulty=2;
    private int ctrl_islefthanded = 0;
    private int old_game_version_patch = 5;
    private int feedback_vibration =1;

    private final String TAG = "OptionUntil";
    private File optionFile;

    public OptionUntil(File file){
        optionFile = file;
        loadFromFile();
    }

    public OptionUntil(String dir){
        if (null != dir && !dir.isEmpty()){
            File file = new File(dir,"options.txt");
            if (file.isFile() && file.exists()){
                optionFile = file;
                loadFromFile();
            }
        }
    }

    public OptionUntil(String dir,String name){
        if (null != dir && null != name && !dir.isEmpty() && !name.isEmpty()){
            File file = new File(dir,name);
            if (file.exists() && file.isFile()){
                optionFile = file;
                loadFromFile();
            }
        }
    }

    public boolean isThirdPerson(){
        return  1 == game_thirdperson;
    }

    public boolean setThirdPerson(boolean isThirdPerson){
        game_thirdperson = true == isThirdPerson ? 1:0;
        return saveToFile();
    }

    private void loadFromFile(){
        if (null != optionFile && optionFile.isFile() && optionFile.exists()){
            try {
                InputStreamReader streamReader = new InputStreamReader(new FileInputStream(optionFile), "ASCII");
                BufferedReader reader = new BufferedReader(streamReader);
                String data = reader.readLine();
                while (data != null && !data.isEmpty()){
                    parseItem(data);
                    data = reader.readLine();
                }
                reader.close();
                streamReader.close();
            }
            catch (Exception e){
            Log.e(TAG,"读取文件时失败，原因："+e.getLocalizedMessage());
            }
        }
        else {
            Log.e(TAG,"options.txt文件不存在");
        }
    }

    private boolean saveToFile(){
        String data = tostring();
        try {
            FileOutputStream outputStream = new FileOutputStream(optionFile);
            outputStream.write(data.getBytes());
            outputStream.close();
            return  true;
        }
        catch (Exception e){
            Log.e(TAG,"写入到文件失败，原因："+e.getLocalizedMessage());
            return false;
        }

    }



    private void parseItem(String item){
        if (null != item && !item.isEmpty()){
            String[] array = item.split(":");
            switch (array[0]){
                case "gfx_gamma":
                    gfx_gamma = Integer.parseInt(array[1]);
                    break;
                case "ctrl_usetouchjoypad":
                    ctrl_usetouchjoypad = Integer.parseInt(array[1]);
                    break;
                case "old_game_version_beta":
                    old_game_version_beta = Integer.parseInt(array[1]);
                    break;
                case "old_game_version_major":
                    old_game_version_major  =Integer.parseInt(array[1]);
                    break;
                case "ctrl_invertmouse":
                    ctrl_invertmouse = Integer.parseInt(array[1]);
                    break;
                case "mp_username":
                    mp_username = array[1];
                    break;
                case "gfx_pixeldensity":
                    gfx_pixeldensity = Float.parseFloat(array[1]);
                    break;
                case "audio_sound":
                    audio_sound = Integer.parseInt(array[1]);
                    break;
                case "gfx_fancyskies":
                    gfx_fancyskies = Integer.parseInt(array[1]);
                    break;
                case "old_game_version_minor":
                    old_game_version_minor = Integer.parseInt(array[1]);
                    break;
                case "ctrl_usetouchscreen":
                    ctrl_usetouchscreen = Integer.parseInt(array[1]);
                    break;
                case "game_limitworldsize":
                    game_limitworldsize = Integer.parseInt(array[1]);
                    break;
                case "gfx_animatetextures":
                    gfx_animatetextures = Integer.parseInt(array[1]);
                    break;
                case "gfx_hidegui":
                    gfx_hidegui = Integer.parseInt(array[1]);
                    break;
                case "ctrl_sensitivity":
                    ctrl_sensitivity = Float.parseFloat(array[1]);
                    break;
                case "game_thirdperson":
                    game_thirdperson = Integer.parseInt(array[1]);
                    break;
                case "gfx_renderdistance_new":
                    gfx_renderdistance_new = Integer.parseInt(array[1]);
                    break;
                case "game_flatworldlayers":
                    game_flatworldlayers = array[1];
                    break;
                case "dev_autoloadlevel":
                    dev_autoloadlevel = Integer.parseInt(array[1]);
                    break;
                case "mp_server_visible":
                    mp_server_visible = Integer.parseInt(array[1]);
                    break;
                case "gfx_fancygraphics":
                    gfx_fancygraphics = Integer.parseInt(array[1]);
                    break;
                case "dev_showchunkmap":
                    dev_showchunkmap = Integer.parseInt(array[1]);
                    break;
                case "dev_disablefilesystem":
                    dev_disablefilesystem = Integer.parseInt(array[1]);
                    break;
                case "game_difficulty":
                    game_difficulty = Integer.parseInt(array[1]);
                    break;
                case "ctrl_islefthanded":
                    ctrl_islefthanded = Integer.parseInt(array[1]);
                    break;
                case "old_game_version_patch":
                    old_game_version_patch = Integer.parseInt(array[1]);
                    break;
                case "feedback_vibration":
                    feedback_vibration = Integer.parseInt(array[1]);
                    break;
            }
        }
    }

    private String tostring(){
        String data = "";
        data = data+"gfx_gamma:" +gfx_gamma +"\n";
        data = data+"ctrl_usetouchjoypad:" +ctrl_usetouchjoypad +"\n";
        data = data+"old_game_version_beta:" +old_game_version_beta +"\n";
        data = data+"old_game_version_major:" +old_game_version_major +"\n";
        data = data+"ctrl_invertmouse:" +ctrl_invertmouse +"\n";
        if (null != mp_username && !mp_username.isEmpty()) {
            data = data + "mp_username:" + mp_username + "\n";
        }
        data = data+"gfx_pixeldensity:" +gfx_pixeldensity +"\n";
        data = data+"audio_sound:" +audio_sound +"\n";
        data = data+"gfx_fancyskies:" +gfx_fancyskies +"\n";
        data = data+"old_game_version_minor:" +old_game_version_minor +"\n";
        data = data+"ctrl_usetouchscreen:" +ctrl_usetouchscreen +"\n";
        data = data+"game_limitworldsize:" +game_limitworldsize +"\n";
        data = data+"gfx_animatetextures:" +gfx_animatetextures +"\n";
        data = data+"gfx_hidegui:" +gfx_hidegui +"\n";
        data = data+"ctrl_sensitivity:" +ctrl_sensitivity +"\n";
        data = data+"game_thirdperson:" +game_thirdperson +"\n";
        data = data+"gfx_renderdistance_new:" +gfx_renderdistance_new +"\n";
        if (null != game_flatworldlayers && !game_flatworldlayers.isEmpty()) {
            data = data + "game_flatworldlayers:" + game_flatworldlayers + "\n";
        }
        data = data+"dev_autoloadlevel:" +dev_autoloadlevel +"\n";
        data = data+"mp_server_visible:" +mp_server_visible +"\n";
        data = data+"gfx_fancygraphics:" +gfx_fancygraphics +"\n";
        data = data+"dev_showchunkmap:" +dev_showchunkmap +"\n";
        data = data+"dev_disablefilesystem:" +dev_disablefilesystem +"\n";
        data = data+"game_difficulty:" +game_difficulty +"\n";
        data = data+"ctrl_islefthanded:" +ctrl_islefthanded +"\n";
        data = data+"old_game_version_patch:" +old_game_version_patch +"\n";
        data = data+"feedback_vibration:" +feedback_vibration;
        return  data;
    }

    public boolean isValid(){
        return optionFile != null && optionFile.exists() && optionFile.isFile();
    }
}
