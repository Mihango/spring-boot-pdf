package com.example.thymeleaf.sample.config.utils;

import com.lowagie.text.DocumentException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
public class PdfGeneratorUtil {

    private TemplateEngine templateEngine;

    public PdfGeneratorUtil(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String createPdf(String templateName, Map map) {
        Assert.notNull(templateName, "The template cannot be null");
        Context context =  new Context();

        if(map != null) {
            for (Object o : map.entrySet()) {
                Map.Entry pair = (Map.Entry) o;
                context.setVariable(pair.getKey().toString(), pair.getValue());
            }
        }

        String processHtml = templateEngine.process(templateName, context);
        FileOutputStream os = null;
        String fileName = UUID.randomUUID().toString();

        try {
            final File outputFile = File.createTempFile(fileName, ".pdf");
            os = new FileOutputStream(outputFile);
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(processHtml);
            renderer.layout();
            renderer.createPDF(os, false);
            renderer.finishPDF();
            System.out.println("PDF generate successful");
            return outputFile.getAbsolutePath();
        } catch (DocumentException e) {
            return null;
        }
        catch (IOException e) {
            // throw error
            return null;
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {

                }
            }
        }
    }

}
