import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RAG 联调小工具：直接运行 main 方法即可测试 Python 向量库
 */
public class RagIntegrationTest {

    private static final String PYTHON_URL = "http://localhost:5000/search";

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. 模拟前端构造的 Prompt (你可以随意修改这里的关键词)
        String mockUserQuery = "我想面试字节跳动的 Java 后端，请问他们常考什么算法题？";

        System.out.println("🚀 正在向 Python 引擎 (5000端口) 发起检索...");
        System.out.println("📝 模拟 Prompt: " + mockUserQuery);

        try {
            // 2. 构造请求参数
            Map<String, Object> request = new HashMap<>();
            request.put("query", mockUserQuery);
            request.put("top_k", 3);

            // 3. 直接发起 POST 请求并接收为 Map
            // 这里用 Map 接收是最省事的，适合快速调试
            Map<String, Object> response = restTemplate.postForObject(PYTHON_URL, request, Map.class);

            // 4. 解析并打印
            if (response != null && response.containsKey("documents")) {
                List<String> docs = (List<String>) response.get("documents");

                System.out.println("\n✅ 检索成功！从向量库中“捞”出了以下 3 条最相关的校友回忆：");
                System.out.println("==========================================================");

                for (int i = 0; i < docs.size(); i++) {
                    System.out.println("【面经片段 " + (i + 1) + "】:");
                    System.out.println(docs.get(i));
                    System.out.println("----------------------------------------------------------");
                }
            } else {
                System.out.println("⚠️ Python 返回了空数据，请确认向量数据库中有相关内容。");
            }

        } catch (Exception e) {
            System.err.println("❌ 失败了！请确认：1. Python 脚本已启动；2. 5000 端口没被占用。");
            System.err.println("错误详情: " + e.getMessage());
        }
    }
}