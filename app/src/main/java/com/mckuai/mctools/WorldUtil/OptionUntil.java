package com.mckuai.mctools.WorldUtil;

import android.util.Log;

import com.mckuai.imc.MCkuai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * Created by kyly on 2015/7/10.
 */
public class OptionUntil {
    private static int gfx_gamma = 0;
    private static int gfx_fancyskies = 1;
    private static float gfx_pixeldensity;
    private static int gfx_animatetextures = 1;
    private static int gfx_hidegui = 0;
    private static int gfx_renderdistance_new;
    private static int gfx_fancygraphics = 1;
    private static int old_game_version_major = 0;
    private static int old_game_version_minor = 10;
    private static int old_game_version_patch = 5;
    private static int old_game_version_beta = 0;
    private static int ctrl_invertmouse = 0;
    private static int ctrl_usetouchscreen = 1;
    private static int ctrl_usetouchjoypad = 0;
    private static float ctrl_sensitivity = 0.5f;
    private static int ctrl_islefthanded = 0;
    private static int game_limitworldsize = 0;
    private static int game_thirdperson = 0;
    private static int game_difficulty = 2;
    private static String game_language = null;    //此为0.11.x新增
    private static String game_skintypefull = null; //此项控制显示的皮肤；史蒂夫：Standard_Steve ，艾丽丝：Standard_Alex;自定义：Standard_Custom
    private static String game_lastcustomskinnew = null;
    private static String game_flatworldlayers;
    private static int dev_autoloadlevel = 0;
    private static int mp_server_visible = 1;
    private static String mp_username;
    private static int dev_showchunkmap = 0;
    private static int dev_disablefilesystem = 0;
    private static int feedback_vibration = 1;
    private static int audio_sound = 1;

    private static final String TAG = "OptionUntil";
    private static boolean hasLoaded = false;

    public OptionUntil() {
        loadFromFile();
    }


    public static boolean isThirdPerson() {
        if (!hasLoaded) {
            File file = new File(MCkuai.getInstance().getSDPath() + "/games/com.mojang/minecraftpe/options.txt");
            if (file != null && file.exists() && file.isFile()) {
                loadFromFile();
            }
        }
        return 1 == game_thirdperson;
    }

    public static boolean setThirdPerson(boolean isThirdPersonView) {
        if (!hasLoaded) {
            loadFromFile();
        }
        int newVision = true == isThirdPersonView ? 1:0;
        if (hasLoaded && game_thirdperson != newVision) {
            game_thirdperson = Math.abs(game_thirdperson - 1);
            return saveFile();
        }
        return false;
    }

    public static String getSkin() {
        if (!hasLoaded) {
            loadFromFile();
        }
        return game_skintypefull;
    }

    /**
     * 设置游戏皮肤
     * @param type 皮肤类型: 0为史蒂夫；1为艾丽丝；2为自定义
     * @return
     */
    public static boolean setSkin(int type) {
        if (!hasLoaded) {
            loadFromFile();
        }
        switch (type) {
            case 0:
                game_skintypefull = "Standard_Steve";
                break;
            case 1:
                game_skintypefull = "Standard_Alex";
                break;
            case 2:
                game_skintypefull = "Standard_Custom";
                break;
        }
        return saveFile();
    }

