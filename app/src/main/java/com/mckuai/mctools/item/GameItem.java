package com.mckuai.mctools.item;

/**
 * Created by kyly on 2015/8/11.
 */
public class GameItem {
    private String packageName = null;
    private String versionName =null;
    private boolean isRunning = false;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public  int getMinorVersion(){
        if (null != versionName && !versionName.isEmpty()){
            String[] version = versionName.split("\\.");
            if (null != version && 3 == version.length){
                try {
                    return Integer.parseInt(version[1]);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }
}
