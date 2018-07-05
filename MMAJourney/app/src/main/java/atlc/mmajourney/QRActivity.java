package atlc.mmajourney;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class QRActivity extends AppCompatActivity
{
    private SurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        surfaceView = (SurfaceView) findViewById(R.id.camara);
        activarCamara();
    }

    public void activarCamara()
    {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).build();
        final CameraSource cameraSource = new CameraSource.Builder(this, barcodeDetector).setAutoFocusEnabled(true).build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback()
        {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder)
            {
                if (ActivityCompat.checkSelfPermission(QRActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                try
                {
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2)
            {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder)
            {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>()
        {

            @Override
            public void release()
            {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections)
            {
                SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0)
                {
                    Intent intent = new Intent();
                    intent.putExtra("barcode", barcodes.valueAt(0));
                    setResult(CommonStatusCodes.SUCCESS, intent);
                    finish();
                }
            }
        });
    }
}
