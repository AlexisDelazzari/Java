package com.iut.banque.test.facade;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.facade.BanqueManager;

//@RunWith indique à JUnit de prendre le class runner de Spirng
@RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration permet de charger le context utilisé pendant les tests.
// Par défault (si aucun argument n'est précisé), cherche le fichier
/// src/com/iut/banque/test/TestsDaoHibernate-context.xml
@ContextConfiguration("classpath:TestsBanqueManager-context.xml")
@Transactional("transactionManager")
public class TestsBanqueManager {

	@Autowired
	private BanqueManager bm;

	// Tests de par rapport à l'ajout d'un client
	@Test
	public void TestCreationDunClient() {
		try {
			bm.loadAllClients();
			bm.createClient("t.test1", "password", "test1nom", "test1prenom", "test town", true, "4242424242","adelazzari8@gmail.com");
		} catch (IllegalOperationException e) {
			e.printStackTrace();
			fail("IllegalOperationException récupérée : " + e.getStackTrace());
		} catch (Exception te) {
			te.printStackTrace();
			fail("Une Exception " + te.getClass().getSimpleName() + " a été récupérée");
		}
	}

	@Test
	public void TestCreationDunClientAvecDeuxNumerosDeCompteIdentiques() {
		try {
			bm.loadAllClients();
			bm.createClient("t.test1", "password", "test1nom", "test1prenom", "test town", true, "0101010101","adelazzari8@gmail.com");
			fail();
		} catch (IllegalOperationException e) {
		} catch (Exception te) {
			te.printStackTrace();
			fail("Une Exception " + te.getClass().getSimpleName() + " a été récupérée");
		}
	}

	// Tests par rapport à la suppression de comptes
	@Test
	public void TestSuppressionDunCompteAvecDecouvertAvecSoldeZero() {
		try {
			bm.deleteAccount(bm.getAccountById("CADV000000"));
		} catch (IllegalOperationException e) {
			e.printStackTrace();
			fail("IllegalOperationException récupérée : " + e.getStackTrace());
		} catch (Exception te) {
			fail("Une Exception " + te.getClass().getSimpleName() + " a été récupérée");
		}
	}

	@Test
	public void TestSuppressionDunCompteAvecDecouvertAvecSoldeDifferentDeZero() {
		try {
			bm.deleteAccount(bm.getAccountById("CADNV00000"));
			fail("Une IllegalOperationException aurait dû être récupérée");
		} catch (IllegalOperationException e) {
		} catch (Exception te) {
			fail("Une Exception " + te.getClass().getSimpleName() + " a été récupérée");
		}
	}

	@Test
	public void TestSuppressionDunCompteSansDecouvertAvecSoldeZero() {
		try {
			bm.deleteAccount(bm.getAccountById("CSDV000000"));
		} catch (IllegalOperationException e) {
			e.printStackTrace();
			fail("IllegalOperationException récupérée : " + e.getStackTrace());
		} catch (Exception te) {
			fail("Une Exception " + te.getClass().getSimpleName() + " a été récupérée");
		}
	}

	@Test
	public void TestSuppressionDunCompteSansDecouvertAvecSoldeDifferentDeZero() {
		try {
			bm.deleteAccount(bm.getAccountById("CSDNV00000"));
			fail("Une IllegalOperationException aurait dû être récupérée");
		} catch (IllegalOperationException e) {
		} catch (Exception te) {
			fail("Une Exception " + te.getClass().getSimpleName() + " a été récupérée");
		}
	}

	// Tests en rapport avec la suppression d'utilisateurs
	@Test
	public void TestSuppressionDunUtilisateurSansCompte() {
		try {
			bm.loadAllClients();
			bm.deleteUser(bm.getUserById("g.pasdecompte"));
		} catch (IllegalOperationException e) {
			e.printStackTrace();
			fail("IllegalOperationException récupérée : " + e.getStackTrace());
		} catch (Exception te) {
			te.printStackTrace();
			fail("Une Exception " + te.getClass().getSimpleName() + " a été récupérée");
		}
	}

	@Test
	public void TestSuppressionDuDernierManagerDeLaBaseDeDonnees() {
		bm.loadAllGestionnaires();
		try {
			bm.deleteUser(bm.getUserById("admin"));
			fail("Une IllegalOperationException aurait dû être récupérée");
		} catch (IllegalOperationException e) {
		} catch (Exception te) {
			te.printStackTrace();
			fail("Une Exception " + te.getClass().getSimpleName() + " a été récupérée");
		}
	}

