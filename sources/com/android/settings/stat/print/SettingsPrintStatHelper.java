package com.android.settings.stat.print;

import android.content.Context;
import android.util.Log;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.util.HashMap;

/* loaded from: classes2.dex */
public class SettingsPrintStatHelper {
    private static volatile SettingsPrintStatHelper mInstance;
    Context mContext;

    public SettingsPrintStatHelper(Context context) {
        this.mContext = context;
    }

    public static SettingsPrintStatHelper getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SettingsPrintStatHelper.class) {
                if (mInstance == null) {
                    mInstance = new SettingsPrintStatHelper(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    /* JADX WARN: Removed duplicated region for block: B:38:0x01d1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public com.android.settings.stat.print.MiPrintStatItem getMiPrintStatDataFromProvider() {
        /*
            Method dump skipped, instructions count: 469
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.stat.print.SettingsPrintStatHelper.getMiPrintStatDataFromProvider():com.android.settings.stat.print.MiPrintStatItem");
    }

    public void traceMiPrintEvent(boolean z) {
        MiPrintStatItem miPrintStatDataFromProvider = getMiPrintStatDataFromProvider();
        if (miPrintStatDataFromProvider == null || (!z && miPrintStatDataFromProvider.isAlreadyStat())) {
            Log.w("SettingsPrintStatHelper", "miprint statItem is null or statItem is already stat");
            return;
        }
        Log.d("SettingsPrintStatHelper", "traceMiPrintEvent MiPrintStatItem : " + miPrintStatDataFromProvider.toString());
        HashMap hashMap = new HashMap();
        hashMap.put("print_page", Integer.valueOf(miPrintStatDataFromProvider.getPrintPageNum()));
        hashMap.put("select_print_button", Integer.valueOf(miPrintStatDataFromProvider.getSelectPrintButtonNum()));
        hashMap.put("search_printers_num", Integer.valueOf(miPrintStatDataFromProvider.getSearchPrintersNum()));
        hashMap.put("connect_printer_num", Integer.valueOf(miPrintStatDataFromProvider.getConnectPrinterNum()));
        hashMap.put("copies_num", Integer.valueOf(miPrintStatDataFromProvider.getCopiesNum()));
        hashMap.put("orientation_num", Integer.valueOf(miPrintStatDataFromProvider.getOrientationNum()));
        hashMap.put("color_num", Integer.valueOf(miPrintStatDataFromProvider.getColorNum()));
        hashMap.put("paper_size_num", Integer.valueOf(miPrintStatDataFromProvider.getPaperSizeNum()));
        hashMap.put("print_num", Integer.valueOf(miPrintStatDataFromProvider.getPrintNum()));
        hashMap.put("print_fail_num", Integer.valueOf(miPrintStatDataFromProvider.getPrintFailNum()));
        hashMap.put("help_page", Integer.valueOf(miPrintStatDataFromProvider.getHelpPage()));
        hashMap.put("support_printer_page", Integer.valueOf(miPrintStatDataFromProvider.getSupportPrinterPage()));
        hashMap.put("printer_door_open", Integer.valueOf(miPrintStatDataFromProvider.getPrinterDoorOpen()));
        hashMap.put("printer_jammed", Integer.valueOf(miPrintStatDataFromProvider.getPrinterJammed()));
        hashMap.put("printer_out_of_paper", Integer.valueOf(miPrintStatDataFromProvider.getPrinterOutOfPaper()));
        hashMap.put("printer_check", Integer.valueOf(miPrintStatDataFromProvider.getPrinterCheck()));
        hashMap.put("printer_out_of_ink", Integer.valueOf(miPrintStatDataFromProvider.getPrinterOutOfInk()));
        hashMap.put("printer_out_of_toner", Integer.valueOf(miPrintStatDataFromProvider.getPrinterOutOfToner()));
        hashMap.put("printer_low_on_ink", Integer.valueOf(miPrintStatDataFromProvider.getPrinterLowOnInk()));
        hashMap.put("printer_low_on_toner", Integer.valueOf(miPrintStatDataFromProvider.getPrinterLowOnToner()));
        hashMap.put("printer_busy", Integer.valueOf(miPrintStatDataFromProvider.getPrinterBusy()));
        hashMap.put("printer_offline", Integer.valueOf(miPrintStatDataFromProvider.getPrinterOffline()));
        hashMap.put("no_connection_to_printer", Integer.valueOf(miPrintStatDataFromProvider.getNoConnectionToPrinter()));
        OneTrackInterfaceUtils.track("miprint_stat_data", hashMap);
    }
}
