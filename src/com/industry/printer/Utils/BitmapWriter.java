package com.industry.printer.Utils;

public class BitmapWriter {

    /**
     * ����bmp��ָ����path��·���µ�ָ���ļ���picName�������pathΪnull���򱣴浽Ĭ��·��
     * @param bmp
     * @param path
     * @param picName
     */
    public static void saveBitmap(Bitmap bmp, String path, String picName)
    {
        if (path == null) {
            path = Configs.USB_ROOT_PATH;
        }
        File f = new File(path, picName);
        //File f = new File("/storage/external_storage/sda1", picName);
        if(f.exists())
        {
            f.delete();
        }
        try{
            FileOutputStream out = new FileOutputStream(f);
            bmp.compress(CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Debug.d(TAG, "PNG save ok");
        }catch(Exception e)
        {
            Debug.d(TAG, "save failed: "+e.getMessage());
        }
    }
}