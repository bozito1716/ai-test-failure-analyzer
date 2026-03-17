package listeners;

import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.AiAnalyzer;

public class AIFailureListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        String testName   = result.getName();
        String error      = result.getThrowable().getMessage();
        String stackTrace = java.util.Arrays.toString(result.getThrowable().getStackTrace());

        System.out.println("\n========== 🤖 AI FAILURE ANALYSIS ==========");
        System.out.println(AiAnalyzer.analyzeFailure(testName, error, stackTrace));
        System.out.println("=============================================\n");
    }
}