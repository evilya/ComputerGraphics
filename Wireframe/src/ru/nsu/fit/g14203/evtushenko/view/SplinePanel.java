package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.model.Model;
import ru.nsu.fit.g14203.evtushenko.model.ModelParameters;
import ru.nsu.fit.g14203.evtushenko.model.geom.Point2D;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SplinePanel extends JPanel implements ActionListener {

    private static final int U0 = 0;
    private static final int V0 = 0;
    private static int U1;
    private static int V1;
    private final List<List<Point2D>> shapes;
    private Model model;
    private List<Point2D> dots;
    private int minNum = -1;
    private ParametersPanel parametersPanel;
    private int current;

    public SplinePanel(Model model) {
        this.model = model;
        shapes = model.getSplines();
        setPreferredSize(new Dimension(500, 500));
        setLayout(new GridBagLayout());

        addParametersPanel();

        addMouseListener(new MouseAdapter() {

            private Point2D clicked;

            @Override
            public void mouseClicked(MouseEvent e) {
                clicked = new Point2D(findX(e.getX()), findY(e.getY()));
                findNearestDot();
                repaint();
            }

            private void findNearestDot() {
                double min = Math.sqrt(
                        Math.pow(dots.get(0).getX() - clicked.getX(), 2)
                                + Math.pow(dots.get(0).getY() - clicked.getY(), 2)) + 1;
                for (int i = 0; i < dots.size(); i++) {
                    double distance = Math.sqrt(Math.pow(dots.get(i).getX() - clicked.getX(), 2) + Math.pow(dots.get(i).getY() - clicked.getY(), 2));
                    if (distance < min) {
                        min = distance;
                        minNum = i;
                    }
                }
            }


        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (minNum == -1)
                    return;
                int u = e.getX();
                int v = e.getY();
                double x = findX(u);
                double y = findY(v);

                dots.remove(minNum);
                dots.add(minNum, new Point2D(x, y));

                shapes.set(current, dots);

                repaint();
            }
        });
    }

    private void addParametersPanel() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 0;
        c.weighty = 1.0;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.PAGE_END;
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 1;
        c.gridwidth = 2;
        c.gridy = 2;
        parametersPanel = new ParametersPanel();
        add(parametersPanel, c);

    }

    private void drawLines(Graphics g) {
        if (dots == null) {
            return;
        }
        for (int i = 0; i < dots.size() - 1; i++) {
            g.setColor(Color.BLACK);

            int u1 = findU(dots.get(i).getX());
            int v1 = findV(dots.get(i).getY());
            int u2 = findU(dots.get(i + 1).getX());
            int v2 = findV(dots.get(i + 1).getY());

            g.drawLine(u1, v1, u2, v2);
        }

        g.setColor(Color.BLUE);

        dots.forEach(d -> g.fillOval(findU(d.getX()), findV(d.getY()), 5, 5));
    }

    private int findU(double x) {
        return (int) ((U1 - U0) * (x + 5) / (10) + U0 + 0.5);
    }

    private int findV(double y) {
        return (int) ((V1 - V0) * (y + 5) / (10) + V0 + 0.5);
    }

    private double findX(int u) {
        return ((10) * (double) (u - U0)) / (U1 - U0) - 5;
    }

    private double findY(int v) {
        return (10) * (double) (v - V0) / (V1 - V0) - 5;
    }

    private void drawSpline(Graphics g) {
        g.setColor(Color.BLACK);
        if (current < shapes.size()) {
            List<Point2D> xy = shapes.get(current);
            for (int i = 0; i < xy.size() - 1; i++) {
                g.drawLine(findU(xy.get(i).getX()),
                        findV(xy.get(i).getY()),
                        findU(xy.get(i + 1).getX()),
                        findV(xy.get(i + 1).getY()));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage splineImage = new BufferedImage(getWidth(),
                (int) (getHeight() * 0.7),
                BufferedImage.TYPE_INT_ARGB);

        Graphics splGraph = splineImage.createGraphics();
        splGraph.setColor(Color.LIGHT_GRAY);
        splGraph.fillRect(0, 0, splineImage.getWidth(), splineImage.getHeight());
        splGraph.setColor(Color.BLACK);
        splGraph.drawLine(0, splineImage.getHeight() / 2, splineImage.getWidth(), splineImage.getHeight() / 2);
        splGraph.drawLine(splineImage.getWidth() / 2, 0, splineImage.getWidth() / 2, splineImage.getHeight());
        U1 = getWidth();
        V1 = getHeight();

        drawLines(splGraph);
        if (minNum != -1) {
            splGraph.setColor(new Color(50, 150, 40));
            splGraph.fillOval(findU(dots.get(minNum).getX()), findV(dots.get(minNum).getY()), 5, 5);
        }

        drawSpline(splGraph);

        g.drawImage(splineImage, 0, 0, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("OK")) {
            return;
        }
        if (e.getActionCommand().equals("add")) {
            Point2D last = dots.get(dots.size() - 1);
            dots.add(new Point2D(last.getX() + 0.1, last.getY()));
            shapes.set(current, dots);
            repaint();
        }
        if (e.getActionCommand().equals("minus")) {
            if (minNum != -1) {
                dots.remove(minNum);
            }
            minNum = -1;
            shapes.set(current, dots);
            repaint();
        }
        if (e.getActionCommand().equals("spline_add")) {
            shapes.add(new ArrayList<>());
            parametersPanel.recreate();
        }
        if (e.getActionCommand().equals("spline_delete")) {
            if (current != -1) {
                shapes.remove(current);
                parametersPanel.recreate();
            }
        }
    }

    public class ParametersPanel extends JPanel implements ListSelectionListener {

        private final JList<String> stringJList;
        private final DefaultListModel<String> listModel;
        private final ModelParameters modelParameters = model.getParameters();

        ParametersPanel() {
            Parameters parameters = new Parameters();
            setLayout(new GridBagLayout());

            JLabel a = new JLabel("a");
            JLabel b = new JLabel("b");
            JLabel c = new JLabel("c");
            JLabel d = new JLabel("d");
            JLabel n = new JLabel("n");
            JLabel m = new JLabel("m");
            JLabel k = new JLabel("k");
            JLabel zn = new JLabel("zn");
            JLabel zf = new JLabel("zw");
            JLabel sw = new JLabel("sw");
            JLabel sh = new JLabel("sh");


            SpinnerNumberModel asnb = new SpinnerNumberModel(modelParameters.getA(), 0, 1, 0.1);
            JSpinner aSpinner = new JSpinner(asnb);
            asnb.addChangeListener(e -> {
                        parameters.A = (double) asnb.getValue();
                modelParameters.setA(parameters.A);
                        model.updateShapes();
                    }
            );

            SpinnerNumberModel bsnb = new SpinnerNumberModel(1, 0, 1, 0.1);
            JSpinner bSpinner = new JSpinner(bsnb);
            bsnb.addChangeListener(e -> {
                parameters.B = (double) bsnb.getValue();
                modelParameters.setB(parameters.B);
                model.updateShapes();
            });

            SpinnerNumberModel csnb = new SpinnerNumberModel(0, 0, 2 * Math.PI, Math.PI / 10);
            JSpinner cSpinner = new JSpinner(csnb);
            csnb.addChangeListener(e -> {
                parameters.C = (double) csnb.getValue();
                modelParameters.setC(parameters.C);
                model.updateShapes();
            });

            SpinnerNumberModel dsnb = new SpinnerNumberModel(2 * Math.PI, 0, 2 * Math.PI, Math.PI / 10);
            JSpinner dSpinner = new JSpinner(dsnb);
            dsnb.addChangeListener(e -> {
                parameters.D = (double) dsnb.getValue();
                modelParameters.setD(parameters.D);
                model.updateShapes();
            });

            SpinnerNumberModel nsnb = new SpinnerNumberModel(modelParameters.getN(), 1, 50, 1);
            JSpinner nSpinner = new JSpinner(nsnb);
            nsnb.addChangeListener(e -> {
                parameters.N = (int) nsnb.getValue();
                modelParameters.setN(parameters.N);
                model.updateShapes();
            });

            SpinnerNumberModel msnb = new SpinnerNumberModel(modelParameters.getM(), 1, 50, 1);
            JSpinner mSpinner = new JSpinner(msnb);
            msnb.addChangeListener(e -> {
                parameters.M = (int) msnb.getValue();
                modelParameters.setM(parameters.M);
                model.updateShapes();
            });


            SpinnerNumberModel ksnb = new SpinnerNumberModel(1, 1, 10, 1);
            JSpinner kSpinner = new JSpinner(ksnb);

            SpinnerNumberModel znsnb = new SpinnerNumberModel(1, 1, 10, 1);
            JSpinner znSpinner = new JSpinner(znsnb);

            SpinnerNumberModel zfsnb = new SpinnerNumberModel(1, 1, 10, 1);
            JSpinner zfSpinner = new JSpinner(zfsnb);

            SpinnerNumberModel swsnb = new SpinnerNumberModel(1, 1, 10, 1);
            JSpinner swSpinner = new JSpinner(swsnb);

            SpinnerNumberModel shsnb = new SpinnerNumberModel(1, 1, 10, 1);
            JSpinner shSpinner = new JSpinner(shsnb);

            GridBagConstraints gbc = new GridBagConstraints();

            gbc.insets = new Insets(10, 5, 10, 5);
            gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;


            gbc.gridy = 0;
            gbc.gridx = 0;
            add(a, gbc);

            gbc.gridx = 1;
            add(aSpinner, gbc);

            gbc.gridx = 2;
            add(b, gbc);

            gbc.gridx = 3;
            add(bSpinner, gbc);

            gbc.gridx = 4;
            add(c, gbc);

            gbc.gridx = 5;
            add(cSpinner, gbc);

            gbc.gridx = 6;
            add(d, gbc);

            gbc.gridx = 7;
            add(dSpinner, gbc);


            gbc.weighty = 1;
            gbc.gridheight = 1;
            gbc.gridy = 1;
            gbc.gridx = 0;
            add(zn, gbc);

            gbc.gridx = 1;
            add(znSpinner, gbc);

            gbc.gridx = 2;
            add(zf, gbc);

            gbc.gridx = 3;
            add(zfSpinner, gbc);

            gbc.gridx = 4;
            add(sw, gbc);

            gbc.gridx = 5;
            add(swSpinner, gbc);

            gbc.gridx = 6;
            add(sh, gbc);

            gbc.gridx = 7;
            add(shSpinner, gbc);


            gbc.gridy = 2;
            gbc.gridx = 0;
            add(n, gbc);

            gbc.gridx = 1;
            add(nSpinner, gbc);

            gbc.gridx = 2;
            add(m, gbc);

            gbc.gridx = 3;
            add(mSpinner, gbc);

            gbc.gridx = 4;
            add(k, gbc);

            gbc.gridx = 5;
            add(kSpinner, gbc);

            gbc.gridx = 8;
            gbc.gridy = 0;
            gbc.gridheight = 3;


            listModel = new DefaultListModel<>();
            recreate();
            stringJList = new JList<>(listModel);
            stringJList.setVisibleRowCount(6);
            stringJList.addListSelectionListener(this);
            add(new JScrollPane(stringJList), gbc);


            gbc.insets = new Insets(1, 1, 1, 1);
            gbc.gridheight = 1;
            gbc.gridx = 8;
            gbc.gridy = 3;
            JButton addSplineButton = new JButton("add new spline");
            addSplineButton.addActionListener(SplinePanel.this);
            addSplineButton.setActionCommand("spline_add");
            add(addSplineButton, gbc);
            gbc.gridx = 8;
            gbc.gridy = 4;
            JButton deleteButton = new JButton("delete spline");
            deleteButton.setActionCommand("spline_delete");
            deleteButton.addActionListener(SplinePanel.this);
            add(deleteButton, gbc);

            gbc.gridx = 9;
            gbc.gridy = 0;
            JButton addPointButton = new JButton("add new point");
            addPointButton.setActionCommand("add");
            addPointButton.addActionListener(SplinePanel.this);
            add(addPointButton, gbc);
            gbc.gridy = 1;
            JButton deletePointButton = new JButton("delete point");
            deletePointButton.setActionCommand("minus");
            deletePointButton.addActionListener(SplinePanel.this);
            add(deletePointButton, gbc);

        }

        public void recreate() {
            listModel.removeAllElements();
            final int[] i = {0};
            shapes.forEach(shape -> {
                listModel.addElement("spline" + i[0]);
                i[0]++;
            });
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {

                if (stringJList.getSelectedIndex() == -1) {

                } else {
                    minNum = -1;
                    current = (stringJList.getSelectedIndex());
                    dots = shapes.get(current);
                    SplinePanel.this.repaint();
                }
            }
        }

        private class Parameters {
            int N = modelParameters.getN();
            int M = modelParameters.getM();
            double A = modelParameters.getA();
            double B = modelParameters.getB();
            double C = modelParameters.getC();
            double D = modelParameters.getD();

        }
    }


}
