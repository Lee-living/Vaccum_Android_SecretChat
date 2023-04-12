package http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.domain.Message;
import org.springframework.stereotype.Repository;

@Repository
public class TestMethodService implements TestMethodDao {

    @Override
    public Message test(String s) throws JsonProcessingException {

        // ObjectMapper是Jackson库提供的反序列化工具类
        ObjectMapper objectMapper = new ObjectMapper();

        // 将JSON字符串反序列化成Message对象
        Message msg = objectMapper.readValue(s, Message.class);


        return msg;
    }

}
