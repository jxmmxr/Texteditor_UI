import java.util.LinkedList;
import java.util.TreeMap;

public class TextHandle implements interfaceCallbackText {
    private final FileHandle fileHandle = new FileHandle();
    private String currText = "No File opened yet!";
    private String savedText = "No File opened yet!";
    private String searchedText;
    private int numLines = 0;
    private int numWords = 0;
    private final TreeMap<Integer, String> currTextMap = new TreeMap<>();
    private final LinkedList<String> lastChanges = new LinkedList<>();
    private final LinkedList<String> lastChangesUndo = new LinkedList<>();
    private final LinkedList<Integer> searchResult = new LinkedList<>();
    private int selectedResult;
    private int selectedStart = 0;
    private int selectedEnd = 0;

    // Methods
    public void newFile(interfaceCallbackUI cbUI) {
        fileHandle.newFile(cbUI, this);
    }

    public void openFile(interfaceCallbackUI cbUI) {
        fileHandle.openFile(cbUI, this);
    }

    public void closeFile(interfaceCallbackUI cbUI) {
        fileHandle.closeFile(cbUI, this);
    }

    public void saveFile(interfaceCallbackUI cbUI) {
        fileHandle.saveFile(cbUI, this);
    }

    public void saveFileAs(interfaceCallbackUI cbUI) {
        fileHandle.saveFileAs(cbUI, this);
    }

    public void searchText(String searchedText, interfaceCallbackUI cbUI) {
        if (searchedText == null) {
            cbUI.setSearchHits(0, 0);
            return;
        }
        // init helpers
        int lengthSearchedText = searchedText.length();
        int lengthText = currText.length();
        int index = 0;
        int checkIndex = lengthSearchedText;
        boolean tagMatch;
        searchResult.clear();
        if (lengthText < lengthSearchedText) {
            cbUI.setSearchHits(0, 0);
            return;
        }
        // start boyer & moore search
        while (index - 1 < lengthText - lengthSearchedText) {
            tagMatch = false;
            for (int i = lengthSearchedText - 1; i >= 0; i--) {
                // loop until one mismatch, or all matching -> set flag
                checkIndex = i + 1;
                if (!String.valueOf(currText.charAt(index + i)).equals(String.valueOf(searchedText.charAt(i))))
                    break;
                if (i == 0) {
                    // add match to linked list
                    searchResult.push(index);
                    index += lengthSearchedText;
                    tagMatch = true;
                }
            }
            if (!tagMatch) {
                int matchAt = -1;
                // check position of mismatch -> shift maximum of chars possible
                for (int i = lengthSearchedText - 1; i >= 0; i--) {
                    if (String.valueOf(currText.charAt(index + checkIndex - 1)).equals(String.valueOf(searchedText.charAt(i)))) {
                        matchAt = i;
                        break;
                    }
                }
                if (checkIndex - (matchAt + 1) < 1){
                    index++;
                } else {
                    index += checkIndex - (matchAt + 1);
                }
            }
        }
        this.searchedText = searchedText;
        // update results to UI
        if (getHitAvailable()) {
            setNextHit(searchedText, cbUI);
        } else {
            cbUI.setSearchHits(0, 0);
        }
    }

    public void resetSearch(){
        searchResult.clear();
        selectedStart = 0;
        selectedEnd = 0;
        selectedResult = 0;
    }

    public int getPosOfHit(int nr) {
        if (nr < searchResult.size())
            nr = searchResult.getFirst();
        if (nr > searchResult.size())
            nr = searchResult.getLast();
        return nr;
    }

    public boolean getHitAvailable() {
        return !searchResult.isEmpty();
    }

    public void setNextHit(String searchedText, interfaceCallbackUI cbUI) {
        // check if operation is possible
        if (!getHitAvailable() | !this.searchedText.equals(searchedText))
            searchText(searchedText, cbUI);
        if (!getHitAvailable())
            return;
        selectedResult--;
        // closing loop of next hit to the end
        if (searchResult.indexOf(searchResult.getFirst()) > selectedResult)
            selectedResult = searchResult.indexOf(searchResult.getLast());
        if (searchResult.indexOf(searchResult.getLast()) < selectedResult)
            selectedResult = searchResult.indexOf(searchResult.getFirst());
        selectedStart = searchResult.get(selectedResult);
        selectedEnd = searchResult.get(selectedResult) + searchedText.length();
        // update UI
        cbUI.setSearchHits(searchResult.size() - selectedResult, searchResult.size());
        cbUI.markText(selectedStart, selectedEnd);
    }

    public void setBeforeHit(String searchedText, interfaceCallbackUI cbUI) {
        // check if operation is possible
        if (!getHitAvailable() | !this.searchedText.equals(searchedText))
            searchText(searchedText, cbUI);
        if (!getHitAvailable())
            return;
        selectedResult++;
        // closing loop of next hit to the beginning
        if (searchResult.indexOf(searchResult.getLast()) < selectedResult)
            selectedResult = searchResult.indexOf(searchResult.getFirst());
        selectedStart = searchResult.get(selectedResult);
        selectedEnd = searchResult.get(selectedResult) + searchedText.length();
        // update UI
        cbUI.setSearchHits(searchResult.size() - selectedResult, searchResult.size());
        cbUI.markText(selectedStart, selectedEnd);
    }

