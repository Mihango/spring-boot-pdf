package com.example.thymeleaf.sample.resources;

import com.example.thymeleaf.sample.config.FileStorageProperties;
import com.example.thymeleaf.sample.config.utils.PdfGenerator;
import com.example.thymeleaf.sample.config.utils.PdfGeneratorUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.TemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Controller
public class SimpleController {

    private PdfGeneratorUtil pdfGenerator;
    private FileStorageProperties properties;
    private TemplateEngine templateEngine;

    public SimpleController(PdfGeneratorUtil pdfGenerator, FileStorageProperties properties, TemplateEngine templateEngine) {
        this.pdfGenerator = pdfGenerator;
        this.properties = properties;
        this.templateEngine = templateEngine;
    }

    @Value("${spring.application.name}")
    String appName;

    @GetMapping("/")
    public String getIndex(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("revenueAuthority", "DOMESTIC REVENUE MINISTRY");
        model.addAttribute("serial", "SN: 123456");
        return "index";
    }

    @GetMapping("/home")
    public String getHome(Model model) {
        model.addAttribute("serial", "SN: 1234343");
        return "home";
    }

    @GetMapping("/certificate")
    public ResponseEntity<Object> generatePdf() {
        Map<String, String> data = new HashMap<>();
        data.put("serial", "SN: 123456789");
        String filePath = pdfGenerator.createPdf("index", data);

        if(filePath != null) {
            return ResponseEntity.ok("PDF Generate " + filePath);
        } else {
            return ResponseEntity.status(400).body("Failed to generate PDF");
        }
    }

    @GetMapping("/text")
    public ResponseEntity<Object> generatePathPdf() {
        System.out.println("File properties = " + (properties == null));
        System.out.println("Template null = " + (templateEngine == null));
        Map<String, String> data = new HashMap<>();
        data.put("serial", "SN: 123456789");
        data.put("revenueAuthority", "DOMESTIC REVENUE MINISTRY");
        PdfGenerator generator = new PdfGenerator(properties, templateEngine);
        String filePath =  generator.createPdf("index", data); //pdfGenerator.createPdf("index", data);

        if(filePath != null) {
            return ResponseEntity.ok("PDF Generate " + filePath);
        } else {
            return ResponseEntity.status(400).body("Failed to generate PDF");
        }
    }
}
