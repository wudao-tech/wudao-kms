import com.wudao.KmsApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = KmsApplication.class)
public class OllamaTest {
    @Resource
    private OllamaChatModel ollamaChatModel;
    @Test
    public void ollamaTest(){
        System.out.println(ollamaChatModel.call(new Prompt("你是谁", OllamaOptions.builder().model("qwen3:0.6b").build())).getResult().getOutput().getText());
    }

    @Test
    public void ollamaDivTest(){
        OllamaApi ollamaApi = OllamaApi.builder().baseUrl("http://localhost:11434").build();
        OllamaChatModel chatModel = OllamaChatModel.builder().ollamaApi(ollamaApi).build();
        OllamaOptions chatOptions = OllamaOptions.builder().model("qwen3:0.6b").build();
        System.out.println(chatModel.call(new Prompt("你是谁", chatOptions)).getResult().getOutput().getText());
    }
}
