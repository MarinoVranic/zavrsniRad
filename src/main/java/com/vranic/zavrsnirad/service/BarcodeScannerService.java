package com.vranic.zavrsnirad.service;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BarcodeScannerService {
    public String readBarcode(byte[] barcodeImage) throws IOException, NotFoundException {
        InputStream inputStream = new ByteArrayInputStream(barcodeImage);
        BufferedImage image = ImageIO.read(inputStream);

        if (image != null) {
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
            Result result = new MultiFormatReader().decode(binaryBitmap);
            return result.getText();
        }

        return null;
    }
}
