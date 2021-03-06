package org.wsh.common.rest.socket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.wsh.common.enums.SessionKey;
import org.wsh.common.enums.msg.MessageKey;
import org.wsh.common.model.basic.UserBasicDO;
import org.wsh.common.model.message.MessageDO;
import org.wsh.common.util.logger.LoggerService;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.zookeeper.server.ServerCnxn.me;
import static org.wsh.common.enums.response.ModelKey.message;

/**
 * author: wsh
 * JDK-version:  JDK1.8
 * comments:  WebSocket处理器
 * since Date： 2016/11/4 9:41
 */
@Component
public class SystemWebSocketHandler extends LoggerService implements WebSocketHandler {

	private static final Map<Long,Map<Long,WebSocketSession>> groupSocketSessionMap;

	private static final Map<Long,WebSocketSession> userSocketSessionMap;
	static {
		userSocketSessionMap = new ConcurrentHashMap<Long,WebSocketSession>();
		groupSocketSessionMap = new ConcurrentHashMap<Long,Map<Long,WebSocketSession>>();
	}

	/**
	 * 建立连接后
	 */
	public void afterConnectionEstablished(WebSocketSession session)
			throws Exception {
		Object userObj = session.getAttributes().get(SessionKey.user.name());
		Object groupObj = session.getAttributes().get(MessageKey.GROUP_ID.name());
		if (userObj != null) {
			UserBasicDO user = (UserBasicDO) userObj;
			userSocketSessionMap.put(user.getId(), session);
			if (groupObj != null){
				Long groupId = Long.parseLong(String.valueOf(groupObj.toString()));
				if (!groupSocketSessionMap.containsKey(groupId)) {// 新建一个聊天组
					Map<Long, WebSocketSession> userSocketSessionMap = new ConcurrentHashMap<Long, WebSocketSession>();
					userSocketSessionMap.put(user.getId(), session);
					groupSocketSessionMap.put(groupId, userSocketSessionMap);
				} else if (!groupSocketSessionMap.get(groupId).containsKey(user.getId())) {// 已存在组,只需要加入新的成员
					groupSocketSessionMap.get(groupId).put(user.getId(), session);
				}
			}
		}else{
			logger.error("获取不到当前连接用户的完整信息!");
		}
	}

	/**
	 * 消息处理，在客户端通过Websocket API发送的消息会经过这里，然后进行相应的处理
	 */
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
			if(message.getPayloadLength()==0){
				return;
			}
			Object userObj = session.getAttributes().get(SessionKey.user.name());
			if (userObj != null) {
				UserBasicDO user = (UserBasicDO) userObj;
				MessageDO messageDO = JSON.parseObject(message.getPayload().toString(),new TypeReference<MessageDO>(){});
				messageDO.setFromId(user.getId());
//				messageDO.setFromUserName(user.getUserName());
				sendMessageToUser(messageDO.getTargetId(), new TextMessage(JSONObject.toJSONString(messageDO,
						SerializerFeature.WriteDateUseDateFormat)));
			}
	}

	/**
	 * 消息传输错误处理
	 */
	public void handleTransportError(WebSocketSession session,
			Throwable exception) throws Exception {
		if (session.isOpen()) {
			session.close();
		}
		removeWebSocketSession(session);
	}

	/**
	 * 关闭连接后
	 */
	public void afterConnectionClosed(WebSocketSession session,
			CloseStatus closeStatus) throws Exception {
		removeWebSocketSession(session);
	}

	public boolean supportsPartialMessages() {
		return false;
	}

	/**
	 * 给所有在线用户发送消息
	 * @param message
	 * @throws IOException
	 */
	public void broadcast(final TextMessage message) throws IOException {
		// 多线程群发
		for (Map.Entry<Long, WebSocketSession> entry : userSocketSessionMap.entrySet()) {
			sendToManyMessage(message, entry.getValue());
		}
	}

	/**
	 * 根据组ID给组成员发信息
	 * @param groupId 组ID
	 * @param message 消息内容
	 * @throws IOException
	 */
	public void sengGroupMessage(Long groupId, TextMessage message) throws IOException {
		if (groupSocketSessionMap.containsKey(groupId)){
			Map<Long,WebSocketSession> groupSessionMap = groupSocketSessionMap.get(groupId);
			for (Map.Entry<Long, WebSocketSession> entry : groupSessionMap.entrySet()) {
				sendToManyMessage(message, entry.getValue());
			}
		}else{
			logger.info("聊天组没人,发毛消息啊!");
		}
	}

	/**
	 * 给多人发消息
	 * @param message
	 * @param webSocketSession
     */
	private void sendToManyMessage(TextMessage message, WebSocketSession webSocketSession) {
		new Thread(new Runnable() {
			public void run() {
				try {
					if (webSocketSession.isOpen()) {
						webSocketSession.sendMessage(message);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}


	/**
	 * 给某个用户发送消息
	 * @param message
	 * @throws IOException
	 */
	public void sendMessageToUser(Long toUserId, TextMessage message)
			throws IOException {
		WebSocketSession session = userSocketSessionMap.get(toUserId);
		if (session != null && session.isOpen()) {
			session.sendMessage(message);
		}
	}

	/**
	 * 移除用户
	 * @param session
     */
	private void removeWebSocketSession(WebSocketSession session) {
		try {
			Object userObj = session.getAttributes().get(SessionKey.user.name());
			Object groupObj = session.getAttributes().get(MessageKey.GROUP_ID.name());
			if (userObj != null){
				UserBasicDO user = (UserBasicDO)userObj;
				userSocketSessionMap.remove(user.getId());
				if (groupObj != null) {
					Long groupId = Long.parseLong(String.valueOf(groupObj.toString()));
					if (groupSocketSessionMap.containsKey(groupId) && groupSocketSessionMap.get(groupId).containsKey(user.getId())) {// 新建一个聊天组
						// 移除组内用户
						groupSocketSessionMap.get(groupId).remove(user.getId());
						if (groupSocketSessionMap.get(groupId).isEmpty()) {
							// 移除组
							groupSocketSessionMap.remove(groupId);
						}
					} else {
						logger.error("移除失败,失败原因:获取不到当前连接用户的信息!");
					}
				}
			}else{
				logger.error("移除失败,失败原因:获取不到当前连接用户的完整信息!");
			}
		}catch (Exception e){
			logger.error("移除失败",e);
		}

	}
}
