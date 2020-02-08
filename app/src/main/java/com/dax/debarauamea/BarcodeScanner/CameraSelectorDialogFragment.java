package com.dax.debarauamea.BarcodeScanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.dax.debarauamea.R;

import java.util.Objects;

public class CameraSelectorDialogFragment extends DialogFragment {
    public interface CameraSelectorDialogListener {
        void onCameraSelected(int cameraId);
    }

    private int mCameraId;
    private CameraSelectorDialogListener mListener;

    public void onCreate(Bundle state) {
        super.onCreate(state);
        setRetainInstance(true);
    }

    static CameraSelectorDialogFragment newInstance(CameraSelectorDialogListener listener, int cameraId) {
        CameraSelectorDialogFragment fragment = new CameraSelectorDialogFragment();
        fragment.mCameraId = cameraId;
        fragment.mListener = listener;
        return fragment;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(mListener == null) {
            dismiss();
//            return null;
        }

        CameraManager manager = (CameraManager) Objects.requireNonNull(getContext()).getSystemService(Context.CAMERA_SERVICE);
        String[] cameraNames = new String[0];
        try {
            if (manager != null) {
                cameraNames = manager.getCameraIdList();
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

//        int numberOfCameras = Camera.getNumberOfCameras();
//        String[] cameraNames = new String[numberOfCameras];
        int checkedIndex = 0;

        for (int i = 0; i < cameraNames.length; i++) {

            try {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraNames[i]);
                if (characteristics.get(CameraCharacteristics.LENS_FACING)!=null)
                {
                    String variable = Objects.requireNonNull(characteristics.get(CameraCharacteristics.LENS_FACING)).toString();
                    if (Integer.parseInt(variable) == CameraCharacteristics.LENS_FACING_FRONT)
                    {
                        cameraNames[i]="Front Facing";
                    }
                    else if (Integer.parseInt(variable) == CameraCharacteristics.LENS_FACING_BACK)  {
                        cameraNames[i]="Rear Facing";
                    }else{
                        cameraNames[i]="Camera ID:"+cameraNames[i];
                    }
                }else  {
                    cameraNames[i]="Camera ID:"+cameraNames[i];
                }

            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

//            Camera.CameraInfo info = new Camera.CameraInfo();
//            Camera.getCameraInfo(i, info);
//            if(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//                cameraNames[i] = "Front Facing";
//            } else if(info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                cameraNames[i] = "Rear Facing";
//            } else {
//                cameraNames[i] = "Camera ID: " + i;
//            }
            if(i == mCameraId) {
                checkedIndex = i;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle(R.string.select_camera)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(cameraNames, checkedIndex,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCameraId = which;
                            }
                        })
                // Set the action buttons
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedIndices results somewhere
                        // or return them to the component that opened the dialog
                        if (mListener != null) {
                            mListener.onCameraSelected(mCameraId);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        return builder.create();
    }
}

