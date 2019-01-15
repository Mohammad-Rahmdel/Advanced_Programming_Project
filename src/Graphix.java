import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    private JLabel labelName;
    private JTextField nameField;
    private JLabel labelExt;
    private JTextField extField;
    private JLabel labelSize;
    private JTextField sizeField;
    private JLabel labelParts;
    private JTextField partsField;

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
        extField = new JTextField("test.png");
        extField.setEditable(false);

        labelSize = new JLabel("Size: ");
        sizeField = new JTextField("test.png");
        sizeField.setEditable(false);

        labelParts = new JLabel("Partition: ");
        partsField = new JTextField("test.png");
        partsField.setEditable(false);

        JPanel propertiesPanel = new JPanel();
        propertiesPanel.add(labelName);
        propertiesPanel.add(nameField);
        propertiesPanel.add(labelExt);
        propertiesPanel.add(extField);
        propertiesPanel.add(labelSize);
        propertiesPanel.add(sizeField);
        propertiesPanel.add(labelParts);
        propertiesPanel.add(partsField);

        fileTabs = new JTabbedPane();
        filePanel.add(fileTabs, BorderLayout.CENTER);

        previewTab = new JPanel(new BorderLayout());
        previewTab.setName("Preview");
        propertiesTab = new JPanel(new BorderLayout());
        propertiesTab.setName("Properties");

        propertiesTab.add(propertiesPanel, BorderLayout.CENTER);


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
        renameButton = new JButton("Rename");
        deleteButton = new JButton("Delete");
        buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.add(downloadButton);
        buttonsPanel.add(duplicateButton);
        buttonsPanel.add(renameButton);
        buttonsPanel.add(deleteButton);
        filePanel.add(buttonsPanel, BorderLayout.SOUTH);

        filesList = new JList();
        listPanel.add(filesList);
        scrollBar1 = new JScrollBar();
        listPanel.add(scrollBar1, BorderLayout.WEST);

        topMenu = new JMenuBar();
        fileMenu =  new JMenu("File");
        JMenuItem fiRefresh = new JMenuItem("Refresh List");
        fileMenu.add(fiRefresh);
        JMenuItem fiExit = new JMenuItem("Exit Application");
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
