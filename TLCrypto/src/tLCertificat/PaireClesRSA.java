package tLCertificat;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

public class PaireClesRSA {
	
    private KeyPair key;
	
	PaireClesRSA() throws NoSuchAlgorithmException {
		// On va mettre un peu d'alea :
		SecureRandom rand = new SecureRandom();
		// On initialise la structure pour la generation de cle :
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		// On definit la taille de cle :
		kpg.initialize(512, rand);
		// On genere la paire de cle :
		this.key = kpg.generateKeyPair();
		
		
	}
	public PublicKey Publique() {
	// Recuperation de la clé publique.
		PublicKey pub = key.getPublic();
		return pub;
	}
	public PrivateKey Privee() {
	// Recuperation de la clé privée.
		PrivateKey prv = key.getPrivate(); 
		return prv;
	}
}