    private boolean replaceTextSet(String searchedText) {
        // check if replace position is selected one
        if (!(getPosOfHit(selectedResult) == selectedStart &&
                getPosOfHit(selectedResult) == selectedEnd - searchedText.length())) {
            selectedStart = getPosOfHit(selectedResult);
            selectedEnd = getPosOfHit(selectedResult) + searchedText.length();
            return false;
        } else {
            return true;
        }
    }

    public void replaceText(String textReplacement, String searchedText, interfaceCallbackUI cbUI) {
        //check for null separate, avoiding runtime exception
        if (this.searchedText == null)
            searchText(searchedText, cbUI);
        if (!getHitAvailable() | !this.searchedText.equals(searchedText))
            searchText(searchedText, cbUI);
        if (replaceTextSet(searchedText)) {
            //replacing text & save for undo
            String tmpTextBefore = currText;
            String tmpText =
                    currText.substring(0, getPosOfHit(selectedResult)) +
                            textReplacement +
                            currText.substring(getPosOfHit(selectedResult) + searchedText.length());
            saveTempLastChange(tmpText);
            textEdited(tmpTextBefore, cbUI);
            saveTempLastChange(tmpText);
            cbUI.setCurrentText();
            // search again for updating results
            searchText(searchedText, cbUI);
        }
    }

    private void saveTempLastChange(String text) {
        //temporary saving text
        currText = text;
        lastChanges.push(currText);
        if (lastChanges.size() >= 50)
            lastChanges.removeLast();
        setCurrTextMap(text);
        lastChangesUndo.clear();
    }

    public void undoTempLastChange(interfaceCallbackUI cbUI) {
        String tmpText = currText;
        // undo last change, if possible and push for redo operation
        if (undoAvailableCheck()) {
            tmpText = lastChanges.pop();
            lastChangesUndo.push(currText);
            if (lastChangesUndo.size() >= 50)
                lastChangesUndo.removeLast();
        }
        cbUI.undoAvailable(undoAvailableCheck());
        cbUI.redoAvailable(redoAvailableCheck());
        setCurrTextMap(tmpText);
        cbUI.setCurrentText();
    }

    public void redoTempLastChange(interfaceCallbackUI cbUI) {
        String tmpText = currText;
        // redo last change, if possible and push for undo
        if (redoAvailableCheck()) {
            tmpText = lastChangesUndo.pop();
            lastChanges.push(currText);
            if (lastChanges.size() >= 50)
                lastChanges.removeLast();
        }
        cbUI.redoAvailable(redoAvailableCheck());
        cbUI.undoAvailable(undoAvailableCheck());
        setCurrTextMap(tmpText);
        cbUI.setCurrentText();
    }

    private boolean undoAvailableCheck() {
        return !lastChanges.isEmpty();
    }

    private boolean redoAvailableCheck() {
        return !lastChangesUndo.isEmpty();
    }

    public boolean fileChangeMade() {
        return !currText.equals(savedText);
    }

    public void setCurrTextMap(String text) {
        // put text in treemap for easier handling of linebreak
        int i = 1;
        String[] tmpString;
        this.currTextMap.clear();
        //split up and count linebreak
        do {
            tmpString = text.split("\n", i + 1);
            this.currTextMap.put(i, tmpString[i - 1]);
            i++;
        } while (i == tmpString.length);
        this.numLines = i - 1;
        //counting number of words
        numWords = 0;
        for (String s : tmpString) {
            String[] tmpWords = s.split(" ", i + 1);
            int tmpEmptyFields = 0;
            for (String s1 : tmpWords){
                if (s1.isEmpty()){
                    tmpEmptyFields++;
                }
            }
            this.numWords += tmpWords.length - tmpEmptyFields;
        }
        currText = text;
    }

    public void saveCurrText() {
        savedText = getCurrTextString();
    }

    public String getCurrTextString() {
        if (numLines == 0)
            return "";
        boolean flag = false;
        // building String for output from treemap
        StringBuilder tmpTextBuilder = new StringBuilder();
        for (int i = 1; i <= this.numLines; i++) {
            if (flag)
                tmpTextBuilder.append("\n");
            flag = true;
            tmpTextBuilder.append(this.currTextMap.get(i));
        }
        return String.valueOf(tmpTextBuilder);
    }

    public void textEdited(String text, interfaceCallbackUI cbUI) {
        if (text.equals(currText))
            return;
        saveTempLastChange(text);
        // check if saved text is equals current text -> update UI
        cbUI.undoAvailable(undoAvailableCheck());
        cbUI.setPanelInfo();
        cbUI.setPanelRows();
    }

    public String getFileName(String title) {
        return fileHandle.getFileName(title, this);
    }

    public boolean getWritable() {
        return fileHandle.getWritable();
    }

    public int getNumWords() {
        return numWords;
    }

    public int getNumLines() {
        return numLines;
    }

    public String getRowNumbers() {
        if (numLines == 0)
            return "";
        boolean flag = false;
        // build String for showing line numbers
        StringBuilder tmpTextBuilder = new StringBuilder();
        for (int i = 1; i <= this.numLines; i++) {
            if (flag)
                tmpTextBuilder.append("\n");
            flag = true;
            tmpTextBuilder.append(i);
        }
        return String.valueOf(tmpTextBuilder);
    }
}