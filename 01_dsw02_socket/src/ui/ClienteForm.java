package ui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ClienteForm {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ClienteForm().iniciar();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            }
        });
    }

    private void iniciar() throws Exception {
        String host = "localhost";
        int puerto = 5000;

        Socket socket = new Socket(host, puerto);
        BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter salida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        JFrame frame = new JFrame("Cliente TCP (Swing)");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JTextArea log = new JTextArea();
        log.setEditable(false);

        JTextField input = new JTextField();
        JButton enviar = new JButton("Enviar");

        enviar.addActionListener(e -> {
            String msg = input.getText().trim();
            if (msg.isEmpty()) return;

            salida.println(msg);
            log.append("Yo: " + msg + "\n");
            input.setText("");

            // leer respuesta del servidor en otro hilo para no congelar la UI
            new Thread(() -> {
                try {
                    String resp = entrada.readLine();
                    if (resp != null) {
                        SwingUtilities.invokeLater(() -> log.append("Servidor: " + resp + "\n"));
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
        });

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(log), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(input, BorderLayout.CENTER);
        bottom.add(enviar, BorderLayout.EAST);

        frame.add(bottom, BorderLayout.SOUTH);
        frame.setSize(450, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        log.append("Conectado a " + host + ":" + puerto + "\n");
        log.append("Escribe y presiona Enviar (exit para salir)\n");
    }
}
