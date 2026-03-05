import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class GUI extends JFrame implements interfaceCallbackUI {

    private JPanel panelDialog;
    private final JTextArea textAreaTextEditor, textAreaRows;
    private final JLabel labelInfo;
    private JMenuItem save;
    private JMenuItem saveAs;
    private JMenuItem close;
    private JMenuItem undo;
    private JMenuItem redo;
    private JMenuItem search;
    private JDialog dialog;
    private JButton buttonSave;
    private JButton buttonSaveAs;
    private JButton buttonUndo;
    private JButton buttonRedo;
    private JButton buttonSearch;
    private JTextArea textAreaSearchField;
    private JTextArea textAreaReplaceField;
    private static final String title = "Text Editor";
    private JLabel labelResults;
    private final TextHandle textHandle;

    public GUI() {
        textHandle = new TextHandle();
        MyActionListener listener = new MyActionListener();

        // create textarea for editing
        JPanel panelMain = new JPanel(new BorderLayout());
        textAreaTextEditor = new JTextArea();
        textAreaTextEditor.setText("No File opened yet!");
        textAreaTextEditor.setEditable(false);
        textAreaTextEditor.addKeyListener(listener);
        panelMain.add(textAreaTextEditor);

        // create info field
        JPanel panelInfo = new JPanel(new FlowLayout());
        labelInfo = new JLabel("");
        labelInfo.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        panelInfo.add(labelInfo, BorderLayout.CENTER);
        panelInfo.setMaximumSize(new Dimension(getWidth(), 8));

        // create field for showing line numbers
        JPanel panelRows = new JPanel(new BorderLayout());
        textAreaRows = new JTextArea("1");
        textAreaRows.setVisible(true);
        textAreaRows.setEditable(false);
        textAreaRows.setFocusable(false);
        panelRows.add(textAreaRows);
        panelRows.setMaximumSize(new Dimension(8, getHeight()));


        // build main board
        setLayout(new BorderLayout(8, 8));
        setSize(600, 600);
        add(panelMain, BorderLayout.CENTER);
        add(panelInfo, BorderLayout.SOUTH);
        add(panelRows, BorderLayout.WEST);

        // init menu
        menu();
        // create symbol bar
        symbolBar();

        setVisible(true);
    }

    private void symbolBar() {

        MyActionListener listener = new MyActionListener();

        JPanel panelSymbols = new JPanel(new GridLayout(1, 8));

        // items
        Icon iconAdd = new ImageIcon(Objects.requireNonNull(GUI.class.getResource("/toolbarButtonGraphics/general/Add24.gif")));
        JButton buttonNew = new JButton(iconAdd);
        buttonNew.setActionCommand("New");
        buttonNew.setToolTipText("Create a new textfile");
        buttonNew.addActionListener(listener);
        panelSymbols.add(buttonNew);

        Icon iconOpen = new ImageIcon(Objects.requireNonNull(GUI.class.getResource("/toolbarButtonGraphics/general/Open24.gif")));
        JButton buttonOpen = new JButton(iconOpen);
        buttonOpen.setActionCommand("Open");
        buttonOpen.setToolTipText("Open a textfile");
        buttonOpen.addActionListener(listener);

        Icon iconSave = new ImageIcon(Objects.requireNonNull(GUI.class.getResource("/toolbarButtonGraphics/general/Save24.gif")));
        buttonSave = new JButton(iconSave);
        buttonSave.setActionCommand("Save");
        buttonSave.setToolTipText("Save changes");
        buttonSave.addActionListener(listener);
        buttonSave.setEnabled(false);

        Icon iconSaveAs = new ImageIcon(Objects.requireNonNull(GUI.class.getResource("/toolbarButtonGraphics/general/SaveAs24.gif")));
        buttonSaveAs = new JButton(iconSaveAs);
        buttonSaveAs.setActionCommand("Save As");
        buttonSaveAs.setToolTipText("Save changes in selectable file");
        buttonSaveAs.addActionListener(listener);
        buttonSaveAs.setEnabled(false);

        Icon iconUndo = new ImageIcon(Objects.requireNonNull(GUI.class.getResource("/toolbarButtonGraphics/general/Undo24.gif")));
        buttonUndo = new JButton(iconUndo);
        buttonUndo.setActionCommand("Undo");
        buttonUndo.setToolTipText("Undo last change");
        buttonUndo.addActionListener(listener);
        buttonUndo.setEnabled(false);

        Icon iconRedo = new ImageIcon(Objects.requireNonNull(GUI.class.getResource("/toolbarButtonGraphics/general/Redo24.gif")));
        buttonRedo = new JButton(iconRedo);
        buttonRedo.setActionCommand("Redo");
        buttonRedo.setToolTipText("Redo last Undo");
        buttonRedo.addActionListener(listener);
        buttonRedo.setEnabled(false);

        Icon iconSearch = new ImageIcon(Objects.requireNonNull(GUI.class.getResource("/toolbarButtonGraphics/general/Find24.gif")));
        buttonSearch = new JButton(iconSearch);
        buttonSearch.setActionCommand("Search");
        buttonSearch.setToolTipText("Search a text");
        buttonSearch.addActionListener(listener);
        buttonSearch.setEnabled(false);

        panelSymbols.add(buttonNew);
        panelSymbols.add(buttonOpen);
        panelSymbols.add(buttonSave);
        panelSymbols.add(buttonSaveAs);
        panelSymbols.add(buttonUndo);
        panelSymbols.add(buttonRedo);
        panelSymbols.add(buttonSearch);

        super.add(panelSymbols, BorderLayout.NORTH);
    }

    private void menu() {
        JMenuBar menuBar = new JMenuBar();

        // main folders
        JMenu menuFile = new JMenu("File");
        JMenu menuEdit = new JMenu("Edit");
        JMenu menuSearch = new JMenu("Search");

        MyActionListener listener = new MyActionListener();

        // items
        JMenuItem aNew = new JMenuItem("New");
        aNew.setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.CTRL_DOWN_MASK));
        aNew.setToolTipText("Create a new textfile");
        aNew.addActionListener(listener);

        JMenuItem open = new JMenuItem("Open");
        open.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));
        open.setToolTipText("Open a textfile");
        open.addActionListener(listener);

        save = new JMenuItem("Save");
        save.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));

        save.setToolTipText("Save changes");
        save.addActionListener(listener);
        save.setEnabled(false);

        saveAs = new JMenuItem("Save As");
        //saveAs.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));
        saveAs.setToolTipText("Save changes in selectable file");
        saveAs.addActionListener(listener);
        saveAs.setEnabled(false);

        close = new JMenuItem("Close");
        close.setAccelerator(KeyStroke.getKeyStroke('Q', InputEvent.CTRL_DOWN_MASK));
        close.setToolTipText("Close current textfile");
        close.addActionListener(listener);
        close.setEnabled(false);

        undo = new JMenuItem("Undo");
        undo.setAccelerator(KeyStroke.getKeyStroke('Z', InputEvent.CTRL_DOWN_MASK));
        undo.setToolTipText("Undo last change");
        undo.addActionListener(listener);
        undo.setEnabled(false);

        redo = new JMenuItem("Redo");
        redo.setAccelerator(KeyStroke.getKeyStroke('Y', InputEvent.CTRL_DOWN_MASK));
        redo.setToolTipText("Redo last Undo");
        redo.addActionListener(listener);
        redo.setEnabled(false);

        search = new JMenuItem("Search");
        search.setAccelerator(KeyStroke.getKeyStroke('F', InputEvent.CTRL_DOWN_MASK));
        search.setToolTipText("Search a text");
        search.addActionListener(listener);
        search.setEnabled(false);

        // pack items to mein folders
        menuFile.add(aNew);
        menuFile.add(open);
        menuFile.add(save);
        menuFile.add(saveAs);
        menuFile.add(close);

        menuEdit.add(undo);
        menuEdit.add(redo);

        menuSearch.add(search);

        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        menuBar.add(menuSearch);

        this.setJMenuBar(menuBar);
    }

    class MyActionListener implements ActionListener, KeyListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            //predefined actions (menubar & shortcuts)
            actionMade(e, null);
        }

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {}

        @Override
        public void keyReleased(KeyEvent e) {
            // check keyevents for constant update temporary changes
            if (e.getModifiersEx() != InputEvent.CTRL_DOWN_MASK)
                actionMade(null, e);
        }
    }

    public void actionMade (ActionEvent e, KeyEvent k){
        if (e != null) {
            if (e.getActionCommand().equals("New")) {
                textHandle.newFile(this);
                setTitle(textHandle.getFileName(title));
            }

            if (e.getActionCommand().equals("Open")) {
                textHandle.openFile(this);
                setTitle(textHandle.getFileName(title));
            }

            if (e.getActionCommand().equals("Save")) {
                textHandle.saveFile(this);
                setTitle(textHandle.getFileName(title));
            }

            if (e.getActionCommand().equals("Save As")) {
                textHandle.saveFileAs(this);
                setTitle(textHandle.getFileName(title));
            }

            if (e.getActionCommand().equals("Close")) {
                textHandle.closeFile(this);
                setTitle(textHandle.getFileName(title));
            }

            if (e.getActionCommand().equals("Undo")) {
                textHandle.undoTempLastChange(this);
                setTitle(textHandle.getFileName(title));
            }

            if (e.getActionCommand().equals("Redo")) {
                textHandle.redoTempLastChange(this);
                setTitle(textHandle.getFileName(title));
            }

            if (e.getActionCommand().equals("Search")) {
                // open dialog
                searchDialog();
            }

            if (e.getActionCommand().equals("Search Text")) {
                textHandle.searchText(textAreaSearchField.getText(), this);
                if (textAreaReplaceField == null & textHandle.getHitAvailable())
                    replaceDialog();
            }
            if (e.getActionCommand().equals(">")) {
                textHandle.setNextHit(textAreaSearchField.getText(), this);
            }
            if (e.getActionCommand().equals("<")) {
                textHandle.setBeforeHit(textAreaSearchField.getText(), this);
            }
            if (e.getActionCommand().equals("Replace")) {
                textHandle.replaceText(textAreaReplaceField.getText(), textAreaSearchField.getText(), this);
                setTitle(textHandle.getFileName(title));
            }
        } else {
            // update temporary text
            textHandle.textEdited(textAreaTextEditor.getText(), this);
            setTitle(textHandle.getFileName(title));
        }
    }

    public void searchDialog(){

        // Reset current search results for new searching init
        textHandle.resetSearch();
        if (textAreaReplaceField != null)
            textAreaReplaceField = null;

        MyActionListener listenerSearch = new MyActionListener();
        textAreaSearchField = new JTextArea();

        labelResults = new JLabel("");

        JButton buttonSearch = new JButton("Search");
        buttonSearch.setActionCommand("Search Text");
        buttonSearch.addActionListener(listenerSearch);

        JButton buttonBefore = new JButton("<");
        buttonBefore.addActionListener(listenerSearch);

        JButton buttonNext = new JButton(">");
        buttonNext.addActionListener(listenerSearch);


        textAreaSearchField.setText(textAreaTextEditor.getSelectedText());
        textAreaSearchField.setLineWrap(true);

        dialog = new JDialog();
        panelDialog = new JPanel();

        dialog.setLayout(new BorderLayout());
        dialog.setTitle("Search");

        panelDialog.setLayout(new GridLayout(2,3));
        panelDialog.add(textAreaSearchField);
        panelDialog.add(buttonSearch);
        panelDialog.add(labelResults);
        panelDialog.add(buttonBefore);
        panelDialog.add(buttonNext);
        panelDialog.add(new Label());

        dialog.add(panelDialog, BorderLayout.CENTER);
        dialog.add(new Label(), BorderLayout.WEST);
        dialog.add(new Label(), BorderLayout.SOUTH);
        dialog.add(new Label(), BorderLayout.EAST);
        dialog.add(new Label(), BorderLayout.NORTH);
        dialog.pack();
        dialog.show();
    }

    public void replaceDialog() {
        MyActionListener listenerSearch = new MyActionListener();

        textAreaReplaceField = new JTextArea();

        JButton buttonReplace = new JButton("Replace");
        buttonReplace.addActionListener(listenerSearch);

        panelDialog.setLayout(new GridLayout(3,3));
        panelDialog.add(buttonReplace);
        panelDialog.add(textAreaReplaceField);
        dialog.pack();
    }

    @Override
    public void markText(int selectedStart, int selectedEnd){
        textAreaTextEditor.setSelectionStart(selectedStart);
        textAreaTextEditor.setSelectionEnd(selectedEnd);
    }

    @Override
    public void setCurrentText(){
        textAreaTextEditor.setText(textHandle.getCurrTextString());
    }

    @Override
    public void setSearchHits(int currHit, int numHits){
        labelResults.setText(currHit + " / " + numHits);
    }

    @Override
    public void undoAvailable(boolean available){
        undo.setEnabled(available);
        buttonUndo.setEnabled(available);
    }

    @Override
    public void redoAvailable(boolean available){
        redo.setEnabled(available);
        buttonRedo.setEnabled(available);
    }

    @Override
    public void fileHandlingAvailable(boolean available){
        save.setEnabled(available);
        saveAs.setEnabled(available);
        close.setEnabled(available);
        search.setEnabled(available);
        buttonSave.setEnabled(available);
        buttonSaveAs.setEnabled(available);
        buttonSearch.setEnabled(available);
    }

    @Override
    public void setPanelInfo(){
        labelInfo.setText((String.valueOf(textHandle.getNumLines())) + " columns, " + (String.valueOf(textHandle.getNumWords())) + " words");
    }

    @Override
    public void setPanelRows(){
        textAreaRows.setText(textHandle.getRowNumbers());
    }

    @Override
    public void actualizeWritable(){
        textAreaTextEditor.setEditable(textHandle.getWritable());
    }

    // catching system function closing event
    protected void processWindowEvent(final WindowEvent e) {
        //super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            textHandle.closeFile(this);
            setTitle(textHandle.getFileName(title));
            // prevent unsaved changes
            if (!textHandle.fileChangeMade()) {
                super.processWindowEvent(e);
                System.exit(0);
            }
        }
    }
}