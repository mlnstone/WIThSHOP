package com.example.backend.common.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.util.XRLog;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Objects;

import static com.openhtmltopdf.pdfboxout.PdfRendererBuilder.FontStyle;

@Service
public class PdfRenderService {

    private InputStream open(String path) {
        return Objects.requireNonNull(
                getClass().getResourceAsStream(path),
                "font not found: " + path
        );
    }

    public byte[] renderHtmlToPdf(String html) {
        try (var baos = new ByteArrayOutputStream()) {
            XRLog.setLoggingEnabled(false);

            var builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.useDefaultPageSize(8.27f, 11.69f, PdfRendererBuilder.PageSizeUnits.INCHES); // A4

            builder.useFont(() -> open("/fonts/Pretendard-Regular.ttf"),
                    "Noto Sans KR", 400, FontStyle.NORMAL, true);
            builder.useFont(() -> open("/fonts/Pretendard-Black.ttf"),
                    "Noto Sans KR", 700, FontStyle.NORMAL, true);

            builder.withHtmlContent(html, null);
            builder.toStream(baos);
            builder.run();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF 생성 실패", e);
        }
    }
}