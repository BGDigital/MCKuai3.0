package com.mckuai.widget.autoupdate.internal;

import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mckuai.imc.R;
import com.mckuai.widget.autoupdate.Version;

public class FoundVersionDialog {

	private Context context;
	private Version version;
	private VersionDialogListener listener;
	
	public FoundVersionDialog(Context context,Version version,VersionDialogListener listener){
		this.context = context;
		this.version = version;
		this.listener = listener;
	}
	
	public void show(){
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_found_version);
        //Log.e("FoundVersionDialog", "activity:" + context.getClass().getName());
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        
        TextView title = (TextView) dialog.findViewById(R.id.title);
        TextView name = (TextView) dialog.findViewById(R.id.version);
        TextView feature = (TextView) dialog.findViewById(R.id.feature);
        feature.setMovementMethod(ScrollingMovementMethod.getInstance());
        title.setText(context.getResources().getString(R.string.latest_version_title));
        name.setText(version.name);
		String[] features = version.feature.split(";");
		String result="";
		if (null != features && 0 != features.length){
			for (int i = 0;i < features.length;i++){
				if (0 == i){
					result += features[i];
				}else {
					result += ('\n'+features[i]);
				}
			}
		}
        feature.setText(0 == result.length() ? version.feature:result);
        
        View ignore = dialog.findViewById(R.id.ignore);
        View update = dialog.findViewById(R.id.update);
        final CheckBox laterOnWifi = (CheckBox) dialog.findViewById(R.id.only_wifi);
        
        if( NetworkUtil.getNetworkType(context) != NetworkUtil.WIFI ){
        	laterOnWifi.setVisibility(View.VISIBLE);
        }else{
        	laterOnWifi.setVisibility(View.GONE);
        }
        
        ignore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				listener.doIgnore();
			}
		});
        
        update.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				boolean laterOnWifiFlag = laterOnWifi.isChecked();
				listener.doUpdate(laterOnWifiFlag);
			}
		});
        
        dialog.show();
        
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();                
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);  
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.heightPixels;
        int height = metrics.widthPixels;
	    if (height > width) {  
	        lp.width = (int) (width * 0.9);          
	    } else {  
	        lp.width = (int) (width * 0.5);                  
	    }  
	    dialog.getWindow().setAttributes(lp);
	}
}
