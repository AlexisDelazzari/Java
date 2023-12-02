package com.iut.banque.controller;

import java.io.InputStream;
import java.util.Random;

import com.iut.banque.facade.BanqueManager;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.iut.banque.modele.Utilisateur;
import org.apache.struts2.ServletActionContext;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class ForgotPwd extends ActionSupport {
    private int codeRecu;
    private String nouveauMdp;
    private String codeUser;
    private String codeMail;
    private BanqueManager bm;
    private Utilisateur user;
    private String userMail;
    private String salt;
    private String email;
    private String mdpemail;
    private String message;
    private String result;


    public String getCodeUser() {
        return codeUser;
    }

    public int getCodeRecu() {
        return codeRecu;
    }

    public void setCodeRecu(int codeRecu) {
        this.codeRecu = codeRecu;
    }

    public String getNouveauMdp() {
        return nouveauMdp;
    }

    public void setNouveauMdp(String nouveauMdp) {
        this.nouveauMdp = nouveauMdp;
    }

    public void setCodeUser(String codeUser) {
        this.codeUser = codeUser;
    }

    public String getCodeMail() {
        return codeMail;
    }

    public void setCodeMail(String codeMail) {
        this.codeMail = codeMail;
    }

    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties;

    /**
     * Renvoie Le message à afficher si la création d'un utilisateur vient
     * d'être essayée.
     *
     * @return le message de l'action précédente
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter du message provenant de l'action précedente.
     *
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Le result indique si l'utilisateur vient d'arriver sur la page ou a tenté
     * la création d'un utilisateur précedemment.
     *
     * @return le status de l'action précedente.
     */
    public String getResult() {
        return result;
    }

    /**
     * Setter du result de l'action précedente
     *
     * @param result
     */
    public void setResult(String result) {
        this.result = result;
    }

    static {
        properties = new Properties();
        try (InputStream input = CreerUtilisateur.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (Exception e) {
            // Gérez les exceptions appropriées en fonction de votre application
            e.printStackTrace();
        }
    }

    public ForgotPwd() {
        System.out.println("In Constructor from ForgotPwd class ");
        ApplicationContext context = WebApplicationContextUtils
                .getRequiredWebApplicationContext(ServletActionContext.getServletContext());

        this.bm = (BanqueManager) context.getBean("banqueManager");
        this.salt = properties.getProperty("hash.salt");
        this.email = properties.getProperty("email");
        this.mdpemail = properties.getProperty("mdpemail");
    }

    public String resetCodeSession() {
        ActionContext.getContext().getSession().put("codeUser", null);
        return "SUCCESS";
    }

    public String sendMail() throws Exception {
        user = bm.getUserById(codeUser);

        if (user == null) {
            this.result = "ERROR";
            this.message = "L'utilisateur n'existe pas.";
            return "ERROR";
        }

        userMail = user.getMail();
        int code = GenerateCode();
        EnvoieMail(code, user);
        bm.updateUser(user, code);
        ActionContext.getContext().getSession().put("codeUser", codeUser);
        this.result = "SUCCESS";
        this.message = "Un e-mail contenant un code de vérification vous a été envoyé.";
        return "SUCCESS";
    }

    public String resetPwd() throws Exception {
        String codeUserSession = (String) ActionContext.getContext().getSession().get("codeUser");
        user = bm.getUserById(codeUserSession);
        if (codeRecu == 0 || nouveauMdp == null) {
            this.result = "ERROR";
            this.message = "Veuillez saisir un code et un nouveau mot de passe.";
            return "ERROR";
        }
        nouveauMdp = BCrypt.hashpw(nouveauMdp, salt);

        if (user.getCodeForgotPwd() == codeRecu) {
            System.out.println("codeRecu: " + codeRecu);
            bm.updatePwd(user, nouveauMdp);
            this.result = "SUCCESS";
            this.message = "Votre mot de passe a bien été modifié.";
            return "SUCCESS";
        }
        this.result = "ERROR";
        this.message = "Le code saisi est incorrect.";
        return "ERROR";
    }

    private int GenerateCode() {
        Random rand = new Random();
        int code = rand.nextInt(999999);
        return code;
    }

    private void EnvoieMail(int code, Utilisateur user) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, mdpemail);
            }
        });
        try {
            // Création du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userMail));
            message.setSubject("Mot de passe oublié");

            // Ajout du code en tant que contenu du message
            String codeContent = "Bonjour " + user.getPrenom() + " " + user.getNom() + ",\n\n"
                    + "Vous avez demandé à réinitialiser votre mot de passe.\n"
                    + "Veuillez saisir le code suivant dans le champ prévu à cet effet sur le site de la banque :\n\n"
                    + code + "\n\n"
                    + "Si vous n'êtes pas à l'origine de cette demande, veuillez ignorer ce message.\n\n"
                    + "Cordialement,\n"
                    + "L'équipe de la banque.";
            message.setText(codeContent);

            // Envoi du message
            Transport.send(message);

            System.out.println("E-mail envoyé avec succès.");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
