package com.mckuai.widget.autoupdate.internal;


import com.mckuai.widget.autoupdate.Version;

public interface ResponseCallback {
	void onFoundLatestVersion(Version version);
	void onCurrentIsLatest();
}
