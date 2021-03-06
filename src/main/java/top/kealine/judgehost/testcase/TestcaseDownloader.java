package top.kealine.judgehost.testcase;

import com.google.common.collect.ImmutableMap;
import okhttp3.Response;
import top.kealine.judgehost.JudgehostInitializer;
import top.kealine.judgehost.util.FileUtil;
import top.kealine.judgehost.util.HttpUtil;

import java.io.InputStream;
import java.util.Objects;

public class TestcaseDownloader {
    private static final String TESTCASE_URL = "/testcase";

    public static boolean download(int testcaseId, boolean isInput, String md5){
        Response response = HttpUtil.post(TESTCASE_URL, ImmutableMap.of(
                "id", testcaseId,
                "input", isInput,
                "md5", md5
        ));
        if (response != null) {
            if (response.code() == 200) {
                String filename = testcaseId + (isInput?".in":".ans");
                InputStream inputStream = Objects.requireNonNull(response.body()).byteStream();
                FileUtil.save(inputStream, filename);
                String fileMd5 = FileUtil.md5Hex(filename);
                TestcaseLocalTemp.setTestcase(filename, fileMd5);
                return true;
            }if (response.code() == 204) {
                return true;
            }
            else if (response.code() == 700) {
                try {
                    JudgehostInitializer.login();
                    return download(testcaseId, isInput, md5);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
