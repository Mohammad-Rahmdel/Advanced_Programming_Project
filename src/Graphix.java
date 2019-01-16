import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;

public class Graphix {
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel listPanel;
    private JPanel filePanel;
    private JTabbedPane fileTabs;
    private JPanel previewTab;
    private JPanel propertiesTab;
    private JButton downloadButton;
    private JButton duplicateButton;
    private JButton renameButton;
    private JButton deleteButton;
    private JPanel buttonsPanel;
    private JList filesList;
    private JScrollBar scrollBar1;
    private JMenuBar topMenu;
    private JMenu fileMenu;
    private JMenu uploadMenu;
    private JMenu toolsMenu;
    private JMenu helpMenu;
    private JPanel propertiesPanel;
    private JLabel labelName;
    private JTextField nameField;
    private JLabel labelExt;
    private JTextField extField;
    private JLabel labelSize;
    private JTextField sizeField;
    private JLabel labelParts;
    private JTextField partsField;
    private JLabel labelDist;
    private JTextField distField;
    private JLabel labelOwner;
    private JTextField ownerField;
    private JLabel labelCreateDate;
    private JTextField createDateField;
    private JLabel labelAccess;
    private JTextField accessField;
    private JMenuItem fiRefresh;
    private JMenuItem fiExit;

    public Graphix(){
        frame = new JFrame();
        frame.setSize(1200,800);
        mainPanel = new JPanel(new BorderLayout());
        listPanel = new JPanel(new BorderLayout());
        filePanel = new JPanel(new BorderLayout());

        mainPanel.add(listPanel, BorderLayout.WEST);
        mainPanel.add(filePanel);

        labelName = new JLabel("File name: ");
        nameField = new JTextField("test.png");
        nameField.setEditable(false);

        labelExt = new JLabel("File extention: ");
        extField = new JTextField("png");
        extField.setEditable(false);

        labelSize = new JLabel("Size: ");
        sizeField = new JTextField("1.5 MB");
        sizeField.setEditable(false);

        labelParts = new JLabel("Partition: ");
        partsField = new JTextField("2");
        partsField.setEditable(false);

        labelDist = new JLabel("File node distribution: ");
        distField = new JTextField("[1:2, 2:2]");
        distField.setEditable(false);

        labelOwner = new JLabel("Owner: ");
        ownerField = new JTextField("Ali");
        ownerField.setEditable(false);

        labelCreateDate = new JLabel("Created: ");
        createDateField = new JTextField("2018-12-05");
        createDateField.setEditable(false);

        labelAccess = new JLabel("Last accessed: ");
        accessField = new JTextField("2018-12-10");
        accessField.setEditable(false);

        propertiesPanel = new JPanel(new GridLayout(8,2));
        propertiesPanel.setMaximumSize(new Dimension(200,500));
        propertiesPanel.add(labelName);
        propertiesPanel.add(nameField);
        propertiesPanel.add(labelExt);
        propertiesPanel.add(extField);
        propertiesPanel.add(labelSize);
        propertiesPanel.add(sizeField);
        propertiesPanel.add(labelParts);
        propertiesPanel.add(partsField);
        propertiesPanel.add(labelParts);
        propertiesPanel.add(partsField);
        propertiesPanel.add(labelDist);
        propertiesPanel.add(distField);
        propertiesPanel.add(labelOwner);
        propertiesPanel.add(ownerField);
        propertiesPanel.add(labelCreateDate);
        propertiesPanel.add(createDateField);
        propertiesPanel.add(labelAccess);
        propertiesPanel.add(accessField);

        fileTabs = new JTabbedPane();
        filePanel.add(fileTabs, BorderLayout.CENTER);

        previewTab = new JPanel(new BorderLayout());
        previewTab.setName("Preview");
        propertiesTab = new JPanel(new BorderLayout());
        propertiesTab.setName("Properties");

        propertiesTab.add(propertiesPanel, BorderLayout.WEST);


        fileTabs.add(previewTab);
        fileTabs.add(propertiesTab);

        downloadButton = new JButton("Download");
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser j = new JFileChooser();

                // invoke the showsOpenDialog function to show the save dialog
                int r = j.showSaveDialog(null);

                // if the user selects a file
                if (r == JFileChooser.APPROVE_OPTION)

                {
                    // set the label to the path of the selected file
                    System.out.println(j.getSelectedFile().getAbsolutePath());
                }
                // if the user cancelled the operation
                else
                    System.out.println("the user cancelled the operation");
            }
        });

        duplicateButton = new JButton("Duplicate");
        duplicateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JDialog dialog = new JDialog(frame, "Duplication Result");
                dialog.add(new JLabel("Done!"));
                dialog.setLayout(new FlowLayout());
                dialog.setSize(200,100);
                dialog.setVisible(true);
            }
        });
        renameButton = new JButton("Rename");
        renameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JDialog dialog = new JDialog(frame, "Rename File");
                dialog.setLayout(new BorderLayout());
                dialog.add(new JLabel("new Name:"), BorderLayout.WEST);
                JTextField newName = new JTextField();
                newName.setPreferredSize(new Dimension(200,25));
                dialog.add(newName, BorderLayout.EAST);
                JButton submitBtn = new JButton("Submint");
                dialog.add(submitBtn, BorderLayout.SOUTH);
                submitBtn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        System.out.println(newName.getText());
                        dialog.dispose();
                    }
                });
                dialog.setSize(400,100);
                dialog.setVisible(true);

            }
        });
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to delete this file?","Warning",0);
                if(dialogResult == JOptionPane.YES_OPTION){
                    System.out.println("Yes is pressed");

                }
            }
        });
        buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.add(downloadButton);
        buttonsPanel.add(duplicateButton);
        buttonsPanel.add(renameButton);
        buttonsPanel.add(deleteButton);
        filePanel.add(buttonsPanel, BorderLayout.SOUTH);

        filesList = new JList();
        String[] listOfFiles = new String[]{"Ubuntu.iso", "Movie.mp4"};
        filesList.setListData(listOfFiles);
        //filesList.add
        filesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                String selected = filesList.getSelectedValue().toString();
                System.out.println(selected);
            }
        });
        listPanel.add(filesList);
        scrollBar1 = new JScrollBar();
        listPanel.add(scrollBar1, BorderLayout.WEST);

        topMenu = new JMenuBar();
        fileMenu =  new JMenu("File");
        fiRefresh = new JMenuItem("Refresh List");
        fileMenu.add(fiRefresh);
        fiExit = new JMenuItem("Exit Application");
        fiExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.dispose();
            }
        });
        fileMenu.add(fiExit);

        topMenu.add(fileMenu);
        uploadMenu = new JMenu("Upload");

        topMenu.add(uploadMenu);
        toolsMenu = new JMenu("Tools");
        topMenu.add(toolsMenu);
        helpMenu =  new JMenu("Help");
        topMenu.add(helpMenu);

        listPanel.add(topMenu, BorderLayout.NORTH);

        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);
        // frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
    public static void main(String[] args) {
        Graphix graphics = new Graphix();
    }
}
