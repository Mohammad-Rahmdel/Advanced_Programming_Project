import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

/**
 *
 * This class handles GUI
 */
class Graphix {
    //this field holds information about files
    private ArrayList<String[]> filesInfo = new ArrayList<>();

    //each user has a port to interact with admin and others
    private int GUIPort;

    //unique id for each user
    private int id;

    //main frame of GUI
    private JFrame frame;
    //central panel of form
    private JPanel mainPanel;
    //this panel contains list and tools on the left side
    private JPanel listPanel;
    //this panel contains preview and properties tab
    private JPanel filePanel;
    private JTabbedPane fileTabs;
    private JPanel previewTab;
    private JPanel propertiesTab;

    //bottom panel containing 4 buttons
    private JPanel buttonsPanel;
    private JButton downloadButton;
    private JButton uploadButton;
    private JButton renameButton;
    private JButton deleteButton;

    //list of files to be shown in listPanel
    private JList filesList;
    private String[] listOfFiles;
    private JScrollPane scrollBar1;

    //top menu bar
    private JMenuBar topMenu;
    private JMenu fileMenu;
    private JMenu helpMenu;

    // these fields will be shown in properties tab of the selected file
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

    //options to be shown in tools menu
    private JMenuItem fiRefresh;
    private JMenuItem fiExit;


