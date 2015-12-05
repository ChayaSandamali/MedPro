package com.example.chaya.medprotest;

import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;


public class DoctorActivityTest extends
        android.test.ActivityUnitTestCase<DoctorActivity> {

    private int buttonId;
    TableLayout msgTable;
    TextView msgTitleTv;
    TextView msgBodyTv;
    TextView msgDateTv;
    ImageView msgImg;
    Button reply;
    private DoctorActivity activity;

    public DoctorActivityTest() {
        super(DoctorActivity.class);
    }
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent(getInstrumentation().getTargetContext(),
                DoctorActivity.class);
        intent.putExtra("DISEASE_NAME", "Cholera");
        startActivity(intent, null, null);
        activity = getActivity();
    }

    public void testLayout() {

        //Extracting View Elements from the layout
        msgTable = (TableLayout) activity.findViewById(R.id.msgTable);
        msgTitleTv = (TextView) activity.findViewById(R.id.msgTitle);
        msgBodyTv = (TextView) activity.findViewById(R.id.msgBody);
        msgDateTv = (TextView) activity.findViewById(R.id.msgDate);
        msgImg = (ImageView) activity.findViewById(R.id.msgImage);
        reply = (Button) activity.findViewById(R.id.reply);

        //Asserting whether elements are not null
        assertNotNull("This cannot be null", reply);
        assertNotNull("This cannot be null", msgTable);
        assertNotNull("This cannot be null", msgTitleTv);
        assertNotNull("This cannot be null", msgBodyTv);
        assertNotNull("This cannot be null", msgDateTv);
        assertNotNull("This cannot be null", msgImg);
        assertNotNull("Button not allowed to be null", reply);

        assertEquals("Incorrect label of the button", "Reply", reply.getText());
    }
} 