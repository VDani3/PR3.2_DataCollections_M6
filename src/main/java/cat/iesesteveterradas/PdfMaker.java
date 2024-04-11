package cat.iesesteveterradas;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.util.List;

public class PdfMaker {

    public static void generatePdf(List<String> lines, String outputPath) {
        try (PDDocument document = new PDDocument()) {
            document.save(outputPath);
            PDPage page = new PDPage();
            document.addPage(page);
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(25, 725); // Adjusted for better alignment
            
            int lineCount = 0;
            for (String line : lines) {
                lineCount++;
                contentStream.showText(line);
                contentStream.newLine();
                
                // Adding a new page if needed
                if (lineCount % 45 == 0) {
                    contentStream.endText();
                    contentStream.close();
                    
                    if (lineCount < lines.size()) { // Check if there are more lines to add
                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        contentStream.beginText();
                        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                        contentStream.setLeading(14.5f);
                        contentStream.newLineAtOffset(25, 725);
                    }
                }
            }
            
            if (contentStream != null) { // Ensure to close the text and content stream properly
                contentStream.endText();
                contentStream.close();
            }
            
            document.save(outputPath);
            System.out.println("PDF created at: " + outputPath);
        } catch (IOException e) {
            System.err.println("Error creating PDF: " + e.getMessage());
        }
    }
}
