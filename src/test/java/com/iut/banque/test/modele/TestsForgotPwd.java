package com.iut.banque.test.modele;

import com.iut.banque.controller.ForgotPwd;
import com.iut.banque.facade.BanqueManager;
import com.iut.banque.modele.Utilisateur;
import org.junit.Before;
import org.junit.Test;

import javax.mail.MessagingException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TestsForgotPwd {

    private ForgotPwd forgotPwd;
    private BanqueManager banqueManager;

    @Before
    public void setUp() {
        forgotPwd = new ForgotPwd();
        banqueManager = mock(BanqueManager.class);
        forgotPwd.setBm(banqueManager);
    }

    @Test
    public void testSendMail() throws Exception {
        // Préparation des données de test
        Utilisateur mockUser = new Utilisateur();
        mockUser.setCodeForgotPwd(1234);
        when(banqueManager.getUserById(anyString())).thenReturn(mockUser);

        // Exécution de la méthode à tester
        forgotPwd.setCodeUser("123");
        String result = forgotPwd.sendMail();

        // Vérifications
        assertEquals("SUCCESS", result);
        assertEquals("Un e-mail contenant un code de vérification vous a été envoyé.", forgotPwd.getMessage());
        verify(banqueManager, times(1)).getUserById("123");
        verify(banqueManager, times(1)).updateUser(mockUser, 1234);
    }

    @Test
    public void testSendMailUserNotFound() throws Exception {
        // Simulation d'un utilisateur non trouvé
        when(banqueManager.getUserById(anyString())).thenReturn(null);

        // Exécution de la méthode à tester
        forgotPwd.setCodeUser("456");
        String result = forgotPwd.sendMail();

        // Vérifications
        assertEquals("ERROR", result);
        assertEquals("L'utilisateur n'existe pas.", forgotPwd.getMessage());
        verify(banqueManager, times(1)).getUserById("456");
        verify(banqueManager, never()).updateUser(any(), anyInt());
    }

    @Test
    public void testResetPwd() throws Exception {
        // Préparation des données de test
        Utilisateur mockUser = new Utilisateur();
        mockUser.setCodeForgotPwd(5678);
        mockUser.setMail("test@example.com");
        when(banqueManager.getUserById(anyString())).thenReturn(mockUser);

        // Exécution de la méthode à tester
        forgotPwd.setCodeUser("789");
        forgotPwd.setCodeRecu(5678);
        forgotPwd.setNouveauMdp("newPassword");
        String result = forgotPwd.resetPwd();

        // Vérifications
        assertEquals("SUCCESS", result);
        assertEquals("Votre mot de passe a bien été modifié.", forgotPwd.getMessage());
        verify(banqueManager, times(1)).getUserById("789");
        verify(banqueManager, times(1)).updatePwd(mockUser, "newPassword");
    }

    @Test
    public void testResetPwdIncorrectCode() throws Exception {
        // Préparation des données de test
        Utilisateur mockUser = new Utilisateur();
        mockUser.setCodeForgotPwd(5678);
        when(banqueManager.getUserById(anyString())).thenReturn(mockUser);

        // Exécution de la méthode à tester
        forgotPwd.setCodeUser("789");
        forgotPwd.setCodeRecu(1234); // Code incorrect
        forgotPwd.setNouveauMdp("newPassword");
        String result = forgotPwd.resetPwd();

        // Vérifications
        assertEquals("ERROR", result);
        assertEquals("Le code saisi est incorrect.", forgotPwd.getMessage());
        verify(banqueManager, times(1)).getUserById("789");
        verify(banqueManager, never()).updatePwd(any(), anyString());
    }

    @Test
    public void testResetPwdMissingCodeOrPassword() throws Exception {
        // Exécution de la méthode à tester sans code ni nouveau mot de passe
        forgotPwd.setCodeUser("789");
        String result = forgotPwd.resetPwd();

        // Vérifications
        assertEquals("ERROR", result);
        assertEquals("Veuillez saisir un code et un nouveau mot de passe.", forgotPwd.getMessage());
        verify(banqueManager, never()).getUserById(anyString());
        verify(banqueManager, never()).updatePwd(any(), anyString());
    }

    @Test
    public void testEnvoieMailException() throws Exception {
        // Simulation d'une exception lors de l'envoi de l'e-mail
        doThrow(new MessagingException("Simulated exception")).when(banqueManager).getUserById(anyString());

        // Exécution de la méthode à tester
        forgotPwd.setCodeUser("123");
        String result = forgotPwd.sendMail();

        // Vérifications
        assertEquals("ERROR", result);
        assertEquals("Une erreur s'est produite lors de l'envoi de l'e-mail.", forgotPwd.getMessage());
        verify(banqueManager, times(1)).getUserById("123");
        verify(banqueManager, never()).updateUser(any(), anyInt());
    }
}
