package kadaiB;

import java.awt.*;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class DrawingData implements Serializable {
    // 描画データクラス（Serializable を実装）
    public static class LineData extends DrawingData implements Serializable {  // Serializable を実装
        public Point start, end;
        public Color color;

        public LineData(Point start, Point end, Color color) {
            this.start = start;
            this.end = end;
            this.color = color;
        }
    }

    public static class RectangleData extends DrawingData implements Serializable {  // Serializable を実装
        public Point start, end;
        public Color color;

        public RectangleData(Point start, Point end, Color color) {
            this.start = start;
            this.end = end;
            this.color = color;
        }
    }

    public static class EllipseData extends DrawingData implements Serializable {  // Serializable を実装
        public Point start, end;
        public Color color;

        public EllipseData(Point start, Point end, Color color) {
            this.start = start;
            this.end = end;
            this.color = color;
        }
    }

    public static class TriangleData extends DrawingData implements Serializable {  // Serializable を実装
        public Point start, end;
        public Color color;

        public TriangleData(Point start, Point end, Color color) {
            this.start = start;
            this.end = end;
            this.color = color;
        }
    }

    private List<LineData> lines = new ArrayList<>();
    private List<RectangleData> rectangles = new ArrayList<>();
    private List<EllipseData> ellipses = new ArrayList<>();
    private List<TriangleData> triangles = new ArrayList<>();

    // 各データを取得するメソッド
    public List<LineData> getLines() {
        return lines;
    }

    public List<RectangleData> getRectangles() {
        return rectangles;
    }

    public List<EllipseData> getEllipses() {
        return ellipses;
    }

    public List<TriangleData> getTriangles() {
        return triangles;
    }

    // 全てのデータをまとめて取得するメソッド
    public void setLines(List<LineData> lines) {
        this.lines = lines;
    }

    public void setRectangles(List<RectangleData> rectangles) {
        this.rectangles = rectangles;
    }

    public void setEllipses(List<EllipseData> ellipses) {
        this.ellipses = ellipses;
    }

    public void setTriangles(List<TriangleData> triangles) {
        this.triangles = triangles;
    }

    // 描画データをリセットするメソッド
    public void clear() {
        lines.clear();
        rectangles.clear();
        ellipses.clear();
        triangles.clear();
    }
}