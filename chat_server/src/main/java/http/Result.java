package http;

public class Result {
    //暂时没用
    private String data;
//    private Integer port;


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "result{" +
                "data='" + data + '\'' +
                '}';
    }
}
