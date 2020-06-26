package com.example.config;

import com.netflix.loadbalancer.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

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
 * ZoneAvoidanceRule 底层默认实现的规则是 RoundRobinRule，这个规则我们可以换的。假如我们想换成 RandomRule，写法如下：
 *     @Bean
 *     public IRule randomRule() {
 *         return new RandomRule();
 *     }
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
 * 需要注意的是，如果应用中有多个 IRule Bean，那么我们必须指定其中一个优先自动装配，加 @Primary 即可实现。
 *
 * @author Fatal
 * @date 2020/6/26 0026 9:30
 * @desc @Primary 在相同类型的多个 Bean 中，选择优先自动装配的 Bean
 */
@Configuration
public class RibbonConfiguration {

    /**
     * 随机
     */
    @Bean
    @Primary
    public IRule randomRule() {
        return new RandomRule();
    }

    /**
     * 轮询
     */
    @Bean
    public IRule roundRobinRule() {
        return new RoundRobinRule();
    }

    /**
     * 重试（基于轮询）
     */
    @Bean
    public IRule retryRule() {
        return new RetryRule();
    }

    /**
     * 权重
     */
    @Bean
    public IRule weightedResponseTimeRule() {
        return new WeightedResponseTimeRule();
    }

    /**
     * 减压
     */
    @Bean
    public IRule bestAvailableRule() {
        return new BestAvailableRule();
    }

    /**
     * 区域回避（基于轮询），默认的负载均衡规则
     * ZoneAvoidancePredicate：过滤掉断路器跳闸的服务器和来自此客户端的并发连接过多的服务器。
     * AvailabilityPredicate：当最坏区域的聚合度量达到阈值，筛选出该区域中所有服务器的服务器。确定最坏区域的逻辑在 ZoneAwareLoadBalancer 中描述
     */
    @Bean
    public IRule zoneAvoidanceRule() {
        return new ZoneAvoidanceRule();
    }


}
