<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html>
<html lang="fr">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Mot de passe oublié</title>
    <link rel="stylesheet" href="/_00_ASBank2023/style/style.css" />
</head>

<body>
<h1>Mot de passe oublié :</h1>

<s:if test="#session.codeUser == null">
    <s:form name="myFormCodeUser" action="sendMail" method="POST">
        <s:textfield name="codeUser" label="Code utilisateur" />
        <s:submit name="Envoyer" value="Envoyer" />
    </s:form>
</s:if>

<s:else>
    <s:form name="myFormCodeRecu" action="resetPwd" method="POST">
        <s:textfield name="codeRecu" label="Code reçu par mail" />
        <s:textfield name="nouveauMdp" label="Nouveau mot de passe" />
        <s:submit name="Envoyer" value="Envoyer" />
    </s:form>
    <p>Si vous n'avez pas reçu de code, veuillez vérifier votre adresse e-mail et réessayer.</p>
</s:else>

<s:if test="(result == \"SUCCESS\")">
    <div class="success">
        <s:property value="message" />
    </div>
</s:if>
<s:else>
    <div class="failure">
        <s:property value="message" />
    </div>
</s:else>

<s:form name="myFormRetour" action="retourAccueil" method="POST">
    <s:submit name="Retour" value="Retour à l'accueil" />
</s:form>
</body>


<jsp:include page="/JSP/Footer.jsp" />
</html>
