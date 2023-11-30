package com.vranic.zavrsnirad.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.EAN13Writer;

import java.awt.image.BufferedImage;


public class BarcodeGeneratorService {

    public static BitMatrix generateEAN13Barcode(String barcodeData) throws WriterException {
        Writer writer = new EAN13Writer();
        return writer.encode(barcodeData, BarcodeFormat.EAN_13, 300, 100);
    }

}

