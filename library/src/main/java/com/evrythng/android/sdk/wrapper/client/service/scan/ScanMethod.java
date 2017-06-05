package com.evrythng.android.sdk.wrapper.client.service.scan;


import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by phillipcui on 5/29/17.
 */

public enum ScanMethod {

    EAN_13("1d", "ean_13", Barcode.EAN_13),
    EAN_8("1d", "ean_8", Barcode.EAN_8),
    UPC_A("1d", "upc_a", Barcode.UPC_A),
    UPC_E("1d", "upc_e", Barcode.UPC_E),
    CODE_39("1d", "code_39", Barcode.CODE_39),
    CODE_93("1d", "code_93", Barcode.CODE_93),
    CODE_128("1d", "code_128", Barcode.CODE_128),
    ITF("1d", "itf", Barcode.ITF),
    CODABAR("1d", "codabar", Barcode.CODABAR),
    QR_CODE("2d", "qr_code", Barcode.QR_CODE),
    DATA_MATRIX("2d", "dm", Barcode.DATA_MATRIX);

    private final String scanMethod;
    private final String scanType;
    private final int format;

    ScanMethod(String scanMethod, String scanType, int format) {
        this.scanMethod = scanMethod;
        this.scanType = scanType;
        this.format = format;
    }

    public String geMethod() {
        return scanMethod;
    }

    public String getType() {
        return scanType;
    }

    public int getFormat() {
        return format;
    }
}
