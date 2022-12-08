package net.absoft.Listeners;

import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {
    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println(result.getMethod().getMethodName() + " takes " + (result.getEndMillis() - result.getStartMillis()) + "ms");
    }
}
