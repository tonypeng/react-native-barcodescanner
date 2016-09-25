package com.eguma.barcodescanner;

import android.os.AsyncTask;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

class BarcodeScannerAsyncTask extends AsyncTask<Void, Void, String[]> {
    public interface Callback {
        void onReadCompleted(String type, String data);
    }

    private QRCodeReader _reader;
    private byte[] _data;
    private int _width;
    private int _height;
    private Callback _callback;

    public BarcodeScannerAsyncTask(byte[] data, int width, int height, Callback callback) {
        _data = data;
        _width = width;
        _height = height;
        _callback = callback;
        _reader = new QRCodeReader();
    }

    @Override
    protected String[] doInBackground(Void... params) {
        Result rawResult = null;
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(_data, _width, _height, 0, 0, _width, _height, false);

        if (source != null) {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                rawResult = _reader.decode(bitmap);
            } catch (ReaderException re) {
                // continue
            } catch (NullPointerException npe) {
                // This is terrible
            } catch (ArrayIndexOutOfBoundsException aoe) {

            } finally {
                _reader.reset();
            }
        }

        if (rawResult != null) {
            return new String[] { rawResult.getBarcodeFormat().toString(), rawResult.getText() };
        }
        return new String[] { null, null };
    }

    @Override
    protected void onPostExecute(String[] strings) {
        _callback.onReadCompleted(strings[0], strings[1]);
    }
}