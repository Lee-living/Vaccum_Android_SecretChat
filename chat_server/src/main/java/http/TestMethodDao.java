package http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lee.domain.Message;
import org.springframework.stereotype.Repository;

@Repository
public interface TestMethodDao {
    //一个方法
    public Message test(String s) throws JsonProcessingException;

}
