package kadaiB;

import java.io.*;
import java.net.*;
import java.util.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DrawingServer {
    private static final int SERVER_PORT = 50012;
    private static List<DrawingData.LineData> lines = new ArrayList<>();
    private static List<DrawingData.RectangleData> rectangles = new ArrayList<>();
    private static List<DrawingData.EllipseData> ellipses = new ArrayList<>();
    private static List<DrawingData.TriangleData> triangles = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server is listening on port " + SERVER_PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class ClientHandler extends Thread {
        private Socket socket;
        private ObjectOutputStream output;
        private ObjectInputStream input;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                output = new ObjectOutputStream(socket.getOutputStream());
                input = new ObjectInputStream(socket.getInputStream());

                while (true) {
                    String request = (String) input.readObject();
                    if ("SAVE".equals(request)) {
                        saveDrawingData();
                    } else if ("LOAD".equals(request)) {
                        sendDrawingData();
                    } else if ("CLOSE".equals(request)) {
                        break;
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void sendDrawingData() throws IOException {

            DrawingData data = new DrawingData();
            data.setLines(lines);
            data.setRectangles(rectangles);
            data.setEllipses(ellipses);
            data.setTriangles(triangles);
            output.writeObject(data);
            output.flush();
            output.reset();
        }

        private void saveDrawingData() throws IOException, ClassNotFoundException {

            DrawingData data = (DrawingData) input.readObject();
            lines = data.getLines();
            rectangles = data.getRectangles();
            ellipses = data.getEllipses();
            triangles = data.getTriangles();
        }
    }
}