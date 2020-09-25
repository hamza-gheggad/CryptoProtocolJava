package tLCertificat;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws Exception {

		Scanner sc = new Scanner(System.in);
		System.out.println("Entrez le nom de l'équipement que vous souhaitez créer");
		String nom = sc.next();
		System.out.println("Entrez le port de l'équipement que vous souhaitez créer");
		int port = sc.nextInt();
		Equipement Eq = new Equipement(nom,port);
		while (true){

		System.out.println("\n" + "\n");
		System.out.println("\t" + "\t" + "\t" + " Equipement : " + nom);
		System.out.println(" i => informations concernant l'équipement ");
		System.out.println(" r => Liste des équipements de RD");
		System.out.println(" u => Liste des équipements de UT");
		System.out.println(" s => Initialisation de l'insertion ( En tant que Serveur )");
		System.out.println(" c => Initialisation de l'insertion ( En tant que Client )");
		System.out.print(" q => Quitter ");
		String a = sc.next();
		switch(a) {
		case "i":
			Eq.affichage();
			break;
		case "r":
			System.out.println("Ceci devrait afficher les equipements de RD");
			break;
		case "u":
			System.out.println("Ceci devrait afficher les equipements de UT");
			break;
		case "s":
			System.out.println("Initlisation de l'insertion en tant que serveur...");
			Eq.start_listening(port);	
			break;
		case "c":
			System.out.println("Initialisation de l'insertion en tant que client...");
			Eq.send_as_client(port);
			break;
		case "q":
			System.out.println("Byebye");
			return;	
		default:
		    System.out.println("\n\n\nVeuillez saisir une commande valide");
			
		}
		
			

		}

	}

}
