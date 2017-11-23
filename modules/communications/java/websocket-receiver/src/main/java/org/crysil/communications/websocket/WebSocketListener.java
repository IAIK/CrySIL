package org.crysil.communications.websocket;

import io.netty.channel.Channel;

public interface WebSocketListener {

	void onMessage(Channel websocketChannel, String msg);

	void onConnect(Channel websocketChannel);

	void onClose(Channel websocketChannel);

}