    private static boolean saveFile() {
        if (hasLoaded) {
            String data = tostring();
            File file = new File(MCkuai.getInstance().getSDPath() + "/games/com.mojang/minecraftpe/options.txt");
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(data.getBytes());
                outputStream.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

  /*  public static boolean isSaveInLevelDB() {
        if (!hasLoaded) {
            loadFromFile();
        }
        if (old_game_version_major > 0) {
            return true;
        } else {
            if (old_game_version_minor > 8) {
                return true;
            }
        }
        return false;
    }*/


    private static void loadFromFile() {
        File file = new File(MCkuai.getInstance().getSDPath() + "/games/com.mojang/minecraftpe/options.txt");
        if (null != file && file.exists() && file.isFile()) {
            try {
                InputStreamReader streamReader = new InputStreamReader(new FileInputStream(file), "ASCII");
                BufferedReader reader = new BufferedReader(streamReader);
                String data = reader.readLine();
                while (data != null && !data.isEmpty()) {
                    parseItem(data);
                    try {
                        data = reader.readLine();
                    }catch (Exception e){
                        Log.e(TAG,"在读取行时失败，原因："+e.getLocalizedMessage());
                    }
                }
                reader.close();
                streamReader.close();
                hasLoaded = true;
            } catch (Exception e) {
                Log.e(TAG, "读取文件时失败，原因：" + e.getLocalizedMessage());
                hasLoaded = false;
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "options.txt文件不存在");
        }
    }


    private static void parseItem(String item) {
        if (null != item && !item.isEmpty()) {
            String[] array = item.split(":");
            switch (array[0]) {
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
                    old_game_version_major = Integer.parseInt(array[1]);
                    break;
                case "ctrl_invertmouse":
                    ctrl_invertmouse = Integer.parseInt(array[1]);
                    break;
                case "mp_username":
                    mp_username = array[1];
                    break;
                case "game_language":
                    game_language = array[1];
                    break;
                case "game_skintypefull":
                    game_skintypefull = array[1];
                    break;
                case "game_lastcustomskinnew":
                    game_lastcustomskinnew = array[1];
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

    private static String tostring() {
        String data = "";
        data = data + "gfx_gamma:" + gfx_gamma + "\n";
        data = data + "ctrl_usetouchjoypad:" + ctrl_usetouchjoypad + "\n";
        data = data + "old_game_version_beta:" + old_game_version_beta + "\n";
        data = data + "old_game_version_major:" + old_game_version_major + "\n";
        data = data + "ctrl_invertmouse:" + ctrl_invertmouse + "\n";
        if (null != mp_username && !mp_username.isEmpty()) {
            data = data + "mp_username:" + mp_username + "\n";
        }
        data = data + "gfx_pixeldensity:" + gfx_pixeldensity + "\n";
        data = data + "audio_sound:" + audio_sound + "\n";
        data = data + "gfx_fancyskies:" + gfx_fancyskies + "\n";
        data = data + "old_game_version_minor:" + old_game_version_minor + "\n";
        data = data + "ctrl_usetouchscreen:" + ctrl_usetouchscreen + "\n";
        data = data + "game_limitworldsize:" + game_limitworldsize + "\n";
        data = data + "gfx_animatetextures:" + gfx_animatetextures + "\n";
        data = data + "gfx_hidegui:" + gfx_hidegui + "\n";
        data = data + "ctrl_sensitivity:" + ctrl_sensitivity + "\n";
        data = data + "game_thirdperson:" + game_thirdperson + "\n";
        if (null != game_language && 1 < game_language.length()) {
            data += "game_language:" + game_language + "\n";
        }
        if (null != game_skintypefull && 1 < game_skintypefull.length()) {
            data += "game_skintypefull:" + game_skintypefull + "\n";
        }
        if (null != game_lastcustomskinnew && 1 < game_lastcustomskinnew.length()) {
            data += "game_lastcustomskinnew" +game_lastcustomskinnew + "\n";
        }
        data = data + "gfx_renderdistance_new:" + gfx_renderdistance_new + "\n";
        if (null != game_flatworldlayers && !game_flatworldlayers.isEmpty()) {
            data = data + "game_flatworldlayers:" + game_flatworldlayers + "\n";
        }
        data = data + "dev_autoloadlevel:" + dev_autoloadlevel + "\n";
        data = data + "mp_server_visible:" + mp_server_visible + "\n";
        data = data + "gfx_fancygraphics:" + gfx_fancygraphics + "\n";
        data = data + "dev_showchunkmap:" + dev_showchunkmap + "\n";
        data = data + "dev_disablefilesystem:" + dev_disablefilesystem + "\n";
        data = data + "game_difficulty:" + game_difficulty + "\n";
        data = data + "ctrl_islefthanded:" + ctrl_islefthanded + "\n";
        data = data + "old_game_version_patch:" + old_game_version_patch + "\n";
        data = data + "feedback_vibration:" + feedback_vibration;
        return data;
    }
}
