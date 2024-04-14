package cat.iesesteveterradas;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.font.PDType0Font;

public class PdfMaker {
    public static void generatePdf(List<String> lines, String outputPath) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Load the font
            File fontFile = new File(System.getProperty("user.dir") + "/data/NotoSans.ttf");
            PDType0Font font = PDType0Font.load(document, fontFile);

            contentStream.setFont(font, 12);
            contentStream.setLeading(14.5f);

            int yPos = 725; // Starting y position for the first line
            for (String line : lines) {
                if (line.contains("\f")) {
                    // Close the current stream and page
                    contentStream.close();
                    // Create a new page and reset the line counter
                    page = new PDPage();
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    contentStream.setFont(font, 12);
                    contentStream.setLeading(14.5f);
                    yPos = 725; // Reset y position for the new page
                    continue; // Skip processing for the form feed itself
                }
                
                String[] splitLines = line.split("\n");
                for (String splitLine : splitLines) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(25, yPos);
                    contentStream.showText(splitLine);
                    contentStream.endText();
                    yPos -= 14.5f; // Move y position for the next line
                }
            }

            contentStream.close();
            document.save(outputPath);
            System.out.println("PDF created at: " + outputPath);
        } catch (IOException e) {
            System.err.println("Error creating PDF: " + e.getMessage());
        }
    }
}



