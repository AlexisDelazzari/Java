package com.iut.banque.controller;

import org.apache.struts2.ServletActionContext;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.facade.BanqueFacade;
import com.opensymphony.xwork2.ActionSupport;

import java.io.InputStream;
import java.util.Properties;
public class CreerUtilisateur extends ActionSupport {

	private static final long serialVersionUID = 1L;
	private BanqueFacade banque;
	private String userId;
	private String nom;
	private String prenom;
	private String adresse;
	private String userPwd;
	private boolean male;
	private boolean client;
	private String numClient;
	private String message;
	private String result;

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the nom
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * @param nom
	 *            the nom to set
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}

	/**
	 * @return the prenom
	 */
	public String getPrenom() {
		return prenom;
	}

	/**
	 * @param prenom
	 *            the prenom to set
	 */
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	/**
	 * @return the adresse
	 */
	public String getAdresse() {
		return adresse;
	}

	/**
	 * @param adresse
	 *            the adresse to set
	 */
	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	/**
	 * @return the userPwd
	 */
	public String getUserPwd() {
		return userPwd;
	}

	/**
	 * @param userPwd
	 *            the userPwd to set
	 */
	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	/**
	 * @return the male
	 */
	public boolean isMale() {
		return male;
	}

	/**
	 * @param male
	 *            the male to set
	 */
	public void setMale(boolean male) {
		this.male = male;
	}

	/**
	 * @return the user
	 */
	public boolean isClient() {
		return client;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setClient(boolean client) {
		this.client = client;
	}

	/**
	 * @return the numClient
	 */
	public String getNumClient() {
		return numClient;
	}

	/**
	 * @param numClient
	 *            the numClient to set
	 */
	public void setNumClient(String numClient) {
		this.numClient = numClient;
	}

	private static final String CONFIG_FILE = "config.properties";
	private static Properties properties;

	static {
		properties = new Properties();
		try (InputStream input = CreerUtilisateur.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
			properties.load(input);
		} catch (Exception e) {
			// Gérez les exceptions appropriées en fonction de votre application
			e.printStackTrace();
		}
	}

	private String salt;



	/**
	 * Constructeur sans paramêtre de CreerUtilisateur
	 */
	public CreerUtilisateur() {
		System.out.println("In Constructor from CreerUtilisateur class ");
		ApplicationContext context = WebApplicationContextUtils
				.getRequiredWebApplicationContext(ServletActionContext.getServletContext());
		this.banque = (BanqueFacade) context.getBean("banqueFacade");

		// Utilisez la propriété chargée
		this.salt = properties.getProperty("hash.salt");
	}

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

	/**
	 * Création d'un utilisateur.
	 * 
	 * @return String : le status de l'action
	 */
	public String creationUtilisateur() {
		try {
			String userHashPwd = BCrypt.hashpw(userPwd, salt);
			if (client) {
				banque.createClient(userId, userHashPwd, nom, prenom, adresse, male, numClient);
			} else {
				banque.createManager(userId, userHashPwd, nom, prenom, adresse, male);
			}
			this.message = "Le nouvel utilisateur avec le user id '" + userId + "' a bien été crée.";
			this.result = "SUCCESS";
			return "SUCCESS";
		} catch (IllegalOperationException e) {
			this.message = "L'identifiant à déjà été assigné à un autre utilisateur de la banque.";
			this.result = "ERROR";
			return "ERROR";
		} catch (TechnicalException e) {
			this.message = "Le numéro de client est déjà assigné à un autre client.";
			this.result = "ERROR";
			return "ERROR";
		} catch (IllegalArgumentException e) {
			this.message = "Le format de l'identifiant est incorrect.";
			this.result = "ERROR";
			return "ERROR";
		} catch (IllegalFormatException e) {
			this.message = "Format du numéro de client incorrect.";
			this.result = "ERROR";
			return "ERROR";
		}
	}
}
