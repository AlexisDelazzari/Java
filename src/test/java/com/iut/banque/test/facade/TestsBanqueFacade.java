package com.iut.banque.test.facade;

import com.iut.banque.exceptions.IllegalOperationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.iut.banque.facade.LoginManager;
import com.iut.banque.facade.BanqueManager;

import java.sql.Timestamp;

import static org.junit.Assert.*;

//@RunWith indique à JUnit de prendre le class runner de Spirng
@RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration permet de charger le context utilisé pendant les tests.
// Par défault (si aucun argument n'est précisé), cherche le fichier
/// src/com/iut/banque/test/TestsDaoHibernate-context.xml
@ContextConfiguration("classpath:TestsBanqueFacade-context.xml")
@Transactional("transactionManager")
public class TestsBanqueFacade {

    @Autowired
    private LoginManager lm;

    @Autowired
    private BanqueManager bm;

    @Before
    public void setUp() {
        bm.loadAllClients();
    }

    @Test
    public void testResetFailedAttemptsSuccesful() {
        try {
            bm.createClient("g.reset1", "password", "test1nom", "test1prenom", "test town", true, "1478542658","adelazzari8@gmail.com");
            bm.getUserById("g.reset1").setNbTentatives(3);
            lm.resetFailedAttempts("g.reset1");
            if (bm.getUserById("g.reset1").getNbTentatives() != 0) {
                fail("Le nombre de tentatives n'a pas été réinitialisé");
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
    public void testResetFailedAttemptsUnkown() {
        try {
            lm.resetFailedAttempts("g.reset1");
            //user n'existe pas
        } catch (Exception te) {
            te.printStackTrace();
            fail("Une Exception " + te.getClass().getSimpleName() + " a été récupérée");
        }
    }

    @Test
    public void testUpdateFailedAttemptsSuccessful() {
        try {
            // Créer un utilisateur pour les tests
            bm.createClient("g.reset1", "password", "test1nom", "test1prenom", "test town", true, "1478542658","adelazzari8@gmail.com");

            // Récupérer l'utilisateur créé
            assertNotNull(bm.getUserById("g.reset1"));

            // Appeler la méthode updateFailedAttempts
            lm.updateFailedAttempts("g.reset1");

            // Vérifier que le nombre de tentatives a été incrémenté
            assertEquals(1, bm.getUserById("g.reset1").getNbTentatives());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception inattendue : " + e.getMessage());
        }
    }

    @Test
    public void testUpdateFailedAttemptsUnknownUser() {
        try {
            // Appeler la méthode updateFailedAttempts pour un utilisateur inconnu
            lm.updateFailedAttempts("unknownUser");

            // Vérifier que l'utilisateur n'a pas été trouvé dans le système
            assertNull(bm.getUserById("unknownUser"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception inattendue : " + e.getMessage());
        }
    }

    @Test
    public void testIsAccountLockedNotBlocked() {
        try {
            // Créer un utilisateur non bloqué
            bm.createClient("g.notblocked1", "password", "testnom", "testprenom", "test town", true, "1478542658","adelazzari8@gmail.com");

            // Vérifier que le compte n'est pas bloqué
            assertFalse(lm.isAccountLocked("g.notblocked1"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception inattendue : " + e.getMessage());
        }
    }

    @Test
    public void testIsAccountLockedBlockedAttempts() {
        try {
            // Créer un utilisateur avec 3 tentatives échouées
            bm.createClient("g.attempts1", "password", "testnom", "testprenom", "test town", true, "1478542658","adelazzari8@gmail.com");
            bm.getUserById("g.attempts1").setNbTentatives(4);

            // Vérifier que le compte est bloqué après 3 tentatives échouées
            assertTrue(lm.isAccountLocked("g.attempts1"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception inattendue : " + e.getMessage());
        }
    }

    @Test
    public void testIsAccountLockedBlockedTimestamp() {
        try {
            // Créer un utilisateur bloqué jusqu'à dans 5 minutes
            bm.createClient("g.timestamp1", "password", "testnom", "testprenom", "test town", true, "1478542658","adelazzari8@gmail.com");
            bm.getUserById("g.timestamp1").setFinBlocageConnexion(new Timestamp(System.currentTimeMillis() + 300000));

            // Vérifier que le compte est bloqué en raison du timestamp
            assertTrue(lm.isAccountLocked("g.timestamp1"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception inattendue : " + e.getMessage());
        }
    }

    @Test
    public void testIsAccountLockedUserNotFound() {
        try {
            // Vérifier que la méthode retourne false pour un utilisateur non trouvé
            assertFalse(lm.isAccountLocked("userNotFound"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception inattendue : " + e.getMessage());
        }
    }



}
