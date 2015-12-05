package com.example.chaya.medprotest;

import android.content.Intent;
import android.widget.Button;


public class DiseaseSearchItemActivityTest extends
        android.test.ActivityUnitTestCase<DiseaseSearchItemActivity> {

    private int buttonId;
    private DiseaseSearchItemActivity activity;

    public DiseaseSearchItemActivityTest() {
        super(DiseaseSearchItemActivity.class);
    }
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent(getInstrumentation().getTargetContext(),
                DiseaseSearchItemActivity.class);
        intent.putExtra("DISEASE_NAME", "Cholera");
        startActivity(intent, null, null);
        activity = getActivity();
    }

    public void testLayout() {
        buttonId = R.id.ViewDrugList;
        assertNotNull(activity.findViewById(buttonId));
        Button view = (Button) activity.findViewById(buttonId);
        assertEquals("Incorrect label of the button", "View DrugList", view.getText());
        assertNotNull("Button not allowed to be null", view);
    }
} 