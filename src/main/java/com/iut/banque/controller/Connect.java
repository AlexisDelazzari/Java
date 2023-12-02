package com.iut.banque.controller;

import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.opensymphony.xwork2.ActionSupport;

import com.iut.banque.constants.LoginConstants;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.Utilisateur;

public class Connect extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private String userCde;
	private String userPwd;
	private BanqueFacade banque;


	/**
	 * Constructeur de la classe Connect
	 * 
	 * @return Un objet de type Connect avec façade BanqueFacade provenant de sa
	 *         factory
	 */
	public Connect() {
		System.out.println("In Constructor from Connect class ");
		ApplicationContext context = WebApplicationContextUtils
				.getRequiredWebApplicationContext(ServletActionContext.getServletContext());
		this.banque = (BanqueFacade) context.getBean("banqueFacade");
	}

	/**
	 * Méthode pour vérifier la connexion de l'utilisateur basé sur les
	 * paramêtres userCde et userPwd de cette classe
	 * 
	 * @return String, le resultat du login; "SUCCESS" si réussi, "ERROR" si
	 *         échec
	 */
	public String login() {
		System.out.println("Essai de login - 20180512...");

		if (userCde == null || userPwd == null) {
			return "ERROR";
		}
		userCde = userCde.trim();

		if (isAccountLocked(userCde)) {
			System.out.println("Le compte est bloqué. Réessayez plus tard.");
			return "ERROR";
		}

		int loginResult;
		try {
			loginResult = banque.tryLogin(userCde, userPwd);

			if (loginResult == LoginConstants.LOGIN_FAILED) {
				updateFailedAttempts(userCde);
			}
			else if (loginResult == LoginConstants.USER_IS_CONNECTED || loginResult == LoginConstants.MANAGER_IS_CONNECTED) {
				// Réinitialiser le nombre de tentatives, la date du dernier échec et la date de fin de blocage en cas de connexion réussie
				resetFailedAttempts(userCde);
			}

		} catch (Exception e) {
			e.printStackTrace();
			loginResult = LoginConstants.ERROR;
		}

		switch (loginResult) {
		case LoginConstants.USER_IS_CONNECTED:
			System.out.println("user logged in");
			return "SUCCESS";
		case LoginConstants.MANAGER_IS_CONNECTED:
			System.out.println("manager logged in");
			return "SUCCESSMANAGER";
		case LoginConstants.LOGIN_FAILED:
			System.out.println("login failed");
			return "ERROR";
		default:
			System.out.println("error");
			return "ERROR";
		}
	}


	/**
	 * Getter du champ userCde
	 * 
	 * @return String, le userCde de la classe
	 */
	public String getUserCde() {
		return userCde;
	}

	/**
	 * Setter du champ userCde
	 * 
	 * @param userCde
	 *            : String correspondant au userCode à établir
	 */
	public void setUserCde(String userCde) {
		this.userCde = userCde;
	}

	/**
	 * Getter du champ userPwd
	 * 
	 * @return String, le userPwd de la classe
	 */
	public String getUserPwd() {
		return userPwd;
	}

	/**
	 * Setter du champ userPwd
	 * 
	 * @param userPwd
	 *            : correspondant au pwdCde à établir
	 */
	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	/**
	 * Getter du champ utilisateur (uilisé pour récupérer l'utilisateur
	 * actuellement connecté à l'application)
	 * 
	 * @return Utilisateur, l'utilisateur de la classe
	 */
	public Utilisateur getConnectedUser() {
		return banque.getConnectedUser();
	}

	/**
	 * Méthode qui va récupérer sous forme de map la liste des comptes du client
	 * actuellement connecté à l'application
	 * 
	 * @return Map<String, Compte> correspondant à l'ID du compte et l'objet
	 *         Compte associé
	 */
	public Map<String, Compte> getAccounts() {
		return ((Client) banque.getConnectedUser()).getAccounts();
	}

	private void updateFailedAttempts(String username) {
		// Mettez à jour le nombre de tentatives infructueuses, la date du dernier échec et la date de fin de blocage dans la base de données
		banque.updateFailedAttempts(username);
	}

	private void resetFailedAttempts(String userCde) {
		// Mettez à jour le nombre de tentatives infructueuses et la date de fin de blocage dans la base de données
		banque.resetFailedAttempts(userCde);
	}

	private boolean isAccountLocked(String userCde) {
		return banque.isAccountLocked(userCde);
	}

	public String logout() {
		System.out.println("Logging out");
		banque.logout();
		return "SUCCESS";
	}

}
