package com.refercity.util;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

public class PDFUtil {
    public static String extractText(MultipartFile file) throws Exception {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            // 按照页面顺序提取文本
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        }
    }
}
