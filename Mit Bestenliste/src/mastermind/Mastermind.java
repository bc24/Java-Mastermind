package mastermind;

import java.awt.Color;
import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Dies ist die Hauptklasse des Spiels MasterMind. In ihr wird der gesamte Spielablauf inklusive der Generierung des Designs realisiert.
 * @author Lukas Schramm
 */
public class Mastermind {
	
	private JFrame frame1 = new JFrame("Mastermind");
	private JButton buttonpruefen = new JButton("Überprüfen");
	private JPanel jPanel1 = new JPanel();
	private int anzahlreihen = 8;
	private int reihe = 0;
	private Farbfeld[][] spielfeld = new Farbfeld[4][anzahlreihen];
	private JPanel[][] kontrollfeld = new JPanel[4][anzahlreihen];
	private int[] geheimnis = new int[4];
	private String umbruch = System.getProperty("line.separator");
	private Bestenliste bestenliste = new Bestenliste();
	private long startzeit;
	
	/**
	 * Diese Methode laedt die Grafik des Spiels.
	 */
	private void grafikladen() {
		bestenliste.anzeigen(false);
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame1.setSize(200,380);
		frame1.setResizable(false);
		Container cp = frame1.getContentPane();
		cp.setLayout(null);
		
		buttonpruefen.setBounds(20, 315, 120, 33);
		buttonpruefen.setMargin(new Insets(2, 2, 2, 2));
		buttonpruefen.setVisible(true);
		buttonpruefen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				buttonpruefe_ActionPerformed(evt);
			}
		});
		cp.add(buttonpruefen);
		
		jPanel1.setBounds(10, 275, 33, 33);
	    jPanel1.setBackground(Color.ORANGE);
	    jPanel1.setVisible(true);
	    
	    int jx = jPanel1.getX();
	    int jy = jPanel1.getY();
	    int jw = jPanel1.getWidth();
	    for(int py=0; py<anzahlreihen; py++) {
	    	for(int px=0; px<4; px++) {
	    		spielfeld[px][py] = new Farbfeld();
	    		spielfeld[px][py].setBounds(jx+px*(jw+5),jy-py*(jw+5),jw,jw);
	    		if(py==0) {
	    			spielfeld[px][py].setAktiviert(true);
	    		}
	    		cp.add(spielfeld[px][py]);
	    	}
	    }
	    for(int py=0; py<anzahlreihen; py++) {
	    	for(int px=0; px<2; px++) {
	    		for(int pz=0; pz<2; pz++) {
	    			kontrollfeld[px+2*pz][py] = new JPanel();
	    			kontrollfeld[px+2*pz][py].setBounds(jx+4*(jw+5)+px*jw/2,jy-py*(jw+5)+pz*jw/2,jw/2-4, jw/2-4);
	    			kontrollfeld[px+2*pz][py].setBackground(Color.LIGHT_GRAY);
	    			cp.add(kontrollfeld[px+2*pz][py]);
	    		}
	    	}
	    }
	    
	    frame1.setLocationRelativeTo(null);
		frame1.setVisible(true);
	}
	
	/**
	 * Diese Methode gibt vier Zufallszahlen aus, welche als "Geheimnis" in die Variable des Loesungscodes gespeichert werden.
	 */
	public void start() {
		Random farbwuerfel = new Random();
		for(int n=0;n<4;n++) {
			geheimnis[n] = farbwuerfel.nextInt(6);
		}
		startzeit = System.currentTimeMillis();
	}
	
	/**
	 * Diese Methode schaut nach, ob an einer Stelle des Geheimnis die Farbe gleich der vom Spieler eingegebenen Farbe ist.<br>
	 * Sollte dies der Fall sein, zaehlt der Zaehler hoch. Es wird die Anzahl an richtigen Treffern ausgegeben.<br>
	 * Anschliessend werden die Werte beider Arrays auf -1 gesetzt, um sie unbrauchbar fuer Rechenfehler in anderen Algorithmen zu machen.<br>
	 * Es werden jedoch nur Kopien verwendet, um das Original behalten zu koennen.<br>
	 * @param aktuelleZeile Dies ist ein Array aus den Versuchen des Spielers in dieser Reihe
	 * @param geheimnisKopie Dies ist ein Array des Ergebnisses
	 * @return Gibt die Anzahl richtiger Farben aus
	 */
	private int rechnerSchwarz(int aktuelleZeile[], int geheimnisKopie[]) {
        int schwarz = 0;
        for(int px=0;px<4;px++) {
            if(aktuelleZeile[px] == geheimnisKopie[px]) {
                schwarz++;
                aktuelleZeile[px] = -1;
                geheimnisKopie[px] = -1;
            }
        }
        return schwarz;
    }
	
	/**
	 * Hier wird Analog zur Methode rechnerSchwarz ueberprueft, wie viele vorhanden sind, aber in falschen Stellen hinzugefuegt wurden.<br>
	 * Bereits benutzte Zellen stehen bereits auf -1 und koennen nicht doppelt bedacht werden.
	 * @param aktuelleZeile Dies ist ein Array aus den Versuchen des Spielers in dieser Reihe
	 * @param geheimnisKopie Dies ist ein Array des Ergebnisses
	 * @return Gibt die Anzahl richtiger Farben an falscher Stelle aus
	 */
    private int rechnerRot(int aktuelleZeile[], int geheimnisKopie[]) {
        int rot = 0;
        for(int px=0;px<4;px++) {
            if(aktuelleZeile[px] != -1) {
                for(int i=0; i<4; i++) {
                    if(geheimnisKopie[i] == aktuelleZeile[px]) {
                        rot++;
                        geheimnisKopie[i] = -1;
                        aktuelleZeile[px] = -1;
                        break;
                    }
                }
            }
        }
        return rot;
    }
	
    /**
     * Diese Methode wird abgerufen, wenn der Spieler auf Ueberpruefen klickt und damit seine aktuelle Reihe als Spielversuch freigibt.<br>
     * Es werden die Methoden zur Ueberpruefung Schwarzer und Roter Kontrollfelder abgerufen und die Kontrollfelder anschliessend eingefuert.<br>
     * Abschliessend wird ueberprueft, ob ein Spieler bereits gewonnen hat oder die Anzahl seiner Versuche ueberschritten wurde.
     * @param evt Das ActionEvent des ButtonDrueckens
     */
	public void buttonpruefe_ActionPerformed(ActionEvent evt) {
		int aktuelleZeile[] = new int[4];
        for(int px=0;px<4;px++) {
            aktuelleZeile[px] = spielfeld[px][reihe].getFarbe();
        }
        int geheimnisKopie[] = geheimnis.clone();
        int schwarz = rechnerSchwarz(aktuelleZeile, geheimnisKopie);
        int rot = rechnerRot(aktuelleZeile, geheimnisKopie);
       
        for(int a=0;a<schwarz;a++) {
            kontrollfeld[a][reihe].setBackground(Color.black);
        }
        for(int b=schwarz;b<schwarz+rot;b++) {
        	kontrollfeld[b][reihe].setBackground(Color.red);
        }
        if(schwarz == 4) {
        	long spieldauer = System.currentTimeMillis() - startzeit;
        	if(reihe == 0) {
        		JOptionPane.showMessageDialog(null, "Du gewinnst nach nur einer Runde!"+umbruch+"Ein wahrer Glückstreffer!", "Gewonnen!", JOptionPane.PLAIN_MESSAGE);
        		gewinneintrag(true,spieldauer);
        		neustartfrage();
        	} else {
        		JOptionPane.showMessageDialog(null, "Du gewinnst das Spiel nach "+(reihe+1)+" Runden!", "Gewonnen!", JOptionPane.PLAIN_MESSAGE);
        		gewinneintrag(true,spieldauer);
        		neustartfrage();
        	}	
        } else if(reihe == 7) {
        	JOptionPane.showMessageDialog(null, "Du hast es nach 8 Runden nicht geschafft"+umbruch+"die Farbkombination zu erraten. Schade!", "Verloren!", JOptionPane.PLAIN_MESSAGE);
        	gewinneintrag(false,-1);
        	neustartfrage();
        } else {
        	neuespielrunde();
        }
	}
	
	/**
	 * Diese Methode schliesst den Rateversuch einer Reihe ab und schaltet die naechste Reihe frei.
	 */
	private void neuespielrunde() {
        for(int px=0;px<4;px++) {
            spielfeld[px][reihe].setAktiviert(false);
            spielfeld[px][reihe+1].setAktiviert(true);
        }
        reihe++;
    }
	
	/**
	 * Diese Methode setzt das Spiel auf Anfang zurueck und resettet das Design auf ein neues Spiel.
	 */
	private void reset() {
		for(int py=0; py<anzahlreihen; py++) {
	    	for(int px=0; px<4; px++) {
	    		spielfeld[px][py].setBackground(Color.darkGray);
	    		spielfeld[px][py].setAktiviert(false);
	    		spielfeld[px][py].setFarbe(6);
	    	}
	    }
	    for(int py=0; py<anzahlreihen; py++) {
	    	for(int px=0; px<2; px++) {
	    		for(int pz=0; pz<2; pz++) {
	    			kontrollfeld[px+2*pz][py].setBackground(Color.lightGray);
	    		}
	    	}
	    }
	    
	    reihe = 0;
	    for(int px=0;px<4;px++) {
	    	spielfeld[px][reihe].setAktiviert(true);
	    }
	    
		frame1.repaint();
		start();
	}
	
	/**
	 * Diese Methode fragt im Falle eines Sieges den Spieler nach seinem Namen und fuegt ihn in die Highscoreliste ein.
	 */
	private void gewinneintrag(boolean gewonnen,long spieldauer) {
		String spielername = "";
		if(gewonnen) {
			JTextField spielernametf = new JTextField(new Feldbegrenzung(12), "", 0);
			Object[] namensfrage = {"Wie ist Dein Name?", spielernametf};
			JOptionPane pane = new JOptionPane(namensfrage, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);
		    pane.createDialog(null,"Siegername").setVisible(true);
		    
		    spielername = spielernametf.getText();
		    if(spielername.equals("")) {
		    	JOptionPane.showMessageDialog(null,"Bitte gib einen Namen ein!","Kein Name eingegeben", JOptionPane.ERROR_MESSAGE);
		    	gewinneintrag(gewonnen,spieldauer);
		    }
		    bestenliste.highscorehinzufuegen(System.currentTimeMillis(),spieldauer,spielername,reihe+1);
		}
	}
	
	/**
	 * Diese Methode fragt den Spieler, ob er eine neue Spielrunde starten, das Programm beenden oder die Bestenliste abrufen moechte.
	 */
	public void neustartfrage() {
		String[] schliessoptionen = {"Ja","Nein","Bestenliste"};
		int dialogneustart = JOptionPane.showOptionDialog(null,"Möchtest Du eine neue Runde starten?","Neue Runde?", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,schliessoptionen,schliessoptionen[0]);
        if(dialogneustart == 0) {
        	reset();
        } else if(dialogneustart == 2) {
        	bestenliste.sortiere();
        	bestenliste.anzeigen(true);
        } else {
        	System.exit(0);
        }
	}

	public static void main(String[] args) {
		Mastermind mm = new Mastermind();
		mm.grafikladen();
		mm.start();
	}
}