//@author Alan Sorrill

//import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


public class DagDisplay<T extends Comparable<T>> extends JFrame implements MouseListener {
    private DAG<T> dag;
    private int elementPadding = 10;
    private int elementMargin = 20;
    private int cornerRad = 5;
    private int heightOffset = 40;
    private boolean needsResize = true;
    private HashMap<T, Rectangle> locations = new HashMap<>();
    private HashMap<T, Color> highlights = new HashMap();
    private LinkedList<DebugStep<T>> debugSteps = new LinkedList<>();


    public static void main(String[] args) throws IOException {
        //Construct DAG and add test data
        DAG<Integer> test = new DAG<>("asflkjh");
        test.add(null, 10);
        test.add(10, 9);
        test.add(10, 8);
        test.add(8, 6);
        test.add(9, 6);
        test.add(6, 5);
        test.add(null, 20);
        test.add(20, 21);
        test.add(20, 22);
        test.add(20, 23);
        test.add(22, 5);
        //Construct display and pass in DAG
        DagDisplay<Integer> disp = new DagDisplay(test);

        //if you modify your test data after the display has been constructed, you must call the update method to update the display
        test.add(5, 32);
        disp.update();

        //the display allows you to step through a debug situation function by function. In order to do this, call addDebugStep with
        //an object implementing DebugStep. This can be done inline as shown below. These DebugSteps will be executed in the order added.
        //One debug step will be executed each time the display is clicked.
        disp.addDebugStep(dag -> {
            dag.remove(5);
        });
        disp.addDebugStep(dag -> {
            //nodes can be highlighted for testing purposes in teh following manner
            disp.highlightNode(8);
        });
        disp.addDebugStep(dag -> {
            //nodes can be highlighted in custom colors like this
            disp.highlightNode(21, Color.GREEN.brighter());
        });
        disp.addDebugStep(dag -> {
            //highlights can be removed like this
            disp.unHighlightNode(8);
        });
    }

    public DagDisplay(DAG<T> dag) {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        setDag(dag);
        setSize(100, 100);
        this.addMouseListener(this);
    }

    public void setDag(DAG<T> dag) {
        this.dag = dag;
        needsResize = true;
        repaint();
    }

    public void addDebugStep(DebugStep<T> step) {
        debugSteps.add(step);
    }

    public void highlightNode(T nodeValue) {
        highlightNode(nodeValue, Color.YELLOW);
    }

    public void highlightNode(T nodeValue, Color color) {
        highlights.put(nodeValue, color);
        update();
    }

    public void unHighlightNode(T nodeValue) {
        highlights.remove(nodeValue);
        update();
    }

    public void clearHighlights() {
        highlights.clear();
        update();
    }

    public void update() {
        needsResize = true;
        repaint();
    }


    private int getMaxDepth(Node<T> node) {
        Integer h = 0;
        ArrayList<Node<T>> children = node.getChildren();
        for (Node<T> n : children) {
            h = Math.max(h, getMaxDepth(n));
        }
        return h + 1;
    }

    private Dimension getSingleNodeDisplaySize(Node<T> n) {
        String text;
        if (n == null || n.getElem() == null) {
            text = "";
        } else {
            text = n.getElem().toString();
        }
        int pad = elementPadding * 2;
        int w = pad + getGraphics().getFontMetrics().stringWidth(text);
        int h = pad + getGraphics().getFontMetrics().getHeight();
        return new Dimension(w, h);
    }

    private Dimension getSingleNodeDisplaySizeWithMargin(Node<T> n) {
        String text;
        if (n == null || n.getElem() == null) {
            text = "null";
        } else {
            text = n.getElem().toString();
        }
        int pad = elementPadding * 2 + elementMargin * 2;
        int w = pad + getGraphics().getFontMetrics().stringWidth(text);
        int h = pad + getGraphics().getFontMetrics().getHeight();
        return new Dimension(w, h);
    }

    private int getTreeDisplayHeight(Node<T> n) {

        int h = 0;
        int totalProcessed = 0;
        ArrayList<Node<T>> ch = n.getChildren();
        for (Node<T> cn : ch) {
            if (cn.getParent().equals(n)) {
                h = h + getTreeDisplayHeight(cn);
                totalProcessed++;
            }
        }
        if (totalProcessed == 0) {
            return getSingleNodeDisplaySizeWithMargin(n).height;
        }
        return h;
    }

    private int getTreeDisplayWidth(Node<T> node) {
        ArrayList<Pair<Integer, Node<T>>> depths = new ArrayList<>();
        calculateDepths(node, depths, 0);
        if (depths.size() == 0) {
            return 100;
        }
        Pair<Integer, Node<T>> max = depths.get(0);
        for (Pair<Integer, Node<T>> pair : depths) {
            if (pair.getKey() > max.getKey()) {
                max = pair;
            }
        }
        int w = 0;
        int nw;
        Node<T> n = max.getValue();
        while (true) {
            nw = getSingleNodeDisplaySizeWithMargin(n).width;
            System.out.println("Calcing " + n + " with " + nw);
            w += nw;
            if (n.getParent() == null) {
                break;
            }
            n = n.getParent();
        }
        return w;
    }

    private void calculateDepths(Node<T> node, ArrayList<Pair<Integer, Node<T>>> depths, int depth) {
        depths.add(new Pair<>(depth, node));
        depth++;
        ArrayList<Node<T>> children = node.getChildren();
        for (Node<T> n : children) {
            calculateDepths(n, depths, depth);
        }

    }


