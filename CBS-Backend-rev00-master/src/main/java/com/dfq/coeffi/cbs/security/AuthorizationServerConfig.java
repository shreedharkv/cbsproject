package com.dfq.coeffi.cbs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

/**
 * @author H Kapil Kumar
 *
 */

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter
{
	@Autowired
	private AuthenticationManager authenticationManager;

    @Autowired
    private Environment environment;

    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private ClientDetailsService clientDetailsService;

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("permitAll()")
		.checkTokenAccess("isAuthenticated()");
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception
    {
        clients.inMemory()
            .withClient("cbs-service")
            .secret("cbs")
            .resourceIds("oauth2-resource")
            .authorizedGrantTypes("client_credentials", "refresh_token", "password")
            .scopes("server")
            .autoApprove(true)
        .and()
            .withClient("report-service")
            .secret("report")
            .resourceIds("report")
            .autoApprove(true)
            .authorizedGrantTypes("client_credentials", "refresh_token")
            .scopes("server")
        .and()
            .withClient("deposit-service")
            .secret("deposit")
            .resourceIds("deposit")
            .authorizedGrantTypes("client_credentials", "refresh_token")
            .scopes("server")
            .autoApprove(true)
        .and()
            .withClient("client")
            .authorizedGrantTypes("client_credentials", "password", "refresh_token", "authorization_code")
            .scopes("read", "write")
            .resourceIds("oauth2-resource")
         //   .accessTokenValiditySeconds(1000000)
         //   .refreshTokenValiditySeconds(100000)
            .secret("secret");
		
	}

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
            .authenticationManager(authenticationManager)
            .userDetailsService(userDetailsService)
            .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
            .tokenStore(tokenStore())
            .tokenServices(tokenServices())
            .accessTokenConverter(accessTokenConverter());
    }


//    @Override
//    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//	    endpoints.tokenStore(tokenStore())
//            .tokenEnhancer(accessTokenConverter())
//            .authenticationManager(authenticationManager)
//            .tokenServices(tokenServices())
//            .userDetailsService(userDetailsService)
//                .allowedTokenEndpointRequestMethods(HttpMethod.POST, HttpMethod.GET);
//	}

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    protected JwtAccessTokenConverter accessTokenConverter() {
        String pwd = environment.getProperty("keystore.password");
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
                new ClassPathResource("jwt.jks"),
                pwd.toCharArray());
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("jwt"));
        return converter;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setClientDetailsService(clientDetailsService);
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setTokenEnhancer(accessTokenConverter());
        return defaultTokenServices;
    }
}