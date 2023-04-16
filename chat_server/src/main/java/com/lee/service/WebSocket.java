package com.lee.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.domain.Message;
import com.lee.function.FriendList;
import com.lee.function.Login;
import com.lee.function.TransferMsg;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;


@Component
@ServerEndpoint("/websocket")
@Slf4j
public class WebSocket {

    //反序列化用的
    ObjectMapper objectMapper = new ObjectMapper();
    private Session session;
    //存连接的用户数据
    private static CopyOnWriteArraySet<Session> SessionSet = new CopyOnWriteArraySet<>();


    @OnOpen
    public void onOpen(Session session) throws IOException {
        this.session = session;
        //把连上的session 放进SessionSet
        SessionSet.add(this.session);
        log.info("新连接，现在总连接数为：" + SessionSet.size());

       /* FriendList friendList = new FriendList(SessionSet);

        //遍历一下我的session连接 打印一
        Iterator<Session> iterator = SessionSet.iterator();
        while (iterator.hasNext()) {
            Session session1 = iterator.next();
            //登陆一次就发一次，给所有人都发
            session1.getBasicRemote().sendText(friendList.RefreshList());
            System.out.println(session1);
        }*/

        //要不要返回一个链接成功
    }


    @OnClose
    public void onClose(Session session) throws IOException {
        // 关闭连接
        this.session = session;
        //关闭连接后删除当前session
        SessionSet.remove(this.session);
        log.info("连接断开，现在总连接数为：" + SessionSet.size());

        FriendList friendList = new FriendList(SessionSet);

        Iterator<Session> iterator = SessionSet.iterator();
        while (iterator.hasNext()) {
            Session session1 = iterator.next();
            //登陆一次就发一次，给所有人都发
            session1.getBasicRemote().sendText(friendList.RefreshList());
            System.out.println(session1);
        }

    }

