import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GUI extends JFrame {
	public String sentMessage = null;
	private static final int DEFAULT_PORT = 5555; // portul la care asculta
													// serverul
	private final JButton connect; // buton pt conectare
	private final JButton sendMessage; // buton pentru trimitere mesaj
	private final JButton transferFile; // buton pentru transfer fisier
	private final JTextField localPort; // aici se afiseaza port-ul
	private final JTextField host;// aici se afiseaza host-ul
	private final JLabel connectToPortText; // text Port
	private final JTextField connectToPort; // se conecteaza la port-ul
											// specificat
	private final JLabel connectToHostText; // text Host
	private final JTextField connectToHost; // se conecteaza la host-ul
											// specificat
	private final JFileChooser fileChooser; // fereastra de unde se alege
											// fisierul ce va fi transferat
	private JTextArea logger; // aici se afiseaza toate mesajele inclusiv
									// cele de eroare
	private Server server; // server
	private final JLabel title; // titlu
	private Client client; // client


	public GUI() throws IOException {
		setTitle("Xor in the air");
		final String localhostname = InetAddress.getLocalHost()
				.getHostAddress(); // returneaza host-ul pc-ului pe care ruleaza
									// aplicatia
		setLayout(null);
		setBounds(500, 30, 600, 600);

		title = new JLabel("Xor in the air");
		title.setBounds(200, 10, 300, 50);
		title.setFont(new Font("", 30, 30));
		add(title);

		transferFile = new JButton("TransferFile");
		transferFile.setBounds(10, 100, 200, 30);
		transferFile.setVisible(true);
		transferFile.setEnabled(false);
		//add(transferFile); TODO HIDDEN TRANSFER FILE

		sendMessage = new JButton("Send Message");
		sendMessage.setBounds(10, 100, 200, 30);
		sendMessage.setVisible(true);
		sendMessage.setEnabled(false);
		add(sendMessage);


		connect = new JButton("Connect");
		connect.setBounds(280, 220, 200, 30);
		connect.setVisible(true);
		add(connect);

		localPort = new JTextField("    Your LocalPort:   5555");
		localPort.setEditable(false);
		localPort.setBounds(300, 100, 190, 25);
		localPort.setForeground(Color.red);
		localPort.setVisible(true);
		add(localPort);

		host = new JTextField("    Your Host:   " + localhostname);
		host.setEditable(false);
		host.setBounds(300, 130, 190, 25);
		host.setForeground(Color.red);
		host.setVisible(true);
		add(host);

		connectToPortText = new JLabel("Connect to Port:");
		connectToPortText.setBounds(230, 160, 120, 25);
		connectToPortText.setVisible(true);
		add(connectToPortText);

		connectToPort = new JTextField("5555");
		connectToPort.setBounds(330, 160, 200, 25);
		connectToPort.setVisible(true);
		connectToPort.setEditable(false);
		add(connectToPort);

		connectToHostText = new JLabel("Connect to Host:");
		connectToHostText.setBounds(230, 190, 120, 25);
		connectToHostText.setVisible(true);
		add(connectToHostText);

		connectToHost = new JTextField();
		connectToHost.setBounds(330, 190, 200, 25);
		connectToHost.setVisible(true);
		add(connectToHost);

		fileChooser = new JFileChooser();
		fileChooser.setVisible(true);
		fileChooser.setBounds(0, 0, 300, 300);
		/*server = new Server(DEFAULT_PORT, this);
		try {
			server.listen(); // serverul incepe sa astepte clienti
		} catch (Exception ex) {
			addTextToLogger("ERROR: Server doesn't start!");
		}*/
		transferFile.addActionListener(new ActionListener() { // se adauga
																// ascultator pt
																// butonul de
																// transfer

					@Override
					public void actionPerformed(ActionEvent e) {
						int status = fileChooser.showOpenDialog(null); // afiseaza
																		// ferestra
																		// de
																		// alegere
																		// fisier
						if (status == JFileChooser.APPROVE_OPTION) { // daca s-a
																		// ales
																		// fisier
																		// (s-a
																		// apasat
																		// butonul
																		// ok)
							File selectedFile = fileChooser.getSelectedFile(); // fisierul
																				// selectat
							addTextToLogger("File selected: "
									+ selectedFile.getName()); // afiseaza in
																// logger numele
																// fisierului
																// selectat
							try {
								RandomAccessFile file = new RandomAccessFile(
										selectedFile, "r");// deschide fiser
								byte[] b = new byte[(int) file.length()];
								file.read(b); // transforma fisierul intr-un sir
												// de biti
								addTextToLogger("Start transfer file: "
										+ selectedFile.getName());
								// trimite fisierul si IP-ul clientului pentru
								// ca
								// fisierul sa nu fie ii fie retrimis.

								client.sendToServer(new Message(
										Message.MessageType.DATA_TRANSFER,
										new Object[] {
												selectedFile.getName(),
												b,
												InetAddress.getLocalHost()
														.getHostAddress() }));
							} catch (IOException e1) {
								addTextToLogger("ERROR: File doesn't send!");
							}

						} else if (status == JFileChooser.CANCEL_OPTION) {
							addTextToLogger("Canceled!"); // afiseaza in logger
															// cancel daca s-a
															// apasat butonul
															// cancel
						}
					}
				});

		connect.addActionListener(new ActionListener() {// adauga ascultator pt
														// butonul de conectare

			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage.setEnabled(true); // activeaza butoanele
				transferFile.setEnabled(true);
				client = new Client(connectToHost.getText(), 5555, getThis());// creeaza
																				// un
																				// client
				try {
					addTextToLogger("Connect to: " + connectToHost.getText());
					client.openConnection(); // deschide conexiunea
				} catch (IOException e4) {
					addTextToLogger("ERROR: Connection could not be established!");
				}
			}
		});

		sendMessage.addActionListener(new ActionListener() { // adauga un
																// ascultator la
																// butonul de
																// trimitere
																// mesaj

					@Override
					public void actionPerformed(ActionEvent e) {
						JDialog msg = new JDialog(); // creeaza o fereastra
						msg.setLayout(null);
						msg.setTitle("Send Message");
						final JTextArea text = new JTextArea();
						JScrollPane msgScroll = new JScrollPane(text); // adauga
																		// bara
																		// de
																		// derulare
						msgScroll.setBounds(10, 10, 250, 100);
						msg.add(msgScroll);
						msg.setSize(300, 200);
						msg.setVisible(true);
						JButton send = new JButton("Send"); // buton de
															// trimitere mesaj
						send.setBounds(70, 120, 100, 25);
						send.addActionListener(new ActionListener() { // adauga
							// ascultator
							// pt
							// butonul
							// de
							// trimitere
							// mesaj

							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									addTextToLogger("Send Message.");
									sentMessage =  InetAddress
                                            .getLocalHost()
                                            .getHostAddress()
                                            + ": " + text.getText();
                                    client.sendToServer(new Message(
											// trimitere mesaj
											Message.MessageType.MSG,
											new Object[] { InetAddress
													.getLocalHost()
													.getHostAddress()
													+ ": " + text.getText() }));
								} catch (IOException e1) {
									addTextToLogger("ERROR: Message could not send!");
								}
								text.setText("");
							}
						});
						msg.add(send);
					}
				});


		logger = new JTextArea(); // logger - fereastra unde apar mesajele
		logger.setBounds(10, 0, 560, 285);
		logger.setEditable(false);
		logger.setMargin(new Insets(10, 10, 10, 10));
		JScrollPane scroll = new JScrollPane(logger); // bara de derulare pt
														// logger
		scroll.setBounds(10, 270, 560, 285);
		add(scroll);

		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // se inchid
														// procesele java cand
														// se apasa pe x
	}

	public static void main(String[] args) throws IOException {
		new GUI(); // creeaza o interfata grafica a aplicatiei
	}

	public final void addTextToLogger(String text) { // adauga mesaje in logger
		final StringBuilder newText = new StringBuilder(logger.getText() + "\n " + text);
		logger.setText(newText.toString());
	}

	// metoda implementata pentru a putea trimite instanta din aceasta clasa
	// atunci cand se creeaza un client (necesar ca si pametru in constructor).
	// Scrierea directa cu ajutorul pointerului this nu se putea face deoarece
	// instantierea pointerului era realizata in ascultatorul butonului connect.
	private GUI getThis() {
		return this;
	}
}
