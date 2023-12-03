package androidx.slice.widget;

/* loaded from: classes.dex */
public class EventInfo {
    public int actionType;
    public int rowIndex;
    public int rowTemplateType;
    public int sliceMode;
    public int actionPosition = -1;
    public int actionIndex = -1;
    public int actionCount = -1;
    public int state = -1;

    public EventInfo(int sliceMode, int actionType, int rowTemplateType, int rowIndex) {
        this.sliceMode = sliceMode;
        this.actionType = actionType;
        this.rowTemplateType = rowTemplateType;
        this.rowIndex = rowIndex;
    }

    private static String actionToString(int action) {
        switch (action) {
            case 0:
                return "TOGGLE";
            case 1:
                return "BUTTON";
            case 2:
                return "SLIDER";
            case 3:
                return "CONTENT";
            case 4:
                return "SEE MORE";
            case 5:
                return "SELECTION";
            case 6:
                return "DATE_PICK";
            case 7:
                return "TIME_PICK";
            default:
                return "unknown action: " + action;
        }
    }

    private static String positionToString(int position) {
        if (position != 0) {
            if (position != 1) {
                if (position != 2) {
                    return "unknown position: " + position;
                }
                return "CELL";
            }
            return "END";
        }
        return "START";
    }

    private static String rowTypeToString(int type) {
        switch (type) {
            case -1:
                return "SHORTCUT";
            case 0:
                return "LIST";
            case 1:
                return "GRID";
            case 2:
                return "MESSAGING";
            case 3:
                return "TOGGLE";
            case 4:
                return "SLIDER";
            case 5:
                return "PROGRESS";
            case 6:
                return "SELECTION";
            case 7:
                return "DATE_PICK";
            case 8:
                return "TIME_PICK";
            default:
                return "unknown row type: " + type;
        }
    }

    public void setPosition(int actionPosition, int actionIndex, int actionCount) {
        this.actionPosition = actionPosition;
        this.actionIndex = actionIndex;
        this.actionCount = actionCount;
    }

    public String toString() {
        return "mode=" + SliceView.modeToString(this.sliceMode) + ", actionType=" + actionToString(this.actionType) + ", rowTemplateType=" + rowTypeToString(this.rowTemplateType) + ", rowIndex=" + this.rowIndex + ", actionPosition=" + positionToString(this.actionPosition) + ", actionIndex=" + this.actionIndex + ", actionCount=" + this.actionCount + ", state=" + this.state;
    }
}
