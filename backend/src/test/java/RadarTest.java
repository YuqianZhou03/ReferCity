import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RadarTest {
    public static void main(String[] args) throws Exception {
        // 模拟 AI 给出的分值
        String valuesStr = "80,90,75,70,85";

        // 模拟 Java 调用过程
        ProcessBuilder pb = new ProcessBuilder("python", "scripts/generate_radar.py", valuesStr);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = reader.readLine();

        if (line != null && line.length() > 100) {
            System.out.println("✅ 测试成功！拿到了 Base64 数据，前 50 位是：" + line.substring(0, 50));
        } else {
            System.out.println("❌ 测试失败，Python 没有返回数据。");
        }
    }
}