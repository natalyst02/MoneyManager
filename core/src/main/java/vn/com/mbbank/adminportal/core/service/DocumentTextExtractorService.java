package vn.com.mbbank.adminportal.core.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.util.CommonErrorCode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
@Service
public class DocumentTextExtractorService {




//
//
//
//    /**
//     * Extract text từ file upload
//     */
//    public String extractText(MultipartFile file) throws IOException {
//        String originalFilename = file.getOriginalFilename();
//        if (originalFilename == null) {
//            throw new PaymentPlatformException(CommonErrorCode.INTERNAL_SERVER_ERROR, "Filename không thể null");
//        }
//
//        String extension = getFileExtension(originalFilename).toLowerCase();
//
//        try (InputStream inputStream = file.getInputStream()) {
//            return switch (extension) {
//                case "pdf" -> extractFromPDF(inputStream);
//                case "docx" -> extractFromDOCX(inputStream);
//                default -> throw new PaymentPlatformException(CommonErrorCode.INTERNAL_SERVER_ERROR, "FileExtension không hỗ trợ"); // Fallback
//            };
//        } catch (Exception e) {
//            throw new IOException("Lỗi khi extract text từ file: " + originalFilename, e);
//        }
//    }
//
//    /**
//     * Extract text từ PDF
//     */
//    public String extractFromPDF(InputStream inputStream) throws IOException {
//        byte[] pdfBytes = inputStream.readAllBytes();
//
//        // Sử dụng Loader.loadPDF() thay vì PDDocument.load()
//        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
//            PDFTextStripper stripper = new PDFTextStripper();
//
//            // Cấu hình để extract text tốt hơn
//            stripper.setSortByPosition(true);
//            stripper.setLineSeparator("\n");
//
//            StringBuilder textBuilder = new StringBuilder();
//
//            // Extract từng page để có thể track vị trí
//            int numberOfPages = document.getNumberOfPages();
//
//            if (numberOfPages == 0) {
//                return "";
//            }
//
//            for (int i = 1; i <= numberOfPages; i++) {
//                stripper.setStartPage(i);
//                stripper.setEndPage(i);
//
//                String pageText = stripper.getText(document);
//                if (pageText != null && !pageText.trim().isEmpty()) {
//                    textBuilder.append("--- Page ").append(i).append(" ---\n");
//                    textBuilder.append(pageText.trim()).append("\n\n");
//                }
//            }
//
//            return textBuilder.toString().trim();
//        }
//    }
//
//    /**
//     * Extract text từ DOCX (Word 2007+)
//     */
//    public String extractFromDOCX(InputStream inputStream) throws IOException {
//        try (XWPFDocument document = new XWPFDocument(inputStream);
//             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
//
//            return extractor.getText();
//        }
//    }
//
//    /**
//     * Lấy extension của file
//     */
//    private String getFileExtension(String filename) {
//        int lastIndex = filename.lastIndexOf('.');
//        return lastIndex > 0 ? filename.substring(lastIndex + 1) : "";
//    }
//
//    /**
//     * Kiểm tra file có được hỗ trợ không
//     */
//    public boolean isSupportedFileType(String filename) {
//        String extension = getFileExtension(filename).toLowerCase();
//        return extension.matches("pdf|docx");
//    }
//
//
//    private int countWords(String text) {
//        if (text == null || text.trim().isEmpty()) {
//            return 0;
//        }
//        return text.trim().split("\\s+").length;
//    }

}