	@Test
	public void TestSuppressionDunClientAvecComptesDeSoldeZero() {
		try {
			bm.loadAllClients();
			bm.deleteUser(bm.getUserById("g.descomptesvides"));
			if (bm.getAccountById("KL4589219196") != null || bm.getAccountById("KO7845154956") != null) {
				fail("Les comptes de l'utilisateur sont encore présents dans la base de données");
			}
		} catch (IllegalOperationException e) {
			e.printStackTrace();
			fail("IllegalOperationException récupérée : " + e.getStackTrace());
		} catch (Exception te) {
			te.printStackTrace();
			fail("Une Exception " + te.getClass().getSimpleName() + " a été récupérée");
		}
	}

	@Test
	public void TestSuppressionDunClientAvecUnCompteDeSoldePositif() {
		try {
			bm.deleteUser(bm.getUserById("j.doe1"));
			fail("Une IllegalOperationException aurait dû être récupérée");
		} catch (IllegalOperationException e) {
		} catch (Exception te) {
			fail("Une Exception " + te.getClass().getSimpleName() + " a été récupérée");
		}
	}

	@Test
	public void TestSuppressionDunClientAvecUnCompteAvecDecouvertDeSoldeNegatif() {
		try {
			bm.deleteUser(bm.getUserById("j.doe1"));
			fail("Une IllegalOperationException aurait dû être récupérée");
		} catch (IllegalOperationException e) {
		} catch (Exception te) {
			fail("Une Exception " + te.getClass().getSimpleName() + " a été récupérée");
		}
	}

	//test insertion d'un nouveau utilisateur pour voir si le mdp est bien crypté

	@Test
	public void TestCreationDunUtilisateurCrypte() {
		try {
			bm.loadAllClients();
			String salt = BCrypt.gensalt();
			String userHashPwd = BCrypt.hashpw("password", salt);
			bm.createClient("t.test1", userHashPwd, "test1nom", "test1prenom", "test town", true, "4242424242","adelazzari8@gmail.com");
			//on regarde si le mdp est bien crypté
			if (bm.getUserById("t.test1").getUserPwd().equals("password")) {
				fail("Le mot de passe n'est pas crypté");
			}
		} catch (IllegalOperationException e) {
			e.printStackTrace();
			fail("IllegalOperationException récupérée : " + e.getStackTrace());
		} catch (Exception te) {
			te.printStackTrace();
			fail("Une Exception " + te.getClass().getSimpleName() + " a été récupérée");
		}
	}
	//on test la foncitonnalité mot de passe oublié
	@Test
	public void changementMdpWithGoodCode(){
		try {
			bm.loadAllClients();
			bm.loadAllClients();
			String salt = BCrypt.gensalt();
			String userHashPwd = BCrypt.hashpw("password", salt);
			bm.createClient("t.test1", userHashPwd, "test1nom", "test1prenom", "test town", true, "4242424242","adelazzari8@gmail.com");
			bm.updateUser(bm.getUserById("t.test1"), 123456);

			String pwd  = "password";

			var user = bm.getUserById("t.test1");
			bm.resetPwd(user, pwd, 123456);

			System.out.println(user.getUserPwd());
			if(user.getUserPwd().equals(userHashPwd)){
				fail("Le mot de passe n'a pas été changé");
			}

			if(user.getCodeForgotPwd() != 0){
				fail("Le code forgot n'a pas été remis à 0");
			}
		} catch (IllegalOperationException e) {
			e.printStackTrace();
			fail("IllegalOperationException récupérée : " + e.getStackTrace());
		} catch (Exception te) {
			te.printStackTrace();
			fail("Une Exception " + te.getClass().getSimpleName() + " a été récupérée");
		}
	}

	@Test
	public void changementMdpWithBadCode(){
		try {
			bm.loadAllClients();
			bm.loadAllClients();
			String salt = BCrypt.gensalt();
			String userHashPwd = BCrypt.hashpw("password", salt);
			bm.createClient("t.test1", userHashPwd, "test1nom", "test1prenom", "test town", true, "4242424242","adelazzari8@gmail.com");
			bm.updateUser(bm.getUserById("t.test1"), 123456);

			String pwd  = "password";

			var user = bm.getUserById("t.test1");
			bm.resetPwd(user, pwd, 123);

			System.out.println(user.getUserPwd());
			if(!user.getUserPwd().equals(userHashPwd)){
				fail("Le mot de passe a été changé avec un mauvais code");
			}

			if(user.getCodeForgotPwd() == 0){
				fail("Le code forgot a pas été remis à 0 avec un mauvais code");
			}
		} catch (IllegalOperationException e) {
			e.printStackTrace();
			fail("IllegalOperationException récupérée : " + e.getStackTrace());
		} catch (Exception te) {
			te.printStackTrace();
			fail("Une Exception " + te.getClass().getSimpleName() + " a été récupérée");
		}
	}
}
