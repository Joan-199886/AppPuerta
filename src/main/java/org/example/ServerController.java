package org.example;

import java.io.BufferedInputStream;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;


import javafx.embed.swing.SwingFXUtils;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ServerController extends Thread implements Initializable {

	@FXML
	private Label label;

	@FXML
	private TextArea text;

	@FXML
	private ImageView entrada;

	@FXML
	private ImageView salida;

	@FXML
	private Pane pane1;

	@FXML
	private Pane pane2;

	@FXML
	private Pane border1;

	@FXML
	private Pane border2;

	private PrintWriter writer;

	private ServerSocket serverSocket;

	private boolean check = false;
	private boolean checkEntrada = false;
	private boolean checkSalida = false;

	private String accion;
	private String code;
	private ObjectInputStream images;

	private Image image = new Image("file:///C:/.resources/chico.png");
	private Image image2 = new Image("file:///C:/.resources/nina.png");
	private Image imagen = new Image("file:///C:/.resources/usuario.png");

	private Image image1 = new Image("file:///C:/.resources/carga.gif");

	private ImageView imageView1 = new ImageView(image1);
	private ImageView imageView2 = new ImageView(image1);
	private Text cargando = new Text("PUERTA CERRADA ");
	private Text cargando2 = new Text("PUERTA CERRADA ");
	private Text cuenta = new Text("");
	private Text cuenta2 = new Text("");

	private BufferedImage captura;
	private String mensaje;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {



		imageView1.relocate(130, 475);
		imageView1.setFitHeight(120);
		imageView1.setFitWidth(150);

		imageView2.relocate(130, 475);
		imageView2.setFitHeight(120);
		imageView2.setFitWidth(150);

		cargando.relocate(90, 480);
		cargando.setFont(Font.font("Verdana", 25));

		cargando2.relocate(90, 480);
		cargando2.setFont(Font.font("Verdana", 25));

		pane1.getChildren().add(cargando);
		pane2.getChildren().add(cargando2);

		cuenta.relocate(196, 530);
		cuenta.setFont(Font.font("Verdana", 25));

		cuenta2.relocate(196, 530);
		cuenta2.setFont(Font.font("Verdana", 25));

		entrada.setImage(imagen);

		salida.setImage(imagen);
		connectSocket();
	}

	public void connectSocket() {
		this.start();
	}

	@Override
	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(1234)) {

			while (true) {
				Socket cliente = serverSocket.accept();

				System.out.println("New client connected");

				DataInputStream dis = new DataInputStream(cliente.getInputStream());
				BufferedInputStream in = new BufferedInputStream(cliente.getInputStream());

				code = dis.readUTF().toString();

				if (!code.equals("3")) {
					accion = dis.readUTF().toString();

					int tamaño = dis.readInt();

					byte[] buffer = new byte[tamaño];

					for (int i = 0; i < buffer.length; i++) {
						buffer[i] = (byte) in.read();
					}

					InputStream is = new ByteArrayInputStream(buffer);
					captura = ImageIO.read(is);

				}

				if (check == false) {
					receive();
				}

				in.close();
				cliente.close();
			}

		} catch (Exception e)
		{

			e.printStackTrace();

		}
	}

	public void receive() {
		switch (code) {
		case "3":

			if (check == false && checkEntrada == false && checkSalida == false) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {

						check = true;
						Platform.runLater(() -> runTask());
					}
				});
			}
			break;

		case "2":

			if (check == false) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if(accion.equals("Ingreso")  ) {
							Platform.runLater(() -> visitanteIngresoRun());
						}
						else {Platform.runLater(() -> visitanteSalidaRun());}
					}
				});
			}
			break;

		case "1":

			if (check == false) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if(accion.equals("Ingreso") )
						{
							 usuarioIngresoRun();
						}
						else { usuarioSalidaRun();}



					}
				});
			}

			break;
		default:
			break;
		}
	}

	private void visitanteIngresoRun() {

		Image image = SwingFXUtils.toFXImage(captura, null);

		if (checkEntrada == false) {
			checkEntrada = true;
			cargando.setText("VISITANTE INGRESANDO...");
			entrada.setImage(image);
			pane1.getChildren().add(imageView1);
			pane1.getChildren().add(cuenta);
			border1.setStyle("-fx-background-color: #008CBA");
			cargando.relocate(40, 470);
		}


		Task<Void> longTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					for (int i = 8; i >= 0; i--) {
						Thread.sleep(1000);
							cuenta.setText(i + "");
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}
		};

		longTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {

					checkEntrada = false;
					entrada.setImage(imagen);
					pane1.getChildren().remove(imageView1);
					pane1.getChildren().remove(cuenta);
					cargando.setText("PUERTA CERRADA");
					border1.setStyle("-fx-background-color:  #bf1b1b");
					cargando.relocate(90, 470);

			}
		});

		new Thread(longTask).start();

	}
	private void visitanteSalidaRun() {

		Image image = SwingFXUtils.toFXImage(captura, null);


			if (checkSalida == false) {
				checkSalida = true;
				cargando2.setText("VISITANTE SALIENDO...");
				salida.setImage(image);
				pane2.getChildren().add(imageView2);
				pane2.getChildren().add(cuenta2);
				border2.setStyle("-fx-background-color: #008CBA");
				cargando2.relocate(60, 470);
			}


		Task<Void> longTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					for (int i = 8; i >= 0; i--) {
						Thread.sleep(1000);
							cuenta2.setText(i + "");

					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}
		};

		longTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {


					checkSalida = false;
					salida.setImage(imagen);
					pane2.getChildren().remove(imageView2);
					pane2.getChildren().remove(cuenta2);
					cargando2.setText("PUERTA CERRADA");
					border2.setStyle("-fx-background-color:  #bf1b1b");
					cargando2.relocate(90, 470);

			}
		});

		new Thread(longTask).start();

	}
	private void usuarioIngresoRun() {

		Image image = SwingFXUtils.toFXImage(captura, null);

		if (checkEntrada == false)
		{

			checkEntrada = true;
			cargando.relocate(50, 470);
			cargando.setText("USUARIO INGRESANDO...");
			entrada.setImage(image);
			pane1.getChildren().add(imageView1);
			pane1.getChildren().add(cuenta);
			border1.setStyle("-fx-background-color: #008CBA");

		}

		Task<Void> longTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					for (int i = 8; i >= 0; i--) {
						Thread.sleep(1000);
						cuenta.setText(i + "");
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}
		};

		longTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {

					checkEntrada = false;
					entrada.setImage(imagen);
					pane1.getChildren().remove(imageView1);
					pane1.getChildren().remove(cuenta);
					cargando.relocate(90, 470);
					cargando.setText("PUERTA CERRADA");
					border1.setStyle("-fx-background-color:  #bf1b1b");

			}
		});

		new Thread(longTask).start();

	}

	private void usuarioSalidaRun() {

		Image image = SwingFXUtils.toFXImage(captura, null);


			if (checkSalida == false)
			{
				checkSalida = true;
				cargando2.setText("USUARIO SALIENDO...");
				salida.setImage(image);
				pane2.getChildren().add(imageView2);
				pane2.getChildren().add(cuenta2);
				border2.setStyle("-fx-background-color: #008CBA");
				cargando2.relocate(70, 470);
			}

		Task<Void> longTask1 = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					for (int i = 8; i >= 0; i--) {
						Thread.sleep(1000);
							cuenta2.setText(i + "");
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}
		};

		longTask1.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {

					checkSalida = false;
					salida.setImage(imagen);
					pane2.getChildren().remove(imageView2);
					pane2.getChildren().remove(cuenta2);
					cargando2.relocate(90, 470);
					cargando2.setText("PUERTA CERRADA");
					border2.setStyle("-fx-background-color:  #bf1b1b");



			}
		});

		new Thread(longTask1).start();


	}
	private void runTask() {

		entrada.setImage(image);
		salida.setImage(image2);

		cargando.setText("PUERTA ABIERTA...");
		cargando2.setText("PUERTA ABIERTA...");

		pane1.getChildren().add(imageView1);

		pane2.getChildren().add(imageView2);

		pane1.getChildren().add(cuenta);

		pane2.getChildren().add(cuenta2);

		border1.setStyle("-fx-background-color: #008CBA");

		border2.setStyle("-fx-background-color: #008CBA");

		Task<Void> longTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				try {
					for (int i = 8; i >= 0; i--) {
						Thread.sleep(1000);
						cuenta.setText(i + "");
						cuenta2.setText(i + "");
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		longTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {

				entrada.setImage(imagen);
				salida.setImage(imagen);
				cuenta.setText("");
				cuenta2.setText("");
				pane1.getChildren().remove(imageView1);
				pane1.getChildren().remove(cuenta);
				pane2.getChildren().remove(imageView2);
				pane2.getChildren().remove(cuenta2);
				cargando.setText("PUERTA CERRADA");
				cargando2.setText("PUERTA CERRADA");
				border1.setStyle("-fx-background-color:  #bf1b1b");
				border2.setStyle("-fx-background-color:  #bf1b1b");
				check = false;

			}
		});

		new Thread(longTask).start();

	}
}
