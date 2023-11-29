package com.iut.banque.facade;

import com.iut.banque.constants.LoginConstants;
import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.Gestionnaire;
import com.iut.banque.modele.Utilisateur;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Timestamp;

public class LoginManager {

	private IDao dao;

	private String salt;
	private Utilisateur user;

	/**
	 * Setter pour la DAO.
	 * 
	 * Utilisé par Spring par Injection de Dependence
	 * 
	 * @param dao
	 *            : la dao nécessaire pour le LoginManager
	 */
	public void setDao(IDao dao) {
		this.dao = dao;
	}

	/**
	 * Méthode pour permettre la connection de l'utilisateur via un login en
	 * confrontant le mdp d'un utilisateur de la base de données avec le mot de
	 * passe donné en paramètre
	 * 
	 * @param userCde
	 *            : un String correspondant au userID de l'utilisateur qui
	 *            cherche à se connecter
	 * @param userPwd
	 *            : un String correspondant au mot de passe qui doit être
	 *            confronté avec celui de la base de données
	 * @return int correspondant aux constantes LoginConstants pour inforer de
	 *         l'état du login
	 */
	public int tryLogin(String userCde, String userPwd) {
		String userHashPwd = BCrypt.hashpw(userPwd, salt);

		if (dao.isUserAllowed(userCde, userHashPwd)) {
			user = dao.getUserById(userCde);
			if (user instanceof Gestionnaire) {
				return LoginConstants.MANAGER_IS_CONNECTED;
			} else {
				return LoginConstants.USER_IS_CONNECTED;
			}
		} else {
			return LoginConstants.LOGIN_FAILED;
		}
	}

	/**
	 * Getter pour avoir l'objet Utilisateur de celui qui est actuellement
	 * connecté à l'application
	 * 
	 * @return Utilisateur : l'objet Utilisateur de celui qui est connecté
	 */
	public Utilisateur getConnectedUser() {
		return user;
	}

	/**
	 * Setter pour changer l'utilisateur actuellement connecté à l'application
	 * 
	 * @param user
	 *            : un objet de type Utilisateur (Client ou Gestionnaire) que
	 *            l'on veut définir comme utilisateur actuellement connecté à
	 *            l'application
	 */
	public void setCurrentUser(Utilisateur user) {
		this.user = user;
	}

	/**
	 * Remet l'utilisateur à null et déconnecte la DAO.
	 */
	public void logout() {
		this.user = null;
		dao.disconnect();
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public void updateFailedAttempts(String username) {
		Utilisateur user = dao.getUserById(username);

		if (user != null) {
			int currentAttempts = user.getNbTentatives();
			user.setNbTentatives(currentAttempts + 1);

			// Mettre à jour la base de données avec le nouveau nombre de tentatives
			dao.updateUser(user);

			System.out.println("tentatives : " + user.getNbTentatives());
		} else {
			System.out.println("Utilisateur non trouvé : " + username);
		}
	}

	public void resetFailedAttempts(String userCde) {
		Utilisateur user = dao.getUserById(userCde);

		if (user != null) {
			user.setNbTentatives(0);

			// Mettre à jour la base de données avec le nouveau nombre de tentatives
			dao.updateUser(user);

			System.out.println("tentatives reset :" + user.getNbTentatives());
		} else {
			System.out.println("Utilisateur non trouvé : " + userCde);
		}
	}


	public boolean isAccountLocked(String userCde) {
		Utilisateur user = dao.getUserById(userCde);

		if (user != null) {
			int nbTentatives = user.getNbTentatives();
			Timestamp finBlocage = user.getFinBlocageConnexion();

			System.out.println(finBlocage);

			// Vérifier si le nombre de tentatives est atteint
			if (nbTentatives >= 3) {
				user.setNbTentatives(0);
				user.setFinBlocageConnexion(new Timestamp(System.currentTimeMillis() + 300000)); // 5 minutes
				return true; // Le compte est bloqué
			}

			// Vérifier si la date de fin de blocage est postérieure à l'heure actuelle
			if (finBlocage != null && finBlocage.compareTo(new Timestamp(System.currentTimeMillis())) > 0) {
				user.setNbTentatives(0);
				return true; // Le compte est bloqué
			}
		} else {
			System.out.println("Utilisateur non trouvé : " + userCde);
		}

		return false; // Le compte n'est pas bloqué
	}


}
