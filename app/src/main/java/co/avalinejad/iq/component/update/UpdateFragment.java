package co.avalinejad.iq.component.update;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import co.avalinejad.iq.R;
import co.avalinejad.iq.component.update.Repo.model.UpdateResult;

import java.io.File;

import co.avalinejad.iq.component.update.Repo.model.UpdateResult;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStorageDirectory;

public class UpdateFragment extends DialogFragment {
    File mkFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), "/roja");
    File folder = new File(DIRECTORY_DOWNLOADS, "/roja");
    private DownloadManager downloadManager;
    private long enqueue;
    private String downloadURL;
    private Act0 dismissListener;
    private BroadcastReceiver receiver;
    private int versionCode = 0;

    public static UpdateFragment newInstance(UpdateResult param1) {
        UpdateFragment fragment = new UpdateFragment();
        Bundle args = new Bundle();
        args.putSerializable("up", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.update_dialogfragment, container, false);
        setCancelable(false);

        Button downloadButton = view.findViewById(R.id.button_download);
        downloadButton.setText("دانلود");
        TextView description = view.findViewById(R.id.description);
        UpdateResult up = (UpdateResult) getArguments().getSerializable("up");
        StringBuilder updateText = new StringBuilder().append(up.getDescription()).append("\n نسخه ")
                .append(up.getVersionName());
        description.setText(updateText.toString());
        versionCode = up.getVersionCode();
        downloadURL = up.getDownloadLink();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enqueue);

                    Cursor c = downloadManager.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c
                                .getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                            Installer.installPackage(getExternalStorageDirectory()
                                            .getPath() + File.separator +
                                            Environment.DIRECTORY_DOWNLOADS + "/roja"
                                    , context, String.valueOf(versionCode));

                        }
                    }
                }
            }
        };
        return view;
    }

    private void downloadFile(File folder) {
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(downloadURL));

        request.setDestinationInExternalPublicDir(folder.getPath(), "roja" + versionCode + ".apk");
        enqueue = downloadManager.enqueue(request);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Dialog dialog = getDialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);

        FragmentActivity activity = getActivity();
        downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);

        activity.registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkForPermissions();
            return;
        }

        if (!mkFile.exists()) {
            mkFile.mkdirs();
        }
        downloadFile(folder);

    }

    public void setDismissListener(Act0 action0) {
        this.dismissListener = action0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dismissListener != null) {
            dismissListener.call();
        }
    }


    void checkForPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1001);
            }
        } else {
            onFileWritePermissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001)
            checkForPermissions();
    }

    private void onFileWritePermissionGranted() {

        mkFile.mkdirs();
        downloadFile(folder);
    }

}