    @OnMessage
    public void onMessage(String receiveMsg) throws IOException {
//        System.out.println(session.getBasicRemote());
        // 处理收到的消息
        // 打印一下消息内容
        System.out.println(receiveMsg);

        //接收到的消息 反序列化成Message
        Message msg= objectMapper.readValue(receiveMsg, Message.class);
        // 打印一下消息内容
//        System.out.println(msg);
        //用线程处理     ？线程池
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                // 这里放置接收到消息后的处理逻辑、
                String responseMsgjson = "";
                //获取到sign 判断处理方法
                switch (msg.getSign()) {
                    case 1:
                        //获取在线信息   1
                        //创建refresh对象
                        FriendList friendList = new FriendList(SessionSet);
                        // 将返回消息发送回客户端
                        session.getBasicRemote().sendText(friendList.RefreshList());
                        //打印返回消息
//                        System.out.println(responseMsgjson);
                        break;
                    case 2:
                        //登陆请求     2
                        //创建login对象
                        Login login = new Login(msg,SessionSet);
                        //用login.proveLogin() 验证登陆 放到responseMsg
                        Message responseMsg = login.proveLogin();

                        if(responseMsg.getSign() == 20) {
                            //登陆成功 把登陆的用户名绑定到当前的session
                            session.getUserProperties().put("username", msg.getUser());
//                            System.out.println("连接上的人" + (String) session.getUserProperties().get("username") + "是" + session.getBasicRemote());
                            //序列化  responseMsgjson
                            responseMsgjson = objectMapper.writeValueAsString(responseMsg);
//                            System.out.println(session.getBasicRemote());
                            // 登陆成功  发送回客户端
                            session.getBasicRemote().sendText(responseMsgjson);

                            FriendList friendList2 = new FriendList(SessionSet);

                            //遍历一下我的session连接 打印一
                            Iterator<Session> iterator = SessionSet.iterator();
                            while (iterator.hasNext()) {
                                Session session1 = iterator.next();
                                //登陆一次就发一次，给所有人都发
                                session1.getBasicRemote().sendText(friendList2.RefreshList());
                                System.out.println(session1);
                            }
                        }else {
                            responseMsgjson = objectMapper.writeValueAsString(responseMsg);
                            // 将登陆失败发送回客户端
                            session.getBasicRemote().sendText(responseMsgjson);
                            //登陆失败移除当前session，再登会再申请
                            SessionSet.remove(session);
                        }
                        break;
                    case 3:
                        //发消息请求       3
                        //获取当前session的username，发给接受者，免得不知道是谁发的 这有点怪
                        String username = (String) session.getUserProperties().get("username");
                        //存 发消息的session，返回回执给 发送消息者用
                        RemoteEndpoint.Basic basicRemote = session.getBasicRemote();
//                        System.out.println(msg);
//                        System.out.println(Arrays.toString(msg.getSecretMsg()));
                        //创建一个Msg对象，处理收发消息
                        TransferMsg copeTransferMsg = new TransferMsg(msg,username);
                        boolean flag = true;
                        //循环找 接受消息者在不在线
                        for (Session session : SessionSet) {
                            //当前遍历session 的name放到sendname
                            String sendname = (String) session.getUserProperties().get("username");
                            // 如果找到对应的用户
                            if (sendname != null && sendname.equals(msg.getUser())) {
//                                byte[] secretMsgCopy = Arrays.copyOf(msg.getSecretMsg(), msg.getSecretMsg().length);
//                                System.out.println(secretMsgCopy);
//                                Message SendMsg = new Message(31,username,null,secretMsgCopy);

//                                System.out.println("发给" + session.getBasicRemote() + " 了：" + sendMsgjson);
                                //发给 接收消息者
                                session.getBasicRemote().sendText(copeTransferMsg.sendMsg());

                                // 发送消息到发送者  因为发消息成功了
                                basicRemote.sendText(copeTransferMsg.responseMsgYes());
                                flag = false;
                                break;
                            }
                        }
                        if(flag){
                            // 发送消息到发送者  因为发消息失败了 copeTransferMsg.responseMsgNot()是Msg对象copeMsg处理过后发给
                            basicRemote.sendText(copeTransferMsg.responseMsgNot());
                        }
                        break;

                    default:
                        //未知错误99
                }
            }
        }).start();

    }

    @OnError
    public void onError(Session session, Throwable error) {
        // 处理错误
    }


    public void sendMessage(Message msg) {
        // 发送消息
    }

}



          /*      if(msg.getSign() == 1){
                    //接受  1  获取在线信息
                    FriendList refresh = new FriendList(SessionSet);
                    String responseMsgjson = objectMapper.writeValueAsString(refresh.RefreshList());
                    // 将消息发送回客户端
                    session.getBasicRemote().sendText(responseMsgjson);
                    System.out.println(responseMsgjson);
                }else if(msg.getSign() == 2){
                    //接受  2 登陆请求
                    Login login = new Login(msg.getDate(),SessionSet);
                    //验证登陆
                    Message responseMsg = login.proveLogin();
                    if(responseMsg.getSign() == 20) {
                        //登陆成功给session绑定username
                        session.getUserProperties().put("username", msg.getDate().getUser());
                        System.out.println((String) session.getUserProperties().get("username"));
                        //再转json
                        String responseMsgjson = objectMapper.writeValueAsString(responseMsg);
                        System.out.println(session.getBasicRemote());
                        // 将登陆成功发送回客户端
                        session.getBasicRemote().sendText(responseMsgjson);
                    }else {
                        String responseMsgjson = objectMapper.writeValueAsString(responseMsg);
                        // 将登陆失败发送回客户端
                        session.getBasicRemote().sendText(responseMsgjson);
                    }
                }else if(msg.getSign() == 3){
                    //接受到 3 发消息请求
                    //获取当前session的username，发给接受者，免得不知道是谁发的。
                    String username = (String) session.getUserProperties().get("username");
                    //创建一个Msg对象，处理收发消息
                    TransferMsg copeMsg = new TransferMsg(msg.getDate(),username);
                    //循环找 接受消息者在不在线
                    for (Session session : SessionSet) {
                        //当前遍历session 的name放到sendname
                        String sendname = (String) session.getUserProperties().get("username");
                        // 如果找到对应的用户
                        if (sendname != null && username.equals(copeMsg.sendMsg().getDate().getUser())) {
                            // 发送消息到接受消息客户端  copeMsg.sendMsg()是Msg对象copeMsg处理过后发给  接受者的 序列化一下
                            String sendMsgjson = objectMapper.writeValueAsString(copeMsg.sendMsg());
                            session.getBasicRemote().sendText(sendMsgjson);

                            // 发送消息到发送者  因为发消息成功了 copeMsg.responseMsgYes()是Msg对象copeMsg处理过后发给  发送者的 序列化一下
                            String responseMsgjson = objectMapper.writeValueAsString(copeMsg.responseMsgYes());
                            session.getBasicRemote().sendText(responseMsgjson);
                        }else {
                            // 发送消息到发送者  因为发消息失败了 copeMsg.responseMsgNot()是Msg对象copeMsg处理过后发给  发送者的 序列化一下
                            String responseMsgjson = objectMapper.writeValueAsString(copeMsg.responseMsgNot());
                            session.getBasicRemote().sendText(responseMsgjson);
                        }
                    }
                }*/

