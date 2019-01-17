package cn.recallcode.iot.mqtt.server.broker.override_netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.TypeParameterMatcher;

/**
 * 重写了一下Netty提供的SimpleChannelInboundHandler,原因：实在看不惯那个read0 和 simple
 * 解释一下这个类：这个类其实是别人帮我们实现的一个比较实用的类，因为如果我们直接实现ChannelInboundHandlerAdapter，还需要
 * 自己去实现消息类型，比图Http ，Mqtt等等，这个类其实就是限制我们处理的消息类型，自动帮我们转换，省去麻烦。当然完全可以自己
 * 去实现一个消息类型转换器，我感觉是非常蛋疼的，啊♂ ！
 *
 * @param <I>I表示一个抽象 ，特指实现了Message的具体消息的类
 */
public abstract class MessageChannelInBoundHandler<I> extends ChannelInboundHandlerAdapter {
    private final TypeParameterMatcher matcher;
    private final boolean autoRelease;

    protected MessageChannelInBoundHandler() {
        this(true);
    }

    protected MessageChannelInBoundHandler(boolean autoRelease) {
        this.matcher = TypeParameterMatcher.find(this, SimpleChannelInboundHandler.class, "I");
        this.autoRelease = autoRelease;
    }

    protected MessageChannelInBoundHandler(Class<? extends I> inboundMessageType) {
        this(inboundMessageType, true);
    }

    protected MessageChannelInBoundHandler(Class<? extends I> inboundMessageType, boolean autoRelease) {
        this.matcher = TypeParameterMatcher.get(inboundMessageType);
        this.autoRelease = autoRelease;
    }

    private boolean acceptInboundMessage(Object msg) throws Exception {
        return this.matcher.match(msg);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean release = true;

        try {
            if (this.acceptInboundMessage(msg)) {
                this.channelMessageRead(ctx, (I) msg);
            } else {
                release = false;
                ctx.fireChannelRead(msg);
            }
        } finally {
            if (this.autoRelease && release) {
                ReferenceCountUtil.release(msg);
            }

        }


    }

    /**
     * 在上一步的 channelRead 中，已经处理了消息转换，所以我们实现这个方法的时候，I 其实就是我们具体的消息了
     *
     * @param channelHandlerContext 具体的处理器的上下文
     * @param i 具体消息
     * @throws Exception
     */
    protected abstract void channelMessageRead(ChannelHandlerContext channelHandlerContext, I i) throws Exception;
}
