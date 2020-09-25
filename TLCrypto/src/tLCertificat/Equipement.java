package tLCertificat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Scanner;

import org.bouncycastle.operator.OperatorCreationException;

public class Equipement {
	public PaireClesRSA maCle; // La paire de cle de l’equipement.
	public Certificat monCert; // Le certificat auto-signe.
	public String monNom; // Identite de l’equipement.
	public int monPort; // Le numéro de port d’ecoute.
	
	Equipement (String nom, int port) throws Exception {
	// Constructeur de l’equipement identifie par nom
	// et qui « écoutera » sur le port port.
		this.monNom = nom;
		this.monPort = port;
		this.maCle = new PaireClesRSA();
		//Création de son certificat auto-signé
		this.monCert = new Certificat(nom, port, nom, port, maCle.Publique(), maCle.Privee(), 10);
	}
	public void affichage_da() {
	// Affichage de la liste des équipements de DA.
	}
	public void affichage_ca() {
	// Affichage de la liste des équipements de CA.
	}
	
	public String getMonNom (){
	// Recuperation de l’identite de l’équipement.
		return this.monNom;
	}
	public PublicKey maClePub() {
	// Recuperation de la clé publique de l’équipement.
		return  this.maCle.Publique();
	}
	public Certificat monCertif() {
	// Recuperation du certificat auto-signé.
		return this.monCert;
	}
	public int getMonPort() {
		return this.monPort;
	}
	public void setMonPort(int monPort) {
		this.monPort = monPort;
	}
	public void affichage() {
		// Affichage de l’ensemble des informations
		// de l’équipement.
			System.out.println( "\t" +"equipement : "+ this.getMonNom());
			System.out.println("Port : " + this.getMonPort());
			System.out.println("Clé publique : " + this.maClePub().toString());
			System.out.println("Certificat : " + this.monCertif().toString());
	    			
	}
	
	public void start_listening(int port) throws IOException, ClassNotFoundException, CertificateException, OperatorCreationException {
		ServerSocket ss = new ServerSocket(port);
		Socket s = ss.accept();
		System.out.println("Connection réussie");
		
		ObjectInputStream ois= new ObjectInputStream(s.getInputStream());	
		
		Certificat received_autosigned_certificat_client = (Certificat) ois.readObject(); 
		
		Certificat new_cert = new Certificat(received_autosigned_certificat_client.issuer,
				received_autosigned_certificat_client.issuer_port, this.getMonNom(), this.getMonPort(), received_autosigned_certificat_client.pub, this.maCle.Privee(),10); 
		
		System.out.println("\n\n\nLe certificat auto-signé du client est : \n"+ received_autosigned_certificat_client.toString()+"\n\n\n"); 
		System.out.println("\n\n\nLe certificat calculé par le serveur est : \n"+ new_cert.toString()+"\n\n\n"); 
		System.out.println("\n\n\nVoulez-vous introduire l'équipement "+ received_autosigned_certificat_client.issuer + "?"); 

		
		
        Scanner scan = new Scanner(System.in);
        String response = scan.next();
        if (response.equals("oui") || response.equals("Oui") || response.equals("OUI")) {      	      	
        	   	
        	ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream()); 
        	oos.writeObject("accept"); 
        	
        	oos = new ObjectOutputStream(s.getOutputStream()); 
        	oos.writeObject(this.monCertif()); 
        	
        	oos = new ObjectOutputStream(s.getOutputStream());  
        	oos.writeObject(new_cert);  
        	        	    	
        	ois = new ObjectInputStream(s.getInputStream());
        	Certificat signed_certificat_client = (Certificat) ois.readObject();
        	
        	ois = new ObjectInputStream(s.getInputStream());	
        	Object received_ok =(String) ois.readObject(); 
        	String ok_str = (String) received_ok.toString();
        	
        	if (ok_str.equals("OK")) {
        	System.out.println("\n\n\nLe certificat signé par le client est : \n"+signed_certificat_client.toString());
        	System.out.println("\n"+ok_str);
        	} 
        	else {
        		System.out.println("Connexion refusée.");
        		}
        	
        	ss.close();
        } else {
        	
        	ObjectOutputStream oos1 = new ObjectOutputStream(s.getOutputStream()); 
        	oos1.writeObject("refus");
        	
        	oos1 = new ObjectOutputStream(s.getOutputStream());          	
        	oos1.writeObject(this.monCertif()); 
        	
        	oos1 = new ObjectOutputStream(s.getOutputStream()); 
        	oos1.writeObject(new_cert);
        	      	
        	ss.close();
        }  
	}
	
	public void send_as_client(int port) throws UnknownHostException, IOException, ClassNotFoundException, OperatorCreationException, CertificateException {
		Socket s = new Socket("localhost",port);
		ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
		
		oos.writeObject(this.monCertif()); 
		
		ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

		Object received_accept =(String) ois.readObject(); // accept ou refus string
		String accept_str = (String) received_accept.toString();
		
		ois = new ObjectInputStream(s.getInputStream());
		Certificat received_autosigned_certificat_srv = (Certificat) ois.readObject();
       

		ois = new ObjectInputStream(s.getInputStream());
		Certificat received_certificat_signed_srv = (Certificat) ois.readObject();
		
	
		Certificat new_cert = new Certificat(received_autosigned_certificat_srv.issuer,
				received_autosigned_certificat_srv.issuer_port, this.getMonNom(), this.getMonPort(), received_autosigned_certificat_srv.pub, this.maCle.Privee(),10); 

		

        if (accept_str.equals("accept")) {
        	
        	System.out.println("\n\n\nLe certificat auto-signé du serveur est : \n"+received_autosigned_certificat_srv.toString() + "\n\n\n");
        	System.out.println("\n\n\nLe certificat calculé par le serveur est : \n"+received_certificat_signed_srv.toString() + "\n\n\n");
        	System.out.println("\n\n\nLe certificat calculé par le client est : \n"+new_cert.toString() + "\n\n\n");
        	System.out.println("\n\n\nVoulez-vous introduire l'équipement "+ received_autosigned_certificat_srv.issuer + "?"); 
        	
        	Scanner scan = new Scanner(System.in);
        	String response = scan.next();
        	
        	
        	if (response.equals("oui") || response.equals("Oui")) 
        	{	
        		oos = new ObjectOutputStream(s.getOutputStream());
        		oos.writeObject(new_cert);
        		
        		oos = new ObjectOutputStream(s.getOutputStream());	
        		oos.writeObject("OK");
        		s.close();
        		}
        	else {
        		
        		oos = new ObjectOutputStream(s.getOutputStream());
        		oos.writeObject(new_cert);
        		
        		oos = new ObjectOutputStream(s.getOutputStream());
        		oos.writeObject("Refus");
        		s.close();
        		}     	
        	
        } else {   	   
        	System.out.println("Connexion refusée.");
        	s.close();      	
        	}       
	}	
}
