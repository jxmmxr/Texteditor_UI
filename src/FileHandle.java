import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FileHandle {
    private String fullPathFile = "";
    private String fileName = "";
    private BufferedReader fileReader;
    private BufferedWriter fileWriter;
    private final static String filetype = "txt";
    private static final int APPROVE_OPTION = 0;
    private static final int DIVERSE_OPTION = 1;
    private static final int CANCEL_OPTION = 2;
    private boolean writable = false;

    // methods
    public void newFile(interfaceCallbackUI cbUI, interfaceCallbackText cbText) {
        if (cbText.fileChangeMade())
            switch (dialogRequestSave()){
                case APPROVE_OPTION: saveFile(cbUI, cbText);
                case CANCEL_OPTION: return;
            }
        if (dialogNew() == DIVERSE_OPTION) {
            return;
        }
        //Update text, UI, helpers
        cbText.setCurrTextMap("");
        cbUI.setPanelInfo();
        cbUI.setPanelRows();
        cbUI.setCurrentText();
        setWritable(true);
        cbUI.fileHandlingAvailable(true);
        actualizeWritable(getWritable(), cbUI);
    }

    public void openFile(interfaceCallbackUI cbUI, interfaceCallbackText cbText) {
        if (cbText.fileChangeMade()){
            switch (dialogRequestSave()){
                case APPROVE_OPTION: saveFile(cbUI, cbText);
                case CANCEL_OPTION: return;
            }
        }
        if (dialogOpen() == CANCEL_OPTION)
            return;
        //Update text, UI, helpers
        cbText.setCurrTextMap(readFile(new File(fullPathFile)));
        cbUI.setPanelInfo();
        cbUI.setPanelRows();
        cbUI.setCurrentText();
        cbText.saveCurrText();
        setWritable(true);
        cbUI.fileHandlingAvailable(true);
        actualizeWritable(getWritable(), cbUI);
    }

    public void closeFile(interfaceCallbackUI cbUI, interfaceCallbackText cbText) {
        if (cbText.fileChangeMade()){
            switch (dialogRequestSave()){
                case APPROVE_OPTION: saveFile(cbUI, cbText);
                case CANCEL_OPTION: return;
            }
        }
        assert fullPathFile != null;
        //Update text, UI, helpers
        fullPathFile = "";
        cbText.setCurrTextMap("No File opened yet!");
        cbUI.setPanelInfo();
        cbUI.setPanelRows();
        cbText.saveCurrText();
        cbUI.setCurrentText();
        setWritable(false);
        cbUI.fileHandlingAvailable(false);
        actualizeWritable(getWritable(), cbUI);
    }

    public void saveFile(interfaceCallbackUI cbUI, interfaceCallbackText cbText) {
        if (cbText.fileChangeMade()){
            if (fullPathFile.isEmpty()) {
                saveFileAs(cbUI, cbText);
                return;
            }
            writeFile(cbText.getCurrTextString(), new File(fullPathFile));
            cbText.saveCurrText();
        }
    }

    public void saveFileAs(interfaceCallbackUI cbUI, interfaceCallbackText cbText) {
        if (!getWritable())
            return;
        do {
            switch (dialogSaveAs()){
                case APPROVE_OPTION: saveFile(cbUI, cbText);
                case CANCEL_OPTION: return;
            }
        } while (true);
    }

    // helper methods
    public void actualizeWritable(boolean writable, interfaceCallbackUI cbUI) {
        cbUI.actualizeWritable();
    }

    private void setWritable(boolean writable) {
        this.writable = writable;
    }

    public boolean getWritable() {
        return writable;
    }

    public String getFileName(String title, interfaceCallbackText cbText){
        // building headline of editor,
        if (writable) {
            if (fullPathFile == null)
                return title + "  (unsaved file)";
            if (fullPathFile.isEmpty())
                return title + "  (unsaved file)";
            if (cbText.fileChangeMade()) {
                return title + " (unsaved changes) [" + fullPathFile + "]";
            } else {
                return title + "  [" + fullPathFile + "]";
            }
        } else {
            return title;
        }
    }

    private void writeFile(String text, File file){
        try {
            fileWriter = new BufferedWriter(new FileWriter(file));
            fileWriter.write(text);
            fileWriter.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex); // not handled, because no usecases known
        }
    }

    private String readFile(File file){
        List fileText;
        try {
            assert file != null;
            fileReader = new BufferedReader(new FileReader(file));
            fileText = new ArrayList<>(fileReader.readAllLines());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        StringBuilder tmpString = new StringBuilder();
        boolean flag = false;
        for (Object o : fileText){
            if (flag)
                tmpString.append("\n");
            flag = true;
            tmpString.append(String.valueOf(o));
        }
        return String.valueOf(tmpString);
    }

    private String fileTypeCorrect (String fullNameWithPath){
        int i = fileName.lastIndexOf('.');
        if (i <= 0)
            return fullNameWithPath + "." + filetype;
        return fullNameWithPath;
    }

    private static class FileFiltertxt extends FileFilter implements java.io.FileFilter{
        @Override
        public boolean accept(File f) {
            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');

            if (i > 0 && i < s.length() - 1)
                ext = s.substring(i + 1).toLowerCase();

            return filetype.equals(ext);
        }

        @Override
        public String getDescription() {
            return "Text files only";
        }
    }

    // - dialog windows:
    // 0: Open                          2: Cancel
    private int dialogNew(){
        JFileChooser fc = new JFileChooser();
        JOptionPane optionPane = new JOptionPane();
        optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
        optionPane.setOptionType(JOptionPane.YES_NO_OPTION);
        optionPane.setMessage("Do you want to create a new file?");
        JDialog dialog = optionPane.createDialog(fc, "New file?");
        dialog.show();
        return Integer.parseInt(optionPane.getValue().toString());
    }

    // 0: Open                          2: Cancel
    private int dialogOpen(){
        JFileChooser fc = new JFileChooser();;
        String fileFolder;
        fileFolder = fullPathFile;
        JComponent.setDefaultLocale(Locale.of(fileFolder));

        FileFiltertxt fileFiltertxt = new FileFiltertxt();
        fc.setFileFilter(fileFiltertxt);

        int returnVal = fc.showOpenDialog(null);

        //check selected option
        File file;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = new File(fc.getSelectedFile().toURI());
            fullPathFile = file.getPath();
            fileName = file.getName();
            fc.setSelectedFile(null);
        }
        return switch (returnVal) {
            case JFileChooser.CANCEL_OPTION -> 2;
            default -> 0;
        };
    }

    // 0: Save      1: Repeat           2: Cancel
    private int dialogSaveAs(){
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.CANCEL_OPTION)
            returnVal = 2;
        //check selected option
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fullPathFile = fileTypeCorrect(String.valueOf(fc.getSelectedFile()));
            File file = new File(fullPathFile);
            //Optional: overwrite existing file
            if (file.exists()) {
                JOptionPane optionPane = new JOptionPane();
                optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
                optionPane.setOptionType(JOptionPane.YES_NO_OPTION);
                optionPane.setMessage("File already exists, do you want to overwrite it?");
                JDialog dialog = optionPane.createDialog(fc, "file conflict!");
                dialog.show();
                returnVal = Integer.parseInt(optionPane.getValue().toString());
                if (!(returnVal == JOptionPane.YES_OPTION))
                    returnVal = 1;
            }
            fileName = String.valueOf(fc.getName());
            fc.setSelectedFile(null);
        }
        return returnVal;
    }

    // 0: Save      1: Ignore           2: Cancel
    private int dialogRequestSave(){
        JFileChooser fc = new JFileChooser();
        JOptionPane optionPane = new JOptionPane();
        optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
        optionPane.setOptionType(JOptionPane.YES_NO_CANCEL_OPTION);
        optionPane.setMessage("File content changed.\ndo you want to save changes?");
        JDialog dialog = optionPane.createDialog(fc, "Save changes?");
        dialog.show();
        return Integer.parseInt(optionPane.getValue().toString());
    }
}
