package com.wei.application.Service.Impl;

import com.wei.application.Service.OcrService;
import com.wei.application.utils.RecognizeBasic;
import org.springframework.stereotype.Service;

@Service("ocrService")
public class OcrServiceImpl implements OcrService {
    @Override
    public String getRecognizeText(String url) throws Exception {
        RecognizeBasic recognizeBasic = new RecognizeBasic();
        return recognizeBasic.GetRecognizeBasic(url);
    }
}
