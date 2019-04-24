package com.mirzet.mbanking;

import org.junit.Test;

import java.util.Date;

import static com.mirzet.mbanking.ViewModels.AccountInfo.stringToDate;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void TestDate(){
        String date = "29.09.1994.";
        Date dt = stringToDate(date,"dd.MM.yyyy.");
    }
}