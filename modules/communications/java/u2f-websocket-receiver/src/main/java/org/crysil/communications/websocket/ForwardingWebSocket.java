package org.crysil.communications.websocket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.crysil.communications.websocket.interfaces.WebSocketListener;
import org.crysil.communications.websocket.ssl.WebsocketTrustManagerFactory;
import org.crysil.logging.Logger;

import java.net.URI;

public class ForwardingWebSocket {

	private final String uriString;
	private final WebSocketListener listener;
	private final TrustManagerFactory trustManagerFactory;
	private final KeyManagerFactory keyManagerFactory;
	private Channel websocketChannel;

	public ForwardingWebSocket(String uriString, WebSocketListener listener,
			WebsocketTrustManagerFactory trustManagerFactory) {
		this(uriString, listener, trustManagerFactory, null);
	}

	public ForwardingWebSocket(String uriString, WebSocketListener listener, TrustManagerFactory trustManagerFactory,
			KeyManagerFactory keyManagerFactory) {
		this.uriString = uriString;
		this.listener = listener;
		this.trustManagerFactory = trustManagerFactory;
		this.keyManagerFactory = keyManagerFactory;
	}

	public void start() {
		try {
			URI uri = new URI(uriString);
			String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
			final String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
			final int port = uri.getPort();
			if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
				System.err.println("Only WS(S) is supported, given URI is " + uriString);
				return;
			}

			final SslContext sslContext;
			if ("wss".equalsIgnoreCase(scheme)) {
				SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();
				if (trustManagerFactory != null) {
					sslContextBuilder = sslContextBuilder.trustManager(trustManagerFactory);
				}
				if (keyManagerFactory != null) {
					sslContextBuilder = sslContextBuilder.keyManager(keyManagerFactory);
				}
				sslContext = sslContextBuilder.build();
			} else {
				sslContext = null;
			}
			Logger.debug(String.format("Connecting to %s, with SSL %b", uri.toString(), sslContext));

			EventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
			final WebsocketClientHandler handler = new WebsocketClientHandler(listener,
					WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, false,
							new DefaultHttpHeaders()));

			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(nioEventLoopGroup).channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) {
							ChannelPipeline p = ch.pipeline();
							if (sslContext != null) {
								p.addLast(sslContext.newHandler(ch.alloc(), host, port));
							}
							p.addLast(new HttpClientCodec(), new HttpObjectAggregator(8192), handler);
						}
					});
			websocketChannel = bootstrap.connect(uri.getHost(), port).sync().channel();
			handler.handshakeFuture().sync();
			listener.onConnect(websocketChannel);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
