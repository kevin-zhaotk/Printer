package com.industry.printer.interceptor;

import android.content.Context;

import com.industry.printer.FileFormat.SystemConfigFile;
import com.printer.corelib.Debug;

/**
 *
 */
public class ExtendInterceptor {

    private static final int EXTEND_PARAM = 37;
    private Context mContext;

    public ExtendInterceptor(Context ctx) {
        mContext = ctx;
    }


    public ExtendStat getExtend() {
        SystemConfigFile config = SystemConfigFile.getInstance(mContext);
        int extend = config.getParam(EXTEND_PARAM);

        int base = extend/10;
        int ext = extend%10;
        if (base < 1 || ext <= 1) {
            // no extension
            return ExtendStat.NONE;
        }
        if (base == 1) {
            switch (ext) {
                case 1:
                    return ExtendStat.NONE;
                case 2:
                    return ExtendStat.EXT_1_TO_2;
                case 3:
                    return ExtendStat.EXT_1_TO_3;
                case 4:
                    return ExtendStat.EXT_1_TO_4;
                case 5:
                    return ExtendStat.EXT_1_TO_5;
                case 6:
                    return ExtendStat.EXT_1_TO_6;
                case 7:
                    return ExtendStat.EXT_1_TO_7;
                case 8:
                    return ExtendStat.EXT_1_TO_8;
                default:
                    return ExtendStat.NONE;
            }
        } else if (base == 2) {
            switch (ext) {

                case 4:
                    return ExtendStat.EXT_2_TO_4;
                case 6:
                    return ExtendStat.EXT_2_TO_6;
                case 8:
                    return ExtendStat.EXT_2_TO_8;
                default:
                    return ExtendStat.NONE;
            }
        } else {
            return ExtendStat.NONE;
        }
    }


    public enum ExtendStat {

        NONE(0,0),
        EXT_1_TO_2(1, 2),
        EXT_1_TO_3(1, 3),
        EXT_1_TO_4(1, 4),
        EXT_1_TO_5(1, 5),
        EXT_1_TO_6(1, 6),
        EXT_1_TO_7(1, 7),
        EXT_1_TO_8(1, 8),
        EXT_2_TO_4(2, 4),
        EXT_2_TO_6(2, 6),
        EXT_2_TO_8(2, 8);

        private int source;
        private int target;

        private ExtendStat(int source, int target) {
            this.source = source;
            this.target = target;
        }

        public int getScale() {
            if (this.equals(NONE)) {
                return 1;
            } else {
            	Debug.d("ExtendStat", "--->target: " + target +  "   source: " + source);
                return target/source;
            }
        }
    }
}