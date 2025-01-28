package kadaiB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class DrawingClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 50012;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private List<DrawingData.LineData> lines = new ArrayList<>();
    private List<DrawingData.RectangleData> rectangles = new ArrayList<>();
    private List<DrawingData.EllipseData> ellipses = new ArrayList<>();
    private List<DrawingData.TriangleData> triangles = new ArrayList<>();
    private DrawingCanvas canvas;
    private String selectedTool = "Line";
    private Point startPoint;
    private Color selectedColor = Color.BLACK;
    private Stack<DrawingData> stack = new Stack<>();

    public static void main(String[] args) {
        new DrawingClient().startClient();
    }

    public void startClient() {
        try {
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            System.out.println("Connected to server at " + SERVER_HOST + ":" + SERVER_PORT);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            JFrame frame = new JFrame("Drawing Tool");
            canvas = new DrawingCanvas();
            frame.add(canvas, BorderLayout.CENTER);
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

            JComboBox<String> toolSelector = new JComboBox<>(new String[]{"Line", "Rectangle", "Ellipse", "Triangle"});
            toolSelector.addActionListener(e -> selectedTool = (String) toolSelector.getSelectedItem());
            toolPanel.add(new JLabel("Tool: "));
            toolPanel.add(toolSelector);

            JButton colorButton = new JButton("Color");
            colorButton.addActionListener(e -> {
                Color color = JColorChooser.showDialog(frame, "Select Color", selectedColor);
                if (color != null) selectedColor = color;
            });

            toolPanel.add(colorButton);
            frame.add(toolPanel, BorderLayout.NORTH);
            toolPanel.setBackground(Color.LIGHT_GRAY);

            JPanel southPanel = new JPanel(new BorderLayout());
            southPanel.setBackground(Color.LIGHT_GRAY);

            JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            controlPanel.setBackground(Color.LIGHT_GRAY);
            JButton clearButton = new JButton("Clear");
            clearButton.addActionListener(e -> clearDrawing());
            controlPanel.add(clearButton);

            JButton undoButton = new JButton("Undo");
            undoButton.addActionListener(e -> undoLastShape());
            controlPanel.add(undoButton);
            southPanel.add(controlPanel, BorderLayout.WEST);

            JPanel saveAndLoadPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            saveAndLoadPanel.setBackground(Color.LIGHT_GRAY);
            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e -> saveDrawingData());
            saveAndLoadPanel.add(saveButton);

            JButton loadButton = new JButton("Load");
            loadButton.addActionListener(e -> loadDrawingData());
            saveAndLoadPanel.add(loadButton);

            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> closeConnection());
            saveAndLoadPanel.add(closeButton);
            southPanel.add(saveAndLoadPanel, BorderLayout.EAST);

            frame.add(southPanel, BorderLayout.SOUTH);

            canvas.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    startPoint = e.getPoint();
                    switch (selectedTool) {
                        case "Line":
                            DrawingData.LineData newLine = new DrawingData.LineData(startPoint, startPoint, selectedColor);
                            lines.add(newLine);
                            stack.push(newLine);
                            break;
                        case "Rectangle":
                            DrawingData.RectangleData newRectangle = new DrawingData.RectangleData(startPoint, startPoint, selectedColor);
                            rectangles.add(newRectangle);
                            stack.push(newRectangle);
                            break;
                        case "Ellipse":
                            DrawingData.EllipseData newEllipse = new DrawingData.EllipseData(startPoint, startPoint, selectedColor);
                            ellipses.add(newEllipse);
                            stack.push(newEllipse);
                            break;
                        case "Triangle":
                            DrawingData.TriangleData newTriangle = new DrawingData.TriangleData(startPoint, startPoint, selectedColor);
                            triangles.add(newTriangle);
                            stack.push(newTriangle);
                            break;
                    }
                }
            });

            canvas.addMouseMotionListener(new MouseAdapter() {
                public void mouseDragged(MouseEvent e) {
                    Point currentPoint = e.getPoint();
                    switch (selectedTool) {
                        case "Line":
                            lines.get(lines.size() - 1).end = currentPoint;
                            break;
                        case "Rectangle":
                            rectangles.get(rectangles.size() - 1).end = currentPoint;
                            break;
                        case "Ellipse":
                            ellipses.get(ellipses.size() - 1).end = currentPoint;
                            break;
                        case "Triangle":
                            triangles.get(triangles.size() - 1).end = currentPoint;
                            break;
                    }
                    canvas.repaint();
                }
            });

            frame.setVisible(true);

        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveDrawingData() {
        try {
            // セーブ処理
            DrawingData drawingData = new DrawingData();
            drawingData.setLines(lines);
            drawingData.setRectangles(rectangles);
            drawingData.setEllipses(ellipses);
            drawingData.setTriangles(triangles);

            output.writeObject("SAVE");
            output.writeObject(drawingData);
            output.flush();
            output.reset();

            JOptionPane.showMessageDialog(null, "保存しました!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            System.err.println("Error saving drawing data: " + e.getMessage());
            e.printStackTrace();
        }
    }




    public void loadDrawingData() {
        try {
            // "LOAD" リクエストをサーバーに送信
            output.writeObject("LOAD");

            // サーバーから描画データを受け取る
            DrawingData data = (DrawingData) input.readObject();

            if (data != null) {
                lines = data.getLines();
                rectangles = data.getRectangles();
                ellipses = data.getEllipses();
                triangles = data.getTriangles();

                SwingUtilities.invokeLater(() -> {
                    canvas.repaint();
                });

                // ロード成功のメッセージを表示
                JOptionPane.showMessageDialog(null, "データを復元しました!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                System.out.println("No data received from server.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading drawing data: " + e.getMessage());
            e.printStackTrace();
        }
    }





    private void clearDrawing() {
        lines.clear();
        rectangles.clear();
        ellipses.clear();
        triangles.clear();
        System.out.println("Cleared all drawings.");
        canvas.repaint();
    }

    private void undoLastShape() {
        if (!stack.isEmpty()) {
            DrawingData data = stack.pop();
            if (data instanceof DrawingData.LineData) {
                lines.remove(data);
            } else if (data instanceof DrawingData.RectangleData) {
                rectangles.remove(data);
            } else if (data instanceof DrawingData.EllipseData) {
                ellipses.remove(data);
            } else if (data instanceof DrawingData.TriangleData) {
                triangles.remove(data);
            }
            canvas.repaint();
        }
    }

    private void closeConnection() {
        int option = JOptionPane.showConfirmDialog(null,
                "アプリケーションを終了しますか?",
                "確認",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            try {
                if (output != null) {
                    output.writeObject("CLOSE");
                    System.out.println("Connection closed.");

                }
                System.exit(0); // アプリケーション終了
            } catch (IOException e) {
                System.err.println("Error closing connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }



    private class DrawingCanvas extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // 各図形を描画する
            for (DrawingData.LineData line : lines) {
                g2d.setColor(line.color);
                g2d.drawLine(line.start.x, line.start.y, line.end.x, line.end.y);
            }
            for (DrawingData.RectangleData rect : rectangles) {
                g2d.setColor(rect.color);
                g2d.drawRect(Math.min(rect.start.x, rect.end.x),
                        Math.min(rect.start.y, rect.end.y),
                        Math.abs(rect.start.x - rect.end.x),
                        Math.abs(rect.start.y - rect.end.y));
            }
            for (DrawingData.EllipseData ellipse : ellipses) {
                g2d.setColor(ellipse.color);
                g2d.drawOval(Math.min(ellipse.start.x, ellipse.end.x),
                        Math.min(ellipse.start.y, ellipse.end.y),
                        Math.abs(ellipse.start.x - ellipse.end.x),
                        Math.abs(ellipse.start.y - ellipse.end.y));
            }
            for (DrawingData.TriangleData triangle : triangles) {
                g2d.setColor(triangle.color);
                int[] xPoints = {triangle.start.x, triangle.end.x, (triangle.start.x + triangle.end.x) / 2};
                int[] yPoints = {triangle.start.y, triangle.start.y, triangle.end.y};
                g2d.drawPolygon(xPoints, yPoints, 3);
            }
        }

    }
}