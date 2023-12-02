package com.iut.banque.controller;

import com.opensymphony.xwork2.ActionSupport;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EnvoieMailAction extends ActionSupport {

    public static void main(String[] args) {

        String destinataire = "destinataire@example.com";
        String sujet = "Sujet du mail";
        String corps = "Contenu du mail";


        envoyerEmail(destinataire, sujet, corps);
    }

    public static void envoyerEmail(String destinataire, String sujet, String corps) {

        String host = "smtp.gmail.com";
        String utilisateur = "testqualite2@gmail.com";
        String motDePasse = "qualite2";


        Properties proprietes = new Properties();
        proprietes.put("mail.smtp.auth", "true");
        proprietes.put("mail.smtp.starttls.enable", "true");
        proprietes.put("mail.smtp.host", host);
        proprietes.put("mail.smtp.port", "587");


        Session session = Session.getInstance(proprietes, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(utilisateur, motDePasse);
            }
        });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(utilisateur));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            message.setSubject(sujet);
            message.setText(corps);


            Transport.send(message);

            System.out.println("Email envoyé avec succès.");

        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }
}
