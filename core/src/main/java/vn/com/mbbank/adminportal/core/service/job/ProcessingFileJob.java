package vn.com.mbbank.adminportal.core.service.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.util.CommonErrorCode;
import vn.com.mbbank.adminportal.common.util.Json;
import vn.com.mbbank.adminportal.core.model.FileStatus;
import vn.com.mbbank.adminportal.core.model.entity.FileEntity;
import vn.com.mbbank.adminportal.core.service.FileService;
import vn.com.mbbank.adminportal.core.thirdparty.presidio.PresidioClient;
import vn.com.mbbank.adminportal.core.thirdparty.presidio.model.SensitiveDto;
import vn.com.mbbank.adminportal.core.thirdparty.presidio.model.TextAnalysisDTO;
import vn.com.mbbank.adminportal.core.thirdparty.presidio.model.TextAnalysisInput;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
@Log4j2
@RequiredArgsConstructor
public class ProcessingFileJob {

    private final FileService fileService;
    private final int batchSize = 100;
    private final String jobCode = "ProcessingFileJob";
    private final PresidioClient presidioClient;


    @Scheduled(fixedDelay = 1000)
    public void processFile() {
        var id = jobCode + UUID.randomUUID();
        ThreadContext.put("clientMessageId", id);
        var files = fileService.getFilesByStatus(batchSize, FileStatus.INIT);
        if (files == null || files.isEmpty()) {
            log.info("No files to process");
            return;
        }

        for (var file : files) {
            try {
                processSingleFile(file);
            } catch (Exception e) {
                file.setStatus(FileStatus.FAILED.name());
                fileService.save(file);
                log.error("Error processing file", e);
            }
        }
    }

    private void processSingleFile(FileEntity file) throws IOException {
        file.setStatus(FileStatus.PROCESSING.name());
        fileService.save(file);

        Path path = Paths.get(file.getFileUrl());
        if (!Files.exists(path)) {
            throw new IOException("File not found at path: " + path.toString());
        }
        String content;
        String ex = null;
        try (InputStream inputStream = Files.newInputStream(path)) {
            content = switch (file.getFileExtension()) {
                case ".pdf" -> extractFromPDF(inputStream);
                case ".docx" -> extractFromDOCX(inputStream);
                default ->
                        throw new PaymentPlatformException(CommonErrorCode.INTERNAL_SERVER_ERROR, "FileExtension không hỗ trợ"); // Fallback
            };
        }
        List<TextAnalysisDTO> result = presidioClient.analyzeText(TextAnalysisInput.builder().text(content).language("en").build()).join();
        var endRes = new ArrayList<SensitiveDto>();

        if (result == null || result.isEmpty()) {
            file.setStatus(FileStatus.CLEAR.name());
            fileService.save(file);
            return;
        }


        for (TextAnalysisDTO r : result) {
            SensitiveDto dto = new SensitiveDto(String.copyValueOf(content.toCharArray(), r.getStart(), r.getEnd() - r.getStart()), "From " + r.getStart() + " to " + r.getEnd());
            endRes.add(dto);
        }
        file.setStatus(FileStatus.SENSITIVE.name());
        file.setFileInfo(Json.encodeToString(endRes));
        file.setTextDetect(Json.encodeToString(endRes));
        fileService.save(file);
    }

    public void highlightPDF(String inputPath, String outputPath, List<TextAnalysisDTO> highlights) {
        try (PDDocument document = PDDocument.load(new File(inputPath))) {
            // Trích xuất text để map vị trí
            PDFTextStripper stripper = new PDFTextStripper();
            String fullText = stripper.getText(document);

            for (PDPage page : document.getPages()) {
                List<PDAnnotation> annotations = new ArrayList<>();

                for (TextAnalysisDTO highlight : highlights) {
                    // Tạo highlight annotation
                    PDAnnotationTextMarkup textMarkup = new PDAnnotationTextMarkup(PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT);

                    // Set màu highlight dựa trên score
                    PDColor color = getColorByScore(highlight.getScore());
                    textMarkup.setColor(color);

                    // Tính toán vị trí highlight trên page
                    // (Cần implement logic map text position to PDF coordinates)
                    // Đây là phần phức tạp nhất, cần tính toán chính xác

                    annotations.add(textMarkup);
                }

                page.setAnnotations(annotations);
            }

            document.save(outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void highlightDOCX(String inputPath, String outputPath, List<TextAnalysisDTO> highlights) {
        try (FileInputStream fis = new FileInputStream(inputPath);
             XWPFDocument document = new XWPFDocument(fis)) {

            // Lấy toàn bộ text content
            StringBuilder fullText = new StringBuilder();
            Map<Integer, XWPFRun> textPositionMap = new HashMap<>();

            // Map text positions với runs
            int currentPosition = 0;
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    String runText = run.getText(0);
                    if (runText != null) {
                        for (int i = 0; i < runText.length(); i++) {
                            textPositionMap.put(currentPosition + i, run);
                        }
                        fullText.append(runText);
                        currentPosition += runText.length();
                    }
                }
                fullText.append("\n");
                currentPosition++;
            }

            // Apply highlights
            for (TextAnalysisDTO highlight : highlights) {
                highlightTextRange(document, highlight.getStart(), highlight.getEnd(),
                        highlight.getScore(), textPositionMap);
            }

            // Save file
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                document.write(fos);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void highlightTextRange(XWPFDocument document, int start, int end, double score,
                                    Map<Integer, XWPFRun> textPositionMap) {
        for (int i = start; i < end; i++) {
            XWPFRun run = textPositionMap.get(i);
            if (run != null) {
                run. setTextHighlightColor(getHighlightColorByScore(score));
            }
        }
    }

    private String getHighlightColorByScore(double score) {
        if (score >= 0.8) {
            return "red";
        } else if (score >= 0.6) {
            return "yellow";
        } else {
            return "lightGreen";
        }
    }
    private PDColor getColorByScore(double score) {
        if (score >= 0.8) {
            return new PDColor(new float[]{1.0f, 0.0f, 0.0f}, PDDeviceRGB.INSTANCE); // Red
        } else if (score >= 0.6) {
            return new PDColor(new float[]{1.0f, 1.0f, 0.0f}, PDDeviceRGB.INSTANCE); // Yellow
        } else {
            return new PDColor(new float[]{0.0f, 1.0f, 0.0f}, PDDeviceRGB.INSTANCE); // Green
        }
    }

    /**
     * Extract text từ PDF
     */
    public String extractFromPDF(InputStream inputStream) throws IOException {
        byte[] pdfBytes = inputStream.readAllBytes();

        // Sử dụng Loader.loadPDF() thay vì PDDocument.load()
        try (PDDocument document = PDDocument.load(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();

            // Cấu hình để extract text tốt hơn
            stripper.setSortByPosition(true);
            stripper.setLineSeparator("\n");

            StringBuilder textBuilder = new StringBuilder();

            // Extract từng page để có thể track vị trí
            int numberOfPages = document.getNumberOfPages();

            if (numberOfPages == 0) {
                return "";
            }

            for (int i = 1; i <= numberOfPages; i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);

                String pageText = stripper.getText(document);
                if (pageText != null && !pageText.trim().isEmpty()) {
                    textBuilder.append("--- Page ").append(i).append(" ---\n");
                    textBuilder.append(pageText.trim()).append("\n\n");
                }
            }

            return textBuilder.toString().trim();
        }
    }

    /**
     * Extract text từ DOCX (Word 2007+)
     */
    public String extractFromDOCX(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {

            return extractor.getText();
        }
    }
}
