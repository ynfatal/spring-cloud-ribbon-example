package com.example.config;

import com.netflix.loadbalancer.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ribbon 默认规则为区域回避，实现类为 ZoneAvoidanceRule，这个类继承了 PredicateBasedRule，
 * PredicateBasedRule 又继承了 ClientConfigEnabledRoundRobinRule， ClientConfigEnabledRoundRobinRule
 * 底层默认实现的规则又是 RoundRobinRule（轮询规则），不过具体轮询方法是使用 PredicateBasedRule 的
 * com.netflix.loadbalancer.PredicateBasedRule#choose(java.lang.Object)
 * 初始化负载均衡默认策略的具体实现在 org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration#ribbonRule(com.netflix.client.config.IClientConfig)，
 * 在第一次服务通信时触发初始化，作为 Bean 被初始化到 Spring 容器中
 * org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration#ribbonLoadBalancer(com.netflix.client.config.IClientConfig, com.netflix.loadbalancer.ServerList, com.netflix.loadbalancer.ServerListFilter, com.netflix.loadbalancer.IRule, com.netflix.loadbalancer.IPing, com.netflix.loadbalancer.ServerListUpdater)
 * com.netflix.loadbalancer.ZoneAwareLoadBalancer#ZoneAwareLoadBalancer(com.netflix.client.config.IClientConfig, com.netflix.loadbalancer.IRule, com.netflix.loadbalancer.IPing, com.netflix.loadbalancer.ServerList, com.netflix.loadbalancer.ServerListFilter, com.netflix.loadbalancer.ServerListUpdater)
 * com.netflix.loadbalancer.DynamicServerListLoadBalancer#DynamicServerListLoadBalancer(com.netflix.client.config.IClientConfig, com.netflix.loadbalancer.IRule, com.netflix.loadbalancer.IPing, com.netflix.loadbalancer.ServerList, com.netflix.loadbalancer.ServerListFilter, com.netflix.loadbalancer.ServerListUpdater)
 * com.netflix.loadbalancer.BaseLoadBalancer#BaseLoadBalancer(com.netflix.client.config.IClientConfig, com.netflix.loadbalancer.IRule, com.netflix.loadbalancer.IPing)
 * com.netflix.loadbalancer.BaseLoadBalancer#initWithConfig(com.netflix.client.config.IClientConfig, com.netflix.loadbalancer.IRule, com.netflix.loadbalancer.IPing, com.netflix.loadbalancer.LoadBalancerStats)
 * com.netflix.loadbalancer.ZoneAwareLoadBalancer#setRule(com.netflix.loadbalancer.IRule)
 * com.netflix.loadbalancer.BaseLoadBalancer#setRule(com.netflix.loadbalancer.IRule)
 *
 * Ribbon 默认负载均衡规则是 ZoneAvoidanceRule ，这个规则我们可以换的。假如我们想换成 RandomRule，写法如下：
 *    @Bean
 *    public IRule randomRule() {
 *        return new RandomRule();
 *    }
 * ILoadBalancer 的 Bean 在初始化的时候，会引入 IRule Bean 作为负载均衡规则。在这里为了直观点，就贴出源码了：
 *    @Bean
 *    @ConditionalOnMissingBean
 *    public ILoadBalancer ribbonLoadBalancer(IClientConfig config,
 * 			ServerList<Server> serverList, ServerListFilter<Server> serverListFilter,
 * 			IRule rule, IPing ping, ServerListUpdater serverListUpdater) {
 * 		if (this.propertiesFactory.isSet(ILoadBalancer.class, name)) {
 * 			return this.propertiesFactory.get(ILoadBalancer.class, config, name);
 *        }
 *      // 负载均衡规则在这里被封装了
 * 		return new ZoneAwareLoadBalancer<>(config, rule, ping, serverList,
 * 				serverListFilter, serverListUpdater);
 *    }
 *
 * 问题： 使用自定义局部配置的 rule 时，出现了一个大问题，就是服务提供者的 ILoadBalancer 出现指定错误问题。
 * 描述： 服务1的 server list：[8080, 8081], 服务2的 server list：[8082, 8083]。后面访问服务1的时候，
 *     拿了服务2 的 server list，所以找不到对应的接口，报了异常，消费者这边报 500，并提示 404，找不到服务1的接口。
 *     可以在方法 com.example.controller.ConsumerController#ribbonContext() 中 debug 查看各个 rule 的 lb。
 * 原因：自己搞出来的 bug，自定义全局配置类的 Bean 加了 @Primary 导致局部的 Bean 不起作用。
 * 解决方式：自定义全局配置类的 @Primary 移到自定义局部配置类。
 *
 * 同时存在自定义局部配置类和自定义全局配置类的话，，配置行为如下（和 Feign 的类似）：
 * - 自定义全局：使用 org.springframework.cloud.netflix.ribbon.RibbonClients#defaultConfiguration() 指定自定义全局配置类，该配置类不需要标注为配置组件
 * - 自定义局部：使用 org.springframework.cloud.netflix.ribbon.RibbonClient#configuration() 指定自定义局部配置类，该配置类不需要标注为配置组件
 * 注意事项：
 * - 相同类型的 Bean，在自定义局部配置类和自定义全局配置类中不能重复，否则会造成局部的 Bean 无法实例化，这样局部就没作用了
 * - 在自义局部配置类给该 Bean 添加标注 @Primary，以表示该类型的 Bean 自定义局部配置的优先级高于自定义全局配置。
 * - 如果是默认全局与其中一个自定义配置搭配，那么随便玩，要是怕出问题也可以以当前这个最严谨的方式配置，也就是两点注意事项。
 *   估计后边继承 NamedContextFactory 的组件都这么玩。
 * @implNote
 * 1. @Primary 在相同类型的多个 Bean 中，选择优先自动装配的 Bean;
 * 2. 自定义的 Ribbon Client 全局配置组件需要用 @RibbonClients defaultConfiguration 属性指定，否则会出现
 *  ILoadBalancer 指定错误问题。
 * 3. org.springframework.context.annotation.Configuration#proxyBeanMethods()（翻译的）
 * 指定是否应该代理{@code @Bean}方法来强制执行bean生命周期行为，例如在用户代码中直接调用{@code @Bean}方法时
 * 返回共享的单例bean实例。这个特性需要方法拦截，通过运行时生成的CGLIB子类实现，它有一些限制，比如不允许
 * configuration类及其方法声明{@code final}。<p>默认是{@code true}，允许在配置类内的“bean间引用”，以及
 * 外部调用这个配置的{@code @Bean}方法，例如从另一个配置类调用。如果由于这个特定配置的每个{@code @Bean}方法都是
 * 独立的，并且被设计为容器使用的普通工厂方法，那么将这个标志切换为{@code false}，以避免CGLIB子类处理。
 * <p>关闭bean方法拦截有效地单独处理{@code @Bean}方法，就像在非{@code @Configuration}类上声明时一样。
 * “@Bean 精简模式”(参见{@link Bean @Bean的javadoc})。因此，它在行为上等同于删除{@code @Configuration}构造型。
 * 理解：这个配置组件中的所有 Bean 都是独立的，并且被设计为容器使用的普通工厂方法（我理解为托管给 Spring 容器管理），
 * 所以在这里设置为 false，可以高效地处理 Bean 方法。
 * @author Fatal
 * @date 2020/6/26 0026 9:30
 */
//@Configuration(proxyBeanMethods = false)
public class RibbonConfiguration {

    /**
     * 区域回避（基于轮询），默认的负载均衡规则
     * ZoneAvoidancePredicate：过滤掉断路器跳闸的服务器和来自此客户端的并发连接过多的服务器。
     * AvailabilityPredicate：当最坏区域的聚合度量达到阈值，筛选出该区域中所有服务器的服务器。确定最坏区域的逻辑在 ZoneAwareLoadBalancer 中描述
     */
    @Bean
    public IRule zoneAvoidanceRule() {
        return new ZoneAvoidanceRule();
    }

    // 随机
    /*@Bean
    public IRule randomRule() {
        return new RandomRule();
    }*/

    // 轮询
    /*@Bean
    public IRule roundRobinRule() {
        return new RoundRobinRule();
    }*/

    // 重试（基于轮询）
    /*@Bean
    public IRule retryRule() {
        return new RetryRule();
    }*/

    // 权重
    /*@Bean
    public IRule weightedResponseTimeRule() {
        return new WeightedResponseTimeRule();
    }*/

    // 减压
    /*@Bean
    public IRule bestAvailableRule() {
        return new BestAvailableRule();
    }*/

}
