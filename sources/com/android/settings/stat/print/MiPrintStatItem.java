package com.android.settings.stat.print;

/* loaded from: classes2.dex */
public class MiPrintStatItem {
    private int mColorNum;
    private int mConnectPrinterNum;
    private int mCopiesNum;
    private int mHelpPage;
    private boolean mIsAlreadyStat;
    private int mNoConnectionToPrinter;
    private int mOrientationNum;
    private int mPaperSizeNum;
    private int mPrintFailNum;
    private int mPrintNum;
    private int mPrintPageNum;
    private int mPrinterBusy;
    private int mPrinterCheck;
    private int mPrinterDoorOpen;
    private int mPrinterJammed;
    private int mPrinterLowOnInk;
    private int mPrinterLowOnToner;
    private int mPrinterOffline;
    private int mPrinterOutOfInk;
    private int mPrinterOutOfPaper;
    private int mPrinterOutOfToner;
    private int mSearchPrintersNum;
    private int mSelectPrintButtonNum;
    private int mSupportPrinterPage;

    /* loaded from: classes2.dex */
    public static final class Builder {
        private int mColorNum;
        private int mConnectPrinterNum;
        private int mCopiesNum;
        private int mHelpPage;
        private boolean mIsAlreadyStat;
        private int mNoConnectionToPrinter;
        private int mOrientationNum;
        private int mPaperSizeNum;
        private int mPrintFailNum;
        private int mPrintNum;
        private int mPrintPageNum;
        private int mPrinterBusy;
        private int mPrinterCheck;
        private int mPrinterDoorOpen;
        private int mPrinterJammed;
        private int mPrinterLowOnInk;
        private int mPrinterLowOnToner;
        private int mPrinterOffline;
        private int mPrinterOutOfInk;
        private int mPrinterOutOfPaper;
        private int mPrinterOutOfToner;
        private int mSearchPrintersNum;
        private int mSelectPrintButtonNum;
        private int mSupportPrinterPage;

        public MiPrintStatItem build() {
            return new MiPrintStatItem(this);
        }

        public Builder setColorNum(int i) {
            this.mColorNum = i;
            return this;
        }

        public Builder setConnectPrinterNum(int i) {
            this.mConnectPrinterNum = i;
            return this;
        }

        public Builder setCopiesNum(int i) {
            this.mCopiesNum = i;
            return this;
        }

        public Builder setHelpPage(int i) {
            this.mHelpPage = i;
            return this;
        }

        public Builder setNoConnectionToPrinter(int i) {
            this.mNoConnectionToPrinter = i;
            return this;
        }

        public Builder setOrientationNum(int i) {
            this.mOrientationNum = i;
            return this;
        }

        public Builder setPaperSizeNum(int i) {
            this.mPaperSizeNum = i;
            return this;
        }

        public Builder setPrintFailNum(int i) {
            this.mPrintFailNum = i;
            return this;
        }

        public Builder setPrintNum(int i) {
            this.mPrintNum = i;
            return this;
        }

        public Builder setPrintPageNum(int i) {
            this.mPrintPageNum = i;
            return this;
        }

        public Builder setPrinterBusy(int i) {
            this.mPrinterBusy = i;
            return this;
        }

        public Builder setPrinterCheck(int i) {
            this.mPrinterCheck = i;
            return this;
        }

        public Builder setPrinterDoorOpen(int i) {
            this.mPrinterDoorOpen = i;
            return this;
        }

        public Builder setPrinterJammed(int i) {
            this.mPrinterJammed = i;
            return this;
        }

        public Builder setPrinterLowOnInk(int i) {
            this.mPrinterLowOnInk = i;
            return this;
        }

        public Builder setPrinterLowOnToner(int i) {
            this.mPrinterLowOnToner = i;
            return this;
        }

        public Builder setPrinterOffline(int i) {
            this.mPrinterOffline = i;
            return this;
        }

        public Builder setPrinterOutOfInk(int i) {
            this.mPrinterOutOfInk = i;
            return this;
        }

        public Builder setPrinterOutOfPaper(int i) {
            this.mPrinterOutOfPaper = i;
            return this;
        }

        public Builder setPrinterOutOfToner(int i) {
            this.mPrinterOutOfToner = i;
            return this;
        }

        public Builder setSearchPrintersNum(int i) {
            this.mSearchPrintersNum = i;
            return this;
        }

        public Builder setSelectPrintButtonNum(int i) {
            this.mSelectPrintButtonNum = i;
            return this;
        }

        public Builder setSupportPrinterPage(int i) {
            this.mSupportPrinterPage = i;
            return this;
        }
    }