    /**
     *
     * @param userId is a unique number given to each user from terminal
     */
    public Graphix(int userId){
        //giving ports
        this.id = userId;
        GUIPort = 12000 + id;
        frame = new JFrame();
        frame.setTitle("User: " + id);
        //setting size of the application
        frame.setSize(900,600);

        //initialization
        mainPanel = new JPanel(new BorderLayout());
        listPanel = new JPanel(new BorderLayout());
        listPanel.setMinimumSize(new Dimension(400, 600));
        filePanel = new JPanel(new BorderLayout());

        mainPanel.add(listPanel, BorderLayout.WEST);
        mainPanel.add(filePanel, BorderLayout.CENTER);


        //properties panel set up
        labelName = new JLabel("File name: ");
        nameField = new JTextField("");
        nameField.setEditable(false);

        labelExt = new JLabel("File extension: ");
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

        //grid layout to show items properly
        propertiesPanel = new JPanel(new GridLayout(8,2));
        propertiesPanel.setMaximumSize(new Dimension(200,500));
        //adding items to panel
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

        //in order to switch between image preview and text preview
        CardLayout cardLayout = new CardLayout();
        previewTab = new JPanel(cardLayout);
        JPanel imageTab = new JPanel(new BorderLayout());
        JPanel textTab = new JPanel(new BorderLayout());
        previewTab.add(imageTab);
        previewTab.add(textTab);
        previewTab.setName("Preview");
        propertiesTab = new JPanel(new BorderLayout());
        propertiesTab.setName("Properties");

        propertiesTab.add(propertiesPanel, BorderLayout.WEST);


        fileTabs.add(previewTab);
        fileTabs.add(propertiesTab);

        //handling download
        downloadButton = new JButton("Download");
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if (filesList.getSelectedIndex() >= 0) {

                    JOptionPane.showMessageDialog(null, "File Downloaded successfully",
                            "" + "Download Response", JOptionPane.INFORMATION_MESSAGE);


                    String fileName = listOfFiles[filesList.getSelectedIndex()];
                    String userId = "" + id;
                    Socket socketDownload = null;
                    DataOutputStream out = null;
                    try {
                        socketDownload = new Socket("localhost", 9999);
                        out = new DataOutputStream(socketDownload.getOutputStream());
                    } catch (UnknownHostException e){} catch (IOException e){}
                    String request = "download " + fileName + " " + userId;
                    try {
                        out.writeUTF(request);
                        out.close();
                        socketDownload.close();
                    } catch (IOException e){}

                }




            }
        });


        //handling upload
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
                    JButton submitBtn = new JButton("Submit");
                    dialog.add(submitBtn, BorderLayout.SOUTH);
                    submitBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            System.out.println("number of partitions :"+ noParts.getText());
                            dialog.dispose();
                            String fileName = j.getSelectedFile().getName();
                            String userId = "" + id;
                            String directory = j.getSelectedFile().getAbsolutePath();
                            directory = directory.substring(0,(directory.length() - fileName.length()));
                            String parts = noParts.getText();



                            Socket socketUpload = null;
                            DataOutputStream out = null;
                            try {
                                socketUpload = new Socket("localhost", 9999);
                                out = new DataOutputStream(socketUpload.getOutputStream());
                            } catch (UnknownHostException e){} catch (IOException e){}
                            String request = "upload " + fileName + " " + userId +
                                    " " + directory + " " + parts;
                            try {
                                out.writeUTF(request);
                                out.close();
                                socketUpload.close();
                            } catch (IOException e){}


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

        //handling rename
        renameButton = new JButton("Rename");
        renameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(filesList.getSelectedIndex() >= 0) {
                    JDialog dialog = new JDialog(frame, "Rename File");
                    //dialog.set  TODO setPosition
                    dialog.setLayout(new BorderLayout());
                    dialog.add(new JLabel("new Name:"), BorderLayout.WEST);
                    JTextField newName = new JTextField();
                    newName.setPreferredSize(new Dimension(200, 25));
                    dialog.add(newName, BorderLayout.EAST);
                    JButton submitBtn = new JButton("Submit");
                    dialog.add(submitBtn, BorderLayout.SOUTH);
                    submitBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            System.out.println("current name:" + listOfFiles[filesList.getSelectedIndex()]);
                            System.out.println("new name:" + newName.getText());
                            System.out.println("user id:" + id);
                            dialog.dispose();

                            String fileName = listOfFiles[filesList.getSelectedIndex()];
                            String userId = "" + id;
                            String newNameText = newName.getText();

                            Socket socketRename = null;
                            DataOutputStream out = null;
                            try {
                                socketRename = new Socket("localhost", 9999);
                                out = new DataOutputStream(socketRename.getOutputStream());
                            } catch (UnknownHostException e) {
                            } catch (IOException e) {
                            }
                            String request = "rename " + fileName + " " + userId +
                                    " " + newNameText;
                            try {
                                out.writeUTF(request);
                                out.close();
                                socketRename.close();
                            } catch (IOException e) {}



                            Socket guiSocket = null;
                            ServerSocket guiSSocket = null;
                            try {
                                guiSSocket = new ServerSocket(GUIPort);
                                System.out.println("Rename listening to " + GUIPort);
                            } catch (IOException e) {
                                System.out.println("Rename error = " + e);
                            }
                            try {
                                System.out.println("Rename accepting ...");
                                guiSocket = guiSSocket.accept();
                                System.out.println("Rename accepted");
                            } catch (IOException e) {
                                System.out.println("Rename accepting error = " + e);
                            }
                            String response = "nothing";
                            try {
                                DataInputStream in = new DataInputStream(guiSocket.getInputStream());
                                response = in.readUTF();
                                guiSocket.close();
                                guiSSocket.close();
                            }catch (IOException e) {}
                            JOptionPane.showMessageDialog(null, response, "" + "Rename Response",
                                    JOptionPane.INFORMATION_MESSAGE);


                        }
                    });
                    dialog.setSize(400, 100);
                    dialog.setVisible(true);
                }

            }
        });

        //handling delete
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (filesList.getSelectedIndex() >= 0) {
                    int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this file?", "Warning", 0);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        System.out.println("Yes is pressed");
                        System.out.println("file name:" + listOfFiles[filesList.getSelectedIndex()]);
                        System.out.println("user id: " + id);

                        String userId = "" + id;
                        String fileName = listOfFiles[filesList.getSelectedIndex()];

                        Socket socketDelete = null;
                        DataOutputStream out = null;
                        try {
                            socketDelete = new Socket("localhost", 9999);
                            out = new DataOutputStream(socketDelete.getOutputStream());
                        } catch (UnknownHostException e){} catch (IOException e){}
                        String request = "delete " + fileName + " " + userId;
                        try {
                            out.writeUTF(request);
                            out.close();
                            socketDelete.close();
                        } catch (IOException e){}



                        Socket guiSocket = null;
                        ServerSocket guiSSocket = null;
                        try {
                            guiSSocket = new ServerSocket(GUIPort);
                        } catch (IOException e) {}
                        try {
                            guiSocket = guiSSocket.accept();
                        } catch (IOException e) {}
                        String response = "nothing";
                        try {
                            DataInputStream in = new DataInputStream(guiSocket.getInputStream());
                            response = in.readUTF();
                            guiSocket.close();
                            guiSSocket.close();
                        }catch (IOException e) {}
                        JOptionPane.showMessageDialog(null, response, "" + "Delete Response",
                                JOptionPane.INFORMATION_MESSAGE);


                    } else {
                        System.out.println("Cancel deleting file");
                    }
                }
            }
        });

        //in order to show buttons in a line
        buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.add(downloadButton);
        buttonsPanel.add(uploadButton);
        buttonsPanel.add(renameButton);
        buttonsPanel.add(deleteButton);
        filePanel.add(buttonsPanel, BorderLayout.SOUTH);

        //managing list of files
        filesList = new JList();
        listOfFiles = new String[]{};
        filesList.setListData(listOfFiles);

        //handling when the user clicks on a list item
        filesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {

                if (!listSelectionEvent.getValueIsAdjusting()) {    //This line prevents double events
                    String selected = "";

                    try {
                        if(filesList!= null && filesList.getSelectedValue() != null){
                            selected = filesList.getSelectedValue().toString();

                            File current = new File(getDirectory(selected) + "/" + selected);
                            nameField.setText(current.getName());
                            extField.setText(getExt(current.getName()));

                            try {
                                BasicFileAttributes attr = Files.readAttributes(current.toPath(), BasicFileAttributes.class);

                                sizeField.setText(attr.size()+" B");
                                partsField.setText(""+getDistribution(selected).split(",").length/2);
                                distField.setText("[ "+ getDistribution(selected)+ " ]");
                                ownerField.setText(getOwner(selected));

                                String x = attr.creationTime().toString();
                                String y = attr.lastAccessTime().toString();
                                createDateField.setText(x.substring(0,10) + " " + x.substring(12,19));
                                accessField.setText(y.substring(0,10) + " " + y.substring(12,19));
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }

                        }
                    } catch (NullPointerException e){
                        System.out.println("null pointer in GUI EXCEPTION = " + e);
                    }

                    System.out.println(selected);

                    if (extField.getText().equals("txt")||extField.getText().equals("docx")) {
                        cardLayout.last(previewTab);
                        String path = getDirectory(nameField.getText()) + "/" + nameField.getText();
                        try {
                            FileReader reader = new FileReader(path);
                            BufferedReader bufferedReader = new BufferedReader(reader);

                            JTextArea textArea= new JTextArea();
                            textTab.add(textArea);
                            textArea.read(bufferedReader,null);
                            bufferedReader.close();
                            textArea.requestFocus();
                            textArea.setEditable(false);

                        } catch (Exception e) {
                            System.out.println("unable to locate text file for preview");
                        }
                    } else if (extField.getText().equals("jpg") || extField.getText().equals("png")) {
                        cardLayout.first(previewTab);
                        String path = getDirectory(nameField.getText())+"/" + nameField.getText();
                        System.out.println(path);
                        try {

                            File file = new File(path);
                            BufferedImage image = ImageIO.read(file);
                            JLabel label = new JLabel(new ImageIcon(image));
                            imageTab.add(label);
                            label.requestFocus();
                        } catch (Exception e) {
                            System.out.println("unable to locate image file for preview");
                        }
                    }
                }
            }
        });

        listPanel.add(filesList);
        scrollBar1 = new JScrollPane(filesList);
        filesList.setFixedCellWidth(150);//preventing list from shrinking in width
        listPanel.add(scrollBar1, BorderLayout.WEST);

        topMenu = new JMenuBar();
        fileMenu =  new JMenu("Tools");
        fiRefresh = new JMenuItem("Refresh List");

        //manually refreshing list of files
        fiRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("Refresh is selected");

                Socket socketList = null;
                DataOutputStream outList = null;
                try {
                    socketList = new Socket("localhost", 8888);
                    outList = new DataOutputStream(socketList.getOutputStream());
                } catch (UnknownHostException e){} catch (IOException e){}
                String request = "send_list " + id;
                try {
                    outList.writeUTF(request);
                    outList.close();
                    socketList.close();
                } catch (IOException e){}



                Socket socketList2 = null;
                ServerSocket socketServerList = null;
                try {
                    socketServerList = new ServerSocket(13000 + id);
                } catch (IOException e) {}
                try {
                    socketList2 = socketServerList.accept();
                } catch (IOException e) {}
                String list = "";
                try {
                    DataInputStream in = new DataInputStream(socketList2.getInputStream());
                    list = in.readUTF();
                    socketList2.close();
                    socketServerList.close();
                }catch (IOException e) {}

                filesInfo.removeAll(filesInfo);
                //updating file info
                if(list.length() > 0) {
                    String[] splitterFiles = list.split("@");
                    for (String files : splitterFiles) {
                        String[] fileSplitter = files.split(" ");
                        String name = fileSplitter[0];
                        String directory = fileSplitter[1];
                        String distribution = fileSplitter[2];
                        String owner = fileSplitter[3];
                        String[] res = new String[4];
                        res[0] = name;
                        res[1] = directory;
                        res[2] = distribution;
                        res[3] = owner;
                        filesInfo.add(res);
                    }
                }

                String[] fileNames = new String[filesInfo.size()];
                for (int i = 0; i < filesInfo.size(); i++){
                    fileNames[i] = filesInfo.get(i)[0];
                }

                filesList.setListData(fileNames);
                listOfFiles = fileNames;

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
        helpMenu =  new JMenu("Help");
        topMenu.add(helpMenu);

        listPanel.add(topMenu, BorderLayout.NORTH);

        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }


//private methods to get file attributes
    private String getOwner(String fileName){
        for(String[] f : filesInfo){
            if (f[0].equals(fileName)){
                return f[3];
            }
        }
        return "";
    }

    private String getDistribution(String fileName){
        for(String[] f : filesInfo){
            if (f[0].equals(fileName)){
                return f[2];
            }
        }
        return "";
    }

    private String getDirectory(String fileName){
        for(String[] f : filesInfo){
            if (f[0].equals(fileName)){
                return f[1];
            }
        }
        return "";
    }

    private String getExt(String fileName) {
        return fileName.split("\\.")[fileName.split("\\.").length -1];
    }


}