    private void drawNode(Graphics g, Node<T> n, int x, int y) {
        Dimension nSize = getSingleNodeDisplaySizeWithMargin(n);
        int fullHeight = getTreeDisplayHeight(n);
        int drawY = y + (fullHeight / 2);
        System.out.println("Drawing " + n.getElem() + " at " + x + ", " + y + " with height of " + fullHeight);

        Rectangle rect = new Rectangle(x + elementMargin, drawY - ((nSize.height - elementMargin) / 2), nSize.width - elementMargin * 2, nSize.height - elementMargin * 2);
        if (highlights.containsKey(n.getElem())) {
            g.setColor(highlights.get(n.getElem()));
        } else {
            g.setColor(Color.WHITE);
        }
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, cornerRad, cornerRad);
        g.setColor(Color.BLACK);
        g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, cornerRad, cornerRad);
        locations.put(n.getElem(), rect);
        g.drawString(n.getElem() + "", x + elementMargin + elementPadding, drawY);
        g.setColor(Color.GREEN);
        //g.fillOval(x - 5, drawY - 5, 10, 10);
        ArrayList<Node<T>> children = n.getChildren();
        Node<T> c;
        int h = 0;
        int yy = y;
        for (int i = 0; i < children.size(); i++) {
            c = children.get(i);
            h = getTreeDisplayHeight(c);
            if (c.getParent().equals(n)) {
                System.out.println("drawing " + c + " whose parent is " + c.getParent() + " from " + n);
                drawNode(g, c, x + nSize.width, yy);
                yy += h;
            } else {
                System.out.println("Not redrawing " + c + " from " + n);
            }
        }
    }

    public void paint(Graphics g) {
        if (needsResize) {
            int w = getTreeDisplayWidth(dag.getSentinel());
            int h = getTreeDisplayHeight(dag.getSentinel());
            needsResize = false;
            setSize(w, h + heightOffset);
        }
        locations.clear();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        Node<T> n = dag.getSentinel();
        drawNode(g, n, 0, heightOffset);
        drawNodeArrows(g, n);
    }

    private void drawNodeArrows(Graphics g, Node<T> n) {
        ArrayList<Node<T>> children = n.getChildren();
        Rectangle nr = locations.get(n.getElem());
        Rectangle cr;
        for (Node<T> c : children) {
            cr = locations.get(c.getElem());
            if (cr == null) {
                System.err.println("Failed to draw arrow from " + n + " to " + c + " because " + c + "'s position could not be found. Has it been deleted?");
                g.setColor(Color.RED);
                g.fillOval(nr.x + nr.width + 3, nr.y + nr.height / 4, nr.height / 2, nr.height / 2);
                continue;
            }
            if (c.getParent().equals(n)) {
                g.setColor(Color.RED);
                drawArrow(g, cr.x, cr.y + cr.height / 2, nr.x + nr.width, nr.y + nr.height / 2);
            }
            g.setColor(Color.GREEN);
            drawArrow(g, nr.x + nr.width, nr.y + nr.height / 2, cr.x, cr.y + cr.height / 2);

            drawNodeArrows(g, c);
        }
    }

    private void drawArrow(Graphics g, int x1, int y1, int x2, int y2) {
        g.drawLine(x1, y1, x2, y2);
        Vector2d line = Vector2d.vectorFromLine(x1, y1, x2, y2);
        Vector2d aBase = line.getUnitVector().multiply(10).add(line.getUnitVector().rotateVector(180));
        Vector2d a1 = aBase.add(aBase.getTangent());
        Vector2d a2 = aBase.add(aBase.getTangent().multiply(-1));

        g.drawLine(x2, y2, x2 - a1.x, y2 - a1.y);
        g.drawLine(x2, y2, x2 - a2.x, y2 - a2.y);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (debugSteps.size() == 0) {
            return;
        }
        DebugStep<T> step = debugSteps.removeFirst();
        step.onStep(dag);
        needsResize = true;
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private static class Vector2d {
        private int x;
        private int y;

        public Vector2d(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Vector2d(double x, double y) {
            this((int) Math.round(x), (int) Math.round(y));
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public static Vector2d vectorFromLine(int x1, int y1, int x2, int y2) {
            return new Vector2d(x2, y2).minus(new Vector2d(x1, y1));
        }

        public Vector2d minus(Vector2d o) {
            return new Vector2d(x - o.getX(), y - o.getY());
        }

        public Vector2d getUnitVector() {
            double length = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
            return new Vector2d((int) Math.round(x / length), (int) Math.round(y / length));
        }

        public Vector2d getTangent() {
            return new Vector2d(y, -x);
        }

        public Vector2d rotateVector(double deg) {
            double ca = Math.cos(Math.toRadians(deg));
            double sa = Math.sin(Math.toRadians(deg));
            return new Vector2d(ca * x - sa * y, sa * x + ca * y);
        }

        public Vector2d multiply(int i) {
            return new Vector2d(x * i, y * i);
        }

        public Vector2d add(Vector2d other) {
            return new Vector2d(other.x + x, other.y + y);
        }
    }

    public interface DebugStep<T extends Comparable<T>> {
        void onStep(DAG<T> dag);
    }
}
