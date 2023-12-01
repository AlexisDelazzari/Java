package com.iut.banque.test.facade;

import com.iut.banque.modele.Utilisateur;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.iut.banque.facade.LoginManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

//@RunWith indique à JUnit de prendre le class runner de Spirng
@RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration permet de charger le context utilisé pendant les tests.
// Par défault (si aucun argument n'est précisé), cherche le fichier
/// src/com/iut/banque/test/TestsDaoHibernate-context.xml
@ContextConfiguration("classpath:TestsLoginManager-context.xml")
@Transactional("transactionManager")
public class TestsLoginManager {

    @Autowired
    private LoginManager lm;

    @Test
    public void testResetFailedAttempts() {
        try {
            String testUsername = "j.doe1";
            lm.updateFailedAttempts(testUsername);
            lm.resetFailedAttempts(testUsername);

        } catch (Exception e) {
            fail("Exception in testResetFailedAttempts: " + e.getMessage());
        }
    }
}
