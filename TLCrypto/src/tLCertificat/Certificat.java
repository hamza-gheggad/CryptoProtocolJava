package tLCertificat;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;

public class Certificat implements Serializable {
    static private BigInteger seqnum = BigInteger.ZERO;
	public X509Certificate x509;
	public String issuer;
	public String subject;
	public int issuer_port;
	public int subject_port;
	public PublicKey pub;
	
	Certificat(String subject_nom, int subject_port, String issuer_nom, int issuer_port,PublicKey pub, PrivateKey prv, int validityDays) throws OperatorCreationException, CertificateException {
		
		// Constructeur d'un certificat auto-signé avec
		// CN = nom, la clé publique contenu dans PaireClesRSA,
		// la durée de validité.
		this.issuer = issuer_nom;
		this.subject = subject_nom;
		this.subject_port = subject_port;
		this.issuer_port = issuer_port;
		this.pub = pub;
		
		//la structure qui contiendra la signature
        Security.addProvider(new BouncyCastleProvider());
        ContentSigner sigGen = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(prv);
        
        //la structure qui contient la cle publique à  certifier
        SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(pub.getEncoded());
        
        //Dans le cas ou le certif est auto-signé on aura issuer = subject
        X500Name issuer = new X500Name("CN="+ issuer_nom);
        X500Name subject = new X500Name("CN="+ subject_nom);
        
        //le numero de serie du futur certif
        seqnum = seqnum.add(BigInteger.ONE);
        
        Date startDate = new Date(System.currentTimeMillis());
        Date endDate = new Date(System.currentTimeMillis() + validityDays*60*24*60*1000);
        
        //la structure qui permettra de creer le certificat
        
        X509v1CertificateBuilder v1CertGen = new X509v1CertificateBuilder(issuer, seqnum, startDate, endDate, subject, subPubKeyInfo);
        
        X509CertificateHolder x509holder = v1CertGen.build(sigGen);
        
        //maintenant on transforme le holder en certificat
        
        this.x509 = new JcaX509CertificateConverter().setProvider("BC").getCertificate(x509holder);
        
        	
	}
 
	
	
	public boolean verifCertif (PublicKey pubkey) throws OperatorCreationException, InvalidKeyException, CertificateException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException {
		// Vérification de la signature du certificat à  l'aide 
		// de la clé publique passée en argument.
			this.x509.verify(pubkey);
			return true;
	}
	
	
	//affichage du certificat
	@Override
	public String toString() {
		return x509.toString();
	}

	
	
}
