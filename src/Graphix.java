import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;

public class Graphix {
    private int id;
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel listPanel;
    private JPanel filePanel;
    private JTabbedPane fileTabs;
    private JPanel previewTab;
    private JPanel propertiesTab;
    private JButton downloadButton;
    private JButton uploadButton;
    private JButton renameButton;
    private JButton deleteButton;
    private JPanel buttonsPanel;
    private JList filesList;
    private String[] listOfFiles;
    private JScrollBar scrollBar1;
    private JMenuBar topMenu;
    private JMenu fileMenu;
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

    public Graphix(int id){
        this.id = id;
        frame = new JFrame();
        frame.setSize(1200,800);
        mainPanel = new JPanel(new BorderLayout());
        listPanel = new JPanel(new BorderLayout());
        listPanel.setMinimumSize(new Dimension(500, 700));
        filePanel = new JPanel(new BorderLayout());

        mainPanel.add(listPanel, BorderLayout.WEST);
        mainPanel.add(filePanel, BorderLayout.CENTER);

        labelName = new JLabel("File name: ");
        nameField = new JTextField("");
        nameField.setEditable(false);

        labelExt = new JLabel("File extention: ");
        extField = new JTextField("");
        extField.setEditable(false);

        labelSize = new JLabel("Size: ");
        sizeField = new JTextField("");
        sizeField.setEditable(false);

        labelParts = new JLabel("Partition: ");
        partsField = new JTextField("");
        partsField.setEditable(false);

        labelDist = new JLabel("File node distribution: ");
        distField = new JTextField("");
        distField.setEditable(false);

        labelOwner = new JLabel("Owner: ");
        ownerField = new JTextField("");
        ownerField.setEditable(false);

        labelCreateDate = new JLabel("Created: ");
        createDateField = new JTextField("");
        createDateField.setEditable(false);

        labelAccess = new JLabel("Last accessed: ");
        accessField = new JTextField("");
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

        previewTab = new JPanel(new CardLayout());
        JPanel imageTab = new JPanel(new BorderLayout());
        imageTab.add(new JLabel("imageTab"));
        JPanel textTab = new JPanel(new BorderLayout());
        textTab.add(new JLabel("textTab"));
        // TODO changeListener implemented in list

        previewTab.add(imageTab);
        previewTab.add(textTab);
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
                    System.out.println("file path:"+j.getSelectedFile().getAbsolutePath());
                    System.out.println("user id :" + id);
                }
                // if the user cancelled the operation
                else
                    System.out.println("the user cancelled the operation");
            }
        });

        uploadButton = new JButton("Upload");
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser j = new JFileChooser();

                // invoke the showsOpenDialog function to show the save dialog
                int r = j.showOpenDialog(null);

                // if the user selects a file
                if (r == JFileChooser.APPROVE_OPTION)

                {
                    // set the label to the path of the selected file
                    System.out.println(j.getSelectedFile().getAbsolutePath());
                    JDialog dialog = new JDialog(frame, "Partitioning");
                    dialog.setLayout(new BorderLayout());
                    dialog.add(new JLabel("number of Partitions:"), BorderLayout.WEST);
                    JTextField noParts = new JTextField();
                    noParts.setPreferredSize(new Dimension(200,25));
                    dialog.add(noParts, BorderLayout.EAST);
                    JButton submitBtn = new JButton("Submint");
                    dialog.add(submitBtn, BorderLayout.SOUTH);
                    submitBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            System.out.println("number of partitions :"+noParts.getText());
                            dialog.dispose();
                        }
                    });
                    dialog.setSize(400,100);
                    dialog.setVisible(true);
                }
                // if the user cancelled the operation
                else
                    System.out.println("the user cancelled the operation");
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
                        System.out.println("current name:"+listOfFiles[filesList.getSelectedIndex()]);
                        System.out.println("new name:"+newName.getText());
                        System.out.println("user id:"+id);
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
                    System.out.println("file name:"+listOfFiles[filesList.getSelectedIndex()]);
                    System.out.println("user id: " + id);
                } else {
                    System.out.println("Cancel deleting file");
                }
            }
        });
        buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.add(downloadButton);
        buttonsPanel.add(uploadButton);
        buttonsPanel.add(renameButton);
        buttonsPanel.add(deleteButton);
        filePanel.add(buttonsPanel, BorderLayout.SOUTH);

        filesList = new JList();
        listOfFiles = new String[]{"Ubuntu.iso", "Movie.mp4"};
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
        fiRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                filesList.setListData(listOfFiles);
                System.out.println("Refresh is selected");
            }
        });
        fileMenu.add(fiRefresh);
        fiExit = new JMenuItem("Exit Application");
        fiExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("Exit is selected");
                frame.dispose();
            }
        });
        fileMenu.add(fiExit);
        topMenu.add(fileMenu);
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

}
