package com.example.thymeleaf.sample.config.utils;

import com.example.thymeleaf.sample.config.FileStorageProperties;
import com.example.thymeleaf.sample.exceptions.FileStorageException;
import com.lowagie.text.DocumentException;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

public class PdfGenerator {

    private final Path fileStorageLocation;
    private TemplateEngine templateEngine;

    public PdfGenerator(FileStorageProperties properties, TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
        this.fileStorageLocation = Paths.get(properties.getUploadDir())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            e.printStackTrace();
            throw  new FileStorageException("Could not create directory where files will be stored.", e);
        }
    }

    public String createPdf(String templateName, Map map) {
        FileOutputStream fileOutputStream = null;
        try {
            Assert.notNull(templateName, "The template cannot be null");
            Context context = new Context();

            if (map != null) {
                for (Object o : map.entrySet()) {
                    Map.Entry pair = (Map.Entry) o;
                    context.setVariable(pair.getKey().toString(), pair.getValue());
                }
            }

            String processHtml = templateEngine.process(templateName, context);
            String fileName = UUID.randomUUID().toString() + ".pdf";

            Path path = this.fileStorageLocation.resolve(fileName);
            Path savedPath = Files.createFile(path);
            File file = savedPath.toFile();
            fileOutputStream = new FileOutputStream(file);

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(processHtml);
            renderer.layout();
            renderer.createPDF(fileOutputStream, false);
            renderer.finishPDF();
            System.out.println("PDF generate successful");


            return file.getName();
        } catch (DocumentException | IOException e){
            return "false";
        } finally {
            try {
                if(fileOutputStream != null) {
                    fileOutputStream.close();
                }
            }catch (IOException e) {
                System.out.println("Error closing file stream");
            }
        }
    }
}
