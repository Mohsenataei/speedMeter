package com.mohsen.speedmeter.component.update;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;


import androidx.core.content.FileProvider;

import com.mohsen.speedmeter.BuildConfig;

import java.io.File;

public class Installer {
        public static void  installPackage(String path, Context context, String version) {
            Uri uri;
            String tmp = "roja"+version+".apk";
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                Intent intentInstall = new Intent(Intent.ACTION_VIEW);
                uri = Uri.fromFile(new File(path+"/"+tmp));
                intentInstall.setDataAndType(uri, "application/vnd.android.package-archive");
                intentInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentInstall);
            } else {
                Intent intentInstall = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID
                        + ".android.fileprovider", new File(path
                        , tmp));
                intentInstall.setData(uri);
                intentInstall.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intentInstall);
            }

        }
}
