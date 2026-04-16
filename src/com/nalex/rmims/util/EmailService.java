package com.nalex.rmims.util;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class EmailService {

    private static final String SMTP_HOST = "smtp.gmail.com"; // Change as needed
    private static final String SMTP_PORT = "587";

    public static boolean sendMonthlyReportEmail(String summaryText, int month, int year) throws Exception {
        // Prompt for credentials
        String username = JOptionPane.showInputDialog(null, "Enter sender email address:", 
                "Email Credentials", JOptionPane.QUESTION_MESSAGE);
        if (username == null || username.trim().isEmpty()) {
            return false; // Cancelled
        }
        
        JPasswordField passwordField = new JPasswordField();
        int option = JOptionPane.showConfirmDialog(null, passwordField, 
                "Enter password for " + username + ":", JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.QUESTION_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return false; // Cancelled
        }
        String password = new String(passwordField.getPassword());
        
        if (password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Password cannot be empty.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        String ceoEmail = JOptionPane.showInputDialog(null, "Enter CEO's email address:", 
                "Recipient Email", JOptionPane.QUESTION_MESSAGE);
        if (ceoEmail == null || ceoEmail.trim().isEmpty()) {
            return false; // Cancelled
        }

        // Generate PDF
        String pdfFileName = "Monthly_Report_" + month + "_" + year + ".pdf";
        System.out.println("Creating PDF: " + pdfFileName);
        try {
            createPDF(summaryText, pdfFileName);
            System.out.println("PDF created successfully");
        } catch (Exception e) {
            System.out.println("PDF creation failed: " + e.getMessage());
            throw new Exception("Failed to create PDF: " + e.getMessage(), e);
        }

        // Send email
        System.out.println("Sending email to: " + ceoEmail);
        try {
            sendEmail(pdfFileName, username, password, ceoEmail);
            System.out.println("Email sent successfully");
        } catch (Exception e) {
            System.out.println("Email sending failed: " + e.getMessage());
            throw new Exception("Failed to send email: " + e.getMessage(), e);
        }

        return true; // Success
    }

    private static void createPDF(String text, String fileName) throws IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();

        // Add title
        Paragraph title = new Paragraph("Monthly Summary Report");
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);

        // Add content
        String[] lines = text.split("\n");
        for (String line : lines) {
            document.add(new Paragraph(line));
        }

        document.close();
    }

    private static void sendEmail(String attachmentPath, String username, String password, String ceoEmail) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(ceoEmail));
        message.setSubject("Monthly Summary Report");

        // Create multipart message
        Multipart multipart = new MimeMultipart();

        // Text part
        BodyPart textPart = new MimeBodyPart();
        textPart.setText("Please find attached the monthly summary report in PDF format.");
        multipart.addBodyPart(textPart);

        // Attachment part
        BodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.setDataHandler(new DataHandler(new FileDataSource(attachmentPath)));
        attachmentPart.setFileName(new File(attachmentPath).getName());
        multipart.addBodyPart(attachmentPart);

        message.setContent(multipart);

        Transport.send(message);
    }
}