    private MiPrintStatItem(Builder builder) {
        this.mIsAlreadyStat = false;
        this.mPrintPageNum = builder.mPrintPageNum;
        this.mSelectPrintButtonNum = builder.mSelectPrintButtonNum;
        this.mSearchPrintersNum = builder.mSearchPrintersNum;
        this.mConnectPrinterNum = builder.mConnectPrinterNum;
        this.mCopiesNum = builder.mCopiesNum;
        this.mOrientationNum = builder.mOrientationNum;
        this.mColorNum = builder.mColorNum;
        this.mPaperSizeNum = builder.mPaperSizeNum;
        this.mPrintNum = builder.mPrintNum;
        this.mPrintFailNum = builder.mPrintFailNum;
        this.mHelpPage = builder.mHelpPage;
        this.mSupportPrinterPage = builder.mSupportPrinterPage;
        this.mPrinterDoorOpen = builder.mPrinterDoorOpen;
        this.mPrinterJammed = builder.mPrinterJammed;
        this.mPrinterOutOfPaper = builder.mPrinterOutOfPaper;
        this.mPrinterCheck = builder.mPrinterCheck;
        this.mPrinterOutOfInk = builder.mPrinterOutOfInk;
        this.mPrinterOutOfToner = builder.mPrinterOutOfToner;
        this.mPrinterLowOnInk = builder.mPrinterLowOnInk;
        this.mPrinterLowOnToner = builder.mPrinterLowOnToner;
        this.mPrinterBusy = builder.mPrinterBusy;
        this.mPrinterOffline = builder.mPrinterOffline;
        this.mNoConnectionToPrinter = builder.mNoConnectionToPrinter;
        this.mIsAlreadyStat = builder.mIsAlreadyStat;
    }

    public int getColorNum() {
        return this.mColorNum;
    }

    public int getConnectPrinterNum() {
        return this.mConnectPrinterNum;
    }

    public int getCopiesNum() {
        return this.mCopiesNum;
    }

    public int getHelpPage() {
        return this.mHelpPage;
    }

    public int getNoConnectionToPrinter() {
        return this.mNoConnectionToPrinter;
    }

    public int getOrientationNum() {
        return this.mOrientationNum;
    }

    public int getPaperSizeNum() {
        return this.mPaperSizeNum;
    }

    public int getPrintFailNum() {
        return this.mPrintFailNum;
    }

    public int getPrintNum() {
        return this.mPrintNum;
    }

    public int getPrintPageNum() {
        return this.mPrintPageNum;
    }

    public int getPrinterBusy() {
        return this.mPrinterBusy;
    }

    public int getPrinterCheck() {
        return this.mPrinterCheck;
    }

    public int getPrinterDoorOpen() {
        return this.mPrinterDoorOpen;
    }

    public int getPrinterJammed() {
        return this.mPrinterJammed;
    }

    public int getPrinterLowOnInk() {
        return this.mPrinterLowOnInk;
    }

    public int getPrinterLowOnToner() {
        return this.mPrinterLowOnToner;
    }

    public int getPrinterOffline() {
        return this.mPrinterOffline;
    }

    public int getPrinterOutOfInk() {
        return this.mPrinterOutOfInk;
    }

    public int getPrinterOutOfPaper() {
        return this.mPrinterOutOfPaper;
    }

    public int getPrinterOutOfToner() {
        return this.mPrinterOutOfToner;
    }

    public int getSearchPrintersNum() {
        return this.mSearchPrintersNum;
    }

    public int getSelectPrintButtonNum() {
        return this.mSelectPrintButtonNum;
    }

    public int getSupportPrinterPage() {
        return this.mSupportPrinterPage;
    }

    public boolean isAlreadyStat() {
        return this.mIsAlreadyStat;
    }

    public String toString() {
        return "MiPrintStatItem{mPrintPageNum=" + this.mPrintPageNum + ", mSelectPrintButtonNum=" + this.mSelectPrintButtonNum + ", mSearchPrintersNum=" + this.mSearchPrintersNum + ", mConnectPrinterNum=" + this.mConnectPrinterNum + ", mCopiesNum=" + this.mCopiesNum + ", mOrientationNum=" + this.mOrientationNum + ", mColorNum=" + this.mColorNum + ", mPaperSizeNum=" + this.mPaperSizeNum + ", mPrintNum=" + this.mPrintNum + ", mPrintFailNum=" + this.mPrintFailNum + ", mHelpPage=" + this.mHelpPage + ", mSupportPrinterPage=" + this.mSupportPrinterPage + ", mPrinterDoorOpen=" + this.mPrinterDoorOpen + ", mPrinterJammed=" + this.mPrinterJammed + ", mPrinterOutOfPaper=" + this.mPrinterOutOfPaper + ", mPrinterCheck=" + this.mPrinterCheck + ", mPrinterOutOfInk=" + this.mPrinterOutOfInk + ", mPrinterOutOfToner=" + this.mPrinterOutOfToner + ", mPrinterLowOnInk=" + this.mPrinterLowOnInk + ", mPrinterLowOnToner=" + this.mPrinterLowOnToner + ", mPrinterBusy=" + this.mPrinterBusy + ", mPrinterOffline=" + this.mPrinterOffline + ", mNoConnectionToPrinter=" + this.mNoConnectionToPrinter + ", mIsAlreadyStat=" + this.mIsAlreadyStat + '}';
    }
}
