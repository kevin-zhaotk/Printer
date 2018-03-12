package com.industry.printer.ui.CustomerDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.industry.printer.MessageTask;
import com.industry.printer.R;
import com.industry.printer.Utils.FileUtil;
import com.industry.printer.ui.CustomerAdapter.MessageListAdater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by kevin on 2018/3/8.
 */

public class MessageGroupsortDialog extends CustomerDialogBase implements View.OnClickListener {

    private ImageButton mUp;
    private ImageButton mDown;
    private ImageButton mOk;
    private ListView    mListview;
    private MessageListAdater mFileAdapter;
    LinkedList<Map<String, Object>> tlkList = new LinkedList<>();

    public MessageGroupsortDialog(Context context, ArrayList<String> tlks) {
        super(context);

        if (tlks != null) {
            for (String t : tlks) {
                Map<String, Object> m = new HashMap<>();
                m.put("title", t);
            }
        }
        mFileAdapter = new MessageListAdater(context,
                tlkList,
                R.layout.message_item_layout,
                new String[]{"title", "abstract", ""},
                // new int[]{R.id.tv_message_title, R.id.tv_message_abstract
                new int[]{R.id.tv_msg_title, R.id.ll_preview, R.id.image_selected});
    }

    @Override
    public void setOnPositiveClickedListener(OnPositiveListener listener) {
        super.setOnPositiveClickedListener(listener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.group_sort_layout);

        mDown = (ImageButton) findViewById(R.id.move_down);
        mDown.setOnClickListener(this);
        mUp = (ImageButton) findViewById(R.id.move_up);
        mUp.setOnClickListener(this);
        mOk = (ImageButton) findViewById(R.id.ok);
        mOk.setOnClickListener(this);

        mListview = (ListView) findViewById(R.id.message_listview);
        mListview.setAdapter(mFileAdapter);
        // mListview.setOnItemSelectedListener();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.move_up:
                int pos = mListview.getSelectedItemPosition();
                if (pos > 0) {
                    Map<String, Object> m = tlkList.remove(pos);
                    tlkList.add(pos - 1, m);
                }
                mFileAdapter.notifyDataSetChanged();
                break;

            case R.id.move_down:
                int p = mListview.getSelectedItemPosition();
                if (p < mListview.getCount()) {
                    Map<String, Object> m = tlkList.remove(p);
                    tlkList.add(p + 1, m);
                }
                mFileAdapter.notifyDataSetChanged();
                break;

            case R.id.ok:
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < tlkList.size(); i++) {
                    Map<String, Object> m = tlkList.get(i);
                    String name = (String) m.get("title");
                    result.append(name);
                    if (i != 0) result.append("^");
                }
                /* save group */
                String group = "Group-1";
                MessageTask.saveGroup(group, result.toString());
                if (pListener != null) {
                    pListener.onClick(group);
                }
                dismiss();
                break;

        }
    }
}
