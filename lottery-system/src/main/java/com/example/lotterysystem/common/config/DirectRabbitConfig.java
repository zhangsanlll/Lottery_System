package com.example.lotterysystem.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DirectRabbitConfig {
    public static final String QUEUE_NAME = "DirectQueue";
    public static final String EXCHANGE_NAME = "DirectExchange";
    public static final String ROUTING = "DirectRouting";
    /***队列起名：DirectQueue**
     @return
     */
    @Bean
    public Queue directQueue() {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使⽤，⽽且当连接关闭后队列即被删除。此参考优先级⾼于durable
        // autoDelete:是否⾃动删除，当没有⽣产者或者消费者使⽤此队列，该队列会⾃动删除。
        //   return new Queue("DirectQueue",true,true,false);
        //⼀般设置⼀下队列的持久化就好,其余两个就是默认false
        return new Queue(QUEUE_NAME,true);
    }
    /**
     * * Direct交换机
     起名：DirectExchange
     *
     *
     @return
     */
    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_NAME,true,false);
    }
    /***绑定将队列和交换机绑定,并设置⽤于匹配键：DirectRouting
     *
     * @return
     */
    @Bean
    Binding bindingDirect() {
        return BindingBuilder.bind(directQueue())
                .to(directExchange())
                .with(ROUTING);
    }
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}