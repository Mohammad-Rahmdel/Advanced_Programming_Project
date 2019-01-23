import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Graphix {

    private ArrayList<String[]> filesInfo = new ArrayList<>();
    private int GUIPort;
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
        GUIPort = 12000 + id;
        frame = new JFrame();
        frame.setTitle("" + id);
        frame.setSize(900,600);
        mainPanel = new JPanel(new BorderLayout());
        listPanel = new JPanel(new BorderLayout());
        listPanel.setMinimumSize(new Dimension(500, 700));
        filePanel = new JPanel(new BorderLayout());

        mainPanel.add(listPanel, BorderLayout.WEST);
        mainPanel.add(filePanel, BorderLayout.CENTER);

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
/*
                JFileChooser j = new JFileChooser();

                // invoke the showsOpenDialog function to show the save dialog
                int r = j.showSaveDialog(null);

                // if the user selects a file
                if (r == JFileChooser.APPROVE_OPTION)

                {
                    // set the label to the path of the selected file
                    System.out.println("file path:"+ j.getSelectedFile().getName());
                    System.out.println("user id :" + id);
                    String fileName = j.getSelectedFile().getName();
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
                // if the user cancelled the operation
                else
                    System.out.println("the user cancelled the operation");
            }
            */
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






        buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.add(downloadButton);
        buttonsPanel.add(uploadButton);
        buttonsPanel.add(renameButton);
        buttonsPanel.add(deleteButton);
        filePanel.add(buttonsPanel, BorderLayout.SOUTH);

        filesList = new JList();
        listOfFiles = new String[]{"Ubuntu.iso", "Movie.mp4", "test.txt", "q.txt"};
        filesList.setListData(listOfFiles);
        //filesList.add
        filesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                String selected = "";
                if(filesList.getSelectedValue().toString() != null)
                    selected = filesList.getSelectedValue().toString();
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
                //filesList.setListData(listOfFiles);
                System.out.println("Refresh is selected");


                //TODO *******************************************************************************
                //TODO *******************************************************************************
                //String[] list = {"a.txt", "b.mp4", "c.jpg", "d.zip"};
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

                //TODO *******************************************************************************
                //TODO *******************************************************************************
                String[] fileNames = new String[filesInfo.size()];
                for (int i = 0; i < filesInfo.size(); i++){
                    fileNames[i] = filesInfo.get(i)[0];
                }


                filesList.setListData(fileNames);
                listOfFiles = fileNames;
                //System.out.println("x = " + list);




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
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }



    public String getOwner(String fileName){
        for(String[] f : filesInfo){
            if (f[0].equals(fileName)){
                return f[3];
            }
        }
        return "";
    }

    public String getDistribution(String fileName){
        for(String[] f : filesInfo){
            if (f[0].equals(fileName)){
                return f[2];
            }
        }
        return "";
    }

    public String getDirectory(String fileName){
        for(String[] f : filesInfo){
            if (f[0].equals(fileName)){
                return f[1];
            }
        }
        return "";
    }

}


//
//class GUIMessageReceiver extends Thread{
//    private int port;
//
//    public GUIMessageReceiver(int port){
//        this.port = port;
//    }
//    public void run(){
//
//        Socket guiSocket = null;
//        ServerSocket guiSSocket = null;
//        try {
//            guiSSocket = new ServerSocket(port);
//        } catch (IOException e) {}
//        try {
//            guiSocket = guiSSocket.accept();
//        } catch (IOException e) {}
//        try {
//            DataInputStream in = new DataInputStream(guiSocket.getInputStream());
//            String response = in.readUTF();
//        }catch (IOException e) {}
//    }
//}